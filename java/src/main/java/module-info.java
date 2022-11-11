module com.example.hack {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;


    opens com.example.hack to javafx.fxml;
    exports com.example.hack;
}