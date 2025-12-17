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

public class QuanlyKhach implements Initializable {

    // --- KHAI BÁO FXML ---
    @FXML private TableView<KhachHang> tableKhach;
    @FXML private TableColumn<KhachHang, String> colHoTen;
    @FXML private TableColumn<KhachHang, String> colSdt;
    @FXML private TableColumn<KhachHang, String> colUsername;

    @FXML private TextField tfHoTen;
    @FXML private TextField tfSdt;
    @FXML private TextField tfUsername;

    @FXML private Button btnTaoTK;
    @FXML private Button btnResetMK;

    // --- BIẾN DÙNG CHUNG ---
    private ObservableList<KhachHang> listKhach;
    private int selectedMaNguoiDung = -1;

    // ==========================================================
    // 1. CLASS MODEL (GỘP BÊN TRONG)
    // ==========================================================
    public static class KhachHang {
        private int maNguoiDung;
        private String hoTen;
        private String sdt;
        private String tenDangNhap;
        
        // Constructor
        public KhachHang(int maNguoiDung, String hoTen, String sdt, String tenDangNhap) {
            this.maNguoiDung = maNguoiDung;
            this.hoTen = hoTen;
            this.sdt = sdt;
            this.tenDangNhap = tenDangNhap;
        }

        // Getters/Setters
        public int getMaNguoiDung() { return maNguoiDung; }
        public String getHoTen() { return hoTen; }
        public String getSdt() { return sdt; }
        public String getTenDangNhap() { return tenDangNhap; }
    }
    // ==========================================================

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Cấu hình cột bảng
        colHoTen.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        colSdt.setCellValueFactory(new PropertyValueFactory<>("sdt"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("tenDangNhap"));

        // Load dữ liệu ban đầu
        loadDataFromDB();

        // Sự kiện click vào bảng -> Đổ dữ liệu lên TextField
        tableKhach.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                fillForm(newVal);
            }
        });

        // Gán sự kiện cho các nút
        btnTaoTK.setOnAction(e -> handleTaoTK());
        btnResetMK.setOnAction(e -> handleResetMK());
    }

    // --- LOGIC DATABASE ---

    private void loadDataFromDB() {
        listKhach = FXCollections.observableArrayList();
        // Chỉ lấy những người dùng có vai trò là KhachHang
        String sql = "SELECT * FROM NguoiDung WHERE VaiTro = 'KhachHang'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                listKhach.add(new KhachHang(
                        rs.getInt("MaNguoiDung"),
                        rs.getString("HoTen"),
                        rs.getString("SoDienThoai"),
                        rs.getString("TenDangNhap")
                ));
            }
            tableKhach.setItems(listKhach);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi tải dữ liệu", e.getMessage());
        }
    }

    private void handleTaoTK() {
        if (!validateForm()) return;

        // Mặc định mật khẩu là 123456 và VaiTro là KhachHang
        String sql = "INSERT INTO NguoiDung (HoTen, SoDienThoai, TenDangNhap, MatKhau, VaiTro) VALUES (?, ?, ?, '123456', 'KhachHang')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tfHoTen.getText());
            pstmt.setString(2, tfSdt.getText());
            pstmt.setString(3, tfUsername.getText());

            pstmt.executeUpdate();
            
            showAlert("Thành công", "Đã tạo tài khoản khách!\nMật khẩu mặc định: 123456");
            loadDataFromDB();
            clearForm();

        } catch (Exception e) {
            e.printStackTrace();
            // Lỗi phổ biến: Trùng Tên đăng nhập (UNIQUE constraint)
            showAlert("Lỗi tạo tài khoản", "Tên đăng nhập đã tồn tại hoặc lỗi kết nối.\n" + e.getMessage());
        }
    }

    private void handleResetMK() {
        if (selectedMaNguoiDung == -1) {
            showAlert("Cảnh báo", "Vui lòng chọn khách hàng cần reset mật khẩu!");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận");
        alert.setHeaderText(null);
        alert.setContentText("Bạn có chắc muốn đặt lại mật khẩu cho tài khoản: " + tfUsername.getText() + " về '123456'?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            
            String sql = "UPDATE NguoiDung SET MatKhau = '123456' WHERE MaNguoiDung = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, selectedMaNguoiDung);
                pstmt.executeUpdate();
                
                showAlert("Thành công", "Đã reset mật khẩu về 123456!");
                
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Lỗi", "Không thể reset mật khẩu.");
            }
        }
    }

    // --- HÀM HỖ TRỢ ---

    private void fillForm(KhachHang k) {
        selectedMaNguoiDung = k.getMaNguoiDung();
        tfHoTen.setText(k.getHoTen());
        tfSdt.setText(k.getSdt());
        tfUsername.setText(k.getTenDangNhap());
        
        // Không cho sửa tên đăng nhập để tránh lỗi hệ thống, chỉ cho tạo mới
        tfUsername.setEditable(false); 
    }

    private void clearForm() {
        tfHoTen.clear();
        tfSdt.clear();
        tfUsername.clear();
        tfUsername.setEditable(true); // Mở lại cho phép nhập khi tạo mới
        selectedMaNguoiDung = -1;
        tableKhach.getSelectionModel().clearSelection();
    }

    private boolean validateForm() {
        if (tfHoTen.getText().trim().isEmpty() || 
            tfSdt.getText().trim().isEmpty() || 
            tfUsername.getText().trim().isEmpty()) {
            
            showAlert("Thiếu thông tin", "Vui lòng nhập đầy đủ Họ tên, SĐT và Tên đăng nhập!");
            return false;
        }
        return true;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}