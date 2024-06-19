package es.iesgrancapitan.proyectohotelfx.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import org.json.JSONArray;
import org.json.JSONObject;

import javafx.scene.control.TextField;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Controlador de la vista de información de una reserva.
 */
public class InfoDeReservaController {
    @FXML
    private TextField tfIDReserva, tfNHabitacion, tfTotal, tfMetodo, tfNDias;
    @FXML
    private DatePicker dpFecha, dpEntrada, dpSalida;
    @FXML
    private ListView<String> lvClientes;
    @FXML
    private TextArea taObservacion;

    private Integer idReserva;

    /**
     * Establece el ID de la reserva y obtiene la información de la misma.
     * @param idReserva
     */
    public void setIdReserva(Integer idReserva) {
        this.idReserva = idReserva;
        obtenerInfoReserva();
    }

    /**
     * Obtiene la información de la reserva.
     * Se realiza una petición HTTP al servidor para obtener la información de la reserva.
     */
    private void obtenerInfoReserva() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://" + LoginController.IP + "/hotel/info-reserva.php"))
                .POST(HttpRequest.BodyPublishers.ofString("token_auth=0de6e41e85570bfcf0afc59179b6f480&idReserva=" + idReserva))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(response -> {
                    JSONArray jsonResponse = new JSONArray(response);

                    Platform.runLater(() -> {
                        for (int i = 0; i < jsonResponse.length(); i++) {
                            JSONObject reserva = jsonResponse.getJSONObject(i);

                            if (i == 0) {
                                tfIDReserva.setText(String.valueOf(reserva.getInt("idreserva")));
                                tfNHabitacion.setText(String.valueOf(reserva.getInt("nhabitacion")));
                                tfNDias.setText(String.valueOf(reserva.getInt("dias")));
                                dpFecha.setValue(LocalDate.parse(reserva.getString("fecha"), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                                dpEntrada.setValue(LocalDate.parse(reserva.getString("fechaentrada"), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                                dpSalida.setValue(LocalDate.parse(reserva.getString("fechasalida"), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                                tfTotal.setText(String.valueOf(reserva.getFloat("pago")));
                                tfMetodo.setText(reserva.getString("metodopago"));
                                taObservacion.setText(reserva.getString("observaciones"));
                            }

                            String cliente = reserva.getString("NIF") + " | " + reserva.getString("nombre") + " " + reserva.getString("apellido1") + " " + reserva.getString("apellido2");
                            lvClientes.getItems().add(cliente);
                        }
                    });
                });
    }
}