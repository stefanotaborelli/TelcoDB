package it.polimi.telcodb2.WEB.controllers;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public abstract class TemplatingServlet extends HttpServlet {

    private final String templatePath;
    private final String pathPrefix;
    private final String pathSuffix;
    protected TemplateMode templateMode;
    protected TemplateEngine templateEngine;

    // If you have sub-path
    public TemplatingServlet(String templatePath, TemplateMode templateMode, String pathPrefix, String pathSuffix) {
        this.templateEngine = new TemplateEngine();
        this.templatePath = templatePath;
        this.templateMode = templateMode;
        this.pathPrefix = pathPrefix;
        this.pathSuffix = pathSuffix;
    }
    // Else
    public TemplatingServlet(String templatePath, TemplateMode templateMode, String pathSuffix) {
        this.templateEngine = new TemplateEngine();
        this.templatePath = templatePath;
        this.templateMode = templateMode;
        this.pathPrefix = "";
        this.pathSuffix = pathSuffix;
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
}