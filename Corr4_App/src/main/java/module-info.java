module com.css123group.corr4_app {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    opens com.css123group.corr4_app to javafx.fxml;
    exports com.css123group.corr4_app;
}