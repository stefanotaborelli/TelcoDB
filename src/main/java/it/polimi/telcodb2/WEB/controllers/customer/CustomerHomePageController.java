package it.polimi.telcodb2.WEB.controllers.customer;

import it.polimi.telcodb2.EJB.entities.Package;
import it.polimi.telcodb2.EJB.services.CustomerService;
import it.polimi.telcodb2.EJB.services.OrderService;
import it.polimi.telcodb2.EJB.services.PackageService;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

@WebServlet(name = "CustomerHomePageController", value = "/customer-home")
public class CustomerHomePageController extends HttpServlet {

    @EJB
    private PackageService packageService;
    @EJB
    private CustomerService customerService;

    private final String templatePath;
    private final String pathPrefix;
    private final String pathSuffix;
    protected TemplateMode templateMode;
    protected TemplateEngine templateEngine;


    public CustomerHomePageController() {
        this.templateEngine = new TemplateEngine();
        this.templatePath = "customer-home";
        this.templateMode = TemplateMode.HTML;
        this.pathPrefix = "";
        this.pathSuffix = ".html";
    }

    @Override
    public void init() {
        ServletContext servletContext = getServletContext();
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);

        templateResolver.setTemplateMode(templateMode);
        templateResolver.setSuffix(pathSuffix);
        templateEngine.setTemplateResolver(templateResolver);
        templateResolver.setPrefix(pathPrefix);
    }

    protected void processTemplate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        processTemplate(request, response, null);
    }

    protected void processTemplate(HttpServletRequest request, HttpServletResponse response,
                                   Map<String, Object> variables) throws IOException {
        ServletContext servletCtx = getServletContext();

        final WebContext webCtx = new WebContext(request, response, servletCtx, request.getLocale());

        if (variables != null) {
            webCtx.setVariables(variables);
        }

        templateEngine.process(templatePath, webCtx, response.getWriter());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        HashMap<String, Object> context = new HashMap<>();

        // If user is logged in, then look for his/her pending orders to show
        if (session.getAttribute("userid") != null) {
            context.put(
                    "pendingOrders",
                    customerService.findPendingOrders((Integer) session.getAttribute("userid"))
            );
        }

        // Get list of packages and add it to the context
        List<Package> packageList = packageService.findAll();
        context.put("packages", packageList);

        this.processTemplate(request, response, context);
    }

}
