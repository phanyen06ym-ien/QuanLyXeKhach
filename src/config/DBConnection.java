/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 *
 * @author Admin
 */


public class DBConnection {

    private static final String URL =
            "jdbc:sqlserver://localhost:1433;databaseName=QuanLyXeKhach;encrypt=true;trustServerCertificate=true";

    private static final String USER = "sa";

    private static final String PASSWORD = "123456";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Lỗi kết nối Database!");
            e.printStackTrace();
            return null;
        }
    }
    
}
