package es.iesgrancapitan.proyectohotelfx.controllers;

import com.google.gson.Gson;
import es.iesgrancapitan.proyectohotelfx.Cliente;
import es.iesgrancapitan.proyectohotelfx.Habitacion;
import es.iesgrancapitan.proyectohotelfx.ReportGenerator;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class HospedarViewController {
    @FXML
    private TextField tfNHabHos;
    @FXML
    private ComboBox<Cliente> cbClientes;
    @FXML
    private TextField tfTotal;
    @FXML
    private DatePicker dateFE;
    @FXML
    private DatePicker dateFS;
    @FXML
    private TextField tfObservacion;
    @FXML
    private ListView<Cliente> lvClientes;
    @FXML
    private Button btnAddUser;
    @FXML
    private ComboBox<String> cbMetodoPago;
    @FXML
    private Label faltanCampos;
    @FXML
    private Button btnUndoUser;
    private AplicacionController aplicacionController;

    private List<Cliente> selectedClients = new ArrayList<>();

    private Habitacion habitacion;

    /**
     * Inicializa la vista.
     */
    public void initialize() {

        ObservableList<String> metodosPago = FXCollections.observableArrayList("Metalico", "Tarjeta de credito", "Transferencia bancaria");
        cbMetodoPago.setItems(metodosPago);

        btnAddUser.setOnAction(event -> {
            Cliente selectedClient = cbClientes.getSelectionModel().getSelectedItem();
            if (selectedClient != null && !selectedClients.contains(selectedClient)) {
                selectedClients.add(selectedClient);
                lvClientes.setItems(FXCollections.observableArrayList(selectedClients));
            }
        });

        cbClientes.setOnShowing(event -> {
            if (aplicacionController != null) {
                aplicacionController.actualizarClientes();
            }
        });

        tfObservacion.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.length() > 255) {
                    tfObservacion.setText(oldValue);
                }
            }
        });
        // Restricción para dateFE
        dateFE.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();

                setDisable(empty || date.compareTo(today) < 0);
            }
        });
        // Restricción para dateFS
        dateFS.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();

                setDisable(empty || date.compareTo(today) < 0);
            }
        });

        btnUndoUser.setOnAction(event -> {
            if (!selectedClients.isEmpty()) {
                selectedClients.remove(selectedClients.size() - 1);
                lvClientes.setItems(FXCollections.observableArrayList(selectedClients));
            }
        });
    }

    /**
     * Evento que se ejecuta al hacer clic en el botón de hospedar.
     * Realiza una solicitud POST al servidor para insertar una reserva.
     * Si la inserción fue exitosa, muestra una alerta de éxito y cierra la ventana.
     * Si la inserción falló, muestra una alerta de error.
     * Si no se rellenan todos los campos, muestra una alerta de que faltan campos.
     */
    @FXML
    private void onBtnHospedarHosClick() {
        // Recoger los datos de los campos
        String nhabitacion = tfNHabHos.getText();
        List<Cliente> clientes = lvClientes.getItems();
        String fechaentrada = dateFE.getValue() != null ? dateFE.getValue().toString() : null;
        String fechasalida = dateFS.getValue() != null ? dateFS.getValue().toString() : null;
        float pago = tfTotal.getText() != null && !tfTotal.getText().isEmpty() ? Float.parseFloat(tfTotal.getText().replace(" €", "").replace(",", ".")) : 0;
        String metodopago = cbMetodoPago.getValue();
        String observaciones = tfObservacion.getText();

        // Verificar si todos los campos están rellenos
        if (nhabitacion.isEmpty() || clientes.isEmpty() || fechaentrada == null || fechasalida == null || metodopago == null || observaciones.isEmpty()) {
            faltanCampos.setOpacity(1);
        } else {
            System.out.println("Entra dentro del if");
            // Crear una lista de NIFs a partir de la lista de clientes
            List<String> nifs = new ArrayList<>();
            for (Cliente cliente : clientes) {
                nifs.add(cliente.getNIF());
            }

            // Realizar la solicitud POST
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://" + LoginController.IP + "/hotel/insertar-reserva.php"))
                    .POST(HttpRequest.BodyPublishers.ofString("nhabitacion=" + nhabitacion + "&clientes=" + new Gson().toJson(nifs) + "&fechaentrada=" + fechaentrada + "&fechasalida=" + fechasalida + "&pago=" + Float.toString(pago) + "&metodopago=" + metodopago + "&observaciones=" + observaciones + "&token_auth=0de6e41e85570bfcf0afc59179b6f480"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .build();
            System.out.println("Despues del request");

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(response -> {
                        System.out.println("Respuesta del servidor: " + response);
                        System.out.println("Antes de parsear");
                        // Parsear la respuesta JSON
                        JSONObject jsonResponse = new JSONObject(response);
                        System.out.println("Antes de if success");
                        // Si la inserción fue exitosa
                        if (jsonResponse.getString("status").equals("success")) {
                            // Obtener el ID de la reserva
                            int idReserva = jsonResponse.getInt("idReserva");

                            // Mostrar una alerta de éxito
                            Platform.runLater(() -> {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Éxito");
                                alert.setHeaderText(null);
                                alert.setContentText("Se ha insertado con éxito");
                                alert.showAndWait();

                                // Cerrar la ventana actual
                                Stage stage = (Stage) tfNHabHos.getScene().getWindow();
                                stage.close();

                                // Actualizar las reservas y las habitaciones
                                aplicacionController.actualizarReservas();
                                aplicacionController.actualizarHabitaciones();
                            });
                            // Generar el informe
                            ReportGenerator reportGenerator = new ReportGenerator();
                            reportGenerator.generateReport(idReserva);
                        }else{
                            System.out.println("Ha ocurrido un error");
                            // Mostrar una alerta de error
                            Platform.runLater(() -> {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Error");
                                alert.setHeaderText(null);
                                alert.setContentText("Ha ocurrido un error");
                                alert.showAndWait();
                            });
                        }
                    });
        }
    }

    /**
     * Establece la habitación y los clientes en la vista.
     * @param habitacion
     * @param clientes
     */
    public void setHabitacion(Habitacion habitacion, List<Cliente> clientes) {
        this.habitacion = habitacion;

        tfNHabHos.setText(String.valueOf(habitacion.getNumero()));
        cbClientes.setItems(FXCollections.observableArrayList(clientes));

        // Realizar la solicitud POST para obtener las fechas de reserva de la habitación
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://" + LoginController.IP + "/hotel/obtener-info.php"))
                .POST(HttpRequest.BodyPublishers.ofString("token_auth=0de6e41e85570bfcf0afc59179b6f480&tiposPeticion=infoReservas"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(response -> {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray infoReservas = jsonResponse.getJSONArray("infoReservas");

                    List<LocalDate> reservedDates = new ArrayList<>();
                    for (int i = 0; i < infoReservas.length(); i++) {
                        JSONObject reserva = infoReservas.getJSONObject(i);
                        if (reserva.getInt("nhabitacion") == habitacion.getNumero()) {
                            LocalDate fechaEntrada = LocalDate.parse(reserva.getString("fechaentrada"));
                            LocalDate fechaSalida = LocalDate.parse(reserva.getString("fechasalida"));
                            while (!fechaEntrada.isAfter(fechaSalida)) {
                                reservedDates.add(fechaEntrada);
                                fechaEntrada = fechaEntrada.plusDays(1);
                            }
                        }
                    }

                    // Restricción para dateFE y dateFS
                    dateFE.setDayCellFactory(createDayCellFactory(reservedDates));
                    dateFS.setDayCellFactory(createDayCellFactory(reservedDates));
                });

        dateFE.setValue(LocalDate.now());

        dateFE.valueProperty().addListener((observable, oldValue, newValue) -> calculateTotal());
        dateFS.valueProperty().addListener((observable, oldValue, newValue) -> calculateTotal());
    }

    /**
     * Crea un DayCellFactory para deshabilitar las fechas reservadas.
     * @param reservedDates
     * @return
     */
    private Callback<DatePicker, DateCell> createDayCellFactory(List<LocalDate> reservedDates) {
        return new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item != null && !empty) {
                            setDisable(item.isBefore(LocalDate.now()) || reservedDates.contains(item));
                        }
                    }
                };
            }
        };
    }

    /**
     * Calcula el total de la reserva.
     */
    private void calculateTotal() {
        LocalDate entrada = dateFE.getValue();
        LocalDate salida = dateFS.getValue();

        if (entrada != null && salida != null && !entrada.isAfter(salida)) {
            long days = ChronoUnit.DAYS.between(entrada, salida);
            double total = days * habitacion.getPrecio();
            tfTotal.setText(String.format("%.2f €", total));
        } else {
            tfTotal.clear();
        }
    }

    public void setAplicacionController(AplicacionController aplicacionController) {
        this.aplicacionController = aplicacionController;
    }
}