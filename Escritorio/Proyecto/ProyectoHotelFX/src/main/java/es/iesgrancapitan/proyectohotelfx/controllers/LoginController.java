package es.iesgrancapitan.proyectohotelfx.controllers;

import es.iesgrancapitan.proyectohotelfx.AlertBox;
import es.iesgrancapitan.proyectohotelfx.Config;
import es.iesgrancapitan.proyectohotelfx.PasswordEncryptor;
import es.iesgrancapitan.proyectohotelfx.UserSession;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

/**
 * Controlador de la vista de inicio de sesión.
 */
public class LoginController {
    public static final String IP = Config.getServerIp();

    @FXML
    private Label errorLabel;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;


    /**
     * Método que se ejecuta al cargar la vista.
     * Se encarga de inicializar los campos de texto y los eventos de los campos de texto.
     */
    @FXML
    public void initialize() {
        usernameField.setOnMouseClicked(event -> errorLabel.setOpacity(0));
        passwordField.setOnMouseClicked(event -> errorLabel.setOpacity(0));
    }

    /**
     * Método que se ejecuta al hacer clic en el botón de inicio de sesión.
     * Se encarga de enviar una solicitud al servidor para iniciar sesión.
     * Si el inicio de sesión es exitoso, se guarda el token en la sesión y se abre la vista de la aplicación.
     * Si el inicio de sesión falla, se muestra un mensaje de error.
     */
    @FXML
    protected void onLoginButtonClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        String passwordHash = PasswordEncryptor.encrypt(password);
        if(passwordHash == null) {
            System.out.println("Error al encriptar la contraseña");
            return;
        }

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://" + IP + "/hotel/login.php"))
                .POST(HttpRequest.BodyPublishers.ofString("username=" + username + "&password=" + passwordHash))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();

        try {
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(response -> {
                        //System.out.println(response);
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status");
                        //System.out.println(status);
                        if ("SUCCESS".equals(status)) {
                            // Aquí puedes proceder con el inicio de sesión
                            System.out.println("Inicio de sesión exitoso");

                            // Guarda el token en la sesión
                            String token = jsonResponse.getString("token");
                            UserSession.setToken(token);

                            // Hacer una solicitud a usersession.php para obtener los datos de la sesión
                            HttpRequest sessionRequest = HttpRequest.newBuilder()
                                    .uri(URI.create("http://" + IP + "/hotel/usersession.php?token=" + UserSession.getToken()))
                                    .build();

                            client.sendAsync(sessionRequest, HttpResponse.BodyHandlers.ofString())
                                    .thenApply(HttpResponse::body)
                                    .thenAccept(sessionResponse -> {
                                        JSONObject sessionJson = new JSONObject(sessionResponse);
                                        String sessionUsername = sessionJson.getString("usuario");
                                        int userType = sessionJson.getInt("tipo");

                                        // Guarda el nombre de usuario y el tipo de usuario en la sesión
                                        UserSession.setUsername(sessionUsername);
                                        UserSession.setUserType(userType);
                                    });

                            Platform.runLater(() -> {
                                try {
                                    Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/es/iesgrancapitan/proyectohotelfx/userapp-view.fxml")));
                                    Scene scene = new Scene(root);
                                    Stage newStage = new Stage();
                                    newStage.setTitle("Gestión del GrandHotel");
                                    newStage.setMaximized(true);
                                    newStage.initStyle(StageStyle.DECORATED);
                                    newStage.setScene(scene);
                                    newStage.show();
                                    Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/hotel.jpg")));
                                    newStage.getIcons().add(icon);

                                    // Cerrar el Stage anterior
                                    Stage oldStage = (Stage) usernameField.getScene().getWindow();
                                    oldStage.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    AlertBox.show(Alert.AlertType.ERROR, "Error", "Ha ocurrido un error.", e);
                                }
                            });
                        } else {
                            errorLabel.setOpacity(1);
                            System.out.println("Inicio de sesión fallido");
                        }
                    })
                    .join();
        } catch (Exception e) {
            e.printStackTrace();
            AlertBox.show(Alert.AlertType.ERROR, "Error", "Error al intentar establecer conexión con el servidor.", e);
        }
    }

    /**
     * Método que se ejecuta al hacer clic en el botón de cerrar.
     */
    @FXML
    protected void closeLoginButton() {
        System.exit(0);
    }


}
