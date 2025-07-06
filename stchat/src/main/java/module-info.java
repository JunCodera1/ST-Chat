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
    requires com.h2database;
    requires org.json;
    requires org.kordamp.ikonli.fontawesome;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;

    opens me.chatapp.stchat to javafx.fxml;
    exports me.chatapp.stchat;
    exports me.chatapp.stchat.api;
    exports me.chatapp.stchat.test;
    exports me.chatapp.stchat.model to com.fasterxml.jackson.databind;
    opens me.chatapp.stchat.test to javafx.fxml;
    opens me.chatapp.stchat.model to com.fasterxml.jackson.databind;
}