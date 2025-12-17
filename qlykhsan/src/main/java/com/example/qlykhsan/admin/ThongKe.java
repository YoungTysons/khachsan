package com.example.qlykhsan.admin;

import com.example.qlykhsan.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class ThongKe implements Initializable {

    @FXML
    private Label lblTongSoPhong;

    @FXML
    private Label lblPhongTrong;

    @FXML
    private Label lblDoanhThu;

    @FXML
    private Pane chartContainer; 

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Gọi hàm tải dữ liệu ngay khi giao diện được mở
        refreshData(); 
    }

    /**
     * Hàm dùng để làm mới toàn bộ dữ liệu trên Dashboard
     * Bạn có thể gọi hàm này từ một nút bấm "Làm mới" trên FXML
     */
    @FXML
    public void refreshData() {
        loadThongKeCoBan();
        loadDanhSachPhongDashboard();
    }

    private void loadThongKeCoBan() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // 1. Cập nhật tổng số phòng từ DB thực tế
            String sqlTongPhong = "SELECT COUNT(*) AS Tong FROM Phong";
            ResultSet rs1 = conn.prepareStatement(sqlTongPhong).executeQuery();
            if (rs1.next()) lblTongSoPhong.setText(String.valueOf(rs1.getInt("Tong")));

            // 2. Cập nhật số phòng trống (TrangThai = 'Trong')
            String sqlPhongTrong = "SELECT COUNT(*) AS Trong FROM Phong WHERE TrangThai = N'Trong'";
            ResultSet rs2 = conn.prepareStatement(sqlPhongTrong).executeQuery();
            if (rs2.next()) lblPhongTrong.setText(String.valueOf(rs2.getInt("Trong")));

            // 3. Cập nhật doanh thu tháng hiện tại
            String sqlDoanhThu = "SELECT SUM(TongTien) AS DoanhThu FROM DatPhong " +
                    "WHERE (TrangThai = N'HoanThanh' OR TrangThai = N'DaDuyet') " +
                    "AND MONTH(NgayCheckIn) = MONTH(GETDATE()) " +
                    "AND YEAR(NgayCheckIn) = YEAR(GETDATE())";
            ResultSet rs3 = conn.prepareStatement(sqlDoanhThu).executeQuery();
            if (rs3.next()) {
                double doanhThu = rs3.getDouble("DoanhThu");
                lblDoanhThu.setText(NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(doanhThu));
            } else {
                lblDoanhThu.setText("0 đ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadDanhSachPhongDashboard() {
        VBox vBoxList = new VBox(8); 
        vBoxList.setPadding(new Insets(10));
        vBoxList.prefWidthProperty().bind(chartContainer.widthProperty());

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Truy vấn lấy dữ liệu mới nhất từ các cột SoPhong, LoaiPhong, GiaPhong, TrangThai
            String sql = "SELECT SoPhong, LoaiPhong, GiaPhong, TrangThai FROM Phong";
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            NumberFormat currencyVN = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

            while (rs.next()) {
                HBox row = new HBox(20);
                row.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: #eee; -fx-border-radius: 3;");
                row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

                // Hiển thị số phòng (ví dụ: P101, P201)
                Label name = new Label(rs.getString("SoPhong")); 
                name.setPrefWidth(150);
                name.setStyle("-fx-font-weight: bold;");

                Label type = new Label(rs.getString("LoaiPhong"));
                type.setPrefWidth(150);

                Label price = new Label(currencyVN.format(rs.getDouble("GiaPhong")));
                price.setPrefWidth(150);
                price.setTextFill(Color.web("#2980b9"));

                String statusText = rs.getString("TrangThai");
                Label status = new Label(statusText);
                status.setPrefWidth(100);
                
                // Tự động đổi màu dựa trên trạng thái trong Database
                if (statusText != null && statusText.equalsIgnoreCase("Trong")) {
                    status.setTextFill(Color.web("#27ae60")); // Màu xanh cho phòng Trống
                } else {
                    status.setTextFill(Color.web("#e74c3c")); // Màu đỏ cho phòng đã đặt (DaDat)
                }

                row.getChildren().addAll(name, type, price, status);
                vBoxList.getChildren().add(row);
            }

            // Xóa danh sách cũ và nạp danh sách mới vừa tải từ Database
            chartContainer.getChildren().clear();
            chartContainer.getChildren().add(vBoxList);

        } catch (Exception e) {
            chartContainer.getChildren().clear();
            Label errorLabel = new Label("Lỗi hiển thị: " + e.getMessage());
            errorLabel.setTextFill(Color.RED);
            chartContainer.getChildren().add(errorLabel);
            e.printStackTrace();
        }
    }
}