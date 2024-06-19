package es.iesgrancapitan.proyectohotelfx.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Controlador de la ventana de información de un pedido.
 */
public class InfoPedidoController {
    private int idPedido;
    private AplicacionController aplicacionController;
    private Stage stage;

    @FXML
    private TextField tfIDPedido;
    @FXML
    private TextField tfNHabitacion;
    @FXML
    private DatePicker dpFecha;
    @FXML
    private ListView<String> lvProductos;
    @FXML
    private TextField tfTotal;
    @FXML
    private TextField tfEstado;
    @FXML
    private TextField tfHora;
    @FXML
    private Button btnCompletar;

    /**
     * Establece el ID del pedido y carga la información del mismo.
     * @param idPedido
     */
    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://" + LoginController.IP + "/hotel/obtener-pedido.php"))
                .POST(HttpRequest.BodyPublishers.ofString("token_auth=0de6e41e85570bfcf0afc59179b6f480&idpedido=" + idPedido))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(response -> {
                    JSONObject jsonResponse = new JSONObject(response);
                    Platform.runLater(() -> {
                        tfIDPedido.setText(String.valueOf(jsonResponse.getInt("idpedido")));
                        tfNHabitacion.setText(String.valueOf(jsonResponse.getInt("nhabitacion")));
                        LocalDateTime dateTime = LocalDateTime.parse(jsonResponse.getString("fechapedido"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        dpFecha.setValue(dateTime.toLocalDate());
                        tfHora.setText(dateTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));  // Extraer y formatear la hora
                        tfTotal.setText(String.valueOf(jsonResponse.getDouble("total")));
                        tfEstado.setText(jsonResponse.getString("estado"));

                        JSONArray productosJson = jsonResponse.getJSONArray("productos");
                        ObservableList<String> productos = FXCollections.observableArrayList();
                        for (int i = 0; i < productosJson.length(); i++) {
                            JSONObject productoJson = productosJson.getJSONObject(i);
                            String producto = productoJson.getString("nombre") + " - " + productoJson.getString("descripcion") + " | " + productoJson.getBigDecimal("precio") + "€ | x" + productoJson.getInt("cantidad");
                            productos.add(producto);
                        }
                        tfEstado.setText(jsonResponse.getString("estado"));

                        if ("Completado".equals(jsonResponse.getString("estado"))) {
                            btnCompletar.setOpacity(0);
                            btnCompletar.setDisable(true);
                        }
                        lvProductos.setItems(productos);
                    });
                });
    }

    /**
     * Marca el pedido como completado.
     * Se envía una petición al servidor para marcar el pedido como completado.
     */
    @FXML
    private void completarPedido() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://" + LoginController.IP + "/hotel/completar-pedido.php"))
                .POST(HttpRequest.BodyPublishers.ofString("token_auth=0de6e41e85570bfcf0afc59179b6f480&idpedido=" + idPedido))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(response -> {
                    JSONObject jsonResponse = new JSONObject(response);
                    Platform.runLater(() -> {
                        if (jsonResponse.getBoolean("success")) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Pedido completado");
                            alert.setHeaderText(null);
                            alert.setContentText("El pedido ha sido completado con éxito.");
                            alert.showAndWait();
                            aplicacionController.actualizarPedidos();
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText(null);
                            alert.setContentText("No se pudo completar el pedido. Error: " + jsonResponse.getString("error"));
                            alert.showAndWait();
                        }


                        stage.close();
                    });
                });
    }

    /**
     * Borra el pedido.
     */
    @FXML
    private void onBorrarPedidoBtn() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación de borrado");
        alert.setHeaderText(null);
        alert.setContentText("¿Estás seguro de que quieres borrar este pedido?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://" + LoginController.IP + "/hotel/borrar-pedido.php"))
                    .POST(HttpRequest.BodyPublishers.ofString("token_auth=0de6e41e85570bfcf0afc59179b6f480&idpedido=" + idPedido))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(response -> {
                        JSONObject jsonResponse = new JSONObject(response);
                        Platform.runLater(() -> {
                            if (jsonResponse.getBoolean("success")) {
                                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                                successAlert.setTitle("Pedido borrado");
                                successAlert.setHeaderText(null);
                                successAlert.setContentText("El pedido ha sido borrado con éxito.");
                                successAlert.showAndWait();
                                aplicacionController.actualizarPedidos();
                            } else {
                                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                                errorAlert.setTitle("Error");
                                errorAlert.setHeaderText(null);
                                errorAlert.setContentText("No se pudo borrar el pedido. Error: " + jsonResponse.getString("error"));
                                errorAlert.showAndWait();
                            }

                            stage.close();
                        });
                    });
        }
    }


    public void setAplicacionController(AplicacionController aplicacionController) {
        this.aplicacionController = aplicacionController;
    }
    public void setStage(Stage stage) {
        this.stage = stage;
    }
}