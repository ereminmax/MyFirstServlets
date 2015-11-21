package Servlets;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Max Eremin on 21.11.2015.
 */
@WebServlet(name = "ClientHostInfo", urlPatterns = {"/chinfo"})
public class ClientHostInfo extends HttpServlet {
    private ServletConfig config = getServletConfig();

    @Override
    public void init(ServletConfig config) throws ServletException{
        try {
            super.init(config);
            this.config = config;
        } catch (Exception e) {
            throw new ServletException("Servlet initialization error found: " + e.getMessage());
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.print(
            "<head>\n" +
            "<meta charset=\"UTF-8\">\n" +
            "<title></title>\n" +
            "</head>\n" +
            "<body>\n"
        );

        // Client info here
        String remoteAddr = request.getRemoteAddr();
        String remoteHost = request.getRemoteHost();
        int remotePort = request.getRemotePort();

        // Host info here
        String localAddr = request.getLocalAddr();
        String localName = request.getLocalName();
        int localPort = request.getLocalPort();

        // Server info here
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();

        out.println(
            "<p>" +
                "The client with ip address: " + remoteAddr +
                " through DNS: " + remoteHost +
                " using " + remotePort +
                " port has just called our server which name is " + serverName +
                " with " + serverPort +
                " port following information: " +
            "</p>" +
            "<table><tr>" +
                "<th>Local Address</th>" +
                "<th>LocalName</th>" +
                "<th>Local Port</th>" +
            "</tr>" +
            "<tr>" +
                "<td>" + localAddr + "</td>" +
                "<td>" + localName + "</td>" +
                "<td>" + localPort + "</td>" +
            "</tr></table>"
        );

        out.println("</body>");
    }
}
