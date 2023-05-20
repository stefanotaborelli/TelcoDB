package it.polimi.telcodb2.WEB.controllers.employee;

import it.polimi.telcodb2.EJB.entities.*;
import it.polimi.telcodb2.EJB.services.SalesReportService;
import it.polimi.telcodb2.EJB.utils.CustomerReport;
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
import java.util.List;
import java.util.Map;

@WebServlet(name = "SalesReportPage", value = "/sales-report")
public class SalesReportPageController extends HttpServlet {
    private final String templatePath;
    private final String pathPrefix;
    private final String pathSuffix;
    protected TemplateMode templateMode;
    protected TemplateEngine templateEngine;

    @EJB
    private SalesReportService salesReportService;

    public SalesReportPageController() {
        this.templateEngine = new TemplateEngine();
        this.templatePath = "employee-sales-report";
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
        // Check if user has logged in
        HttpSession session = request.getSession();
        if (session.getAttribute("username") == null || session.getAttribute("userid") == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid sessions");
        }

        // Get views
        List<AvgProductSoldView> avgProductSoldViewList = salesReportService.findAvgProductSold();
        BestSellerProductView bestSellerProductView = salesReportService.findBestSellerProduct();
        List<InsolventCustomersReportView> insolventCustomersReportViewList = salesReportService.findInsolventCustomersReport();
        List<PurchasesPerPackageValidityView> purchasesPerPackageValidityViewList = salesReportService.findPurchasePerPackageValidity();
        List<PurchasesPerPackageView> purchasesPerPackageViewsList = salesReportService.findPurchasesPerPackage();
        List<TotalSalesValueView> totalSalesValueViewList = salesReportService.findTotalSalesValue();

        List<CustomerReport> customerReportList = CustomerReport.fromCustomersReportView(insolventCustomersReportViewList);

        // Add views to context
        HashMap<String, Object> context = new HashMap<>();
        context.put("avgProductSoldList", avgProductSoldViewList);
        context.put("bestSellerProductView", bestSellerProductView);
        context.put("customerReportList", customerReportList);
        context.put("purchasesPerPackageValidityViewList", purchasesPerPackageValidityViewList);
        context.put("purchasesPerPackageViewsList", purchasesPerPackageViewsList);
        context.put("totalSalesValueViewList", totalSalesValueViewList);

        this.processTemplate(request, response, context);
    }

}
