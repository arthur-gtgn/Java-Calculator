module com.arthue.calculator {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires java.scripting;

    opens com.arthue.calculator to javafx.fxml;
    exports com.arthue.calculator;
}