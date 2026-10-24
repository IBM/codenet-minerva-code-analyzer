# `--workspace-root`: repo-relative file keys

**Status:** accepted · 2026-10-24 · tracks _(issue to be filed)_

## Problem

Every v2 file key is relative to `--input`. When the analyzed project sits inside a larger
repository (`services/orders/` in a monorepo), keys read `src/main/java/.../App.java` rather than
the repository path `services/orders/src/main/java/.../App.java`. Tooling that speaks repository
paths — git, IDEs, PR diffs, scanners — cannot join on them without knowing and re-adding the
prefix.

## Contract-impact triage

| Question | Answer |
| --- | --- |
| Changes `analysis.json` v2 shape? | No. No node, edge or field is added, removed or renamed. |
| Changes v2 id/key meaning? | Yes, opt-in: with `--workspace-root` the `<file>` segment is relative to the workspace root instead of `--input`. Default output is byte-identical. |
| Changes CLI? | Yes — one new optional flag. Additive → **minor, 3.4.0**. |
| Repos touched | `codeanalyzer-java` (this spec's PR). |

## Decisions (user-approved 2026-10-24)

1. **New optional flag `--workspace-root <dir>`.** When set, every path-derived v2 key is computed
   relative to it rather than to `--input`:
   - `application.symbol_table` keys (`L1Extractor.fileKey`);
   - `module.id` and every durable id below it (`can://<app>/java/<file>/…`), since they are built
     from the file key;
   - artifact keys and ids (`can://<app>/artifact/<rel>`, `ArtifactDiscovery`).

   Unset, behavior is unchanged: the root is `--input`.
2. **The app segment does not move.** `can://<app>` stays `--app-name`, else the `--input`
   directory name. Only the `<file>` segment changes, so the L1 cache key, the Neo4j `can://<app>/`
   prefix wipe/prune and `@external` ids keep their current scoping.
3. **`--input` must be inside the workspace root.** Both are resolved to absolute, normalized
   paths; if `--input` is not the root or a descendant of it, exit non-zero at argument validation.
   This keeps the schema invariant intact: keys are relative, never absolute, never `..`.
4. **v2 only.** v1 keys are absolute paths, so there is nothing to relativize. `--workspace-root`
   with `--schema v1` is a hard argument error.
5. **No new schema field.** Consumers read source text from `module.source` and never resolve a
   key against the filesystem, so the output need not record which root keys are relative to.
6. **Language-neutral semantics.** Artifact and `@external` ids are deliberately shared across
   sibling analyzers over the same `<app>`. Sibling analyzers that adopt a workspace root are
   expected to use these same semantics (decisions 1–4), so a repository-relative artifact id is
   the same node whichever analyzer emitted it.

## Implementation notes

- Thread one `keyRoot` path (workspace root if set, else `--input`) to the two relativization
  sites: `L1Extractor.fileKey` (via `extractAll`) and `ArtifactDiscovery.discover`. Source
  discovery, builds and dependency download still run against `--input`.
- `CanId.moduleId` strips leading `[./]+` from the key. Decision 3 makes `..` keys unreachable;
  pin that with a test rather than relying on the strip.
- `L3WalaOverlays.deriveApplicationId` recovers the app id by stripping the file key off a module
  id; it is key-shape agnostic, but cover it with a nested-root fixture.
- Incremental cache: keys change when the flag is toggled, so a cache written without the flag
  simply misses for a run with it. No migration; document it.

## Verification

- Unit: nested fixture (`<tmp>/repo/services/app/…`) run with `--workspace-root <tmp>/repo` —
  every `symbol_table` key, module id and artifact id starts with `services/app/`; no key is
  absolute or contains `..`.
- Unit: without the flag, output is byte-identical to today's.
- CLI: `--input` outside the root → non-zero exit with a clear message; `--workspace-root` with
  `--schema v1` → non-zero exit.
- L2/L3 on the nested fixture: call-graph `src`/`dst` and L3 overlay ids carry the same
  repo-relative module ids.
- Neo4j: an incremental push with the flag wipes/prunes only that app's nodes.

## Release plan

Ships in **codeanalyzer-java 3.4.0** as an opt-in flag; default output is unchanged, so no
consumer needs to move in lockstep. README documents the flag, the repo-relative keys it produces,
and the cache miss on toggling it.
