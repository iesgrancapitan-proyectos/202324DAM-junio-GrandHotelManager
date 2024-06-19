package es.iesgrancapitan.proyectohotelfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;

/**
 Nombre del Proyecto: Grand Hotel Manager
 Última Modificación: 15/06/2024
 *
 @author: Antonio Castro Gómez y Hugo Salamanca Nuñez
 @version: 1.0
 */

/**
 * Clase principal de la aplicación.
 * Se encarga de cargar la vista de inicio de sesión.
 */
public class LoginApplication extends Application {

    private double xOffset = 0;
    private double yOffset = 0;
    @Override
    public void start(Stage stage) throws IOException {
        System.out.println("Java version: " + System.getProperty("java.version"));
        FXMLLoader fxmlLoader = new FXMLLoader(LoginApplication.class.getResource("login-view.fxml"));


        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root, 579.2, 406);

        stage.initStyle(StageStyle.UNDECORATED);

        root.setOnMousePressed(mouseEvent -> {
            xOffset = mouseEvent.getSceneX();
            yOffset = mouseEvent.getSceneY();
        });

        root.setOnMouseDragged(mouseEvent -> {
            stage.setX(mouseEvent.getScreenX() - xOffset);
            stage.setY(mouseEvent.getScreenY() - yOffset);
        });

        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/hotel.jpg")));
        stage.getIcons().add(icon);
        stage.setTitle("Login");
        stage.setResizable(false);
        stage.setScene(scene);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}