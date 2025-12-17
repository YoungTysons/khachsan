module com.example.qlykhsan {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires com.microsoft.sqlserver.jdbc; // Đảm bảo dòng này khớp với thư viện bạn dùng

    opens com.example.qlykhsan to javafx.fxml;
    exports com.example.qlykhsan;

    opens com.example.qlykhsan.admin to javafx.fxml;
    exports com.example.qlykhsan.admin;

    // Phải viết hoa đúng chữ N và D theo thư mục NguoiDung của bạn
    opens com.example.qlykhsan.NguoiDung to javafx.fxml;
    exports com.example.qlykhsan.NguoiDung;
}