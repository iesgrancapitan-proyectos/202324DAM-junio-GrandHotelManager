package es.iesgrancapitan.proyectohotelfx.controllers;

import es.iesgrancapitan.proyectohotelfx.Cliente;
import es.iesgrancapitan.proyectohotelfx.Habitacion;
import es.iesgrancapitan.proyectohotelfx.Reserva;
import javafx.fxml.FXML;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Controlador de la vista de una habitación.
 * Se encarga de mostrar la información de una habitación y de gestionar las acciones que se pueden realizar sobre ella.
 * También se encarga de mostrar la información de las reservas de la habitación y de permitir al usuario verlas.
 * Además, permite al usuario hospedar a un cliente en la habitación y desocuparla.
 * También permite al usuario ver la foto de la habitación.
 */
public class HabitacionViewController {
    @FXML
    private TextField tfNHab;
    @FXML
    private TextField tfNPer;
    @FXML
    private TextField tfNCam;
    @FXML
    private TextField tfMC;
    @FXML
    private TextField tfEstado;
    @FXML
    private TextField tfEstadoLimpieza;
    @FXML
    private TextField tfPrecioNoche;
    @FXML
    private Button btnHospedar;
    @FXML
    private Button btnDesocupar;
    @FXML
    private Button btnVerReservas;
    private String estadoTexto;
    private List<Cliente> clientes;
    private AplicacionController aplicacionController;

    /**
     * Muestra la información de una habitación en la vista.
     * @param habitacion
     * @param reservas
     * @param clientes
     */
    public void setHabitacion(Habitacion habitacion, List<Reserva> reservas, List<Cliente> clientes) {
        tfNHab.setText(String.valueOf(habitacion.getNumero()));
        tfNPer.setText(String.valueOf(habitacion.getnPersonas()));
        tfNCam.setText(String.valueOf(habitacion.getnCamas()));
        tfMC.setText(String.valueOf(habitacion.getmCuadrados()));
        tfPrecioNoche.setText(String.valueOf(habitacion.getPrecio()) + " €");
        tfEstadoLimpieza.setText(habitacion.getEstado());
        this.clientes = clientes;


        String estadoTexto = "DISPONIBLE";
        LocalDate hoy = LocalDate.now();

        for (Reserva reserva : reservas) {
            if (reserva.getNHabitacion() == habitacion.getNumero()) {
                if ((hoy.isAfter(reserva.getFechaEntrada()) || hoy.isEqual(reserva.getFechaEntrada())) && (hoy.isBefore(reserva.getFechaSalida()) || hoy.isEqual(reserva.getFechaSalida()))) {
                    estadoTexto = "OCUPADA";
                    break;
                }
            }
        }

        tfEstado.setText(estadoTexto);

        // Mostrar y ocultar botones según el estado de la habitación
        switch (estadoTexto) {
            case "DISPONIBLE":
                btnHospedar.setVisible(true);
                btnDesocupar.setVisible(false);
                btnVerReservas.setVisible(true);
                break;
            case "OCUPADA":
                btnHospedar.setVisible(true);
                btnDesocupar.setVisible(true);
                btnVerReservas.setVisible(true);
                break;
        }

        btnHospedar.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/es/iesgrancapitan/proyectohotelfx/hospedar-view.fxml"));
                Parent root = loader.load();

                HospedarViewController hospedarViewController = loader.getController();
                hospedarViewController.setHabitacion(habitacion, clientes);
                hospedarViewController.setAplicacionController(aplicacionController); // Pass the AplicacionController instance to the HospedarViewController instance

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setResizable(false);
                stage.setTitle("Hospedar");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        btnVerReservas.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/es/iesgrancapitan/proyectohotelfx/info-reservas-view.fxml"));
                Parent root = loader.load();

                InfoReservasViewController infoReservasViewController = loader.getController();
                infoReservasViewController.setAplicacionController(this.aplicacionController); // Aquí se utiliza setAplicacionController
                infoReservasViewController.setReservas(habitacion.getNumero(), reservas);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setResizable(false);
                stage.setTitle("Información de las reservas");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        btnDesocupar.setOnAction(event -> {
            LocalDate today = LocalDate.now();
            Reserva reservaToDelete = null;

            // Find the reservation where today is between the check-in and check-out dates
            for (Reserva reserva : reservas) {
                if (reserva.getNHabitacion() == habitacion.getNumero()) {
                    if ((today.isAfter(reserva.getFechaEntrada()) || today.isEqual(reserva.getFechaEntrada())) &&
                            (today.isBefore(reserva.getFechaSalida()) || today.isEqual(reserva.getFechaSalida()))) {
                        reservaToDelete = reserva;
                        break;
                    }
                }
            }

            if (reservaToDelete != null) {
                // Show a confirmation dialog
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmación");
                alert.setHeaderText(null);
                alert.setContentText("¿Está seguro de que desea desocupar la habitación?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    // User chose OK, proceed with deletion
                    // Send a POST request to the server to delete the reservation
                    HttpClient client = HttpClient.newHttpClient();
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create("http://" + LoginController.IP + "/hotel/borrar-reserva.php"))
                            .POST(HttpRequest.BodyPublishers.ofString("token_auth=0de6e41e85570bfcf0afc59179b6f480&idReserva=" + reservaToDelete.getIdReserva()))
                            .header("Content-Type", "application/x-www-form-urlencoded")
                            .build();

                    client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                            .thenApply(HttpResponse::body)
                            .thenAccept(response -> {
                                System.out.println("Response from server: " + response);

                            });
                }
            } else {
                System.out.println("No reservation found for today");
            }
        });
    }

    /**
     * Muestra la foto de la habitación en una ventana.
     */
    @FXML
    private void onBtnFotoClick() {
        int nhabitacion = Integer.parseInt(tfNHab.getText());
        String token = "0de6e41e85570bfcf0afc59179b6f480";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://" + LoginController.IP + "/hotel/descargar-foto-hab.php"))
                .POST(HttpRequest.BodyPublishers.ofString("token_auth=" + token + "&nhabitacion=" + nhabitacion))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray())
                .thenAccept(response -> {
                    try {
                        // Crea la carpeta 'fotos' si no existe
                        Path path = Paths.get(System.getProperty("user.dir"), "fotos");
                        if (!Files.exists(path)) {
                            Files.createDirectory(path);
                        }

                        // Guarda la foto en un archivo en la carpeta 'fotos'
                        Path filePath = Paths.get(path.toString(), "foto" + nhabitacion + ".jpg");
                        Files.write(filePath, response.body());

                        // Abre el archivo con el visor de imágenes predeterminado del sistema
                        Desktop.getDesktop().open(filePath.toFile());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    public void setAplicacionController(AplicacionController aplicacionController) {
        this.aplicacionController = aplicacionController;
    }

}
