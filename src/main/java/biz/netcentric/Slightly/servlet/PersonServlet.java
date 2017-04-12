package biz.netcentric.Slightly.servlet;

import biz.netcentric.Slightly.renderer.PersonRenderer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

public class PersonServlet extends HttpServlet {

    private PersonRenderer renderer;

    public void initRenderer (){
        renderer = new PersonRenderer();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        initRenderer();
        this.renderer.getContext().addVariableToContext("request", request);
        this.renderer.getContext().addVariableToContext("response", response);

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Expires", "0");

        final String realPath = this.getServletContext().getRealPath("/");
        File htmlBase = new File(realPath + "/pages" + "/index.html");
        renderer.prepareParser(htmlBase);
        renderer.parse();
        response.getWriter().println(this.renderer.getRenderedFile());
    }
}