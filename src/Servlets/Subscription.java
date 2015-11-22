package Servlets;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Max Eremin on 22.11.2015.
 */
@WebServlet(name = "Subscription", urlPatterns = {"/subscription"},
    initParams = {
            @WebInitParam(name = "driver", value = "com.mysql.jdbc.Driver"),
            @WebInitParam(name = "url", value = "jdbc:mysql://localhost:3306/subscribers"),
            @WebInitParam(name = "username", value = "root"),
            @WebInitParam(name = "password", value = "root")
    }
)
public class Subscription extends HttpServlet {
    Connection connection;
    ServletConfig config;

    public void init() throws ServletException{
        config = getServletConfig();
        String driver = config.getInitParameter("driver");
        String url = config.getInitParameter("url");
        String username = config.getInitParameter("username");
        String password = config.getInitParameter("password");

        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            log("Initialization error found: ", e);
            throw new ServletException("Initialization error found: " + e.getMessage(), e);
        }
    }

    public void destroy() {
        try {
            connection.close();
        } catch (SQLException e) {
            // close it anyway
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
