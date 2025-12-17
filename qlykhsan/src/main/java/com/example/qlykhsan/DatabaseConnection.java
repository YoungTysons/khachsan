package com.example.qlykhsan;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // 1. Chuỗi kết nối (Giữ nguyên như bạn gửi)
    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=QlyKhachSanDB;encrypt=true;trustServerCertificate=true;";
    
    // 2. Tài khoản & Mật khẩu
    private static final String USER = "sa";      
    private static final String PASS = "123456";  // Nhớ đổi nếu mật khẩu của bạn khác

    public static Connection getConnection() throws SQLException {
        // 3. Truyền đủ 3 tham số: URL, User, Pass
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }
}