package com.example.qlykhsan.NguoiDung;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.sql.*;

public class ThongBao {

    @FXML
    private VBox vboxDanhSachThongBao;

    // Thông tin kết nối CSDL
    private final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=QlyKhachSanDB;encrypt=true;trustServerCertificate=true;";
    private final String DB_USER = "sa";
    private final String DB_PASS = "123456";

    @FXML
    public void initialize() {
        loadThongBao();
    }

    private void loadThongBao() {
        vboxDanhSachThongBao.getChildren().clear(); // Xóa sạch giao diện cũ

        // Lấy ID người dùng đang đăng nhập từ file GuiPhanAnh
        int userId = GuiPhanAnh.CURRENT_USER_ID;

        // Câu lệnh SQL: Lấy thông báo chung (NULL) HOẶC thông báo riêng của User này
        String sql = "SELECT TieuDe, NoiDung, NgayTao FROM ThongBao " +
                "WHERE MaNguoiDung IS NULL OR MaNguoiDung = ? " +
                "ORDER BY NgayTao DESC";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String tieuDe = rs.getString("TieuDe");
                String noiDung = rs.getString("NoiDung");
                Timestamp ngayTao = rs.getTimestamp("NgayTao");

                // Tạo giao diện thông báo động
                VBox cardThongBao = createNotificationCard(tieuDe, noiDung, ngayTao.toString());
                vboxDanhSachThongBao.getChildren().add(cardThongBao);
            }

            if (vboxDanhSachThongBao.getChildren().isEmpty()) {
                vboxDanhSachThongBao.getChildren().add(new Label("Hiện tại không có thông báo nào."));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            vboxDanhSachThongBao.getChildren().add(new Label("Lỗi khi tải thông báo từ máy chủ."));
        }
    }

    /**
     * Hàm tạo Card giao diện cho từng thông báo giống như thiết kế FXML ban đầu
     */
    private VBox createNotificationCard(String title, String content, String date) {
        VBox card = new VBox();
        card.setPadding(new Insets(10));
        card.setStyle("-fx-border-color: #dddddd; -fx-background-color: #fcfcfc; -fx-border-radius: 3;");

        BorderPane header = new BorderPane();
        header.setPadding(new Insets(0, 0, 5, 0));

        Label lblTitle = new Label(title);
        lblTitle.setFont(Font.font("System", FontWeight.BOLD, 14));

        Label lblDate = new Label(date);
        lblDate.setTextFill(Color.web("#757575"));
        lblDate.setFont(Font.font("System", 11));

        header.setLeft(lblTitle);
        header.setRight(lblDate);

        Label lblContent = new Label(content);
        lblContent.setWrapText(true);
        lblContent.setMaxWidth(850); // Đảm bảo text không tràn màn hình

        card.getChildren().addAll(header, lblContent);
        return card;
    }
}