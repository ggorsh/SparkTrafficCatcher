package ru.work.application;
import java.sql.*;
/**
 * Класс работы с БД. Сформирован как сингелтон.
 * Выборка наиболее свежих данных effective_date происходит через sql - запрос.
 * */

public class DataBase {
    private final String driverName = "org.sqlite.JDBC";
    private final String connectionString = "jdbc:sqlite:traffic_limits.db";
    private long min, max;
    public Connection conn;
    public Statement statmt;
    public ResultSet resSet;

    public long getMin() {
        return min;
    }

    public long getMax() {
        return max;
    }

    private static volatile DataBase instance;

    public static DataBase getInstance() {
        if (instance == null)
            synchronized (DataBase.class) {
                if (instance == null)
                    instance = new DataBase();
            }
        return instance;
    }


    public void connect() throws ClassNotFoundException, SQLException {
        conn = null;
        Class.forName(driverName);
        conn = DriverManager.getConnection(connectionString);
    }

    public void update() throws SQLException {

        statmt = conn.createStatement();
        resSet = statmt.executeQuery("SELECT limit_value FROM limits_per_hour WHERE limit_name='min' AND effective_date=(SELECT MAX(effective_date) FROM limits_per_hour WHERE limit_name='min')");
        min = resSet.getLong(1)/12; //Делю на 12 потому что в таблице данные per_hour  а мы считаем по 5 минут.
        resSet = statmt.executeQuery("SELECT limit_value FROM limits_per_hour WHERE limit_name='max' AND effective_date=(SELECT MAX(effective_date) FROM limits_per_hour WHERE limit_name='max')");
        max = resSet.getLong(1)/12;

    }

    public void disconnect() throws SQLException {
        conn.close();
        statmt.close();
        resSet.close();
    }
}
