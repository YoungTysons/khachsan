package com.example.qlykhsan.admin;

import com.example.qlykhsan.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.net.URL;
import java.sql.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;
import java.util.ResourceBundle;

public class HopDong implements Initializable {

    // --- FXML UI ---
    @FXML private TableView<HopDongModel> tableHopDong;
    @FXML private TableColumn<HopDongModel, String> colKhachThue;
    @FXML private TableColumn<HopDongModel, String> colPhong;
    @FXML private TableColumn<HopDongModel, String> colNgayBD;
    @FXML private TableColumn<HopDongModel, String> colNgayKT;
    @FXML private TableColumn<HopDongModel, String> colTienCoc;

    @FXML private ComboBox<ComboItem> cbKhachThue;
    @FXML private ComboBox<ComboItem> cbPhong;
    @FXML private DatePicker dpNgayBD;
    @FXML private DatePicker dpNgayKT;
    @FXML private TextField tfTienCoc;
    @FXML private Button btnTaoHopDong;

    // --- BIẾN DÙNG CHUNG ---
    private ObservableList<HopDongModel> listHopDong;

    // ==========================================================
    // 1. CÁC CLASS MODEL (INNER CLASS)
    // ==========================================================
    
    // Class dùng cho TableView hiển thị
    public static class HopDongModel {
        private int maHopDong;
        private String tenKhach;
        private String tenPhong;
        private Date ngayBD;
        private Date ngayKT;
        private String tienCoc; // Lưu dạng chuỗi format tiền cho đẹp

        public HopDongModel(int maHopDong, String tenKhach, String tenPhong, Date ngayBD, Date ngayKT, String tienCoc) {
            this.maHopDong = maHopDong;
            this.tenKhach = tenKhach;
            this.tenPhong = tenPhong;
            this.ngayBD = ngayBD;
            this.ngayKT = ngayKT;
            this.tienCoc = tienCoc;
        }

        // Getters
        public String getTenKhach() { return tenKhach; }
        public String getTenPhong() { return tenPhong; }
        public Date getNgayBD() { return ngayBD; }
        public Date getNgayKT() { return ngayKT; }
        public String getTienCoc() { return tienCoc; }
    }

    // Class dùng cho ComboBox (Lưu Mã và Tên)
    public static class ComboItem {
        private int id;
        private String name;

        public ComboItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() { return id; }
        
        @Override
        public String toString() { return name; } // Để hiển thị tên trên ComboBox
    }
    // ==========================================================

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Cấu hình TableView
        colKhachThue.setCellValueFactory(new PropertyValueFactory<>("tenKhach"));
        colPhong.setCellValueFactory(new PropertyValueFactory<>("tenPhong"));
        colNgayBD.setCellValueFactory(new PropertyValueFactory<>("ngayBD"));
        colNgayKT.setCellValueFactory(new PropertyValueFactory<>("ngayKT"));
        colTienCoc.setCellValueFactory(new PropertyValueFactory<>("tienCoc"));

        // Load dữ liệu
        loadAllData();

        // Sự kiện nút
        btnTaoHopDong.setOnAction(e -> handleTaoHopDong());
    }

    // --- CÁC HÀM LOAD DỮ LIỆU ---

    private void loadAllData() {
        loadTableData();
        loadComboKhach();
        loadComboPhong();
    }

    private void loadTableData() {
        listHopDong = FXCollections.observableArrayList();
        // Query Join bảng: HopDong -> DatPhong -> NguoiDung & Phong
        String sql = "SELECT hd.MaHopDong, nd.HoTen, p.SoPhong, dp.NgayCheckIn, dp.NgayCheckOut, hd.NoiDungHopDong " +
                     "FROM HopDong hd " +
                     "JOIN DatPhong dp ON hd.MaDatPhong = dp.MaDatPhong " +
                     "JOIN NguoiDung nd ON dp.MaNguoiDung = nd.MaNguoiDung " +
                     "JOIN Phong p ON dp.MaPhong = p.MaPhong";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                // Xử lý lấy tiền cọc từ nội dung hợp đồng (giả sử mình lưu text)
                // Hoặc đơn giản hiển thị nội dung
                String noiDung = rs.getString("NoiDungHopDong");
                
                listHopDong.add(new HopDongModel(
                        rs.getInt("MaHopDong"),
                        rs.getString("HoTen"),
                        rs.getString("SoPhong"),
                        rs.getDate("NgayCheckIn"),
                        rs.getDate("NgayCheckOut"),
                        noiDung // Tạm thời hiển thị nội dung vào cột tiền cọc hoặc format sau
                ));
            }
            tableHopDong.setItems(listHopDong);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadComboKhach() {
        ObservableList<ComboItem> list = FXCollections.observableArrayList();
        String sql = "SELECT MaNguoiDung, HoTen FROM NguoiDung WHERE VaiTro = 'KhachHang'";
        try (Connection conn = DatabaseConnection.getConnection();
             ResultSet rs = conn.createStatement().executeQuery(sql)) {
            while (rs.next()) {
                list.add(new ComboItem(rs.getInt("MaNguoiDung"), rs.getString("HoTen")));
            }
            cbKhachThue.setItems(list);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadComboPhong() {
        ObservableList<ComboItem> list = FXCollections.observableArrayList();
        // Chỉ lấy phòng còn TRỐNG
        String sql = "SELECT MaPhong, SoPhong FROM Phong WHERE TrangThai = N'Trong'";
        try (Connection conn = DatabaseConnection.getConnection();
             ResultSet rs = conn.createStatement().executeQuery(sql)) {
            while (rs.next()) {
                list.add(new ComboItem(rs.getInt("MaPhong"), rs.getString("SoPhong")));
            }
            cbPhong.setItems(list);
        } catch (Exception e) { e.printStackTrace(); }
    }

    // --- LOGIC TẠO HỢP ĐỒNG (TRANSACTION) ---

    private void handleTaoHopDong() {
        if (!validateForm()) return;

        ComboItem khach = cbKhachThue.getValue();
        ComboItem phong = cbPhong.getValue();
        LocalDate ngayBD = dpNgayBD.getValue();
        LocalDate ngayKT = dpNgayKT.getValue();
        String tienCoc = tfTienCoc.getText();
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction

            // Bước 1: Tạo record trong bảng DatPhong (Trạng thái = HoanThanh vì đã làm hợp đồng)
            String sqlDatPhong = "INSERT INTO DatPhong (MaNguoiDung, MaPhong, NgayCheckIn, NgayCheckOut, TrangThai) VALUES (?, ?, ?, ?, N'HoanThanh')";
            PreparedStatement pst1 = conn.prepareStatement(sqlDatPhong, Statement.RETURN_GENERATED_KEYS);
            pst1.setInt(1, khach.getId());
            pst1.setInt(2, phong.getId());
            pst1.setDate(3, java.sql.Date.valueOf(ngayBD));
            pst1.setDate(4, java.sql.Date.valueOf(ngayKT));
            pst1.executeUpdate();

            // Lấy ID DatPhong vừa tạo
            ResultSet rsKeys = pst1.getGeneratedKeys();
            int maDatPhongMoi = 0;
            if (rsKeys.next()) maDatPhongMoi = rsKeys.getInt(1);

            // Bước 2: Tạo record trong bảng HopDong
            String noiDungHD = "Tiền cọc: " + tienCoc + " VNĐ. Hợp đồng thuê phòng dài hạn.";
            String sqlHopDong = "INSERT INTO HopDong (MaDatPhong, NoiDungHopDong) VALUES (?, ?)";
            PreparedStatement pst2 = conn.prepareStatement(sqlHopDong);
            pst2.setInt(1, maDatPhongMoi);
            pst2.setString(2, noiDungHD);
            pst2.executeUpdate();

            // Bước 3: Cập nhật trạng thái Phong -> 'DaDat'
            String sqlUpdatePhong = "UPDATE Phong SET TrangThai = N'DaDat' WHERE MaPhong = ?";
            PreparedStatement pst3 = conn.prepareStatement(sqlUpdatePhong);
            pst3.setInt(1, phong.getId());
            pst3.executeUpdate();

            // Commit Transaction
            conn.commit();
            
            showAlert("Thành công", "Tạo hợp đồng thành công!");
            clearForm();
            loadAllData(); // Load lại bảng và list phòng trống

        } catch (Exception e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            showAlert("Lỗi", "Không thể tạo hợp đồng: " + e.getMessage());
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); conn.close(); } catch (SQLException e) {}
        }
    }

    // --- HÀM PHỤ ---

    private void clearForm() {
        cbKhachThue.getSelectionModel().clearSelection();
        cbPhong.getSelectionModel().clearSelection();
        dpNgayBD.setValue(null);
        dpNgayKT.setValue(null);
        tfTienCoc.clear();
    }

    private boolean validateForm() {
        if (cbKhachThue.getValue() == null || cbPhong.getValue() == null || 
            dpNgayBD.getValue() == null || dpNgayKT.getValue() == null || tfTienCoc.getText().isEmpty()) {
            showAlert("Thiếu thông tin", "Vui lòng nhập đầy đủ các trường!");
            return false;
        }
        if (dpNgayKT.getValue().isBefore(dpNgayBD.getValue())) {
            showAlert("Lỗi ngày tháng", "Ngày kết thúc phải sau ngày bắt đầu!");
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