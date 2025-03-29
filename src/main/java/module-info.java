module com.example.skaiciuotuvas {
    requires javafx.controls;
    requires javafx.fxml;

    exports com.example.skaiciuotuvas to javafx.graphics;
    opens com.example.skaiciuotuvas to javafx.fxml, javafx.base;

}