package it.polimi.telcodb2.WEB.controllers.customer;

import it.polimi.telcodb2.EJB.entities.Order;
import it.polimi.telcodb2.EJB.entities.Product;
import it.polimi.telcodb2.EJB.entities.Schedule;
import it.polimi.telcodb2.EJB.services.CustomerService;
import it.polimi.telcodb2.EJB.services.OrderService;
import it.polimi.telcodb2.EJB.utils.OrderSummary;
import it.polimi.telcodb2.EJB.utils.PaymentService;
import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "BuyController", value = "/buy")
public class BuyController extends HttpServlet {

    @EJB
    private OrderService orderService;
    @EJB
    private CustomerService customerService;


    public BuyController() {
        super();
    }


    public void init() {
        ServletContext servletContext = getServletContext();
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setSuffix(".html");
    }


    // GET requests are not allowed
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "GET is not allowed");
    }


    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path;
        HttpSession session = request.getSession();

        // If customer id is in session, then parse it, otherwise redirect with error
        Integer customerId;
        if (session.getAttribute("userid") == null) {
            path = getServletContext().getContextPath() + "/customer-home?error=Missing customer id";
            response.sendRedirect(path);
            return;
        } else {
            customerId = (Integer) session.getAttribute("userid");
        }

        // Parse parameters from session
        Object orderSummaryObject = session.getAttribute("orderSummary");
        // Check if order is null, else cast it as an order object
        if (orderSummaryObject == null) {
            path = getServletContext().getContextPath() + "/customer-home?error=Order was empty";
            response.sendRedirect(path);
            return;
        }
        OrderSummary orderSummary = (OrderSummary) orderSummaryObject;

        int paymentStatus = orderService.payOrder(orderSummary, customerId);
        switch (paymentStatus) {
            case -1:
                path = getServletContext().getContextPath() + "/customer-home?error=Unexpected payment status";
                break;
            case 0:
                path = getServletContext().getContextPath() + "/customer-home?success=true";
                break;
            case 1:
                path = getServletContext().getContextPath() + "/customer-home?warning=Failed payment!";
                break;
            case 2:
                path = getServletContext().getContextPath() + "/customer-home?error=Ops! Unexpected order id";
                break;
            case 3:
                path = getServletContext().getContextPath() + "/customer-home?error=Ops! Payment was successful but an error occurred while retrieving related entities";
                break;
            case 4:
                path = getServletContext().getContextPath() + "/customer-home?error=Ops! Payment was successful but an error occurred while retrieving the order";
                break;
            case 5:
                path = getServletContext().getContextPath() + "/customer-home?error=Ops! Payment failed but an error occurred while retrieving related entities";
                break;
            default:
                path = getServletContext().getContextPath() + "/customer-home?error=Ops! Something went wrong";
                break;
        }
        session.setAttribute("orderSummary", null);
        response.sendRedirect(path);
    }
}
