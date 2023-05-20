package it.polimi.telcodb2.WEB.controllers.employee;

import it.polimi.telcodb2.EJB.entities.Service;
import it.polimi.telcodb2.EJB.services.ServiceService;
import it.polimi.telcodb2.EJB.utils.Pair;
import it.polimi.telcodb2.EJB.utils.ParseUtils;
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
import java.util.List;

@WebServlet(name = "CreateService", value = "/create-service")
public class CreateServiceController extends HttpServlet {

    @EJB
    private ServiceService serviceService;

    public CreateServiceController() {super();}

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

        // Parse data
        Integer type = ParseUtils.toInteger(request.getParameter("type"), null);
        Pair<Integer, Float> minutesData = new Pair<Integer, Float>(
                ParseUtils.toInteger(request.getParameter("minutes"), 0),
                ParseUtils.toFloat(request.getParameter("extra-minutes"), 0)
        );
        Pair<Integer, Float> smsData = new Pair<Integer, Float>(
                ParseUtils.toInteger(request.getParameter("sms"), 0),
                ParseUtils.toFloat(request.getParameter("extra-sms"), 0)
        );
        Pair<Integer, Float> gigaData = new Pair<Integer, Float>(
                ParseUtils.toInteger(request.getParameter("giga"), 0),
                ParseUtils.toFloat(request.getParameter("extra-giga"), 0)
        );
        // Check if every necessary data has been passed
        if(type == null) {
            response.sendRedirect(getServletContext().getContextPath() + "/employee-home?error=Missing or illegal field");
            return;
        }
        switch(type) {
            case 0:
                if (minutesData.getX() > 0 || minutesData.getY() > 0 || smsData.getX() > 0 || smsData.getY() > 0 || gigaData.getX() > 0 || gigaData.getY() > 0) {
                    response.sendRedirect(getServletContext().getContextPath() + "/employee-home?error=Missing or illegal field");
                    return;
                }
                break;
            case 1:
            case 3:
                if (minutesData.getX() > 0 || minutesData.getY() > 0 || smsData.getX() > 0 || smsData.getY() > 0 || gigaData.getX() <= 0 || gigaData.getY() <= 0) {
                    response.sendRedirect(getServletContext().getContextPath() + "/employee-home?error=Missing or illegal field");
                    return;
                }
                break;
            case 2:
                if (minutesData.getX() <= 0 || minutesData.getY() <= 0 || smsData.getX() <= 0 || smsData.getY() <= 0 || gigaData.getX() > 0 || gigaData.getY() > 0) {
                    response.sendRedirect(getServletContext().getContextPath() + "/employee-home?error=Missing or illegal field");
                    return;
                }
                break;
        }

        List<Service> serviceList = serviceService.findEquivalents(
                type,
                minutesData.getX(),
                minutesData.getY(),
                smsData.getX(),
                smsData.getY(),
                gigaData.getX(),
                gigaData.getY()
        );
        if (serviceList.isEmpty()) {
            Service service = serviceService.createService(
                    type,
                    minutesData.getX(),
                    minutesData.getY(),
                    smsData.getX(),
                    smsData.getY(),
                    gigaData.getX(),
                    gigaData.getY()
            );
            if (service == null) {
                response.sendRedirect(getServletContext().getContextPath() + "/employee-home?error=An unexpected error occurred! Try again.");
            }
            else {
                response.sendRedirect(getServletContext().getContextPath() + "/employee-home?success=True");
            }
        } else {
            response.sendRedirect(getServletContext().getContextPath() + "/employee-home?error=Already existing service");
        }

    }
}
