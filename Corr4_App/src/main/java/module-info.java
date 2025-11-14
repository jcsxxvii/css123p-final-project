module com.css123group.corr4_app {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires java.logging;
    requires java.sql;
    requires transitive javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    opens com.css123group.corr4_app to javafx.fxml;
    exports com.css123group.corr4_app;
}