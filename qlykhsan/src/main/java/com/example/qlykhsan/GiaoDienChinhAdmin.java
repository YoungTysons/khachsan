package com.example.qlykhsan;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;      // Thêm import Alert
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType; // Thêm import ButtonType
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;              // Thêm import Optional
import java.util.ResourceBundle;

public class GiaoDienChinhAdmin implements Initializable {

    @FXML
    private AnchorPane contentArea; // Khu vực hiển thị nội dung bên phải

    // Các Button menu
    @FXML private Button btnDashboard;
    @FXML private Button btnQuanLyPhong;
    @FXML private Button btnQuanLyKhach;
    @FXML private Button btnHopDong;
    @FXML private Button btnThongBao;
    @FXML private Button btnPhanAnh;
    @FXML private Button btnDuyetDatPhong;
    @FXML private Button Dangxuat;

    // --- Hàm khởi chạy đầu tiên ---
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Mặc định load trang Thống kê
        loadView("admin/ThongKe.fxml");
    }

    // --- CÁC HÀM XỬ LÝ SỰ KIỆN MENU ---
    
    @FXML
    void handleShowDashboard(ActionEvent event) {
        System.out.println("Hiển thị Dashboard");
        loadView("admin/ThongKe.fxml");
    }

    @FXML
    void handleShowQuanLyPhong(ActionEvent event) {
        System.out.println("Hiển thị Quản lý phòng");
        loadView("admin/QuanLyPhong.fxml");
    }

    @FXML
    void handleShowQuanLyKhach(ActionEvent event) {
        System.out.println("Hiển thị Quản lý khách");
        loadView("admin/QuanLyKhach.fxml");
    }

    @FXML
    void handleShowHopDong(ActionEvent event) {
        System.out.println("Hiển thị Hợp đồng");
        loadView("admin/HopDong.fxml");
    }

    @FXML
    void handleShowThongBao(ActionEvent event) {
        System.out.println("Hiển thị Thông báo");
        loadView("admin/ThongBao.fxml");
    }

    @FXML
    void handleShowPhanAnh(ActionEvent event) {
        System.out.println("Hiển thị Phản ánh");
        loadView("admin/XemPhanAnh.fxml");
    }

    @FXML
    void handleShowDuyetDatPhong(ActionEvent event) {
        System.out.println("Hiển thị Duyệt đặt phòng");
        loadView("admin/DuyetDatPhong.fxml");
    }

    // --- Hàm dùng chung để load view con ---
    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node node = loader.load();
            
            AnchorPane.setTopAnchor(node, 0.0);
            AnchorPane.setBottomAnchor(node, 0.0);
            AnchorPane.setLeftAnchor(node, 0.0);
            AnchorPane.setRightAnchor(node, 0.0);

            contentArea.getChildren().setAll(node);
            
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Không tìm thấy file FXML: " + fxmlPath);
        }
    }

    // --- XỬ LÝ ĐĂNG XUẤT CÓ XÁC NHẬN ---
    @FXML
    void handleDangXuat(ActionEvent event) {
        // 1. Tạo hộp thoại xác nhận
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận đăng xuất");
        alert.setHeaderText(null);
        alert.setContentText("Bạn có chắc chắn muốn đăng xuất không?");

        // 2. Hiện hộp thoại và chờ người dùng bấm nút
        Optional<ButtonType> result = alert.showAndWait();

        // 3. Nếu bấm OK thì mới thực hiện đăng xuất
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("DangNhap.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setTitle("Đăng Nhập Hệ Thống");
                stage.setScene(new Scene(root));
                stage.centerOnScreen();
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}