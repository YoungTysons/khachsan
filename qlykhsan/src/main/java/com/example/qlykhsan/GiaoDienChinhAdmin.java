package com.example.qlykhsan;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;

public class GiaoDienChinhAdmin {

    @FXML
    private AnchorPane contentArea; // Khu vực ở giữa màn hình

    @FXML
    public void handleShowDashboard() {
        loadPage("dashboard_view.fxml"); // Tên file FXML của trang Dashboard
    }

    @FXML
    public void handleShowQuanLyPhong() {
        loadPage("quan_ly_phong.fxml"); // Tên file FXML của trang Quản lý phòng
    }

    // Hàm dùng chung để load giao diện con
    private void loadPage(String fxmlFileName) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlFileName));
            contentArea.getChildren().removeAll();
            contentArea.getChildren().setAll(view);

            // Nếu muốn view con full màn hình cha
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}