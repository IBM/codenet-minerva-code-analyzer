package orderarchive.lite;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Random;

import jakarta.annotation.Resource;
import javax.naming.InitialContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import jakarta.transaction.Status;
import jakarta.transaction.UserTransaction;

import sqlj.OrderSQLJ;
import sqlj.OrderlineSQLJ;

@WebServlet(name = "OrderArchiveServlet", urlPatterns = { "/OrderArchiveServlet" })
public class OrderArchiveServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;

    @Resource
    DataSource ds;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            webAutoGenInput(request, response);
        } catch (Exception e) {
            e.printStackTrace(response.getWriter());
            e.printStackTrace(System.out);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            webAutoGenInput(request, response);
        } catch (Exception e) {
            e.printStackTrace(response.getWriter());
            e.printStackTrace(System.out);
        }
    }

    /**
     * webAutoGenInput() is called by doPost() and contains
     * the business logic for the AutoGeneration button
     * 
     * webAutoGenInput()
     * (Business Logic) // first method of the class not counting doPost() and doGet()
     */
    public void webAutoGenInput(HttpServletRequest request, HttpServletResponse response) throws Exception {
        PrintWriter out = response.getWriter();

        print(out, ">>> ENTER WebOrderArchiveLite_SQLJ OrderArchiveServlet.");
        UserTransaction tran = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");

        try {
            tran.begin();
            Random rand = new Random();
            short randomWarehouseId = (short) (rand.nextInt(25) + 1);//MaxValues.warehouseId(database));
            short randomDistrictId = (short) (rand.nextInt(10) + 1);//MaxValues.districtId(database));
            print(out, "The randomly-generated Warehouse ID = " + randomWarehouseId);
            print(out, "The randomly-generated District ID = " + randomDistrictId);

            // JPA equivalent of what's about to happen
            // queryMinOrderId = em.createNamedQuery("findMinOrderId"); 
            // queryMinOrderId.setParameter("warehouseId", randomWarehouseId);
            // queryMinOrderId.setParameter("districtId", randomDistrictId);
            OrderSQLJ order = OrderSQLJ.newInstance(ds);
            int orderId = order.findMinOrderId(randomWarehouseId, randomDistrictId);
            //orderId = (int) queryMinOrderId.getSingleResult();
            print(out, "OrderId = " + orderId);
            //oldestOrder = em.find(OrderSQLJ.class, oldestOrderKey);
            OrderSQLJ oldestOrder = order.find(randomDistrictId, orderId, randomWarehouseId);

            if (oldestOrder == null) {
                printNoOrdersFound(response, randomWarehouseId, randomDistrictId);
                return;
            }

            print(out, "The order to be removed from the archive was found successfully. Here is its information:");
            print(out, "Warehouse ID: " + oldestOrder.WarehouseId);
            print(out, "District ID: " + oldestOrder.DistrictId);
            print(out, "OrderID: " + oldestOrder.OrderId);

            int numberOrderLines = oldestOrder.OrderLineCount.intValue();

            orderId = oldestOrder.OrderId;
            short districtId = oldestOrder.DistrictId;
            short warehouseId = oldestOrder.WarehouseId;

            print(out, "* There are " + numberOrderLines + " order lines associated with this order to be deleted.");
            print(out, "The associated Order Lines have the following information...");

            // JPA equivalent of what's about to happen
            // orderlineQuery = em.createNamedQuery("findOrderLinesWithSpecificOrderId"); 
            // orderlineQuery.setParameter("orderId", orderId);
            // orderlineQuery.setParameter("districtId", districtId);
            // orderlineQuery.setParameter("warehouseId", warehouseId);
            OrderlineSQLJ orderline = OrderlineSQLJ.newInstance(ds);
            List<OrderlineSQLJ> resultSet = orderline.findOrderLinesWithSpecificOrderId(orderId, districtId, warehouseId); // orderlineQuery.getResultList();

            // Delete all associated order lines
            for (OrderlineSQLJ ol : resultSet) {
                print(out, "WarehouseID = " + warehouseId);
                print(out, "DistrictID = " + districtId);
                print(out, "OrderID = " + orderId);
                print(out, "Number = " + ol.getOlNumber());
                orderline.remove(ol); // em.remove(resultSet.get(i));
            }

            oldestOrder.remove(oldestOrder); // em.remove(oldestOrder);
            print(out, "*The above order has been removed from the order archive.*");

            tran.commit();

            // Dispatch AutoGeneration Successful Results 
            webAutoGenResults(oldestOrder, request, response);
        } finally {
            if (tran.getStatus() != Status.STATUS_NO_TRANSACTION)
                tran.rollback();
            print(out, "<<< EXIT  WebOrderArchiveLite_SQLJ OrderArchiveServlet.");
        }
    }

    private void printNoOrdersFound(HttpServletResponse response, short randomWarehouseId, short randomDistrictId) throws Exception
    {
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        out.println("<html>");
        out.println("<center><H1>Order Archive Lite Results Form</H1></center>");
        out.println("<body bgcolor=\"#f8f7cd\">");
        out.println("<h3 center>Status: FAILED</h3> ");
        out.println("<h3 center>Message:<h3/><h4>An empty warehouse/district combination was randomly-generated and currently contains no orders.  Below is the information for the empty warehouse/district combination:</h4> ");

        out.println("<TABLE border=\"8\" bgcolor=\"#cccccc\" bordercolor=\"#FFCC99\" >");
        out.println("<tr><th></th>");
        out.println("<tr><th>Warehouse ID:</th>");

        out.println("<td>" + randomWarehouseId + "</td></tr>");

        out.println("<tr><th>District ID:</th>");
        out.println("<td>" + randomDistrictId + "</td>");

        out.println("<tr><th>Order ID:</th>");
        out.println("<td>" + "-" + "</td>");

        out.println("<br></br>");
        out.println("<br></br>");

        out.println("</table>");

        out.println("<br></br>");
        out.println("<br></br>");
        out.println("<h3 center>Exception:</h3>");
        out.println("<h3 center>Stack:</h3>");
        out.println("</body>");
        out.println("</html>");
        out.flush();
    }

    public void webAutoGenResults(OrderSQLJ theOb, HttpServletRequest request, HttpServletResponse response)
                    throws Exception
    {
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        out.println("<html>");
        out.println("<center><H1>Order Archive Lite Results Form</H1></center>");
        out.println("<body bgcolor=\"#f8f7cd\">");
        out.println("<h3 center>Status: SUCCESSFUL</h3> ");
        out.println("<h3 center>Message:</h3>");
        out.println("<h2 center>The following order has been removed:</h2> ");
        out.println("</head> ");
        out.println("<TABLE border=\"8\" bgcolor=\"#cccccc\" bordercolor=\"#FFCC99\" >");

        out.println("<tr><th></th>");
        out.println("<tr><th>Warehouse ID:</th>");
        out.println("<td>" + theOb.WarehouseId + "</td></tr>");
        out.println("<tr><th>District ID:</th>");
        out.println("<td>" + theOb.DistrictId + "</td>");
        out.println("<tr><th>Order ID:</th>");
        out.println("<td>" + theOb.OrderId + "</td>");
        out.println("</table>");
        out.println("<h3 center>Exception:</h3>");
        out.println("<h3 center>Stack:</h3>");
        out.println("</body>");
        out.println("</html>");
        out.flush();
    }

    private void print(PrintWriter out, String msg) {
        out.println(msg + "<br>");
        System.out.println(msg);
    }
}
