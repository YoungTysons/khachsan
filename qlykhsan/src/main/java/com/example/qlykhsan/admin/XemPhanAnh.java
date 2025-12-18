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

    @FXML private TableView<PhanAnhModel> tablePhanAnh;
    @FXML private TableColumn<PhanAnhModel, String> colNguoiGui;
    @FXML private TableColumn<PhanAnhModel, String> colTieuDe;
    @FXML private TableColumn<PhanAnhModel, String> colNoiDung;
    @FXML private TableColumn<PhanAnhModel, String> colNgayGui;

    private ObservableList<PhanAnhModel> listPhanAnh;

    public static class PhanAnhModel {
        private String nguoiGui;
        private String tieuDe;
        private String noiDung;
        private String ngayGui;

        public PhanAnhModel(String nguoiGui, String tieuDe, String noiDung, String ngayGui) {
            this.nguoiGui = nguoiGui;
            this.tieuDe = tieuDe;
            this.noiDung = noiDung;
            this.ngayGui = ngayGui;
        }

        public String getNguoiGui() { return nguoiGui; }
        public String getTieuDe() { return tieuDe; }
        public String getNoiDung() { return noiDung; }
        public String getNgayGui() { return ngayGui; }
    }
    // ==========================================================

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colNguoiGui.setCellValueFactory(new PropertyValueFactory<>("nguoiGui"));
        colTieuDe.setCellValueFactory(new PropertyValueFactory<>("tieuDe"));
        colNoiDung.setCellValueFactory(new PropertyValueFactory<>("noiDung"));
        colNgayGui.setCellValueFactory(new PropertyValueFactory<>("ngayGui"));

        loadDataFromDB();
    }

    private void loadDataFromDB() {
        listPhanAnh = FXCollections.observableArrayList();
        
        String sql = "SELECT pa.TieuDe, pa.NoiDung, pa.NgayGui, nd.HoTen " +
                     "FROM PhanAnh pa " +
                     "JOIN NguoiDung nd ON pa.MaNguoiDung = nd.MaNguoiDung " +
                     "ORDER BY pa.NgayGui DESC"; // Mới nhất lên đầu

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            while (rs.next()) {
                String tenNguoi = rs.getString("HoTen");
                String tieuDe = rs.getString("TieuDe");
                String noiDung = rs.getString("NoiDung");
                
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