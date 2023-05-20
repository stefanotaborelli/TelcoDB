package it.polimi.telcodb2.WEB.controllers.customer;

import it.polimi.telcodb2.EJB.entities.Customer;
import it.polimi.telcodb2.EJB.services.CustomerService;
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

@WebServlet(name = "SignupCustomer", value = "/signup-customer")
public class SignupCustomerController extends HttpServlet {

    @EJB
    private CustomerService customerService;


    public SignupCustomerController() {
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

        String username = StringEscapeUtils.escapeJava(request.getParameter("username"));
        String password = StringEscapeUtils.escapeJava(request.getParameter("password"));
        String email = StringEscapeUtils.escapeJava(request.getParameter("email"));

        if (username == null || username.isEmpty() || password == null || password.isEmpty() || email == null || email.isEmpty()) {
            path = getServletContext().getContextPath() + "/customer-landing?error=Missing or empty field.";
            response.sendRedirect(path);
            return;
        }

        if (!customerService.findByEmail(email).isEmpty()) {
            path = getServletContext().getContextPath() + "/customer-landing?error=There is already another user for this email.";
            response.sendRedirect(path);
            return;
        }
        if (!customerService.findByUsername(username).isEmpty()) {
            path = getServletContext().getContextPath() + "/customer-landing?error=There is already another user with this username.";
            response.sendRedirect(path);
            return;
        }

        // Create customer
        Customer customer = customerService.createCustomer(username, password, email);
        // If the returned object is null, then something went wrong while accessing the database
        if (customer == null) {
            path = getServletContext().getContextPath() + "/customer-landing?error=Ops! Something went wrong.";
            response.sendRedirect(path);
            return;
        }

        // If everything went good, then proceed
        HttpSession session = request.getSession();
        // If there is no order in session, then the user is requesting directly the landing page
        if (session.getAttribute("orderSummary") == null) {
            path = getServletContext().getContextPath() + "/customer-landing?success=True";
        }
        // If there is an order in session, then the user is requesting the landing page from the confirmation form
        else {
            session.setAttribute("userid", customer.getIdCustomer());
            session.setAttribute("username", customer.getUsername());
            path = getServletContext().getContextPath() + "/confirmation-page";
        }
        response.sendRedirect(path);

    }
}
