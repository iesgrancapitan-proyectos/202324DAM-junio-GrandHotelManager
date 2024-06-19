package es.iesgrancapitan.proyectohotelfx.controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import es.iesgrancapitan.proyectohotelfx.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Clase controladora de la vista de la aplicación
 * Se encarga de gestionar la interfaz gráfica de la aplicación
 * y de realizar las peticiones al servidor
 * @author Hugo Salamanca y Antonio Castro
 */
public class AplicacionController{


    @FXML
    private Label userLabel;

    @FXML
    private Label nClientesTotales;

    @FXML
    private Label nHabTotales;

    @FXML
    private Label nEmpleadosTotales;

    @FXML
    private Label nHabDisponibles;

    @FXML
    private Label nHabReservadas;

    @FXML
    private Label nHabOcupadas;

    @FXML
    private ScrollPane infPane;
    @FXML
    private ScrollPane clientesPane;
    @FXML
    private ScrollPane habPane;
    @FXML
    private AnchorPane reservasPane;
    @FXML
    private ScrollPane pedidosPane;
    @FXML
    private ScrollPane empleadosPane;

    @FXML
    private TableView<Reserva> tvReservasPrin;

    @FXML
    private TableColumn<Reserva, Integer> cIdReserva;

    @FXML
    private TableColumn<Reserva, Integer> cNHabitacion;

    @FXML
    private TableColumn<Reserva, LocalDate> cFecha;

    @FXML
    private TableColumn<Reserva, LocalDate> cFechaEntrada;

    @FXML
    private TableColumn<Reserva, LocalDate> cFechaSalida;

    @FXML
    private TableColumn<Reserva, Float> cTotal;

    @FXML
    private TableColumn<Reserva, String> cMetodo;

    @FXML
    private TableColumn<Reserva, String> cObservaciones;

    @FXML
    private VBox vboxHabitaciones;
    @FXML
    private ComboBox<String> cbReservasPrin;
    @FXML
    private Button btnInfoReserva, btnDescargarPDF, btnBorrarReserva, btnBorrarOldReservas;

    @FXML
    private TableView<Cliente> tvClientes;
    @FXML
    private ComboBox<String> cbClientes;

    @FXML
    private TableColumn<Cliente, String> cNif;
    @FXML
    private TableColumn<Cliente, String> cTDocumento;
    @FXML
    private TableColumn<Cliente, String> cNombre;
    @FXML
    private TableColumn<Cliente, String> cApellido1;
    @FXML
    private TableColumn<Cliente, String> cApellido2;
    @FXML
    private TableColumn<Cliente, String> cSexo;
    @FXML
    private TableColumn<Cliente, String> cNacimiento;
    @FXML
    private TableColumn<Cliente, String> cNacionalidad;
    @FXML
    private Button btnInfoCliente;
    @FXML
    private Button addHab;


    @FXML
    private VBox vboxClientes;
    @FXML
    private TextField tfBuscarCliente;
    @FXML
    private TextField tfBuscarEmpleado;
    @FXML
    private VBox vboxPedidos;
    @FXML
    private VBox vboxEmpleados;
    @FXML
    private Button btnEmpleados;

    private List<Habitacion> habitaciones = new ArrayList<>();
    private List<Reserva> reservas = new ArrayList<>();
    private List<Cliente> clientes = new ArrayList<>();
    private List<Pedido> pedidos = new ArrayList<>();
    private List<Producto> productos = new ArrayList<>();
    private List<Empleado> empleados = new ArrayList<>();

    /**
     * Método que se ejecuta al cargar la vista
     * Se encarga de inicializar los componentes de la vista
     * y de cargar los datos de las habitaciones
     * y las reservas
     */
    @FXML
    public void initialize() {
        String username = UserSession.getUsername();
        userLabel.setText("Bienvenido, " + username);

        if(UserSession.getUserType() == 0) {
            addHab.setDisable(false);
            addHab.setVisible(true);
            btnEmpleados.setDisable(false);
            btnEmpleados.setVisible(true);
        }
        btnInfoReserva.setOnAction(event -> abrirInfoDeReserva());
        btnDescargarPDF.setOnAction(event -> descargarReserva());
        btnBorrarReserva.setOnAction(event ->borrarReserva());
        btnBorrarOldReservas.setOnAction(event -> borrarReservasAntiguas());

        tfBuscarCliente.textProperty().addListener((observable, oldValue, newValue) -> {
            buscarCliente(newValue);
        });

        tfBuscarEmpleado.textProperty().addListener((observable, oldValue, newValue) -> {
            buscarEmpleado(newValue);
        });

        actualizarDatos();
        actualizarReservas();
        actualizarClientes();
        actualizarProductos();
        actualizarPedidos();
        actualizarEmpleados();

    }

     // METODOS PARA ACTUALIZAR DATOS

    /**
     * Método que se encarga de actualizar las reservas
     * de la base de datos
     * Se conecta al servidor y obtiene las reservas
     * y las almacena en la lista de reservas
     * Posteriormente, genera las tarjetas de las habitaciones
     * con los datos actualizados
     * Se ejecuta en un hilo secundario
     * para no bloquear la interfaz gráfica
     * y se actualiza la interfaz gráfica en el hilo principal
     * con Platform.runLater()
     */
    public void actualizarReservas() {
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
                    JSONArray reservasJson = jsonResponse.getJSONArray("infoReservas");
                    Platform.runLater(() -> {
                        reservas.clear();
                        for (int i = 0; i < reservasJson.length(); i++) {
                            JSONObject reservaJson = reservasJson.getJSONObject(i);
                            Reserva reserva = new Reserva(reservaJson.getInt("idreserva"),
                                    reservaJson.getInt("nhabitacion"),
                                    LocalDate.parse(reservaJson.getString("fecha")),
                                    LocalDate.parse(reservaJson.getString("fechaentrada")),
                                    LocalDate.parse(reservaJson.getString("fechasalida")),
                                    reservaJson.getFloat("pago"),
                                    reservaJson.getString("metodopago"),
                                    reservaJson.getString("observaciones"));
                            reservas.add(reserva);
                        }
                        generarTarjetasHabitaciones();
                    });
                });
        Platform.runLater(this::llenarTablaReservas);
        Platform.runLater(this::llenarComboBoxReservas);
    }

    /**
     * Método que se encarga de actualizar las habitaciones
     * de la base de datos
     * Se conecta al servidor y obtiene las habitaciones
     * y las almacena en la lista de habitaciones
     * Posteriormente, genera las tarjetas de las habitaciones
     * con los datos actualizados
     * Se ejecuta en un hilo secundario
     * para no bloquear la interfaz gráfica
     * y se actualiza la interfaz gráfica en el hilo principal
     * con Platform.runLater()
     */
    public void actualizarHabitaciones() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://" + LoginController.IP + "/hotel/obtener-info.php"))
                .POST(HttpRequest.BodyPublishers.ofString("token_auth=0de6e41e85570bfcf0afc59179b6f480&tiposPeticion=infoHabitaciones"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(response -> {
                    JSONObject jsonResponse = new JSONObject(response);

                    JSONArray habitacionesJson = jsonResponse.getJSONArray("infoHabitaciones");
                    Platform.runLater(() -> {
                        habitaciones.clear();
                        for (int i = 0; i < habitacionesJson.length(); i++) {
                            JSONObject habitacionJson = habitacionesJson.getJSONObject(i);
                            Habitacion habitacion = new Habitacion(habitacionJson.getInt("nhabitacion"),
                                    habitacionJson.getInt("npersonas"),
                                    habitacionJson.getInt("ncamas"),
                                    habitacionJson.getInt("metroscuadrados"),
                                    habitacionJson.getFloat("precio"),
                                    habitacionJson.getString("estado"));
                            habitaciones.add(habitacion);
                        }
                        generarTarjetasHabitaciones();
                    });
                });
    }

    /**
     * Método que se encarga de actualizar los clientes
     * de la base de datos
     * Se conecta al servidor y obtiene los clientes
     * y los almacena en la lista de clientes
     * Posteriormente, genera las tarjetas de las habitaciones
     * con los datos actualizados
     * Se ejecuta en un hilo secundario
     * para no bloquear la interfaz gráfica
     * y se actualiza la interfaz gráfica en el hilo principal
     * con Platform.runLater()
     */
    public void actualizarClientes() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://" + LoginController.IP + "/hotel/obtener-info.php"))
                .POST(HttpRequest.BodyPublishers.ofString("token_auth=0de6e41e85570bfcf0afc59179b6f480&tiposPeticion=infoClientes"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(response -> {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray clientesJson = jsonResponse.getJSONArray("infoClientes");
                    Platform.runLater(() -> {
                        clientes.clear();
                        for (int i = 0; i < clientesJson.length(); i++) {
                            JSONObject clienteJson = clientesJson.getJSONObject(i);
                            Cliente cliente = new Cliente(clienteJson.getString("NIF"),
                                    clienteJson.getString("tipodocumento"),
                                    clienteJson.getString("nombre"),
                                    clienteJson.getString("apellido1"),
                                    clienteJson.getString("apellido2"),
                                    clienteJson.getString("sexo"),
                                    clienteJson.getString("nacimiento"),
                                    clienteJson.getString("nacionalidad"));
                            clientes.add(cliente);
                        }
                        generarTarjetasClientes();
                    });
                });
    }

    /**
     * Método que se encarga de actualizar los datos
     * de la base de datos
     * Se conecta al servidor y obtiene los datos
     * y los almacena en las etiquetas correspondientes
     * Se ejecuta en un hilo secundario
     * para no bloquear la interfaz gráfica
     * y se actualiza la interfaz gráfica en el hilo principal
     * con Platform.runLater()
     */
    @FXML
    protected void actualizarDatos() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://" + LoginController.IP + "/hotel/obtener-info.php"))
                .POST(HttpRequest.BodyPublishers.ofString("token_auth=0de6e41e85570bfcf0afc59179b6f480&tiposPeticion=totalClientes,totalHabitaciones,totalEmpleados,habDisponibles,habReservadas,habOcupadas"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(response -> {
                    JSONObject jsonResponse = new JSONObject(response);
                    Platform.runLater(() -> {
                        nClientesTotales.setText(jsonResponse.getString("totalClientes"));
                        nHabTotales.setText(jsonResponse.getString("totalHabitaciones"));
                        nEmpleadosTotales.setText(jsonResponse.getString("totalEmpleados"));
                        nHabDisponibles.setText(jsonResponse.getString("habDisponibles"));
                        nHabReservadas.setText(jsonResponse.getString("habReservadas"));
                        nHabOcupadas.setText(jsonResponse.getString("habOcupadas"));
                    });
                });
    }


    /**
     * Método que se encarga de actualizar los pedidos
     * de la base de datos
     * Se conecta al servidor y obtiene los pedidos
     * y los almacena en la lista de pedidos
     * Posteriormente, genera las tarjetas de los pedidos
     * con los datos actualizados
     * Se ejecuta en un hilo secundario
     * para no bloquear la interfaz gráfica
     * y se actualiza la interfaz gráfica en el hilo principal
     * con Platform.runLater()
     */
    public void actualizarPedidos() {
        pedidos.clear();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://" + LoginController.IP + "/hotel/obtener-info.php"))
                .POST(HttpRequest.BodyPublishers.ofString("token_auth=0de6e41e85570bfcf0afc59179b6f480&tiposPeticion=infoPedidos"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(response -> {
                    JSONObject jsonResponse = new JSONObject(response);
                    //System.out.println(jsonResponse);
                    JSONArray pedidosJson = jsonResponse.getJSONArray("infoPedidos");
                    for (int i = 0; i < pedidosJson.length(); i++) {
                        JSONObject pedidoJson = pedidosJson.getJSONObject(i);
                        Pedido pedido = new Pedido(
                                pedidoJson.getInt("idpedido"),
                                pedidoJson.getInt("nhabitacion"),
                                LocalDateTime.parse(pedidoJson.getString("fechapedido"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                                pedidoJson.getString("estado"),
                                (float) pedidoJson.getDouble("total")
                                //new ArrayList<>()
                        );

                        pedidos.add(pedido);
                        //System.out.println("--------" + pedidos.get(i).getIdPedido() + " " + pedidos.get(i).getNHabitacion() + " " + pedidos.get(i).getFechaPedido() + " " + pedidos.get(i).getEstado() + " " + pedidos.get(i).getTotal());
                    }
                    Platform.runLater(this::generarTarjetasPedidos);
                });
    }

    /**
     * Método que se encarga de actualizar los productos
     * de la base de datos
     * Se conecta al servidor y obtiene los productos
     * y los almacena en la lista de productos
     * Se ejecuta en un hilo secundario
     * para no bloquear la interfaz gráfica
     * y se actualiza la interfaz gráfica en el hilo principal
     * con Platform.runLater()
     */
    public void actualizarProductos() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://" + LoginController.IP + "/hotel/obtener-info.php"))
                .POST(HttpRequest.BodyPublishers.ofString("token_auth=0de6e41e85570bfcf0afc59179b6f480&tiposPeticion=infoProductos"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(response -> {
                    JSONObject jsonResponse = new JSONObject(response);
                    //System.out.printf("Productos: %s\n", jsonResponse);
                    JSONArray productosJson = jsonResponse.getJSONArray("infoProductos");
                    for (int i = 0; i < productosJson.length(); i++) {
                        JSONObject productoJson = productosJson.getJSONObject(i);
                        Producto producto = new Producto(
                                productoJson.getInt("idproducto"),
                                productoJson.getString("nombre"),
                                productoJson.getString("descripcion"),
                                (float) productoJson.getDouble("precio"),
                                productoJson.getInt("cantidad")
                        );

                        productos.add(producto);
                    }
                });
    }

    /**
     * Método que se encarga de actualizar los empleados
     * de la base de datos
     * Se conecta al servidor y obtiene los empleados
     * y los almacena en la lista de empleados
     * Se ejecuta en un hilo secundario
     * para no bloquear la interfaz gráfica
     * y se actualiza la interfaz gráfica en el hilo principal
     * con Platform.runLater()
     */
    public void actualizarEmpleados() {
        empleados.clear();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://" + LoginController.IP + "/hotel/obtener-info.php"))
                .POST(HttpRequest.BodyPublishers.ofString("token_auth=0de6e41e85570bfcf0afc59179b6f480&tiposPeticion=infoEmpleados"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(response -> {
                    JSONObject jsonResponse = new JSONObject(response);

                    JSONArray empleadosArray = jsonResponse.getJSONArray("infoEmpleados");
                    for (int i = 0; i < empleadosArray.length(); i++) {
                        JSONObject empleadoObject = empleadosArray.getJSONObject(i);
                        String usuario = empleadoObject.isNull("usuario") ? "" : empleadoObject.getString("usuario");
                        Empleado empleado = new Empleado(
                                empleadoObject.getInt("idempleado"),
                                empleadoObject.getString("NIF"),
                                empleadoObject.getString("nombre"),
                                empleadoObject.getString("apellido1"),
                                empleadoObject.getString("apellido2"),
                                usuario
                        );
                        empleados.add(empleado);
                    }
                    Platform.runLater(this::generarTarjetasEmpleados);
                });
    }

    /**
     * Método que se encarga de generar las tarjetas de las habitaciones
     * Se recorren las habitaciones y se genera una tarjeta para cada una
     * Se añaden las tarjetas a un VBox que contiene las tarjetas
     * Se añade el VBox al ScrollPane de las habitaciones
     * Se añade un efecto de escala cuando el ratón entra en la tarjeta
     * y se restaura la escala original cuando el ratón sale de la tarjeta
     * Se añade un evento de clic en la tarjeta que muestra la información
     * de la habitación en una nueva ventana
     */
    private void generarTarjetasHabitaciones() {
        vboxHabitaciones.setPadding(new javafx.geometry.Insets(50, 0, 0, 0));
        vboxHabitaciones.getChildren().clear();
        for (int i = 0; i < habitaciones.size(); i++) {
            Habitacion habitacion = habitaciones.get(i);
            AnchorPane tarjetaHabitacion = generarTarjetaHabitacion(habitacion);
            if (i % 3 == 0) {
                HBox hBox = new HBox();
                hBox.setSpacing(50);
                vboxHabitaciones.getChildren().add(hBox);
            }
            ((HBox) vboxHabitaciones.getChildren().get(i / 3)).getChildren().add(tarjetaHabitacion);
        }
    }

    /**
     * Método que se encarga de generar una tarjeta de una habitación
     * Se crea un AnchorPane que contiene la información de la habitación
     * Se añade un ícono de hotel a la tarjeta
     * Se añade el número de la habitación a la tarjeta
     * Se añade el estado de la habitación a la tarjeta
     * Se añade un efecto de escala cuando el ratón entra en la tarjeta
     * y se restaura la escala original cuando el ratón sale de la tarjeta
     * Se añade un evento de clic en la tarjeta que muestra la información
     * de la habitación en una nueva ventana
     * @param habitacion
     * @return
     */
    private AnchorPane generarTarjetaHabitacion(Habitacion habitacion) {
        AnchorPane tarjetaHabitacion = new AnchorPane();
        tarjetaHabitacion.setPrefSize(349, 200);
        tarjetaHabitacion.setMinSize(349, 200);
        tarjetaHabitacion.setCursor(javafx.scene.Cursor.HAND);
        tarjetaHabitacion.getStyleClass().add("card");

        FontAwesomeIconView iconView = new FontAwesomeIconView(FontAwesomeIcon.HOTEL);
        iconView.setSize("64");
        iconView.setFill(Color.WHITE);

        double iconHeight = Double.parseDouble(iconView.getSize());
        double paneHeight = tarjetaHabitacion.getPrefHeight();
        double topAnchor = (paneHeight - iconHeight) / 2;

        AnchorPane.setRightAnchor(iconView, 10.0);
        AnchorPane.setTopAnchor(iconView, topAnchor);


        Label labelNumero = new Label("Nro:" + habitacion.getNumero());
        labelNumero.setLayoutX(10);
        labelNumero.setLayoutY(10);
        labelNumero.setTextFill(Color.WHITE);
        labelNumero.setFont(javafx.scene.text.Font.font("Arial", 72));


        String estadoTexto = "DISPONIBLE";
        String color = "linear-gradient(to bottom, #9cd97d, #ad935e)";
        LocalDate hoy = LocalDate.now();

        for (Reserva reserva : reservas) {
            //System.out.println(reserva.getNHabitacion() + " " + habitacion.getNumero() + " " + reserva.getFechaEntrada() + " " + reserva.getFechaSalida());
            if (reserva.getNHabitacion() == habitacion.getNumero()) {
                if ((hoy.isAfter(reserva.getFechaEntrada()) || hoy.isEqual(reserva.getFechaEntrada())) && (hoy.isBefore(reserva.getFechaSalida()) || hoy.isEqual(reserva.getFechaSalida()))) {
                    estadoTexto = "OCUPADA";
                    color = "linear-gradient(to bottom, #e01d1d, #ad935e)";
                    break;
                }
            }
        }

        Label labelEstado = new Label(estadoTexto);
        labelEstado.setTextFill(Color.WHITE);
        labelEstado.setFont(javafx.scene.text.Font.font("Arial", 18));
        labelEstado.setAlignment(Pos.CENTER);

        AnchorPane.setBottomAnchor(labelEstado, 0.0);
        AnchorPane.setLeftAnchor(labelEstado, 0.0);
        AnchorPane.setRightAnchor(labelEstado, 0.0);

        tarjetaHabitacion.setStyle("-fx-background-color: " + color + "; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 5, 0, 0, 0);");

        tarjetaHabitacion.getChildren().addAll(labelNumero, labelEstado);
        tarjetaHabitacion.getChildren().add(iconView);
        tarjetaHabitacion.setPadding(new javafx.geometry.Insets(10, 10, 10, 10));


        tarjetaHabitacion.setOnMouseEntered(event -> {
            tarjetaHabitacion.setScaleX(1.1);
            tarjetaHabitacion.setScaleY(1.1);

        });

        tarjetaHabitacion.setOnMouseExited(event -> {
            tarjetaHabitacion.setScaleX(1.0);
            tarjetaHabitacion.setScaleY(1.0);
        });

        tarjetaHabitacion.setOnMouseClicked(event -> onHabitacionClick(habitacion));

        return tarjetaHabitacion;
    }


    /**
     * Método que se encarga de buscar un cliente
     * en la lista de clientes
     * Se recorre la lista de clientes y se comprueba
     * si el nombre o apellido del cliente contiene el texto
     * ingresado
     * Si coincide, se genera la tarjeta del cliente y se añade
     * al contenedor
     * @param texto
     */
    private void buscarCliente(String texto) {
        if(texto.isEmpty()){
            generarTarjetasClientes();
            return;
        }
        vboxClientes.getChildren().clear();

        // Iterar sobre la lista de clientes
        for (Cliente cliente : clientes) {
            // Comprobar si el nombre o apellido del cliente contiene el texto ingresado
            if (cliente.getNombre().toLowerCase().contains(texto.toLowerCase()) ||
                    cliente.getApellido1().toLowerCase().contains(texto.toLowerCase())) {
                // Si coincide, generar la tarjeta del cliente y agregarla al contenedor
                AnchorPane tarjetaCliente = generarTarjetaCliente(cliente);
                vboxClientes.getChildren().add(tarjetaCliente);
            }
        }
    }

    /**
     * Método que se encarga de generar las tarjetas de los clientes
     * Se recorren los clientes y se genera una tarjeta para cada uno
     * Se añaden las tarjetas a un VBox que contiene las tarjetas
     * Se añade el VBox al ScrollPane de los clientes
     * Se añade un efecto de escala cuando el ratón entra en la tarjeta
     * y se restaura la escala original cuando el ratón sale de la tarjeta
     */
    private void generarTarjetasClientes() {
        vboxClientes.setPadding(new javafx.geometry.Insets(50, 0, 0, 0));
        vboxClientes.getChildren().clear();
        for (int i = 0; i < clientes.size(); i++) {
            Cliente cliente = clientes.get(i);
            AnchorPane tarjetaCliente = generarTarjetaCliente(cliente);
            if (i % 3 == 0) {
                HBox hBox = new HBox();
                hBox.setSpacing(50);
                vboxClientes.getChildren().add(hBox);
            }
            ((HBox) vboxClientes.getChildren().get(i / 3)).getChildren().add(tarjetaCliente);
        }
    }

    /**
     * Método que se encarga de generar una tarjeta de un cliente
     * Se crea un AnchorPane que contiene la información del cliente
     * Se añade un ícono de usuario a la tarjeta
     * Se añade el nombre del cliente a la tarjeta
     * Se añade un efecto de escala cuando el ratón entra en la tarjeta
     * y se restaura la escala original cuando el ratón sale de la tarjeta
     * Se añade un evento de clic en la tarjeta que muestra la información
     * @param cliente
     * @return
     */
    private AnchorPane generarTarjetaCliente(Cliente cliente) {
        AnchorPane tarjetaCliente = new AnchorPane();
        tarjetaCliente.setPrefSize(349, 200);
        tarjetaCliente.setMinSize(349, 200);
        tarjetaCliente.setCursor(javafx.scene.Cursor.HAND);
        tarjetaCliente.getStyleClass().add("card");

        FontAwesomeIconView iconView = new FontAwesomeIconView(FontAwesomeIcon.USER);
        iconView.setSize("64");
        iconView.setFill(Color.WHITE);
        // Configura la posición del ícono
        double iconHeight = Double.parseDouble(iconView.getSize());
        double paneHeight = tarjetaCliente.getPrefHeight();
        double topAnchor = (paneHeight - iconHeight) / 2;

        AnchorPane.setRightAnchor(iconView, 10.0);
        AnchorPane.setTopAnchor(iconView, topAnchor);

        Label labelNombre = new Label(cliente.getNombre() + " " + cliente.getApellido1() + " " + cliente.getApellido2());
        labelNombre.setLayoutX(10);
        labelNombre.setLayoutY(10);
        labelNombre.setTextFill(Color.WHITE);
        labelNombre.setFont(javafx.scene.text.Font.font("Arial", 24));

        tarjetaCliente.setStyle("-fx-background-color:  linear-gradient(to bottom, #d9ba7d, #ad935e); " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 5, 0, 0, 0);");

        tarjetaCliente.getChildren().addAll(labelNombre);
        tarjetaCliente.getChildren().add(iconView);
        tarjetaCliente.setPadding(new javafx.geometry.Insets(10, 10, 10, 10));

        // Añade un efecto de escala cuando el ratón entra en la tarjeta
        tarjetaCliente.setOnMouseEntered(event -> {
            tarjetaCliente.setScaleX(1.1);
            tarjetaCliente.setScaleY(1.1);
        });

        // Restaura la escala original cuando el ratón sale de la tarjeta
        tarjetaCliente.setOnMouseExited(event -> {
            tarjetaCliente.setScaleX(1.0);
            tarjetaCliente.setScaleY(1.0);
        });

        tarjetaCliente.setOnMouseClicked(event -> onClienteTarjetaClick(cliente));

        return tarjetaCliente;
    }

    /**
     * Método que se encarga de generar las tarjetas de los pedidos
     * Se recorren los pedidos y se genera una tarjeta para cada uno
     */
    private void generarTarjetasPedidos() {
        vboxPedidos.getChildren().clear();
        for (int i = 0; i < pedidos.size(); i++) {
            Pedido pedido = pedidos.get(i);
            AnchorPane tarjetaPedido = generarTarjetaPedido(pedido);
            if (i % 3 == 0) {
                HBox hBox = new HBox();
                hBox.setSpacing(50);
                vboxPedidos.getChildren().add(hBox);
            }
            ((HBox) vboxPedidos.getChildren().get(i / 3)).getChildren().add(tarjetaPedido);
        }
    }

    /**
     * Método que se encarga de generar una tarjeta de un pedido
     *  Se crea un AnchorPane que contiene la información del pedido
     *  Se añade un ícono de carrito de compras a la tarjeta
     *  Se añade el número del pedido a la tarjeta
     *  Se añade un efecto de escala cuando el ratón entra en la tarjeta
     *  y se restaura la escala original cuando el ratón sale de la tarjeta
     *  Se añade un evento de clic en la tarjeta que muestra la información
     * @param pedido
     * @return
     */
    private AnchorPane generarTarjetaPedido(Pedido pedido) {
        AnchorPane tarjetaPedido = new AnchorPane();
        tarjetaPedido.setPrefSize(349, 200);
        tarjetaPedido.setMinSize(349, 200);
        tarjetaPedido.setCursor(javafx.scene.Cursor.HAND);
        tarjetaPedido.getStyleClass().add("card");

        FontAwesomeIconView iconView = new FontAwesomeIconView(FontAwesomeIcon.SHOPPING_CART);
        iconView.setSize("64");
        iconView.setFill(Color.WHITE);
        double iconHeight = Double.parseDouble(iconView.getSize());
        double paneHeight = tarjetaPedido.getPrefHeight();
        double topAnchor = (paneHeight - iconHeight) / 2;

        AnchorPane.setRightAnchor(iconView, 10.0);
        AnchorPane.setTopAnchor(iconView, topAnchor);

        Label labelNumero = new Label("Pedido Nro: " + pedido.getIdPedido());
        labelNumero.setLayoutX(10);
        labelNumero.setLayoutY(10);
        labelNumero.setTextFill(Color.WHITE);
        labelNumero.setFont(javafx.scene.text.Font.font("Arial", 24));

        String color;
        // Cambia el color de la tarjeta según el estado del pedido
        if (pedido.getEstado().equals("Procesando")) {
            color = "linear-gradient(to bottom, #59C5D8, #ad935e)";
        } else {
            color = "linear-gradient(to bottom, #9cd97d, #ad935e)";
        }

        tarjetaPedido.getChildren().addAll(labelNumero);
        tarjetaPedido.getChildren().add(iconView);
        tarjetaPedido.setStyle("-fx-background-color: " + color + "; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 5, 0, 0, 0);");
        tarjetaPedido.setPadding(new javafx.geometry.Insets(10, 10, 10, 10));

        tarjetaPedido.setOnMouseEntered(event -> {
            tarjetaPedido.setScaleX(1.05);
            tarjetaPedido.setScaleY(1.05);
        });

        tarjetaPedido.setOnMouseExited(event -> {
            tarjetaPedido.setScaleX(1);
            tarjetaPedido.setScaleY(1);
        });

        tarjetaPedido.setOnMouseClicked(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/es/iesgrancapitan/proyectohotelfx/info-pedido.fxml"));
                Parent root = loader.load();

                InfoPedidoController controller = loader.getController();
                controller.setIdPedido(pedido.getIdPedido());

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Información del pedido: " + pedido.getIdPedido());
                stage.setResizable(false);
                Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/hotel.jpg")));
                stage.getIcons().add(icon);
                stage.show();

                controller.setAplicacionController(this);
                controller.setStage(stage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return tarjetaPedido;
    }

    /**
     * Método para buscar un empleado
     * en la lista de empleados
     * Se recorre la lista de empleados y se comprueba
     * si el nombre o apellido del empleado contiene el texto
     * ingresado
     * Si coincide, se genera la tarjeta del empleado y se añade
     * al contenedor
     * @param texto
     */
    private void buscarEmpleado(String texto) {
        if(texto.isEmpty()){
            generarTarjetasEmpleados();
            return;
        }
        // Limpiar el contenedor de tarjetas
        vboxEmpleados.getChildren().clear();

        // Iterar sobre la lista de clientes
        for (Empleado empleado : empleados) {
            // Comprobar si el nombre o apellido del cliente contiene el texto ingresado
            if (empleado.getNombre().toLowerCase().contains(texto.toLowerCase()) ||
                    empleado.getApellido1().toLowerCase().contains(texto.toLowerCase())) {
                // Si coincide, generar la tarjeta del cliente y agregarla al contenedor
                AnchorPane tarjetaEmpleado = generarTarjetaEmpleado(empleado);
                vboxEmpleados.getChildren().add(tarjetaEmpleado);
            }
        }
    }

    /**
     * Método que se encarga de generar una tarjeta de un empleado
     * Se crea un AnchorPane que contiene la información del empleado
     * Se añade un ícono de usuario a la tarjeta
     * Se añade el nombre del empleado a la tarjeta
     * Se añade un efecto de escala cuando el ratón entra en la tarjeta
     * y se restaura la escala original cuando el ratón sale de la tarjeta
     * Se añade un evento de clic en la tarjeta que muestra la información
     * @param empleado
     * @return
     */
    private AnchorPane generarTarjetaEmpleado(Empleado empleado) {
        AnchorPane tarjetaEmpleado = new AnchorPane();
        tarjetaEmpleado.setPrefSize(349, 200);
        tarjetaEmpleado.setMinSize(349, 200);
        tarjetaEmpleado.setCursor(javafx.scene.Cursor.HAND);
        tarjetaEmpleado.getStyleClass().add("card");

        Label labelNombre = new Label(empleado.getNombre() + " " + empleado.getApellido1() + " " + empleado.getApellido2());
        labelNombre.setLayoutX(10);
        labelNombre.setLayoutY(10);
        labelNombre.setTextFill(Color.WHITE);
        labelNombre.setFont(javafx.scene.text.Font.font("Arial", 24));

        FontAwesomeIconView iconView = new FontAwesomeIconView(FontAwesomeIcon.USER);
        iconView.setSize("64");
        iconView.setFill(Color.WHITE);
        double iconHeight = Double.parseDouble(iconView.getSize());
        double paneHeight = tarjetaEmpleado.getPrefHeight();
        double topAnchor = (paneHeight - iconHeight) / 2;

        AnchorPane.setRightAnchor(iconView, 10.0);
        AnchorPane.setTopAnchor(iconView, topAnchor);

        tarjetaEmpleado.setStyle("-fx-background-color:  linear-gradient(to bottom, #d9ba7d, #ad935e); " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 5, 0, 0, 0);");

        tarjetaEmpleado.getChildren().addAll(labelNombre);
        tarjetaEmpleado.getChildren().add(iconView);
        tarjetaEmpleado.setPadding(new javafx.geometry.Insets(10, 10, 10, 10));

        tarjetaEmpleado.setOnMouseEntered(event -> {
            tarjetaEmpleado.setScaleX(1.1);
            tarjetaEmpleado.setScaleY(1.1);
        });

        tarjetaEmpleado.setOnMouseExited(event -> {
            tarjetaEmpleado.setScaleX(1.0);
            tarjetaEmpleado.setScaleY(1.0);
        });

        tarjetaEmpleado.setOnMouseClicked(event -> onEmpleadoTarjetaClick(empleado));

        return tarjetaEmpleado;
    }

    /**
     * Método que se encarga de generar las tarjetas de los empleados
     */
    private void generarTarjetasEmpleados() {
        vboxEmpleados.setPadding(new javafx.geometry.Insets(50, 0, 0, 0));
        vboxEmpleados.getChildren().clear();
        for (int i = 0; i < empleados.size(); i++) {
            Empleado empleado = empleados.get(i);
            AnchorPane tarjetaEmpleado = generarTarjetaEmpleado(empleado);
            if (i % 3 == 0) {
                HBox hBox = new HBox();
                hBox.setSpacing(50);
                hBox.getChildren().add(tarjetaEmpleado);
                vboxEmpleados.getChildren().add(hBox);
            } else {
                HBox hBox = (HBox) vboxEmpleados.getChildren().get(vboxEmpleados.getChildren().size() - 1);
                hBox.getChildren().add(tarjetaEmpleado);
            }
        }
    }


    /**
     * Método que se encarga de llenar la tabla de reservas
     * con las reservas obtenidas de la base de datos
     */
    private void llenarTablaReservas() {
        ObservableList<Reserva> reservasObservableList = FXCollections.observableArrayList(reservas);

        cIdReserva.setCellValueFactory(new PropertyValueFactory<>("idReserva"));
        cNHabitacion.setCellValueFactory(new PropertyValueFactory<>("nHabitacion"));
        cFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        cFechaEntrada.setCellValueFactory(new PropertyValueFactory<>("fechaEntrada"));
        cFechaSalida.setCellValueFactory(new PropertyValueFactory<>("fechaSalida"));
        cTotal.setCellValueFactory(new PropertyValueFactory<>("pago"));
        cMetodo.setCellValueFactory(new PropertyValueFactory<>("metodopago"));
        cObservaciones.setCellValueFactory(new PropertyValueFactory<>("observaciones"));

        tvReservasPrin.setItems(reservasObservableList);
    }

    private void llenarComboBoxReservas() {
        ObservableList<String> reservasIdList = FXCollections.observableArrayList();

        for (Reserva reserva : reservas) {
            reservasIdList.add("Número de reserva: " + reserva.getIdReserva());
        }

        cbReservasPrin.setItems(reservasIdList);
    }


    private void abrirInfoDeReserva() {
        if (cbReservasPrin.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Advertencia");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, selecciona una reserva antes de continuar.");
            alert.showAndWait();
            return;
        }

        Integer idReserva = Integer.parseInt(cbReservasPrin.getValue().replaceFirst("Número de reserva: ", ""));


        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/es/iesgrancapitan/proyectohotelfx/info-de-reserva.fxml"));
            Parent root = loader.load();

            InfoDeReservaController controller = loader.getController();
            controller.setIdReserva(idReserva);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/hotel.jpg")));
            stage.getIcons().add(icon);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void descargarReserva(){
        if (cbReservasPrin.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Advertencia");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, selecciona una reserva antes de continuar.");
            alert.showAndWait();
            return;
        }

        int idReserva = Integer.parseInt(cbReservasPrin.getValue().replaceFirst("Número de reserva: ", ""));

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

    }

    private void borrarReserva(){
        if (cbReservasPrin.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Advertencia");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, selecciona una reserva antes de continuar.");
            alert.showAndWait();
            return;
        }

        int idReserva = Integer.parseInt(cbReservasPrin.getValue().replaceFirst("Número de reserva: ", ""));

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
                    actualizarReservas();
                    llenarTablaReservas();
                    llenarComboBoxReservas();


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

    }

    private void borrarReservasAntiguas() {
        // Crear una alerta de confirmación
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación de borrado");
        alert.setHeaderText("Estás a punto de borrar todas las reservas anteriores a la fecha de hoy.");
        alert.setContentText("¿Estás seguro de que quieres continuar?");

        // Mostrar la alerta y esperar la respuesta del usuario
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Si el usuario confirma, proceder con el borrado de las reservas
            try {
                // Crear un cliente HTTP
                HttpClient client = HttpClient.newHttpClient();

                // Crear una solicitud HTTP a la API
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://" + LoginController.IP + "/hotel/borrar-reservas-antiguas.php"))
                        .POST(HttpRequest.BodyPublishers.ofString("token_auth=0de6e41e85570bfcf0afc59179b6f480"))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .build();

                // Enviar la solicitud y obtener la respuesta
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                // Parsear la respuesta JSON
                JSONObject jsonResponse = new JSONObject(response.body());

                // Comprobar si la respuesta es exitosa
                if (jsonResponse.getString("status").equals("success")) {
                    actualizarReservas();
                    llenarComboBoxReservas();
                    llenarTablaReservas();
                    // Mostrar una alerta de confirmación
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Borrado exitoso");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Las reservas se han borrado correctamente.");
                    successAlert.showAndWait();

                } else {
                    // Mostrar una alerta de error
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText("Ha ocurrido un error al intentar borrar las reservas.");
                    errorAlert.showAndWait();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void onHabitacionClick(Habitacion habitacion) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/es/iesgrancapitan/proyectohotelfx/HabitacionView.fxml"));
            Parent root = loader.load();

            HabitacionViewController controller = loader.getController();
            controller.setHabitacion(habitacion, reservas, clientes); // Pasa la lista de reservas
            controller.setAplicacionController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.setTitle("Información sobre la habitacion nº " + habitacion.getNumero());
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/hotel.jpg")));
            stage.getIcons().add(icon);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onBtnAddEmpleadoClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/es/iesgrancapitan/proyectohotelfx/crear-empleado.fxml"));
            Parent root = loader.load();

            CrearEmpleadoController controller = loader.getController();
            controller.setAplicacionController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.setTitle("Añadir empleado");
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/hotel.jpg")));
            stage.getIcons().add(icon);
            stage.show();
            controller.setStage(stage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onBtnAddUsuarioClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/es/iesgrancapitan/proyectohotelfx/crear-usuario.fxml"));
            Parent root = loader.load();

            CrearUsuarioController controller = loader.getController();
            controller.setAplicacionController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.setTitle("Añadir usuario");
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/hotel.jpg")));
            stage.getIcons().add(icon);
            stage.show();
            controller.setStage(stage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void onBtnAddClienteClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/es/iesgrancapitan/proyectohotelfx/crear-cliente.fxml"));
            Parent root = loader.load();

            CrearClienteController controller = loader.getController();
            controller.setAplicacionController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.setTitle("Añadir cliente");
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/hotel.jpg")));
            stage.getIcons().add(icon);
            stage.show();
            controller.setStage(stage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onActualizarClientesClick() {
        actualizarClientes();
    }

    @FXML
    private void onClienteTarjetaClick(Cliente cliente) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/es/iesgrancapitan/proyectohotelfx/info-cliente.fxml"));
            Parent root = loader.load();

            InfoClienteController controller = loader.getController();
            controller.setCliente(cliente);

            Stage stage = new Stage();  // Create a new Stage
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.setTitle("Información sobre el cliente");
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/hotel.jpg")));
            stage.getIcons().add(icon);
            stage.show();

            controller.setStage(stage);  // Pass the Stage to the controller
            controller.setAplicacionController(this);  // Pass the AplicacionController to the controller
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onEmpleadoTarjetaClick(Empleado empleado) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/es/iesgrancapitan/proyectohotelfx/info-empleado.fxml"));
            Parent root = loader.load();

            InfoEmpleadoController controller = loader.getController();
            controller.setEmpleado(empleado);

            Stage stage = new Stage();  // Create a new Stage
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.setTitle("Información sobre el empleado");
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/hotel.jpg")));
            stage.getIcons().add(icon);
            stage.show();

            controller.setStage(stage);  // Pass the Stage to the controller
            controller.setAplicacionController(this);  // Pass the AplicacionController to the controller
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void onActualizarPedidosBtn() {
        actualizarPedidos();
    }

    @FXML
    private void onAddHabBtnClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/es/iesgrancapitan/proyectohotelfx/crear-habitacion.fxml"));
            Parent root = loader.load();

            CrearHabitacionController crearHabitacionController = loader.getController();
            crearHabitacionController.setAplicacionController(this);
            Stage stage = new Stage();
            crearHabitacionController.setStage(stage);

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setTitle("Crear habitación");
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/hotel.jpg")));
            stage.getIcons().add(icon);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @FXML
    protected void onClientesBtn(){
        infPane.setVisible(false);
        infPane.setDisable(true);
        clientesPane.setVisible(true);
        clientesPane.setDisable(false);
        habPane.setVisible(false);
        habPane.setDisable(true);
        reservasPane.setVisible(false);
        reservasPane.setDisable(true);
        pedidosPane.setVisible(false);
        pedidosPane.setDisable(true);
        empleadosPane.setVisible(false);
        empleadosPane.setDisable(true);

        actualizarClientes();
    }

    @FXML
    protected void onInfBtn(){
        infPane.setVisible(true);
        infPane.setDisable(false);
        clientesPane.setVisible(false);
        clientesPane.setDisable(true);
        habPane.setVisible(false);
        habPane.setDisable(true);
        reservasPane.setVisible(false);
        reservasPane.setDisable(true);
        pedidosPane.setVisible(false);
        pedidosPane.setDisable(true);
        empleadosPane.setVisible(false);
        empleadosPane.setDisable(true);

        actualizarDatos();
    }

    @FXML
    protected void onHabBtn(){
        infPane.setVisible(false);
        infPane.setDisable(true);
        clientesPane.setVisible(false);
        clientesPane.setDisable(true);
        habPane.setVisible(true);
        habPane.setDisable(false);
        reservasPane.setVisible(false);
        reservasPane.setDisable(true);
        pedidosPane.setVisible(false);
        pedidosPane.setDisable(true);
        empleadosPane.setVisible(false);
        empleadosPane.setDisable(true);

        actualizarReservas();
        actualizarHabitaciones();
    }

    @FXML
    protected void onReservasBtn(){
        infPane.setVisible(false);
        infPane.setDisable(true);
        clientesPane.setVisible(false);
        clientesPane.setDisable(true);
        habPane.setVisible(false);
        habPane.setDisable(true);
        reservasPane.setVisible(true);
        reservasPane.setDisable(false);
        pedidosPane.setVisible(false);
        pedidosPane.setDisable(true);
        empleadosPane.setVisible(false);
        empleadosPane.setDisable(true);

        actualizarReservas();

    }

    @FXML
    protected void onPedidosBtn(){
        infPane.setVisible(false);
        infPane.setDisable(true);
        clientesPane.setVisible(false);
        clientesPane.setDisable(true);
        habPane.setVisible(false);
        habPane.setDisable(true);
        reservasPane.setVisible(false);
        reservasPane.setDisable(true);
        pedidosPane.setVisible(true);
        pedidosPane.setDisable(false);
        empleadosPane.setVisible(false);
        empleadosPane.setDisable(true);


        actualizarProductos();
        actualizarPedidos();

    }

    @FXML
    protected void onEmpleadosBtn(){
        infPane.setVisible(false);
        infPane.setDisable(true);
        clientesPane.setVisible(false);
        clientesPane.setDisable(true);
        habPane.setVisible(false);
        habPane.setDisable(true);
        reservasPane.setVisible(false);
        reservasPane.setDisable(true);
        pedidosPane.setVisible(false);
        pedidosPane.setDisable(true);
        empleadosPane.setVisible(true);
        empleadosPane.setDisable(false);

        actualizarEmpleados();
    }

    public List<Reserva> getReservas() {
        return reservas;
    }
}
