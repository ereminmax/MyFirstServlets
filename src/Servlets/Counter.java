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

    //�������������� ��������� ������
    public void init() throws ServletException {
        ServletConfig config = getServletConfig();
        try {
            countFile = new File(config.getInitParameter("countFile"));
            interval = Integer.parseInt(config.getInitParameter("interval"));
            lastSaveTime = System.currentTimeMillis();
            loadState();
        } catch(Exception ex) {
            throw new ServletException("������ ��� ������������� ��������: " + ex.getMessage(), ex);
        }
    }

    //�������� ������ � ���� ��� ��������� ��������
    public void destroy() {
        try {
            saveState(); // ����� ����������. �� �����
        } catch (Exception ex) {} // ���������� ��� ������ �� ����� ����������
    }

    // ����� ���������� ������ � ����. ������������ ������������
    private void saveState () throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("countFile")));
        out.writeObject(countMap);
        out.close();
    }

    // ����� �������� ������ �� �����. ������������ ��������������
    private void loadState() throws IOException {
        // ���� ����� � ������� �������� �� ����������, �������� ���
        if (!countFile.exists()) {
            countMap = new HashMap();
            return; // ������ ������� ������ ��������� ������, ���� ���� ����������
        }

        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(new BufferedInputStream(new FileInputStream("countFile")));
            countMap = (HashMap) in.readObject();
        } catch (ClassNotFoundException ex) {
            throw new IOException("������ ��� ���������� ������: " + ex.getMessage());
        } finally {
            try {
                in.close();
            } catch (Exception ex) {} // ���������� ��� ������ �� ����� ��������
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String counterName = request.getParameter(PARAMETER); // ������� ��� �������� �� ��������� �������
        if (counterName == null) request.getAttribute(ATTRIBUTE); // ����� ��������� �������
        if (counterName == null) counterName = request.getRequestURI(); // ����� �������� ����� �� ������ URL
        Integer count; // �������

        // ������������� ������������� ������������� ���������� ���������� ����������� ��������
        synchronized (countMap) {
            count = (Integer) countMap.get("counterName"); // �������� ������� �������� ��������
            if (count != null) count++; // ����������� ���� ��� ����� ����
            else {
                log("������� ����� ������� " + counterName); // ������� �������� � \logs � ������ URL �� ����
                count = 1; // ������ ������ � 1
            }
            countMap.put(counterName, count); // ��������� ����-�������� � ����������

            // �������� �������������� ���������� � ����
            if (System.currentTimeMillis() - lastSaveTime > interval) {
                saveState();
                lastSaveTime = System.currentTimeMillis();
            }

            // ��� ����������� ����� �� ������������� �.�. ������ ���� ������� ������������ ��� ���������������
            PrintWriter out = response.getWriter();
            out.print(count);
        }
    }
}
