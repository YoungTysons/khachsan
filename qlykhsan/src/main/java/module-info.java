module com.example.qlykhsan {
    requires javafx.controls;
    requires javafx.fxml;

    // Các thư viện khác của bạn (nếu có)
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql; // <--- CẦN THÊM DÒNG NÀY
    requires com.microsoft.sqlserver.jdbc; // (Nếu dùng module)

    // Mở quyền cho gói chính (đã có sẵn)
    opens com.example.qlykhsan to javafx.fxml;
    exports com.example.qlykhsan;

    // --- THÊM 2 DÒNG NÀY ---
    // 1. Cho phép JavaFX đọc file FXML trong gói admin
    opens com.example.qlykhsan.admin to javafx.fxml;

    // 2. Cho phép các phần khác truy cập code trong gói admin
    exports com.example.qlykhsan.admin;
}