package com.example.qlykhsan.admin;

import com.example.qlykhsan.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

public class DuyetDatPhong implements Initializable {

    @FXML private TableView<YeuCau> tableYeuCau;
    @FXML private TableColumn<YeuCau, String> colKhachHang;
    @FXML private TableColumn<YeuCau, String> colPhong;
    @FXML private TableColumn<YeuCau, String> colNgayDat;
    @FXML private TableColumn<YeuCau, Void> colThaoTac; 

    private ObservableList<YeuCau> listYeuCau;

    public static class YeuCau {
        private int maDatPhong;
        private String tenKhach;
        private String tenPhong;
        private String ngayDat;

        public YeuCau(int maDatPhong, String tenKhach, String tenPhong, String ngayDat) {
            this.maDatPhong = maDatPhong;
            this.tenKhach = tenKhach;
            this.tenPhong = tenPhong;
            this.ngayDat = ngayDat;
        }

        public int getMaDatPhong() { return maDatPhong; }
        public String getTenKhach() { return tenKhach; }
        public String getTenPhong() { return tenPhong; }
        public String getNgayDat() { return ngayDat; }
    }
    // ==========================================================

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colKhachHang.setCellValueFactory(new PropertyValueFactory<>("tenKhach"));
        colPhong.setCellValueFactory(new PropertyValueFactory<>("tenPhong"));
        colNgayDat.setCellValueFactory(new PropertyValueFactory<>("ngayDat"));

        setupButtonColumn();

        loadDataFromDB();
    }

    private void loadDataFromDB() {
        listYeuCau = FXCollections.observableArrayList();
        
        String sql = "SELECT dp.MaDatPhong, nd.HoTen, p.SoPhong, dp.NgayDat " +
                     "FROM DatPhong dp " +
                     "JOIN NguoiDung nd ON dp.MaNguoiDung = nd.MaNguoiDung " +
                     "JOIN Phong p ON dp.MaPhong = p.MaPhong " +
                     "WHERE dp.TrangThai = 'ChoDuyet'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            while (rs.next()) {
                String ngayDat = "";
                if (rs.getTimestamp("NgayDat") != null) {
                    ngayDat = sdf.format(rs.getTimestamp("NgayDat"));
                }

                listYeuCau.add(new YeuCau(
                        rs.getInt("MaDatPhong"),
                        rs.getString("HoTen"),
                        rs.getString("SoPhong"),
                        ngayDat
                ));
            }
            tableYeuCau.setItems(listYeuCau);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupButtonColumn() {
        Callback<TableColumn<YeuCau, Void>, TableCell<YeuCau, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<YeuCau, Void> call(final TableColumn<YeuCau, Void> param) {
                return new TableCell<>() {
                    private final Button btnDuyet = new Button("Duyệt");
                    private final Button btnHuy = new Button("Hủy");

                    {
                        btnDuyet.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold;");
                        btnHuy.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");

                        btnDuyet.setOnAction(event -> {
                            YeuCau data = getTableView().getItems().get(getIndex());
                            updateTrangThai(data.getMaDatPhong(), "DaDuyet");
                        });

                        btnHuy.setOnAction(event -> {
                            YeuCau data = getTableView().getItems().get(getIndex());
                            updateTrangThai(data.getMaDatPhong(), "DaHuy");
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox hBox = new HBox(10, btnDuyet, btnHuy);
                            setGraphic(hBox);
                        }
                    }
                };
            }
        };

        colThaoTac.setCellFactory(cellFactory);
    }

    private void updateTrangThai(int maDatPhong, String trangThaiMoi) {
        String sql = "UPDATE DatPhong SET TrangThai = ? WHERE MaDatPhong = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, trangThaiMoi);
            pstmt.setInt(2, maDatPhong);
            pstmt.executeUpdate();

            String msg = trangThaiMoi.equals("DaDuyet") ? "Đã duyệt yêu cầu!" : "Đã hủy yêu cầu!";
            showAlert("Thành công", msg);
            
            loadDataFromDB();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể cập nhật trạng thái.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}