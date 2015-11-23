package Servlets;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

/**
 * Created by Max Eremin on 22.11.2015.
 */
@WebServlet(name = "Subscription", urlPatterns = {"/subscription"},
    initParams = {
            @WebInitParam(name = "driver", value = "com.mysql.jdbc.Driver"),
            @WebInitParam(name = "url", value = "jdbc:mysql://localhost:3306/subscriberdb"),
            @WebInitParam(name = "username", value = "root"),
            @WebInitParam(name = "password", value = "root")
    }
)
public class Subscription extends HttpServlet {
    Connection connection;
    ServletConfig config;
    PreparedStatement statement;
    PrintWriter out;

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
            out.close();
        } catch (SQLException e) {
            // close it anyway
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Read parameters from request
        request.setCharacterEncoding("UTF-8");
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String course = request.getParameter("course");

        // Set html page preferences
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        out = response.getWriter();

        // Check if all fields are filled
        if (name == null || email == null || course == null) {
            out.println(
                "<head>\n" +
                "<title>Ошибка</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1>Проверьте, все ли поля заполнены!</h1>\n" +
                "</body>"
            );
        }

        // Add client's data into DB
        try {
            statement = connection.prepareStatement("INSERT INTO subscribers(name, email, course) values (?, ?, ?)");
            statement.setString(1, name);
            statement.setString(2, email);
            statement.setString(3, course);
        } catch (SQLException e) {
            out.println("SQL error in statement found: " + e.getMessage());
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (Exception e) {
                out.println("Statement error found: " + e.getMessage());
            }
        }

        // Create html page
        out.println(
            "<head>\n" +
            "<title>Успешно</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "<h1>Добро пожаловать, " + name +
            "</h1>\nВы успешно подписались на: \"" + course +
            "\"\n</body>"
        );
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
