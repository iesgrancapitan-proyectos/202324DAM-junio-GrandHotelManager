package es.iesgrancapitan.proyectohotelfx.controllers;

import es.iesgrancapitan.proyectohotelfx.Cliente;
import es.iesgrancapitan.proyectohotelfx.Empleado;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Controlador de la ventana de información de un empleado.

 */
public class InfoEmpleadoController {
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
     * Establece los datos del empleado en los campos de texto.
     * @param empleado
     */
    public void setEmpleado(Empleado empleado) {
        tfIdEmpleado.setText(String.valueOf(empleado.getIdempleado()));
        tfNIF.setText(empleado.getNIF());
        tfNombre.setText(empleado.getNombre());
        tfApellido1.setText(empleado.getApellido1());
        tfApellido2.setText(empleado.getApellido2());
        tfUsuario.setText(empleado.getUsuario());
    }

    /**
     * Actualiza los datos del empleado en la base de datos.
     * Se envía una petición HTTP al servidor para actualizar los datos del empleado.
     * Si la petición es correcta, se cierra la ventana y se actualiza la lista de empleados.
     * Si la petición no es correcta, se muestra un mensaje de error.
     */
    @FXML
    private void onBtnActualizarClick() {
        HttpClient client = HttpClient.newHttpClient();
        String idempleado = tfIdEmpleado.getText();
        String nif = tfNIF.getText();
        String nombre = tfNombre.getText();
        String apellido1 = tfApellido1.getText();
        String apellido2 = tfApellido2.getText();
        String usuario = tfUsuario.getText();
        String token_auth = "0de6e41e85570bfcf0afc59179b6f480";

        String body = "idempleado=" + idempleado +"&NIF=" + nif + "&nombre=" + nombre + "&apellido1=" + apellido1 + "&apellido2=" + apellido2 + "&usuario=" + usuario + "&token_auth=" + token_auth;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://" + LoginController.IP + "/hotel/actualizar-empleado.php"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(response -> {
                    JSONObject jsonResponse = new JSONObject(response);
                    Platform.runLater(() -> {
                        if (jsonResponse.getBoolean("success")) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Empleado actualizado");
                            alert.setHeaderText(null);
                            alert.setContentText("El empleado ha sido actualizado con éxito.");
                            alert.showAndWait();
                            aplicacionController.actualizarEmpleados();
                            stage.close();
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText(null);
                            alert.setContentText("No se pudo actualizar al empleado. Error: " + jsonResponse.getString("error"));
                            alert.showAndWait();
                        }
                    });
                });
    }

    /**
     * Borra el empleado de la base de datos.
     */
    @FXML
    private void onBtnBorrarEmpleadoClick() {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmación de borrado");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("¿Estás seguro de que deseas eliminar el empleado?");

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {

            String idSeleccionado = tfIdEmpleado.getText();

            HttpClient client = HttpClient.newHttpClient();
            String token_auth = "0de6e41e85570bfcf0afc59179b6f480";

            String body = "idempleado=" + idSeleccionado + "&token_auth=" + token_auth;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://" + LoginController.IP + "/hotel/borrar-empleado.php"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(response -> {
                        Platform.runLater(() -> {
                            Alert alert;
                            if (response.equals("El borrado se ha realizado correctamente")) {
                                alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Borrado exitoso");
                                alert.setHeaderText(null);
                                alert.setContentText("El borrado se ha realizado correctamente");
                                aplicacionController.actualizarEmpleados();
                                if (stage != null) {
                                    stage.close();
                                }
                            } else {
                                alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Error");
                                alert.setHeaderText(null);
                                alert.setContentText("No se ha podido realizar el borrado");
                            }
                            alert.showAndWait();
                        });
                    })
                    .join();
        }
    }

    public void setAplicacionController(AplicacionController aplicacionController) {
        this.aplicacionController = aplicacionController;
    }
    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
