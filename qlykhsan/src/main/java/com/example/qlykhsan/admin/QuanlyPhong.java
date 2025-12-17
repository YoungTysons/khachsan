package com.example.qlykhsan.admin;

import com.example.qlykhsan.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.ResourceBundle;

public class QuanlyPhong implements Initializable {

    // --- KHAI BÁO FXML ---
    @FXML private TableView<Phong> tablePhong;
    @FXML private TableColumn<Phong, String> colTenPhong;
    @FXML private TableColumn<Phong, Double> colGia;
    @FXML private TableColumn<Phong, String> colMoTa;
    @FXML private TableColumn<Phong, String> colTrangThai;

    @FXML private TextField tfTenPhong;
    @FXML private TextField tfGiaThue;
    @FXML private TextArea taMoTa;
    @FXML private ComboBox<String> cbTrangThai;

    @FXML private Button btnThem;
    @FXML private Button btnSua;
    @FXML private Button btnXoa;
    @FXML private Button btnHuy;

    // --- BIẾN DÙNG CHUNG ---
    private ObservableList<Phong> listPhong;
    private int selectedMaPhong = -1; // -1 nghĩa là chưa chọn phòng nào

    // ==========================================================
    // 1. CLASS MODEL (GỘP VÀO ĐÂY)
    // ==========================================================
    public static class Phong {
        private int maPhong;
        private String soPhong;
        private String loaiPhong;
        private double giaPhong;
        private String trangThai;

        public Phong(int maPhong, String soPhong, String loaiPhong, double giaPhong, String trangThai) {
            this.maPhong = maPhong;
            this.soPhong = soPhong;
            this.loaiPhong = loaiPhong;
            this.giaPhong = giaPhong;
            this.trangThai = trangThai;
        }

        // Getters và Setters (Bắt buộc để TableView hiển thị dữ liệu)
        public int getMaPhong() { return maPhong; }
        public void setMaPhong(int maPhong) { this.maPhong = maPhong; }

        public String getSoPhong() { return soPhong; }
        public void setSoPhong(String soPhong) { this.soPhong = soPhong; }

        public String getLoaiPhong() { return loaiPhong; }
        public void setLoaiPhong(String loaiPhong) { this.loaiPhong = loaiPhong; }

        public double getGiaPhong() { return giaPhong; }
        public void setGiaPhong(double giaPhong) { this.giaPhong = giaPhong; }

        public String getTrangThai() { return trangThai; }
        public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    }
    // ==========================================================

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Cấu hình ComboBox
        cbTrangThai.setItems(FXCollections.observableArrayList("Trong", "DaDat", "DangSuaChua"));
        cbTrangThai.getSelectionModel().selectFirst();

        // Cấu hình Cột bảng (Mapping với tên biến trong class Phong ở trên)
        colTenPhong.setCellValueFactory(new PropertyValueFactory<>("soPhong"));
        colGia.setCellValueFactory(new PropertyValueFactory<>("giaPhong"));
        colMoTa.setCellValueFactory(new PropertyValueFactory<>("loaiPhong"));
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));

        // Load dữ liệu
        loadDataFromDB();

        // Sự kiện click bảng
        tablePhong.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) fillForm(newVal);
        });

        // Gán sự kiện nút bấm
        btnThem.setOnAction(e -> handleThem());
        btnSua.setOnAction(e -> handleSua());
        btnXoa.setOnAction(e -> handleXoa());
        btnHuy.setOnAction(e -> clearForm());
    }

    // --- XỬ LÝ DATABASE ---
    private void loadDataFromDB() {
        listPhong = FXCollections.observableArrayList();
        String sql = "SELECT * FROM Phong";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                listPhong.add(new Phong(
                        rs.getInt("MaPhong"),
                        rs.getString("SoPhong"),
                        rs.getString("LoaiPhong"),
                        rs.getDouble("GiaPhong"),
                        rs.getString("TrangThai")
                ));
            }
            tablePhong.setItems(listPhong);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi tải dữ liệu", e.getMessage());
        }
    }

    private void handleThem() {
        if (!validateForm()) return;

        String sql = "INSERT INTO Phong (SoPhong, LoaiPhong, GiaPhong, TrangThai) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tfTenPhong.getText());
            pstmt.setString(2, taMoTa.getText());
            pstmt.setDouble(3, Double.parseDouble(tfGiaThue.getText()));
            pstmt.setString(4, cbTrangThai.getValue());

            pstmt.executeUpdate();
            showAlert("Thông báo", "Thêm thành công!");
            loadDataFromDB();
            clearForm();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Thêm thất bại (Có thể trùng tên phòng).");
        }
    }

    private void handleSua() {
        if (selectedMaPhong == -1) {
            showAlert("Cảnh báo", "Chọn phòng cần sửa trước!");
            return;
        }
        if (!validateForm()) return;

        String sql = "UPDATE Phong SET SoPhong=?, LoaiPhong=?, GiaPhong=?, TrangThai=? WHERE MaPhong=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tfTenPhong.getText());
            pstmt.setString(2, taMoTa.getText());
            pstmt.setDouble(3, Double.parseDouble(tfGiaThue.getText()));
            pstmt.setString(4, cbTrangThai.getValue());
            pstmt.setInt(5, selectedMaPhong);

            pstmt.executeUpdate();
            showAlert("Thông báo", "Cập nhật thành công!");
            loadDataFromDB();
            clearForm();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Cập nhật thất bại.");
        }
    }

    private void handleXoa() {
        if (selectedMaPhong == -1) {
            showAlert("Cảnh báo", "Chọn phòng cần xóa trước!");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận");
        alert.setContentText("Bạn có chắc muốn xóa phòng này?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String sql = "DELETE FROM Phong WHERE MaPhong=?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, selectedMaPhong);
                pstmt.executeUpdate();
                
                showAlert("Thông báo", "Đã xóa!");
                loadDataFromDB();
                clearForm();
            } catch (Exception e) {
                showAlert("Lỗi", "Không thể xóa (Phòng đang có dữ liệu đặt).");
            }
        }
    }

    // --- HÀM PHỤ ---
    private void fillForm(Phong p) {
        selectedMaPhong = p.getMaPhong();
        tfTenPhong.setText(p.getSoPhong());
        tfGiaThue.setText(String.format("%.0f", p.getGiaPhong()));
        taMoTa.setText(p.getLoaiPhong());
        cbTrangThai.setValue(p.getTrangThai());
    }

    private void clearForm() {
        tfTenPhong.clear();
        tfGiaThue.clear();
        taMoTa.clear();
        cbTrangThai.getSelectionModel().selectFirst();
        selectedMaPhong = -1;
        tablePhong.getSelectionModel().clearSelection();
    }

    private boolean validateForm() {
        if (tfTenPhong.getText().trim().isEmpty() || tfGiaThue.getText().trim().isEmpty()) {
            showAlert("Lỗi", "Nhập thiếu tên hoặc giá!");
            return false;
        }
        try {
            Double.parseDouble(tfGiaThue.getText().trim());
        } catch (NumberFormatException e) {
            showAlert("Lỗi", "Giá thuê phải là số!");
            return false;
        }
        return true;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}