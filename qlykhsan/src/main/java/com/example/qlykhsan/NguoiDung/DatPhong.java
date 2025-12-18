package com.example.qlykhsan.NguoiDung;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.sql.*;

public class DatPhong {

    @FXML private FlowPane flowPanePhong;

    private final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=QlyKhachSanDB;encrypt=true;trustServerCertificate=true;";
    private final String DB_USER = "sa";
    private final String DB_PASS = "123456";

    @FXML
    public void initialize() {
        loadDanhSachPhong();
    }

    private void loadDanhSachPhong() {
        flowPanePhong.getChildren().clear();
        String sql = "SELECT * FROM Phong WHERE TrangThai = N'Trong'";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                VBox card = createRoomCard(
                        rs.getInt("MaPhong"),
                        rs.getString("SoPhong"),
                        rs.getString("LoaiPhong"),
                        rs.getDouble("GiaPhong")
                );
                flowPanePhong.getChildren().add(card);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private VBox createRoomCard(int id, String so, String loai, double gia) {
        VBox box = new VBox(10);
        box.setPrefWidth(280);
        box.setStyle("-fx-background-color: white; -fx-border-color: #dddddd;");        
    

        Label lblSo = new Label("PHÒNG " + so);
        lblSo.setMaxWidth(Double.MAX_VALUE);
        lblSo.setPadding(new Insets(10));
        
       

        VBox info = new VBox(5);
        info.setPadding(new Insets(10));
        Label lblGia = new Label(String.format("%,.0f VNĐ/đêm", gia));
        lblGia.setFont(Font.font("System", FontWeight.BOLD, 16));
        lblGia.setTextFill(Color.DARKBLUE);

        Label lblLoai = new Label("Loại: " + loai);
        Button btnDat = new Button("ĐẶT NGAY");
        btnDat.setMaxWidth(Double.MAX_VALUE);
    

        btnDat.setOnAction(e -> thucHienDat(id, so, gia));

        info.getChildren().addAll(lblGia, lblLoai, btnDat);
        box.getChildren().addAll(lblSo, info);
        return box;
    }

    private void thucHienDat(int maPhong, String soPhong, double gia) {
        int userId = GuiPhanAnh.CURRENT_USER_ID;

        if (userId == 0) {
            hienThongBao("Lỗi", "Bạn cần đăng nhập lại!");
            return;
        }

        String sqlDat = "INSERT INTO DatPhong (MaNguoiDung, MaPhong, NgayDat, NgayCheckIn, NgayCheckOut, TongTien, TrangThai) VALUES (?, ?, GETDATE(), GETDATE(), GETDATE()+1, ?, N'ChoDuyet')";
        String sqlUpdate = "UPDATE Phong SET TrangThai = N'DaDat' WHERE MaPhong = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            conn.setAutoCommit(false);
            try (PreparedStatement p1 = conn.prepareStatement(sqlDat);
                 PreparedStatement p2 = conn.prepareStatement(sqlUpdate)) {

                p1.setInt(1, userId);
                p1.setInt(2, maPhong);
                p1.setDouble(3, gia);
                p1.executeUpdate();

                p2.setInt(1, maPhong);
                p2.executeUpdate();

                conn.commit();
                hienThongBao("Thành công", "Đã đặt phòng " + soPhong + ". Vui lòng chờ Admin duyệt!");
                loadDanhSachPhong();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void hienThongBao(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}