package com.example.qlykhsan;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class GiaoDienChinhNguoiDung implements Initializable {

    @FXML
    private AnchorPane contentArea; // Khu vực hiển thị nội dung bên phải (fx:id trong FXML)

    @FXML private Button btnDatPhong;
    @FXML private Button btnThongBao;
    @FXML private Button btnGuiPhanAnh;
    @FXML private Button btnDangXuat;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Mặc định khi vừa vào sẽ hiện trang Đặt Phòng
        loadView("DatPhong.fxml");
    }

    // --- CÁC HÀM XỬ LÝ SỰ KIỆN MENU ---

    @FXML
    void handleShowDatPhong(ActionEvent event) {
        System.out.println("Người dùng: Xem phòng trống");
        loadView("DatPhong.fxml");
    }

    @FXML
    void handleShowThongBao(ActionEvent event) {
        System.out.println("Người dùng: Xem thông báo");
        loadView("ThongBao.fxml");
    }

    @FXML
    void handleShowGuiPhanAnh(ActionEvent event) {
        System.out.println("Người dùng: Gửi phản ánh");
        loadView("GuiPhanAnh.fxml");
    }

    // --- HÀM LOAD VIEW DÙNG CHUNG ---
    private void loadView(String fxmlFileName) {
        try {
            // Đường dẫn tuyệt đối tính từ thư mục resources
            String path = "/com/example/qlykhsan/NguoiDung/" + fxmlFileName;
            URL resource = getClass().getResource(path);

            if (resource == null) {
                System.err.println("❌ KHÔNG TÌM THẤY FILE TẠI: " + path);
                return;
            }

            // Tạo loader và load node
            FXMLLoader loader = new FXMLLoader(resource);
            Node node = loader.load();

            // Neo các cạnh để view con giãn full vùng chứa
            AnchorPane.setTopAnchor(node, 0.0);
            AnchorPane.setBottomAnchor(node, 0.0);
            AnchorPane.setLeftAnchor(node, 0.0);
            AnchorPane.setRightAnchor(node, 0.0);

            contentArea.getChildren().setAll(node);
            System.out.println("✅ Đã hiển thị trang: " + fxmlFileName);

        } catch (IOException e) {
            System.err.println("❌ Lỗi khi load FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- XỬ LÝ ĐĂNG XUẤT ---
    @FXML
    void handleDangXuat(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận");
        alert.setHeaderText(null);
        alert.setContentText("Bạn có chắc chắn muốn đăng xuất?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("DangNhap.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Đăng Nhập");
                stage.centerOnScreen();
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}