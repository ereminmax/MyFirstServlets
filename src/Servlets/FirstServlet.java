package Servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by jacksolovey on 11.11.2015.
 */
//@WebServlet(name = "FirstServlet")
public class FirstServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //w
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //get parameter from URL
        String value = request.getParameter("value");
        if (value == null) value = "World";

        //make page type. we ignore jsp pages here
        response.setContentType("text/html");
        //make output stream
        PrintWriter out = response.getWriter();
        out.println("Hello, " + value + "!");
    }
}
