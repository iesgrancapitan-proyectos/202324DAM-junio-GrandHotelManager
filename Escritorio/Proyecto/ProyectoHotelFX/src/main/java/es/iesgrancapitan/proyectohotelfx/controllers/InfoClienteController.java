package es.iesgrancapitan.proyectohotelfx.controllers;

import es.iesgrancapitan.proyectohotelfx.Cliente;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Controlador de la ventana de información de un cliente.
 */
public class InfoClienteController {
    private AplicacionController aplicacionController;
    private Stage stage;
    @FXML
    private TextField tfNIF;
    @FXML
    private TextField tfTipoDocumento;
    @FXML
    private TextField tfNombre;
    @FXML
    private TextField tfApellido1;
    @FXML
    private TextField tfApellido2;
    @FXML
    private ComboBox<String> cbSexo;
    @FXML
    private DatePicker dpNacimiento;
    @FXML
    private TextField tfNacionalidad;
    @FXML
    private Label faltanCampos;

    /**
     * Inicializa el ComboBox de sexo con las opciones H, M y Otro.
     */
    @FXML
    public void initialize() {
        ObservableList<String> opcionesSexo = FXCollections.observableArrayList("H", "M", "Otro");
        cbSexo.setItems(opcionesSexo);
    }


    /**
     * Establece los datos del cliente en los campos de texto.
     * @param cliente
     */
    public void setCliente(Cliente cliente) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        tfNIF.setText(cliente.getNIF());
        tfTipoDocumento.setText(cliente.getTipodocumento());
        tfNombre.setText(cliente.getNombre());
        tfApellido1.setText(cliente.getApellido1());
        tfApellido2.setText(cliente.getApellido2());
        cbSexo.setValue(cliente.getSexo());
        dpNacimiento.setValue(LocalDate.parse(cliente.getNacimiento(), formatter));
        tfNacionalidad.setText(cliente.getNacionalidad());
    }

    /**
     * Actualiza los datos del cliente en la base de datos.
     * Se envía una petición POST al servidor con los datos del cliente.
     * Si la petición se realiza correctamente, se cierra la ventana.
     * Si no, se muestra un mensaje de error.
     * Si faltan campos por rellenar, se muestra un mensaje de error.
     */
    @FXML
    private void onBtnActualizarClick() {
        HttpClient client = HttpClient.newHttpClient();
        String nif = tfNIF.getText();
        String nombre = tfNombre.getText();
        String tipodocumento = tfTipoDocumento.getText();
        String apellido1 = tfApellido1.getText();
        String apellido2 = tfApellido2.getText();
        String sexo = cbSexo.getValue();
        String nacimiento = dpNacimiento.getValue() != null ? dpNacimiento.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : null;
        String nacionalidad = tfNacionalidad.getText();
        String token_auth = "0de6e41e85570bfcf0afc59179b6f480";

        if (nif.isEmpty() || nombre.isEmpty() || tipodocumento.isEmpty() || apellido1.isEmpty() || apellido2.isEmpty() || sexo == null || nacimiento == null || nacionalidad.isEmpty()) {
            faltanCampos.setOpacity(1);
            return;
        }

        String body = "NIF=" + nif + "&nombre=" + nombre + "&tipodocumento=" + tipodocumento + "&apellido1=" + apellido1 + "&apellido2=" + apellido2 + "&sexo=" + sexo + "&nacimiento=" + nacimiento + "&nacionalidad=" + nacionalidad + "&token_auth=" + token_auth;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://" + LoginController.IP + "/hotel/actualizar-cliente.php"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(response -> {
                    Platform.runLater(() -> {
                        Alert alert;
                        if (response.equals("La actualización se ha realizado correctamente")) {
                            alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Actualización exitosa");
                            alert.setHeaderText(null);
                            alert.setContentText("La actualización se ha realizado correctamente");
                            aplicacionController.actualizarClientes();
                            if (stage != null) {
                                stage.close();
                            }
                        } else {
                            alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText(null);
                            alert.setContentText("No se ha podido realizar la actualización");
                        }
                        alert.showAndWait();
                    });
                })
                .join();
    }

    /**
     * Borra el cliente de la base de datos.
     */
    @FXML
    private void onBtnBorrarClienteClick() {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmación de borrado");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("¿Estás seguro de que deseas eliminar el cliente?");

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // El usuario ha confirmado la eliminación del cliente

            String nifSeleccionado = tfNIF.getText();

            HttpClient client = HttpClient.newHttpClient();
            String token_auth = "0de6e41e85570bfcf0afc59179b6f480";

            String body = "NIF=" + nifSeleccionado + "&token_auth=" + token_auth;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://" + LoginController.IP + "/hotel/borrar-cliente.php"))
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
                                aplicacionController.actualizarClientes();
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