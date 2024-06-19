package es.iesgrancapitan.proyectohotelfx.controllers;

import es.iesgrancapitan.proyectohotelfx.Config;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.format.DateTimeFormatter;

/**
 * Clase controlador de la vista CrearEmpleadoView.
 * Permite la creación de un nuevo empleado en la base de datos.
 * Se envía una petición POST al servidor con los datos del empleado a insertar.
 * Si la inserción es exitosa, se cierra la ventana y se actualiza la tabla de empleados.
 * Si no, se muestra un mensaje de error.
 */
public class CrearEmpleadoController {
    String serverIp = Config.getServerIp();
    private AplicacionController aplicacionController;
    private Stage stage;
    @FXML
    private TextField tfIdEmpleado;
    @FXML
    private TextField tfNIF;
    @FXML
    private TextField tfNombre;
    @FXML
    private TextField tfApellido1;
    @FXML
    private TextField tfApellido2;
    @FXML
    private TextField tfUsuario;
    @FXML
    private Label faltanCampos;

    /**
     * Método que se ejecuta al pulsar el botón "Crear empleado".
     * Se recogen los datos de los campos de texto y se envían al servidor.
     * Si la inserción es exitosa, se cierra la ventana y se actualiza la tabla de empleados.
     * Si no, se muestra un mensaje de error.
     */
    @FXML
    private void onBtnCrearEmpleado() {
        String NIF = tfNIF.getText();
        String nombre = tfNombre.getText();
        String apellido1 = tfApellido1.getText();
        String apellido2 = tfApellido2.getText();
        String usuario = tfUsuario.getText();
        String token_auth = "0de6e41e85570bfcf0afc59179b6f480";

        // Check if all fields are filled
        if (NIF.isEmpty() || nombre.isEmpty() || apellido1.isEmpty() || apellido2.isEmpty()) {
            faltanCampos.setOpacity(1);
            return;
        }

        HttpClient client = HttpClient.newHttpClient();
        String body =  "NIF=" + NIF + "&nombre=" + nombre + "&apellido1=" + apellido1 + "&apellido2=" + apellido2 + "&usuario=" + usuario + "&token_auth=" + token_auth;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://" + LoginController.IP + "/hotel/insertar-empleado.php"))
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
                            alert.setContentText("Empleado insertado correctamente.");
                            aplicacionController.actualizarEmpleados();
                            stage.close();

                        } else {
                            alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText(null);
                            alert.setContentText("No se ha podido insertar el empleado");
                        }
                        alert.showAndWait();
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
