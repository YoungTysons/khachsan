package com.example.qlykhsan;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller cho giao diện chính của Người Dùng.
 * Class này quản lý thanh menu bên trái và khu vực nội dung (contentArea)
 */
public class GiaoDienChinhNguoiDung {

    @FXML
    private AnchorPane contentArea; // Khu vực ở giữa màn hình (để chứa các view con)

    /**
     * Phương thức khởi tạo (tự động gọi sau khi FXML load xong)
     * Thường dùng để load màn hình mặc định khi ứng dụng mở ra.
     */
    @FXML
    public void initialize() {
        // Load màn hình mặc định khi giao diện chính được mở
        loadPage("/com/example/qlykhsan/NguoiDung/DatPhong.fxml");

    }

    // --- Các phương thức xử lý sự kiện Menu ---

    @FXML
    public void handleShowDatPhong(ActionEvent event) {
        // Giả định file FXML cho màn hình đặt phòng nằm trong thư mục 'nguoidung'
        loadPage("/com/example/qlykhsan/NguoiDung/DatPhong.fxml");

    }

    @FXML
    public void handleShowThongBao(ActionEvent event) {
        // Giả định file FXML cho màn hình thông báo nằm trong thư mục 'nguoidung'
        loadPage("/com/example/qlykhsan/NguoiDung/ThongBao.fxml");
    }

    @FXML
    public void handleShowGuiPhanAnh(ActionEvent event) {
        // Giả định file FXML cho màn hình gửi phản ánh nằm trong thư mục 'nguoidung'
        loadPage("/com/example/qlykhsan/NguoiDung/GuiPhanAnh.fxml");
    }

    @FXML
    public void handleDangXuat(ActionEvent event) {
        try {
            // 1. Ẩn/Đóng cửa sổ hiện tại
            ((Node)event.getSource()).getScene().getWindow().hide();

            // 2. Load màn hình đăng nhập (Giả sử file là hello-view.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/qlykhsan/hello-view.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Đăng nhập Hệ thống");
            stage.show();
        } catch (IOException e) {
            System.err.println("Lỗi khi chuyển đến màn hình Đăng nhập: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Hàm dùng chung để load giao diện con (FXML) vào khu vực nội dung chính.
     * @param fxmlFileName Đường dẫn tương đối đến file FXML (bắt đầu bằng /)
     */
    private void loadPage(String fxmlFileName) {
        try {
            var resource = getClass().getResource(fxmlFileName);

            // Kiểm tra kỹ nếu resource không tồn tại
            if (resource == null) {
                System.err.println("KHÔNG TÌM THẤY FILE FXML: " + fxmlFileName);
                System.err.println("Vui lòng kiểm tra lại đường dẫn và thư mục resources!");
                return;
            }

            System.out.println("Đang load file FXML tại: " + resource);
            // Sử dụng FXMLLoader để load view con
            Parent view = FXMLLoader.load(resource);

            // Xóa view cũ và thêm view mới vào contentArea
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