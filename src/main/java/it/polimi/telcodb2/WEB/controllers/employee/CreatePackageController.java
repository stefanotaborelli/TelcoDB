package it.polimi.telcodb2.WEB.controllers.employee;

import it.polimi.telcodb2.EJB.entities.Package;
import it.polimi.telcodb2.EJB.services.*;
import it.polimi.telcodb2.EJB.utils.Pair;
import it.polimi.telcodb2.EJB.utils.ParseUtils;
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
import java.util.*;
import java.util.stream.Collectors;

@WebServlet(name = "CreatePackage", value="/create-package")
public class CreatePackageController extends HttpServlet {

    @EJB
    private PackageService packageService;

    public CreatePackageController() {
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

        // Parse name
        String name = StringEscapeUtils.escapeJava(request.getParameter("name"));
        // Parse validity options
        List<Pair<Integer, Float>> validityOptions = Pair.createPairList(
                ParseUtils.toIntegerList(
                        ParseUtils.toStringListSafe(request.getParameterValues("duration"))
                                .stream()
                                .filter(elem -> !elem.isEmpty())
                                .collect(Collectors.toList()),
                        true),
                ParseUtils.toFloatList(
                        ParseUtils.toStringListSafe(request.getParameterValues("fee"))
                                .stream()
                                .filter(elem -> !elem.isEmpty())
                                .collect(Collectors.toList()),
                        true)
        );
        // Parse services
        List<Integer> serviceIds = ParseUtils.toIntegerList(
                ParseUtils.toStringListSafe(request.getParameterValues("services")), false);
        List<Integer> serviceIdsMultiplied = new ArrayList<>();
        serviceIds.forEach(elem -> {
            int multiplier = ParseUtils.toInteger(request.getParameter("mult-" + elem), -1);
            if (multiplier > 0) {
                for (int i=0; i<multiplier; i++) {
                    serviceIdsMultiplied.add(elem);
                }
            } else {
                try {
                    response.sendRedirect(getServletContext().getContextPath() + "/employee-home?error=Ops! Something went wrong");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        // Parse optional products
        List<Integer> productIds = ParseUtils.toIntegerList(
                ParseUtils.toStringListSafe(request.getParameterValues("products")), false);

        //  Check empty fields
        if (name.isEmpty()) {
            response.sendRedirect(getServletContext().getContextPath() + "/employee-home?error=No name was submitted");
            return;
        }
        if (validityOptions == null) {
            response.sendRedirect(getServletContext().getContextPath() + "/employee-home?error=No validity was submitted");
            return;
        }
        if (serviceIds.isEmpty()) {
            response.sendRedirect(getServletContext().getContextPath() + "/employee-home?error=No service was submitted");
            return;
        }

        // Check if there is already a package with the same name
        if (!packageService.findByName(name).isEmpty()) {
            response.sendRedirect(getServletContext().getContextPath() + "/employee-home?error=Already existing package");
            return;
        }

        // Check if there are validity options with the same duration
        if (validityOptions.stream().map(Pair::getX).distinct().count() != validityOptions.size()) {
            response.sendRedirect(getServletContext().getContextPath() + "/employee-home?error=Repeated duration in validity options");
            return;
        }

        // Create package
        Package newPackage = packageService.createPackage(name, validityOptions, serviceIdsMultiplied, productIds);
        if (newPackage == null) {
            response.sendRedirect(getServletContext().getContextPath() + "/employee-home?error=Ops! Something went wrong");
            return;
        }
        response.sendRedirect(getServletContext().getContextPath() + "/employee-home?success=true");
    }
}
