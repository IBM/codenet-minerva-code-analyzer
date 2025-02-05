package org.entrypoints.jakarta;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.batch.api.Batchlet;
import jakarta.batch.api.chunk.ItemProcessor;
import jakarta.ejb.*;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jws.WebService;
import jakarta.jws.WebMethod;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import jakarta.xml.ws.WebServiceProvider;
import java.io.IOException;

@WebServlet(urlPatterns = "/example", asyncSupported = true)
@WebService
@ServerEndpoint("/websocket")
public class JavaEEEntrypoints extends HttpServlet implements ServletContextListener, MessageListener {

    // Servlet HTTP Methods
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

    @Override
    protected void doHead(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

    @Override
    protected void doTrace(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

    // Servlet Lifecycle Methods
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    // Filter Methods
    @WebFilter("/path/*")
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
    }

    // Async Servlet Methods
    @Override
    public void onComplete(AsyncEvent event) throws IOException {
    }

    @Override
    public void onTimeout(AsyncEvent event) throws IOException {
    }

    @Override
    public void onError(AsyncEvent event) throws IOException {
    }

    @Override
    public void onStartAsync(AsyncEvent event) throws IOException {
    }

    // WebSocket Methods
    @OnOpen
    public void onOpen(Session session) {
    }

    @OnMessage
    public void onMessage(Session session, String message) {
    }

    @OnClose
    public void onClose(Session session) {
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
    }

    // Batch Processing
    @Batchlet
    public String process() {
        return "COMPLETED";
    }

    @ItemProcessor
    public Object processItem(Object item) {
        return item;
    }

    // ServletContext Listener Methods
    @Override
    @WebListener
    public void contextInitialized(ServletContextEvent sce) {
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

    // Session Listener Methods
    @WebListener
    public void sessionCreated(HttpSessionEvent se) {
    }

    public void sessionDestroyed(HttpSessionEvent se) {
    }

    // Request Listener Methods
    @WebListener
    public void requestInitialized(ServletRequestEvent sre) {
    }

    public void requestDestroyed(ServletRequestEvent sre) {
    }

    // EJB Timer Methods
    @Schedule(second = "*/10", minute = "*", hour = "*", persistent = false)
    public void scheduledTask() {
    }

    @Timeout
    public void programmaticTimeout(Timer timer) {
    }

    @AutomaticTimeout
    public void automaticTimeout() {
    }

    // Lifecycle Callbacks
    @PostConstruct
    public void postConstruct() {
    }

    @PreDestroy
    public void preDestroy() {
    }

    // Message-Driven Bean Methods
    @MessageDriven(
            activationConfig = {
                @ActivationConfigProperty(
                        propertyName = "destinationType",
                        propertyValue = "jakarta.jms.Queue"
                )
            }
    )
    @Override
    public void onMessage(Message message) {
    }

    // Web Service Methods
    @WebMethod
    public String webServiceOperation(String input) {
        return input;
    }

    @WebServiceProvider
    public void handleRequest(String request) {
    }

    // JAX-RS Resource Methods (REST)
    @GET
    @Path("/resource")
    public String getResource() {
        return "resource";
    }

    // EJB Remote Business Interface Methods
    @Remote
    public interface RemoteBusinessInterface {

        void businessMethod();
    }

    // Security Related Methods
    @DeclareRoles({"admin", "user"})
    @RolesAllowed({"admin"})
    public void securedMethod() {
    }

    // JPA Entity Lifecycle Methods
    @PrePersist
    public void prePersist() {
    }

    @PostPersist
    public void postPersist() {
    }

    @PreUpdate
    public void preUpdate() {
    }

    @PostUpdate
    public void postUpdate() {
    }

    @PreRemove
    public void preRemove() {
    }

    @PostRemove
    public void postRemove() {
    }

    @PostLoad
    public void postLoad() {
    }
}
