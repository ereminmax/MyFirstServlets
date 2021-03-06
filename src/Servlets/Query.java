package Servlets;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

/**
 * Created by Max Eremin on 16.11.2015.
 */
@WebServlet(
        name = "Query",
        urlPatterns = {"/Query"},
        initParams = {
                @WebInitParam(name = "driverClassName", value = "com.mysql.jdbc.Driver"),
                @WebInitParam(name = "url", value = "jdbc:mysql://localhost:3306/myfirstdbschema"),
                @WebInitParam(name = "username", value = "root"),
                @WebInitParam(name = "password", value = "root")
        }
)
public class Query extends HttpServlet {
    Connection db;

    public void init() throws ServletException{
        ServletConfig config = getServletConfig();
        String driverClassName = config.getInitParameter("driverClassName");
        String url = config.getInitParameter("url");
        String username = config.getInitParameter("username");
        String password = config.getInitParameter("password");

        try {
            Class.forName(driverClassName);
            db = DriverManager.getConnection(url, username, password);
        } catch (Exception ex) {
            log("Ошибка во время соединения с базой данных: ", ex);
            throw new ServletException("Ошибка при инициализации сервлета: " + ex.getMessage(), ex);
        }
    }

    public void destroy() {
        try {
            db.close();
        } catch (SQLException ex) {} // игнорируем все возможные ошибки т.к. соединение нужно закрыть обязательно
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println(
            "<head>\n" +
            "<meta charset=\"UTF-8\">\n" +
            "<title></title>\n" +
            "</head>\n" +
            "<body>\n" +
            "<h1>Input SQL command below:</h1>\n" +
            "<form>Query: <input name='query'>\n" +
            "<input type=submit></form>"
        );

        // считываем введенные на странице данные из формы
        String query = request.getParameter("query");
        if (query != null) out.println("<h1>" + query + "</h1>");

        // начало работы с JDBC
        Statement statement = null;
        try {
            statement = db.createStatement();
            ResultSet results = statement.executeQuery(query);

            ResultSetMetaData metadata = results.getMetaData();
            int numberOfColumns = metadata.getColumnCount();

            // начало формирования динамического контента
            out.println("<table><tr>");
            // сформируем строку заголовков столбцов
            for (int i = 1; i <= numberOfColumns; i++) { // в SQL столбцы нумеруются с единицы
                out.println(
                    "<th>" +
                    metadata.getColumnLabel(i) +
                    "</th>"
                );
            }
            out.println("</tr>");

            // сформируем строки
            while (results.next()) { // пока имеются строки, удовлетворяющие запросу
                out.println("<tr>");
                for (int i = 1; i <= numberOfColumns; i++) { // в SQL столбцы нумеруются с единицы
                    out.println(
                        "<th>" +
                        results.getObject(i) +
                        "</th>"
                    );
                }
                out.println("</tr>");
            }
            out.println("</table>");
        } catch (SQLException ex) {
            out.println("An error found in SQL query: " + ex.getMessage());
        } finally {
            try {
                statement.close();
            } catch (Exception ex) {} // завершаем запрос в любом случае
        } // конец работы с JDBC

        // вызовем сервлет Counter
        RequestDispatcher dispatcher = request.getRequestDispatcher("/Counter");
        if (dispatcher != null) {
            out.println("<br>Number of visits: ");
            // отправим атрубуту сервлета Counter имя текущего класса
            request.setAttribute(Counter.ATTRIBUTE, Query.class.getName());
            // добавим в текущий сервлет вывод сервлета Counter
            dispatcher.include(request, response);
        }
        out.println("</body>");
    }
}
