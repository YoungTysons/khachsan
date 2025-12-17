package com.example.qlykhsan.admin;

import com.example.qlykhsan.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

public class ThongBao implements Initializable {

    @FXML private TextField tfTieuDe;
    @FXML private TextArea taNoiDung;
    @FXML private Button btnGuiThongBao;
    @FXML private VBox vboxDanhSachThongBao; // Nơi chứa danh sách các thẻ thông báo

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // 1. Xóa các mẫu thiết kế cứng trong FXML (nếu có) để nạp dữ liệu thật
        vboxDanhSachThongBao.getChildren().clear();

        // 2. Load danh sách thông báo từ DB
        loadThongBaoFromDB();

        // 3. Gán sự kiện gửi
        btnGuiThongBao.setOnAction(e -> handleGuiThongBao());
    }

    // --- LOGIC GỬI THÔNG BÁO ---
    private void handleGuiThongBao() {
        String tieuDe = tfTieuDe.getText().trim();
        String noiDung = taNoiDung.getText().trim();

        if (tieuDe.isEmpty() || noiDung.isEmpty()) {
            showAlert("Thiếu thông tin", "Vui lòng nhập tiêu đề và nội dung!");
            return;
        }

        // Insert vào DB (MaNguoiDung để NULL vì là thông báo chung của Admin)
        String sql = "INSERT INTO ThongBao (MaNguoiDung, TieuDe, NoiDung, NgayTao) VALUES (NULL, ?, ?, GETDATE())";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tieuDe);
            pstmt.setString(2, noiDung);
            pstmt.executeUpdate();

            showAlert("Thành công", "Đã gửi thông báo!");
            
            // Clear form và load lại danh sách
            tfTieuDe.clear();
            taNoiDung.clear();
            loadThongBaoFromDB();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Gửi thất bại: " + e.getMessage());
        }
    }

    // --- LOGIC LOAD DANH SÁCH (TẠO GIAO DIỆN ĐỘNG) ---
    private void loadThongBaoFromDB() {
        // Xóa danh sách cũ trước khi load mới
        vboxDanhSachThongBao.getChildren().clear();

        String sql = "SELECT * FROM ThongBao ORDER BY NgayTao DESC"; // Mới nhất lên đầu

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            while (rs.next()) {
                String tieuDe = rs.getString("TieuDe");
                String noiDung = rs.getString("NoiDung");
                String ngayTao = sdf.format(rs.getTimestamp("NgayTao"));

                // Tạo giao diện item thông báo bằng code Java (giống mẫu FXML)
                VBox item = createThongBaoItem(tieuDe, ngayTao, noiDung);
                
                // Thêm vào VBox chính
                vboxDanhSachThongBao.getChildren().add(item);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Hàm hỗ trợ vẽ giao diện cho 1 thông báo
    private VBox createThongBaoItem(String title, String date, String content) {
        // 1. VBox container bao ngoài
        VBox container = new VBox();
        container.setSpacing(5);
        container.setPadding(new Insets(10));
        container.setStyle("-fx-border-color: #dddddd; -fx-background-color: #fcfcfc; -fx-border-radius: 3;");

        // 2. Header (BorderPane): Trái là Title, Phải là Date
        BorderPane header = new BorderPane();
        
        Label lblTitle = new Label(title);
        lblTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        Label lblDate = new Label(date);
        lblDate.setTextFill(Color.web("#757575"));
        lblDate.setFont(new Font(11));

        header.setLeft(lblTitle);
        header.setRight(lblDate);

        // 3. Nội dung
        Label lblContent = new Label(content);
        lblContent.setWrapText(true); // Tự xuống dòng nếu dài

        // 4. Ghép lại
        container.getChildren().addAll(header, lblContent);

        return container;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}