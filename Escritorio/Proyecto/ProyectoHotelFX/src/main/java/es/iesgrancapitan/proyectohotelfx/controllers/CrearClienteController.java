package es.iesgrancapitan.proyectohotelfx.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.format.DateTimeFormatter;

/**
 * Controlador de la ventana de creación de clientes.
 * Permite la inserción de un nuevo cliente en la base de datos.
 * Se encarga de enviar la petición al servidor y mostrar el resultado de la operación.
 */
public class CrearClienteController {
    private AplicacionController aplicacionController;
    private Stage stage;
    @FXML
    private TextField tfNIF, tfTipoDocumento, tfNombre, tfApellido1, tfApellido2, tfNacionalidad;
    @FXML
    private ComboBox<String> cbSexo;
    @FXML
    private DatePicker dpNacimiento;
    @FXML
    private Label faltanCampos;

    /**
     * Inicializa el ComboBox de sexo con los valores posibles.
     */
    public void initialize() {
        cbSexo.getItems().addAll("H", "M", "Otro");
    }

    /**
     * Evento que se ejecuta al pulsar el botón de crear cliente.
     * Realiza la petición al servidor para insertar un nuevo cliente en la base de datos.
     * Muestra un mensaje de éxito o error en función de la respuesta del servidor.
     * Si la operación es exitosa, cierra la ventana.
     * Si no se han rellenado todos los campos, muestra un mensaje de error.
     */
    @FXML
    private void onBtnCrearClienteClick() {
        String NIF = tfNIF.getText();
        String tipoDocumento = tfTipoDocumento.getText();
        String nombre = tfNombre.getText();
        String apellido1 = tfApellido1.getText();
        String apellido2 = tfApellido2.getText();
        String sexo = cbSexo.getValue();
        String nacimiento = dpNacimiento.getValue() != null ? dpNacimiento.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : null;
        String nacionalidad = tfNacionalidad.getText();
        String token_auth = "0de6e41e85570bfcf0afc59179b6f480";

        if (NIF.isEmpty() || tipoDocumento.isEmpty() || nombre.isEmpty() || apellido1.isEmpty() || apellido2.isEmpty() || sexo == null || nacimiento == null || nacionalidad.isEmpty()) {
            faltanCampos.setOpacity(1);
            return;
        }

        HttpClient client = HttpClient.newHttpClient();
        String body = "NIF=" + NIF + "&tipodocumento=" + tipoDocumento + "&nombre=" + nombre + "&apellido1=" + apellido1 + "&apellido2=" + apellido2 + "&sexo=" + sexo + "&nacimiento=" + nacimiento + "&nacionalidad=" + nacionalidad + "&token_auth=" + token_auth;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://" + LoginController.IP + "/hotel/insertar-cliente.php"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(response -> {
                    Platform.runLater(() -> {
                        Alert alert;
                        if (response.equals("Client inserted successfully.")) {
                            alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Inserción exitosa");
                            alert.setHeaderText(null);
                            alert.setContentText("Cliente insertado correctamente.");
                            aplicacionController.actualizarClientes();
                            stage.close();

                        } else {
                            alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText(null);
                            alert.setContentText("No se ha podido insertar el cliente");
                        }
                        alert.showAndWait();
                    });
                })
                .join();
    }

    /**
     * Establece la ventana principal de la aplicación.
     * @param aplicacionController
     */
    public void setAplicacionController(AplicacionController aplicacionController) {
        this.aplicacionController = aplicacionController;
    }
    /**
     * Establece la ventana principal de la aplicación.
     * @param stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }
}