package Servlets;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;

/**
 * Created by Max Eremin on 14.11.2015.
 */
//@WebServlet(name = "Counter")
public class Counter extends HttpServlet {
    private HashMap countMap;
    private File countFile;
    private long interval;
    private long lastSaveTime;

    public static final String PARAMETER = "counter";
    public static final String ATTRIBUTE = "Servlets.Counter.counter";

    //инициализируем начальные данные
    public void init() throws ServletException {
        ServletConfig config = getServletConfig();
        try {
            countFile = new File(config.getInitParameter("countFile"));
            interval = Integer.parseInt(config.getInitParameter("interval"));
            lastSaveTime = System.currentTimeMillis();
            loadState();
        } catch(Exception ex) {
            throw new ServletException("Ошибка при инициализации сервлета: " + ex.getMessage(), ex);
        }
    }

    //сохраним данные в файл при остановке сервлета
    public void destroy() {
        try {
            saveState(); // метод сохранения. см далее
        } catch (Exception ex) {} // игнорируем все ошибки во время сохранения
    }

    // метод сохранения данных в файл. используется сериализация
    private void saveState () throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("countFile")));
        out.writeObject(countMap);
        out.close();
    }

    // метод загрузки данных из файла. используется десериализация
    private void loadState() throws IOException {
        // если файла с данными счетчика не существует, создадим его
        if (!countFile.exists()) {
            countMap = new HashMap();
            return; // даллее следует логика обработки данных, тттк файл существует
        }

        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(new BufferedInputStream(new FileInputStream("countFile")));
            countMap = (HashMap) in.readObject();
        } catch (ClassNotFoundException ex) {
            throw new IOException("Ошибка при считывании данных: " + ex.getMessage());
        } finally {
            try {
                in.close();
            } catch (Exception ex) {} // игнорируем все ошибки во время закрытия
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String counterName = request.getParameter(PARAMETER); // считаем имя счетчика из параметра запроса
        if (counterName == null) request.getAttribute(ATTRIBUTE); // иначе считываем атрибут
        if (counterName == null) counterName = request.getRequestURI(); // иначе создадим новый на основе URL
        Integer count; // счетчик

        // синхронизация предотвращает одновременное обновление хештаблицы несколькими потоками
        synchronized (countMap) {
            count = (Integer) countMap.get("counterName"); // пытаемся извлечь значение счетчика
            if (count != null) count++; // увеличиваем если там чтото было
            else {
                log("Создаем новый счетчик " + counterName); // запишем название в \logs с именем URL см выше
                count = 1; // начнем отсчет с 1
            }
            countMap.put(counterName, count); // сохраняем ключ-значение в хештаблицу

            // настроим автосохранение хештаблицы в файл
            if (System.currentTimeMillis() - lastSaveTime > interval) {
                saveState();
                lastSaveTime = System.currentTimeMillis();
            }

            // тип содержимого можем не устанавливать т.к. обычно этот сервлет используется как вспомогательный
            PrintWriter out = response.getWriter();
            out.print(count);
        }
    }
}
