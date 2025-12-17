package com.example.qlykhsan;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class GiaoDienChinhAdmin {

    @FXML
    private AnchorPane contentArea; // Khu vực ở giữa màn hình

    @FXML
    public void handleShowDashboard() {
        loadPage("/com/example/qlykhsan/admin/ThongKe.fxml");
    }

    @FXML
    public void handleShowQuanLyPhong() {
        loadPage("/com/example/qlykhsan/admin/QuanLyPhong.fxml");
    }

    @FXML
    public void handleShowQuanLyKhach() {
        loadPage("/com/example/qlykhsan/admin/QuanLyKhach.fxml");
    }

    @FXML
    public void handleShowHopDong() {
        loadPage("/com/example/qlykhsan/admin/HopDong.fxml");
    }

    @FXML
    public void handleShowThongBao() {
        loadPage("/com/example/qlykhsan/admin/ThongBao.fxml");
    }

    @FXML
    public void handleShowPhanAnh() {
        loadPage("/com/example/qlykhsan/admin/XemPhanAnh.fxml");
    }

    @FXML
    public void handleShowDuyetDatPhong() {
        loadPage("/com/example/qlykhsan/admin/DuyetDatPhong.fxml");
    }

    @FXML
    public void handleDangXuat(javafx.event.ActionEvent event) {
        try {
            // Ẩn/Đóng cửa sổ hiện tại
            ((Node)event.getSource()).getScene().getWindow().hide();

            // Load lại màn hình đăng nhập (giả sử tên là hello-view.fxml hoặc Login.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/qlykhsan/hello-view.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Đăng nhập");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Hàm dùng chung để load giao diện con
    private void loadPage(String fxmlFileName) {
        try {
            var resource = getClass().getResource(fxmlFileName);

            // Kiểm tra kỹ trước khi load
            if (resource == null) {
                System.err.println("KHÔNG TÌM THẤY FILE FXML: " + fxmlFileName);
                System.err.println("Kiểm tra lại thư mục src/main/resources/...");
                return; // Dừng lại để không bị crash
            }

            System.out.println("Đang tìm file tại: " + resource);
            Parent view = FXMLLoader.load(resource);

            // Xóa view cũ và thêm view mới
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);

            // Neo (Anchor) để view con giãn full màn hình cha
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);

        } catch (IOException e) {
            System.err.println("Lỗi khi load file FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }
}