package it.polimi.telcodb2.WEB.controllers.customer;

import it.polimi.telcodb2.EJB.entities.Order;
import it.polimi.telcodb2.EJB.services.OrderService;
import it.polimi.telcodb2.EJB.utils.OrderSummary;
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
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "OrderRetrieval", value = "/order-retrieval")
public class OrderRetrievalController extends HttpServlet {

    @EJB
    private OrderService orderService;

    private final String templatePath;
    private final String pathPrefix;
    private final String pathSuffix;
    protected TemplateMode templateMode;
    protected TemplateEngine templateEngine;

    public OrderRetrievalController() {
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
        // Parse order id
        Integer orderId = ParseUtils.toInteger(request.getParameter("id"), -1);
        if (orderId == -1) { // check validity
            String path = getServletContext().getContextPath() + "/customer-home?error=Missing order ID";
            response.sendRedirect(path);
            return;
        }

        // Find order by id
        OrderSummary orderSummary = orderService.findSummary(orderId);
        request.getSession().setAttribute("orderSummary", orderSummary);

        // Render confirmation page
        this.processTemplate(request, response, new HashMap<>());
    }
}
