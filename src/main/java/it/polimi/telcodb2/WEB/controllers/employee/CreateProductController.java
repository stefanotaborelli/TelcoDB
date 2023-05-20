package it.polimi.telcodb2.WEB.controllers.employee;

import it.polimi.telcodb2.EJB.entities.Product;
import it.polimi.telcodb2.EJB.services.ProductService;
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

@WebServlet(name = "CreateProduct", value = "/create-product")
public class CreateProductController extends HttpServlet {

    @EJB
    private ProductService productService;


    public CreateProductController() {
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
        // Check if session is valid
        HttpSession session = request.getSession();
        if (session.getAttribute("username") == null || session.getAttribute("userid") == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid sessions");
        }

        // Parse parameters
        String name = StringEscapeUtils.escapeJava(request.getParameter("name"));
        float fee = Float.parseFloat(request.getParameter("fee"));

        // Check if parameters are valid
        if (name == null || name.isEmpty() || fee <= 0) {
            response.sendRedirect(getServletContext().getContextPath() + "/employee-home?error=Empty field");
            return;
        }
        if (!productService.findByName(name).isEmpty()) {
            response.sendRedirect(getServletContext().getContextPath() + "/employee-home?error=Already existing product");
            return;
        }

        // Create product
        Product product = productService.createProduct(name, fee);
        if (product == null) {
            response.sendRedirect(getServletContext().getContextPath() + "/employee-home?error=An unexpected error occurred! Try again.");
            return;
        }
        response.sendRedirect(getServletContext().getContextPath() + "/employee-home?success=True");

    }
}
