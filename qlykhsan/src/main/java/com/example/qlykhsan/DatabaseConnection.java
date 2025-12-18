package com.example.qlykhsan;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=QlyKhachSanDB;encrypt=true;trustServerCertificate=true;";
    
    private static final String USER = "sa";      
    private static final String PASS = "123456";  // Nhớ đổi mật khẩu

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }
}