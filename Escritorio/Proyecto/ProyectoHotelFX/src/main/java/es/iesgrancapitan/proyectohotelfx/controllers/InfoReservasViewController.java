package es.iesgrancapitan.proyectohotelfx.controllers;

import es.iesgrancapitan.proyectohotelfx.Reserva;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controlador de la vista InfoReservasView.

 */
public class InfoReservasViewController {
    @FXML
    private TextField tfNHabRes;
    @FXML
    private TableView<Reserva> tvReservas;
    @FXML
    private ComboBox<Integer> cbReservas;

    @FXML
    private TableColumn<Reserva, Integer> idReservaColumn;
    @FXML
    private TableColumn<Reserva, String> fechaEntradaColumn;
    @FXML
    private TableColumn<Reserva, String> fechaSalidaColumn;
    @FXML
    private TableColumn<Reserva, Double> pagoColumn;
    @FXML
    private TableColumn<Reserva, String> metodoPagoColumn;
    @FXML
    private TableColumn<Reserva, String> observacionesColumn;
    @FXML
    private Button btnDescargarFactura;
    @FXML
    private Button btnBorrarReserva;

    private AplicacionController aplicacionController;

    /**
     * Inicializa la vista.
     */
    public void initialize() {
        idReservaColumn.setCellValueFactory(new PropertyValueFactory<>("idReserva"));
        fechaEntradaColumn.setCellValueFactory(new PropertyValueFactory<>("fechaEntrada"));
        fechaSalidaColumn.setCellValueFactory(new PropertyValueFactory<>("fechaSalida"));
        pagoColumn.setCellValueFactory(new PropertyValueFactory<>("pago"));
        metodoPagoColumn.setCellValueFactory(new PropertyValueFactory<>("metodopago"));
        observacionesColumn.setCellValueFactory(new PropertyValueFactory<>("observaciones"));
        btnDescargarFactura.setOnAction(event -> {
            int idReserva = cbReservas.getValue();

            try {
                // Crear un cliente HTTP
                HttpClient client = HttpClient.newHttpClient();

                // Crear una solicitud HTTP a la API
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://" + LoginController.IP + "/hotel/descargar-factura.php"))
                        .POST(HttpRequest.BodyPublishers.ofString("token_auth=0de6e41e85570bfcf0afc59179b6f480&idReserva=" + idReserva))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .build();

                // Enviar la solicitud y obtener la respuesta
                HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

                // Asegurarse de que la carpeta 'facturas' exista
                File directory = new File("facturas");
                if (! directory.exists()){
                    directory.mkdir();
                }

                // Guardar la respuesta como un archivo PDF
                String outputFileName = String.format("facturas/factura%d.pdf", idReserva);
                File outputFile = new File(outputFileName);
                if (!outputFile.getParentFile().exists()) {
                    outputFile.getParentFile().mkdirs();
                }
                Files.copy(response.body(), outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Factura downloaded: " + outputFileName);

                // Mostrar una alerta de confirmación
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Descarga completada");
                alert.setHeaderText(null);
                alert.setContentText("La factura se ha descargado correctamente.");
                alert.showAndWait();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        btnBorrarReserva.setOnAction(event -> {
            int idReserva = cbReservas.getValue();

            // Crear una alerta de confirmación
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmación de borrado");
            alert.setHeaderText("Estás a punto de borrar la reserva con ID: " + idReserva);
            alert.setContentText("¿Estás seguro de que quieres continuar?");

            // Mostrar la alerta y esperar la respuesta del usuario
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Si el usuario confirma, proceder con el borrado de la reserva
                try {
                    // Crear un cliente HTTP
                    HttpClient client = HttpClient.newHttpClient();

                    // Crear una solicitud HTTP a la API
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create("http://" + LoginController.IP + "/hotel/borrar-reserva.php"))
                            .POST(HttpRequest.BodyPublishers.ofString("token_auth=0de6e41e85570bfcf0afc59179b6f480&idReserva=" + idReserva))
                            .header("Content-Type", "application/x-www-form-urlencoded")
                            .build();

                    // Enviar la solicitud y obtener la respuesta
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                    // Comprobar si la respuesta es exitosa
                    if (response.statusCode() == 200) {
                        System.out.println("Reserva borrada: " + idReserva);
                        aplicacionController.actualizarReservas();
                        this.setReservas(Integer.parseInt(tfNHabRes.getText()), aplicacionController.getReservas());


                        // Mostrar una alerta de confirmación
                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        successAlert.setTitle("Borrado exitoso");
                        successAlert.setHeaderText(null);
                        successAlert.setContentText("La reserva ha sido borrada correctamente.");
                        successAlert.showAndWait();
                    } else {
                        System.out.println("Error al borrar la reserva: " + response.body());
                        // Mostrar una alerta de error
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Error");
                        errorAlert.setHeaderText(null);
                        errorAlert.setContentText("Ha ocurrido un error al intentar borrar la reserva.");
                        errorAlert.showAndWait();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Establece las reservas de una habitación.
     * @param numeroHabitacion
     * @param reservas
     */
    public void setReservas(int numeroHabitacion, List<Reserva> reservas) {
        tfNHabRes.setText(String.valueOf(numeroHabitacion));

        List<Reserva> filteredReservas = reservas.stream()
                .filter(reserva -> reserva.getNHabitacion() == numeroHabitacion)
                .toList();

        tvReservas.getItems().clear();
        tvReservas.getItems().addAll(filteredReservas);

        cbReservas.getItems().clear();
        for (Reserva reserva : filteredReservas) {
            cbReservas.getItems().add(reserva.getIdReserva());
        }
    }

    public void setAplicacionController(AplicacionController aplicacionController) {
        this.aplicacionController = aplicacionController;
    }


}