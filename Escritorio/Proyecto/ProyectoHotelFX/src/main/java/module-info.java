module es.iesgrancapitan.proyectohotelfx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.net.http;
    requires org.json;
    requires de.jensd.fx.glyphs.fontawesome;
    requires java.desktop;
    requires com.google.gson;
    requires jasperreports;
    requires java.sql;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;
    requires org.apache.httpcomponents.httpmime;
    requires tyrus.standalone.client;

    opens es.iesgrancapitan.proyectohotelfx to javafx.fxml;
    exports es.iesgrancapitan.proyectohotelfx;
    exports es.iesgrancapitan.proyectohotelfx.controllers;
    opens es.iesgrancapitan.proyectohotelfx.controllers to javafx.fxml;
}