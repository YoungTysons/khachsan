package com.example.qlykhsan.admin;

import com.example.qlykhsan.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

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
    private Pane chartContainer; // Pane chứa biểu đồ

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadThongKeCoBan();
        loadBieuDoTrangThaiPhong();
    }

    // 1. Load các số liệu text (Tổng phòng, Phòng trống, Doanh thu)
    private void loadThongKeCoBan() {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();

            // a. Đếm tổng số phòng
            String sqlTongPhong = "SELECT COUNT(*) AS Tong FROM Phong";
            PreparedStatement pst1 = conn.prepareStatement(sqlTongPhong);
            ResultSet rs1 = pst1.executeQuery();
            if (rs1.next()) {
                lblTongSoPhong.setText(String.valueOf(rs1.getInt("Tong")));
            }

            // b. Đếm phòng trống (TrangThai = 'Trong')
            // Lưu ý: SQL Server cần thêm N trước chuỗi tiếng Việt nếu cột là NVARCHAR
            String sqlPhongTrong = "SELECT COUNT(*) AS Trong FROM Phong WHERE TrangThai = N'Trong'";
            PreparedStatement pst2 = conn.prepareStatement(sqlPhongTrong);
            ResultSet rs2 = pst2.executeQuery();
            if (rs2.next()) {
                lblPhongTrong.setText(String.valueOf(rs2.getInt("Trong")));
            }

            // c. Tính doanh thu tháng hiện tại
            // Tính tổng tiền từ bảng DatPhong where trạng thái đã thanh toán hoặc đã duyệt
            String sqlDoanhThu = "SELECT SUM(TongTien) AS DoanhThu " +
                    "FROM DatPhong " +
                    "WHERE (TrangThai = N'HoanThanh' OR TrangThai = N'DaDuyet') " +
                    "AND MONTH(NgayCheckIn) = MONTH(GETDATE()) " +
                    "AND YEAR(NgayCheckIn) = YEAR(GETDATE())";

            PreparedStatement pst3 = conn.prepareStatement(sqlDoanhThu);
            ResultSet rs3 = pst3.executeQuery();
            if (rs3.next()) {
                double doanhThu = rs3.getDouble("DoanhThu");
                
                // Format tiền VND
                Locale localeVN = new Locale("vi", "VN");
                NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);
                lblDoanhThu.setText(currencyVN.format(doanhThu));
            } else {
                lblDoanhThu.setText("0 đ");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 2. Vẽ biểu đồ tròn (PieChart) trạng thái phòng
    private void loadBieuDoTrangThaiPhong() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Group by trạng thái để đếm số lượng
            String sqlChart = "SELECT TrangThai, COUNT(*) AS SoLuong FROM Phong GROUP BY TrangThai";
            PreparedStatement pst = conn.prepareStatement(sqlChart);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String status = rs.getString("TrangThai");
                int count = rs.getInt("SoLuong");
                pieChartData.add(new PieChart.Data(status + " (" + count + ")", count));
            }

            // Tạo biểu đồ
            PieChart pieChart = new PieChart(pieChartData);
            pieChart.setTitle("Tỉ lệ trạng thái phòng");
            pieChart.setLegendVisible(true);

            // Bind kích thước biểu đồ theo Pane cha để responsive
            pieChart.prefWidthProperty().bind(chartContainer.widthProperty());
            pieChart.prefHeightProperty().bind(chartContainer.heightProperty());

            // Thêm vào giao diện
            chartContainer.getChildren().clear();
            chartContainer.getChildren().add(pieChart);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}