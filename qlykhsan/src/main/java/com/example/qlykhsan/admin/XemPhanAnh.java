package com.example.qlykhsan.admin;

import com.example.qlykhsan.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

public class XemPhanAnh implements Initializable {

    // --- FXML UI ---
    @FXML private TableView<PhanAnhModel> tablePhanAnh;
    @FXML private TableColumn<PhanAnhModel, String> colNguoiGui;
    @FXML private TableColumn<PhanAnhModel, String> colTieuDe;
    @FXML private TableColumn<PhanAnhModel, String> colNoiDung;
    @FXML private TableColumn<PhanAnhModel, String> colNgayGui;

    // --- BIẾN DÙNG CHUNG ---
    private ObservableList<PhanAnhModel> listPhanAnh;

    // ==========================================================
    // 1. CLASS MODEL (INNER CLASS)
    // ==========================================================
    public static class PhanAnhModel {
        private String nguoiGui;
        private String tieuDe;
        private String noiDung;
        private String ngayGui; // Lưu dạng chuỗi đã format cho đẹp

        public PhanAnhModel(String nguoiGui, String tieuDe, String noiDung, String ngayGui) {
            this.nguoiGui = nguoiGui;
            this.tieuDe = tieuDe;
            this.noiDung = noiDung;
            this.ngayGui = ngayGui;
        }

        // Getters (Bắt buộc để TableView hiển thị)
        public String getNguoiGui() { return nguoiGui; }
        public String getTieuDe() { return tieuDe; }
        public String getNoiDung() { return noiDung; }
        public String getNgayGui() { return ngayGui; }
    }
    // ==========================================================

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Cấu hình cột bảng (map với tên getter trong class PhanAnhModel)
        colNguoiGui.setCellValueFactory(new PropertyValueFactory<>("nguoiGui"));
        colTieuDe.setCellValueFactory(new PropertyValueFactory<>("tieuDe"));
        colNoiDung.setCellValueFactory(new PropertyValueFactory<>("noiDung"));
        colNgayGui.setCellValueFactory(new PropertyValueFactory<>("ngayGui"));

        // Load dữ liệu
        loadDataFromDB();
    }

    private void loadDataFromDB() {
        listPhanAnh = FXCollections.observableArrayList();
        
        // Query JOIN bảng PhanAnh và NguoiDung để lấy tên người gửi
        String sql = "SELECT pa.TieuDe, pa.NoiDung, pa.NgayGui, nd.HoTen " +
                     "FROM PhanAnh pa " +
                     "JOIN NguoiDung nd ON pa.MaNguoiDung = nd.MaNguoiDung " +
                     "ORDER BY pa.NgayGui DESC"; // Mới nhất lên đầu

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            // Định dạng ngày giờ hiển thị (Ví dụ: 15/12/2023 14:30)
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            while (rs.next()) {
                String tenNguoi = rs.getString("HoTen");
                String tieuDe = rs.getString("TieuDe");
                String noiDung = rs.getString("NoiDung");
                
                // Xử lý ngày tháng
                String ngayGui = "";
                if (rs.getTimestamp("NgayGui") != null) {
                    ngayGui = sdf.format(rs.getTimestamp("NgayGui"));
                }

                listPhanAnh.add(new PhanAnhModel(tenNguoi, tieuDe, noiDung, ngayGui));
            }
            
            tablePhanAnh.setItems(listPhanAnh);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}