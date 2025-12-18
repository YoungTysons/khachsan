package com.example.qlykhsan.admin;

import com.example.qlykhsan.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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

    @FXML private TableView<KhachHang> tableKhach;
    @FXML private TableColumn<KhachHang, Integer> colMaKhach; // Cột mới thêm
    @FXML private TableColumn<KhachHang, String> colHoTen;
    @FXML private TableColumn<KhachHang, String> colSdt;
    @FXML private TableColumn<KhachHang, String> colUsername;

    @FXML private TextField tfHoTen;
    @FXML private TextField tfSdt;
    @FXML private TextField tfUsername;


    private ObservableList<KhachHang> listKhach;
    private int selectedMaNguoiDung = -1;


    public static class KhachHang {
        private int maNguoiDung;
        private String hoTen;
        private String sdt;
        private String tenDangNhap;

        public KhachHang(int maNguoiDung, String hoTen, String sdt, String tenDangNhap) {
            this.maNguoiDung = maNguoiDung;
            this.hoTen = hoTen;
            this.sdt = sdt;
            this.tenDangNhap = tenDangNhap;
        }

        public int getMaNguoiDung() { return maNguoiDung; }
        public String getHoTen() { return hoTen; }
        public String getSdt() { return sdt; }
        public String getTenDangNhap() { return tenDangNhap; }
    }
    // ==========================================================

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colMaKhach.setCellValueFactory(new PropertyValueFactory<>("maNguoiDung"));
        colHoTen.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        colSdt.setCellValueFactory(new PropertyValueFactory<>("sdt"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("tenDangNhap"));

        loadDataFromDB();

        tableKhach.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                fillForm(newVal);
            }
        });
    }

    private void loadDataFromDB() {
        listKhach = FXCollections.observableArrayList();
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
            showAlert("Lỗi", "Không thể tải dữ liệu: " + e.getMessage());
        }
    }

    @FXML
    void handleThem(ActionEvent event) {
        if (!validateForm()) return;

        String sql = "INSERT INTO NguoiDung (HoTen, SoDienThoai, TenDangNhap, MatKhau, VaiTro) VALUES (?, ?, ?, '123456', 'KhachHang')";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tfHoTen.getText().trim());
            pstmt.setString(2, tfSdt.getText().trim());
            pstmt.setString(3, tfUsername.getText().trim());

            pstmt.executeUpdate();
            
            showAlert("Thành công", "Đã thêm khách hàng mới!");
            loadDataFromDB();
            clearForm(null);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Thêm thất bại (Có thể tên đăng nhập đã tồn tại).");
        }
    }

    @FXML
    void handleSua(ActionEvent event) {
        if (selectedMaNguoiDung == -1) {
            showAlert("Cảnh báo", "Vui lòng chọn khách hàng cần sửa trên bảng!");
            return;
        }
        if (tfHoTen.getText().isEmpty() || tfSdt.getText().isEmpty()) {
            showAlert("Lỗi", "Họ tên và SĐT không được để trống!");
            return;
        }

        String sql = "UPDATE NguoiDung SET HoTen = ?, SoDienThoai = ? WHERE MaNguoiDung = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tfHoTen.getText().trim());
            pstmt.setString(2, tfSdt.getText().trim());
            pstmt.setInt(3, selectedMaNguoiDung);

            pstmt.executeUpdate();
            
            showAlert("Thành công", "Cập nhật thông tin thành công!");
            loadDataFromDB();
            clearForm(null);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Cập nhật thất bại: " + e.getMessage());
        }
    }

    @FXML
    void handleXoa(ActionEvent event) {
        if (selectedMaNguoiDung == -1) {
            showAlert("Cảnh báo", "Vui lòng chọn khách hàng cần xóa!");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText(null);
        alert.setContentText("Bạn có chắc muốn xóa khách hàng: " + tfUsername.getText() + "?\n(Hành động này không thể hoàn tác)");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String sql = "DELETE FROM NguoiDung WHERE MaNguoiDung = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, selectedMaNguoiDung);
                pstmt.executeUpdate();
                
                showAlert("Thành công", "Đã xóa khách hàng!");
                loadDataFromDB();
                clearForm(null);

            } catch (Exception e) {
                showAlert("Không thể xóa", "Khách hàng này đang có dữ liệu giao dịch (Đặt phòng/Hóa đơn).\nKhông thể xóa khỏi hệ thống.");
            }
        }
    }

    @FXML
    void handleResetMK(ActionEvent event) {
        if (selectedMaNguoiDung == -1) {
            showAlert("Cảnh báo", "Vui lòng chọn khách hàng cần Reset mật khẩu!");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận Reset Mật Khẩu");
        alert.setHeaderText(null);
        alert.setContentText("Mật khẩu của tài khoản [" + tfUsername.getText() + "] sẽ được đặt về mặc định '123456'.\nBạn có chắc không?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String sql = "UPDATE NguoiDung SET MatKhau = '123456' WHERE MaNguoiDung = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, selectedMaNguoiDung);
                pstmt.executeUpdate();
                
                showAlert("Thành công", "Mật khẩu đã được đặt lại thành '123456'!");

            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Lỗi", "Không thể reset mật khẩu.");
            }
        }
    }

    @FXML
    void clearForm(ActionEvent event) {
        tfHoTen.clear();
        tfSdt.clear();
        tfUsername.clear();
        tfUsername.setEditable(true); 
        selectedMaNguoiDung = -1;
        tableKhach.getSelectionModel().clearSelection();
    }


    private void fillForm(KhachHang k) {
        selectedMaNguoiDung = k.getMaNguoiDung();
        tfHoTen.setText(k.getHoTen());
        tfSdt.setText(k.getSdt());
        tfUsername.setText(k.getTenDangNhap());
        
        tfUsername.setEditable(false); 
    }

    private boolean validateForm() {
        if (tfHoTen.getText().trim().isEmpty() || 
            tfSdt.getText().trim().isEmpty() || 
            tfUsername.getText().trim().isEmpty()) {
            
            showAlert("Thiếu thông tin", "Vui lòng nhập đầy đủ: Họ tên, SĐT và Tên đăng nhập!");
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