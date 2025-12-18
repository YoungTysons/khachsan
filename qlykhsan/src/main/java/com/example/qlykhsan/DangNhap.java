package com.example.qlykhsan;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DangNhap {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lblError;

    @FXML
    public void handleLogin(ActionEvent event) {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        String role = checkLogin(username, password);

        if (role != null) {
            System.out.println("=> Đăng nhập THÀNH CÔNG. Vai trò: " + role);
            
            if (role.equalsIgnoreCase("Admin")) {
                chuyenManHinh(event, "GiaoDienChinhAdmin.fxml", "Hệ thống Quản Lý - Admin");
            } else {
                chuyenManHinh(event, "GiaoDienChinhNguoiDung.fxml", "Hệ thống Quản Lý - Khách Hàng");
            }
        } else {
            System.out.println("=> Đăng nhập THẤT BẠI");
            lblError.setText("Sai tên đăng nhập hoặc mật khẩu!");
        }
    }

    private String checkLogin(String username, String password) {
        String role = null;
        String query = "SELECT MaNguoiDung, HoTen, VaiTro FROM NguoiDung WHERE TenDangNhap = ? AND MatKhau = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                role = rs.getString("VaiTro");

                com.example.qlykhsan.NguoiDung.GuiPhanAnh.CURRENT_USER_ID = rs.getInt("MaNguoiDung");
                com.example.qlykhsan.NguoiDung.GuiPhanAnh.CURRENT_USER_NAME = rs.getNString("HoTen");
                // ---------------------------------------------------------

                if (role != null) role = role.trim();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            lblError.setText("Lỗi kết nối CSDL!");
        }
        return role;
    }

    private void chuyenManHinh(ActionEvent event, String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            lblError.setText("Không tìm thấy file: " + fxmlFile);
            System.err.println("Lỗi load FXML: " + e.getMessage());
        }
    }
}