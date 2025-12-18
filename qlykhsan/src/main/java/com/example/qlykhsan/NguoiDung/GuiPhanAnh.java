package com.example.qlykhsan.NguoiDung;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GuiPhanAnh {

    public static int CURRENT_USER_ID;
    public static String CURRENT_USER_NAME;

    @FXML
    private TextField tfTieuDe;

    @FXML
    private TextArea taNoiDung;

    @FXML
    private Button btnGuiPhanAnh;

    private final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=QlyKhachSanDB;encrypt=true;trustServerCertificate=true;";
    private final String DB_USER = "sa";
    private final String DB_PASS = "123456";

    @FXML
    public void initialize() {
        tfTieuDe.clear();
        taNoiDung.clear();

        if (CURRENT_USER_NAME != null) {
            tfTieuDe.setPromptText(CURRENT_USER_NAME + " ơi, nhập tiêu đề tại đây...");
        }

        btnGuiPhanAnh.setOnAction(event -> xuLyGuiPhanAnh());
    }

    private void xuLyGuiPhanAnh() {
        String tieuDe = tfTieuDe.getText().trim();
        String noiDung = taNoiDung.getText().trim();

        if (tieuDe.isEmpty() || noiDung.isEmpty()) {
            hienThongBao(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập đầy đủ phản ánh!");
            return;
        }

        saveToDatabase(CURRENT_USER_ID, tieuDe, noiDung);
    }

    private void saveToDatabase(int userId, String title, String content) {
        String sql = "INSERT INTO PhanAnh (MaNguoiDung, TieuDe, NoiDung, NgayGui, TrangThai) VALUES (?, ?, ?, GETDATE(), N'ChuaXuLy')";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, title);
            pstmt.setString(3, content);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                hienThongBao(Alert.AlertType.INFORMATION, "Thành công",
                        "Cảm ơn " + CURRENT_USER_NAME + ", phản ánh của bạn đã được gửi!");
                tfTieuDe.clear();
                taNoiDung.clear();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            hienThongBao(Alert.AlertType.ERROR, "Lỗi SQL", e.getMessage());
        }
    }

    private void hienThongBao(Alert.AlertType type, String title, String header) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.showAndWait();
    }
}