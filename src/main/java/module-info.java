module com.example.sneakershop3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;
    requires java.desktop;
    requires layout;
    requires kernel;

    opens com.example.sneakershop3.Controllers to javafx.fxml;
    opens com.example.sneakershop3 to javafx.fxml;
    exports com.example.sneakershop3;
    exports com.example.sneakershop3.Controllers;
    exports com.example.sneakershop3.Models;
    opens com.example.sneakershop3.Models to javafx.base, javafx.fxml;

}