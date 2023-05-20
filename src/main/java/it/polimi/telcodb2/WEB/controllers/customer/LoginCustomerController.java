package it.polimi.telcodb2.WEB.controllers.customer;

import it.polimi.telcodb2.EJB.entities.Customer;
import it.polimi.telcodb2.EJB.exceptions.CredentialsException;
import it.polimi.telcodb2.EJB.services.CustomerService;
import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.ejb.EJB;
import javax.persistence.NonUniqueResultException;
import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;

@WebServlet(name = "LoginCustomer", value = "/login-customer")
public class LoginCustomerController extends HttpServlet {

    private TemplateEngine templateEngine;

    @EJB(name = "it.polimi.telcodb2.EJB.services/CustomerService")
    private CustomerService customerService;

    public LoginCustomerController() {
        super();
    }


    public void init() {
        ServletContext servletContext = getServletContext();
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);
        templateResolver.setSuffix(".html");
    }


    // GET requests are not allowed
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "GET is not allowed");
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Read parameters from request
        String username = StringEscapeUtils.escapeJava(request.getParameter("username"));
        String password = StringEscapeUtils.escapeJava(request.getParameter("password"));
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            String path = getServletContext().getContextPath() + "/customer-landing?error=Missing or empty credentials.";
            response.sendRedirect(path);
            return;
        }

        Customer customer;
        try {
            customer = customerService.checkCredentials(username, password);
        } catch (NonUniqueResultException e) {
            String path = getServletContext().getContextPath() + "/customer-landing?error=Could not check credentials.";
            response.sendRedirect(path);
            return;
        }

        // If the return customer is null, then there was an error while accessing the database
        String path;
        if (customer == null) {
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
            path = getServletContext().getContextPath() + "/customer-landing?error=Wrong username or password.";
            response.sendRedirect(path);
            return;
        }

        // If everything went good then add the user to the session and proceed
        HttpSession session = request.getSession();
        session.setAttribute("username", customer.getUsername());
        session.setAttribute("userid", customer.getIdCustomer());
        // If there is no order in session, then the user is requesting directly the landing page
        if (session.getAttribute("orderSummary") == null) {
            path = getServletContext().getContextPath() + "/customer-home";
        }
        // If there is an order in session, then the user is requesting the landing page from the confirmation form
        else {
            path = getServletContext().getContextPath() + "/confirmation-page";
        }
        response.sendRedirect(path);
    }


    public void destroy() {}
}