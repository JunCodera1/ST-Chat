module me.chatapp.stchat {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.logging;
    requires java.sql;
    requires jakarta.persistence;
    requires org.postgresql.jdbc;
    requires annotations;
    requires java.desktop;

    opens me.chatapp.stchat to javafx.fxml;
    exports me.chatapp.stchat;
}