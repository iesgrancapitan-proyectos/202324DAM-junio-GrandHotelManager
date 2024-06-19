package es.iesgrancapitan.proyectohotelfx.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.json.JSONObject;

import javafx.scene.control.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Controlador de la ventana de creación de habitaciones.
 * Se encarga de enviar la petición al servidor para crear una habitación.
 */
public class CrearHabitacionController {
    private AplicacionController aplicacionController;
    private Stage stage;

    @FXML
    private TextField tfNHab;
    @FXML
    private TextField tfNPer;
    @FXML
    private TextField tfNCam;
    @FXML
    private TextField tfMC;
    @FXML
    private TextField tfPrecioNoche;

    /**
     * Método que se ejecuta al pulsar el botón de crear habitación.
     * Envia una petición al servidor para crear una habitación.
     */
    @FXML
    private void onCrearHabitacionBtn() {
        HttpClient client = HttpClient.newHttpClient();
        String requestBody = String.format("token_auth=0de6e41e85570bfcf0afc59179b6f480&tfNHab=%s&tfNPer=%s&tfNCam=%s&tfMC=%s&tfPrecioNoche=%s",
                tfNHab.getText(), tfNPer.getText(), tfNCam.getText(), tfMC.getText(), tfPrecioNoche.getText());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://" + LoginController.IP + "/hotel/insertar-habitacion.php"))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(response -> {
                    JSONObject jsonResponse = new JSONObject(response);
                    Platform.runLater(() -> {
                        if (jsonResponse.getBoolean("success")) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Habitación creada");
                            alert.setHeaderText(null);
                            alert.setContentText("La habitación ha sido creada con éxito.");
                            alert.showAndWait();
                            aplicacionController.actualizarHabitaciones();
                            stage.close();
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText(null);
                            alert.setContentText("No se pudo crear la habitación. Error: " + jsonResponse.getString("error"));
                            alert.showAndWait();
                        }
                    });
                });
    }

    public void setAplicacionController(AplicacionController aplicacionController) {
        this.aplicacionController = aplicacionController;
    }
    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
