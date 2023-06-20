module com.example.certmanager {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.certmanager to javafx.fxml;
    exports com.example.certmanager;
}