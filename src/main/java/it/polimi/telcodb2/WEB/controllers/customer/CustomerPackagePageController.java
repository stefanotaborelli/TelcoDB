package it.polimi.telcodb2.WEB.controllers.customer;

import it.polimi.telcodb2.EJB.entities.Package;
import it.polimi.telcodb2.EJB.services.OrderService;
import it.polimi.telcodb2.EJB.services.PackageService;
import it.polimi.telcodb2.EJB.utils.ParseUtils;
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
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "CustomerPackagePage", value = "/package-page")
public class CustomerPackagePageController extends HttpServlet {

    @EJB
    private PackageService packageService;

    private final String templatePath;
    private final String pathPrefix;
    private final String pathSuffix;
    protected TemplateMode templateMode;
    protected TemplateEngine templateEngine;


    public CustomerPackagePageController() {
        this.templateEngine = new TemplateEngine();
        this.templatePath = "customer-package";
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
        // Clear order from session if user is coming back from confirmation page
        HttpSession session = request.getSession();
        session.setAttribute("orderSummary", null);

        // Parse package id
        Integer packageId = ParseUtils.toInteger(request.getParameter("id"), null);
        if (packageId == null) {
            String path = getServletContext().getContextPath() + "/customer-home?error=Missing package id";
            response.sendRedirect(path);
            return;
        }

        // Get list of packages and add it to the context
        HashMap<String, Object> context = new HashMap<>();
        Package currentPackage = packageService.find(packageId);
        context.put("package", currentPackage);

        this.processTemplate(request, response, context);
    }
}
