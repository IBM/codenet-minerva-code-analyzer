package com.ibm.cldk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ibm.cldk.schema.CanId;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.io.TempDir;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;
import org.testcontainers.containers.Neo4jContainer;

/**
 * {@code --workspace-root} against a live Neo4j: the incremental push's wipe and orphan prune are
 * scoped by the app's {@code can://<app>/} prefix and each module's own id, so repo-relative module
 * ids must still be pruned when their file vanishes, and must not reach another application's graph.
 *
 * <p>Needs a container runtime, so it is opt-in like {@code Neo4jBoltWriterTest}: it runs only when
 * {@code RUN_CONTAINER_TESTS} is set.
 */
@EnabledIfEnvironmentVariable(named = "RUN_CONTAINER_TESTS", matches = ".+")
class WorkspaceRootNeo4jPushTest {

    private static final String PASSWORD = "testpassword123";

    @SuppressWarnings("resource")
    private static final Neo4jContainer<?> CONTAINER =
            new Neo4jContainer<>("neo4j:5").withAdminPassword(PASSWORD);

    private static Driver driver;

    @BeforeAll
    static void startup() {
        CONTAINER.start();
        driver = GraphDatabase.driver(CONTAINER.getBoltUrl(), AuthTokens.basic("neo4j", PASSWORD));
    }

    @AfterAll
    static void teardown() {
        if (driver != null) {
            driver.close();
        }
        CONTAINER.stop();
    }

    /** The CLI's pre-existing options are static; reset the ones this test sets, before and after. */
    @BeforeEach
    @AfterEach
    void resetStaticOptions() throws Exception {
        set("emit", "json");
        set("analysisLevel", 1);
        set("output", null);
        set("input", null);
        set("appName", null);
        set("neo4jUri", null);
        set("neo4jUser", null);
        set("neo4jPassword", null);
        CodeAnalyzer.projectRootPom = null;
    }

    private static void set(String field, Object value) throws Exception {
        Field f = CodeAnalyzer.class.getDeclaredField(field);
        f.setAccessible(true);
        f.set(null, value);
    }

    private static void writeClass(Path srcRoot, String name) throws IOException {
        Path pkg = Files.createDirectories(srcRoot.resolve("src/main/java/com/example"));
        Files.writeString(pkg.resolve(name + ".java"),
                "package com.example;\npublic class " + name + " {\n  public int m() { return 1; }\n}\n",
                StandardCharsets.UTF_8);
    }

    private static int push(Path in, Path repo, String app, Path out) {
        return new picocli.CommandLine(new CodeAnalyzer()).execute(
                "-i", in.toString(), "-o", out.toString(), "--emit", "neo4j", "--no-build",
                "--workspace-root", repo.toString(), "--app-name", app,
                "--neo4j-uri", CONTAINER.getBoltUrl(), "--neo4j-user", "neo4j", "--neo4j-password", PASSWORD);
    }

    private static long count(String cypher, Object... params) {
        try (Session s = driver.session()) {
            return s.run(cypher, Values.parameters(params)).single().get(0).asLong();
        }
    }

    @Test
    void aVanishedRepoRelativeModuleIsPrunedAndOtherAppsAreUntouched(@TempDir Path tmp) throws IOException {
        Path repo = tmp.resolve("repo");
        Path orders = repo.resolve("services/orders");
        Path billing = repo.resolve("services/billing");
        writeClass(orders, "Keep");
        writeClass(orders, "Gone");
        writeClass(billing, "Gone");

        assertEquals(0, push(orders, repo, "orders", tmp.resolve("o1")));
        assertEquals(0, push(billing, repo, "billing", tmp.resolve("o2")));

        String key = "services/orders/src/main/java/com/example/";
        String gone = CanId.moduleId(CanId.applicationId("orders"), key + "Gone.java");
        String keep = CanId.moduleId(CanId.applicationId("orders"), key + "Keep.java");
        String billingGone = CanId.moduleId(CanId.applicationId("billing"),
                "services/billing/src/main/java/com/example/Gone.java");
        assertEquals(1, count("MATCH (m:JModule {id: $id}) RETURN count(m)", "id", gone),
                "precondition: the module is pushed under its repo-relative id");
        long billingNodes = count("MATCH (n) WHERE n.id STARTS WITH $p RETURN count(n)",
                "p", CanId.applicationId("billing") + "/");
        assertTrue(billingNodes > 0, "precondition: the other app is in the same database");

        Files.delete(orders.resolve("src/main/java/com/example/Gone.java"));
        assertEquals(0, push(orders, repo, "orders", tmp.resolve("o3")));

        assertEquals(0, count("MATCH (n) WHERE n.id = $id OR n.id STARTS WITH $p RETURN count(n)",
                "id", gone, "p", gone + "/"), "the vanished module and its declarations are pruned");
        assertEquals(1, count("MATCH (m:JModule {id: $id}) RETURN count(m)", "id", keep),
                "the surviving module stays");
        assertEquals(1, count("MATCH (m:JModule {id: $id}) RETURN count(m)", "id", billingGone),
                "a same-named file in another app is a different node and is not touched");
        assertEquals(billingNodes, count("MATCH (n) WHERE n.id STARTS WITH $p RETURN count(n)",
                "p", CanId.applicationId("billing") + "/"), "the other app's graph is unchanged");
    }
}
