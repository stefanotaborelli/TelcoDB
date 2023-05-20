package it.polimi.telcodb2.WEB.controllers.employee;

import it.polimi.telcodb2.EJB.entities.Employee;
import it.polimi.telcodb2.EJB.exceptions.CredentialsException;
import it.polimi.telcodb2.EJB.services.EmployeeService;
import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.ejb.EJB;
import javax.persistence.NonUniqueResultException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "LoginEmployee", value = "/login-employee")
public class LoginEmployeeController extends HttpServlet {

    private TemplateEngine templateEngine;

    private static final long serialVersionUID = 1L;

    @EJB(name = "it.polimi.telcodb2.EJB.services/EmployeeService")
    private EmployeeService employeeService;

    public LoginEmployeeController() {
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


    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Obtain and escape params
        String username = StringEscapeUtils.escapeJava(request.getParameter("username"));
        String password = StringEscapeUtils.escapeJava(request.getParameter("password"));
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            String path = getServletContext().getContextPath() + "/employee-login?error=Missing or empty credentials.";
            response.sendRedirect(path);
            return;
        }

        Employee employee;
        try {
            employee = employeeService.checkCredentials(username, password);
        } catch (NonUniqueResultException e) {
            String path = getServletContext().getContextPath() + "/customer-login?error=Could not check credentials.";
            response.sendRedirect(path);
            return;
        }

        // If the user exists, add info to the session and go to home page, otherwise
        // show login page with error message
        String path;
        if (employee == null) {
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
            path = getServletContext().getContextPath() + "/employee-login?error=Wrong username or password.";
            response.sendRedirect(path);
        } else{
            HttpSession session = request.getSession();
            session.setAttribute("username", employee.getUsername());
            session.setAttribute("userid", employee.getIdEmployee());
            path = getServletContext().getContextPath() + "/employee-home";
            response.sendRedirect(path);
        }
    }

    public void destroy() {
    }
}

