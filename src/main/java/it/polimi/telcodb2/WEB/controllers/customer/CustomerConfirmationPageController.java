package it.polimi.telcodb2.WEB.controllers.customer;

import it.polimi.telcodb2.EJB.entities.Order;
import it.polimi.telcodb2.EJB.services.OrderService;
import it.polimi.telcodb2.EJB.utils.OrderSummary;
import it.polimi.telcodb2.EJB.utils.ParseUtils;
import it.polimi.telcodb2.EJB.utils.PaymentService;
import org.apache.commons.lang.StringEscapeUtils;
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
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@WebServlet(name = "CustomerConfirmationPage", value = "/confirmation-page")
public class CustomerConfirmationPageController extends HttpServlet {

    @EJB
    private OrderService orderService;

    private final String templatePath;
    private final String pathPrefix;
    private final String pathSuffix;
    protected TemplateMode templateMode;
    protected TemplateEngine templateEngine;


    public CustomerConfirmationPageController() {
        this.templateEngine = new TemplateEngine();
        this.templatePath = "customer-confirmation";
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

        // If it is the first time loading the page, then create an order object and add it to the session
        if (session.getAttribute("userid") == null || session.getAttribute("orderSummary") == null) {
            // Parse package parameters
            Integer packageId = ParseUtils.toInteger(request.getParameter("id"), -1);
            Integer validityId = ParseUtils.toInteger(request.getParameter("validity"), -1);
            List<Integer> productIds = ParseUtils.toIntegerList(
                    ParseUtils.toStringListSafe(request.getParameterValues("products")), true);
            LocalDate startDate = ParseUtils.toDate(request.getParameter("start-date"));

            // Check
            String path;
            if (packageId == -1) {
                path = getServletContext().getContextPath() + "/customer-home?error=Missing package id";
                response.sendRedirect(path);
                return;
            }
            if (validityId == -1) {
                path = getServletContext().getContextPath() + "/customer-home?error=Missing validity id";
                response.sendRedirect(path);
                return;
            }
            if (productIds.stream().anyMatch(Objects::isNull)) {
                path = getServletContext().getContextPath() + "/customer-home?error=Invalid product id";
                response.sendRedirect(path);
                return;
            }

            // Get order summery
            OrderSummary orderSummary = orderService.getSummary(validityId, productIds, packageId, startDate);
            // Add summary to session
            session.setAttribute("orderSummary", orderSummary);
        }

        this.processTemplate(request, response, new HashMap<>());
    }
}
