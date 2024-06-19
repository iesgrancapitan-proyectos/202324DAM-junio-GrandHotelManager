package es.iesgrancapitan.proyectohotelfx.controllers;

import es.iesgrancapitan.proyectohotelfx.PasswordEncryptor;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import javafx.scene.control.TextField;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Controlador de la ventana de creación de usuarios.
 * Permite la creación de usuarios en la base de datos.
 * Se conecta con la base de datos a través de una petición HTTP.
 */
public class CrearUsuarioController {
    private AplicacionController aplicacionController;
    private Stage stage;
    @FXML
    private TextField tfUsuario;
    @FXML
    private PasswordField pfPass;
    @FXML
    private ComboBox<String> cbTipo;

    /**
     * Inicializa el ComboBox con los tipos de usuario.
     */
    public void initialize() {
        cbTipo.getItems().addAll("Administrador", "Empleado", "Cliente", "Limpieza");

    }

    /**
     * Método que se ejecuta al pulsar el botón de crear usuario.
     * Realiza una petición HTTP para insertar un usuario en la base de datos.
     */
    @FXML
    private void onBtnCrearUsuario(){
        String usuario = tfUsuario.getText();
        String password = pfPass.getText();
        String tipo = cbTipo.getValue();

        int tipoDB;
        switch (tipo) {
            case "Administrador":
                tipoDB = 0;
                break;
            case "Empleado":
                tipoDB = 1;
                break;
            case "Cliente":
                tipoDB = 2;
                break;
            case "Limpieza":
                tipoDB = 3;
                break;
            default:
                System.out.println("Tipo de usuario no reconocido");
                return;
        }

        String passwordHash = PasswordEncryptor.encrypt(password);
        if(passwordHash == null) {
            System.out.println("Error al encriptar la contraseña");
            return;
        }

        HttpClient client = HttpClient.newHttpClient();
        String body =  "usuario=" + usuario + "&password=" + passwordHash + "&tipo=" + tipoDB + "&token_auth=" + "0de6e41e85570bfcf0afc59179b6f480";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://" + LoginController.IP + "/hotel/insertar-usuario.php"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(response -> {
                    Platform.runLater(() -> {
                        Alert alert;
                        if (response.equals("Inserted.")) {
                            alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Inserción exitosa");
                            alert.setHeaderText(null);
                            alert.setContentText("Usuario insertado correctamente.");
                            stage.close();
                        } else {
                            alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText(null);
                            alert.setContentText("No se ha podido insertar el usuario");
                            alert.showAndWait();
                        }
                    });
                })
                .join();
    }

    public void setAplicacionController(AplicacionController aplicacionController) {
        this.aplicacionController = aplicacionController;
    }
    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
