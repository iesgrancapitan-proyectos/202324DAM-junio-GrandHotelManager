package com.example.hotelapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.hotelapp.Objetos.Reserva;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Esta clase representa la actividad de reservas en la aplicación.
 * Extiende AppCompatActivity, lo que indica que esta es una clase de Actividad.
 */
public class Reservas extends AppCompatActivity {
    private TableLayout tableLayout;
    private Toolbar toolbar;

    /**
     * Este método se llama cuando la actividad está iniciando.
     * Aquí es donde ocurre la mayoría de la inicialización.
     * @param savedInstanceState Si la actividad se está reinicializando después de haber sido cerrada previamente, este Bundle contiene los datos que suministró más recientemente en onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.reservas);

        // Configura la Toolbar
        toolbar = findViewById(R.id.toolbar);
        //toolbarTitle = findViewById(R.id.toolbar_title);

        // Agrega un margen superior a la Toolbar igual a la altura de la barra de estado
        int statusBarHeight = getResources().getDimensionPixelSize(
                getResources().getIdentifier("status_bar_height", "dimen", "android"));
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) toolbar.getLayoutParams();
        params.setMargins(0, statusBarHeight, 0, 0);
        toolbar.setLayoutParams(params);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Initialize the TableLayout
        tableLayout = findViewById(R.id.table_layout);

        fetchReservas();


    }

    /**
     * Este método se utiliza para obtener las reservas desde el servidor.
     */
    private void fetchReservas(){
        OkHttpClient client = new OkHttpClient();

        // Crear el cuerpo de la solicitud
        RequestBody formBodyR = new FormBody.Builder()
                .add("token_auth", "0de6e41e85570bfcf0afc59179b6f480") // Asegúrate de que este es tu token de autenticación correcto
                .add("tiposPeticion", "infoReservas")
                .build();

        // Crear la solicitud
        Request requestR = new Request.Builder()
                .url("http://34.175.164.212/hotel/obtener-info.php") // Reemplaza con la URL de tu API
                .post(formBodyR)
                .build();

        // Hacer la solicitud
        client.newCall(requestR).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        List<Reserva> reservas = new ArrayList<>();
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        JSONArray jsonArrayReserva = jsonResponse.getJSONArray("infoReservas");
                        for (int i = 0; i < jsonArrayReserva.length(); i++) {
                            JSONObject jsonObjectReserva = jsonArrayReserva.getJSONObject(i);
                            Reserva reserva = new Reserva();
                            reserva.setIdReserva(jsonObjectReserva.getInt("idreserva"));
                            reserva.setnHabitacion(jsonObjectReserva.getInt("nhabitacion"));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                reserva.setFecha(LocalDate.parse(jsonObjectReserva.getString("fecha")));
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                reserva.setFecha_entrada(LocalDate.parse(jsonObjectReserva.getString("fechaentrada")));
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                reserva.setFecha_salida(LocalDate.parse(jsonObjectReserva.getString("fechasalida")));
                            }
                            reserva.setPago(jsonObjectReserva.getInt("pago"));
                            reserva.setMetodoPago(jsonObjectReserva.getString("metodopago"));
                            reserva.setObservaciones(jsonObjectReserva.getString("observaciones"));
                            reservas.add(reserva);
                        }

                        new Handler(Looper.getMainLooper()).post(() -> {
                            for (Reserva reserva : reservas) {
                                TableRow row = new TableRow(Reservas.this);

                                // Ajusta los pesos de las celdas
                                TableRow.LayoutParams idParams = new TableRow.LayoutParams(0, dpToPx(35), 0.5f);
                                TableRow.LayoutParams otherParams = new TableRow.LayoutParams(0, dpToPx(35), 1f);

                                TextView idReserva = new TextView(Reservas.this);
                                idReserva.setLayoutParams(idParams);
                                idReserva.setTextSize(16);
                                idReserva.setTextColor(Color.BLACK);
                                idReserva.setBackgroundResource(R.drawable.border);
                                idReserva.setPadding(8, 0, 0, 0);
                                idReserva.setGravity(Gravity.CENTER);
                                idReserva.setBackgroundColor(ContextCompat.getColor(Reservas.this, R.color.gold_claro));
                                idReserva.setText(String.valueOf(reserva.getIdReserva()));
                                row.addView(idReserva);

                                TextView nHabitacion = new TextView(Reservas.this);
                                nHabitacion.setLayoutParams(otherParams);
                                nHabitacion.setTextSize(16);
                                nHabitacion.setTextColor(Color.BLACK);
                                nHabitacion.setBackgroundResource(R.drawable.border);
                                nHabitacion.setPadding(8, 0, 0, 0);
                                nHabitacion.setGravity(Gravity.CENTER);
                                nHabitacion.setBackgroundColor(ContextCompat.getColor(Reservas.this, R.color.gris_claro));
                                nHabitacion.setText(String.valueOf(reserva.getnHabitacion()));
                                row.addView(nHabitacion);

                                TextView fechaEntrada = new TextView(Reservas.this);
                                fechaEntrada.setLayoutParams(otherParams);
                                fechaEntrada.setTextSize(16);
                                fechaEntrada.setTextColor(Color.BLACK);
                                fechaEntrada.setBackgroundResource(R.drawable.border);
                                fechaEntrada.setPadding(8, 0, 0, 0);
                                fechaEntrada.setGravity(Gravity.CENTER);
                                fechaEntrada.setBackgroundColor(ContextCompat.getColor(Reservas.this, R.color.gris_claro));
                                fechaEntrada.setText(reserva.getFecha_entrada().toString());
                                row.addView(fechaEntrada);

                                TextView fechaSalida = new TextView(Reservas.this);
                                fechaSalida.setLayoutParams(otherParams);
                                fechaSalida.setTextSize(16);
                                fechaSalida.setTextColor(Color.BLACK);
                                fechaSalida.setBackgroundResource(R.drawable.border);
                                fechaSalida.setPadding(8, 0, 0, 0);
                                fechaSalida.setGravity(Gravity.CENTER);
                                fechaSalida.setBackgroundColor(ContextCompat.getColor(Reservas.this, R.color.gris_claro));
                                fechaSalida.setText(reserva.getFecha_salida().toString());
                                row.addView(fechaSalida);


                                // Guarda la reserva en la fila
                                row.setTag(reserva);

                                // Agrega un OnClickListener a la fila
                                row.setOnClickListener(v -> {
                                    // Recupera la reserva de la fila
                                    Reserva clickedReserva = (Reserva) v.getTag();

                                    // Crea y muestra el AlertDialog
                                    AlertDialog.Builder builder = new AlertDialog.Builder(Reservas.this);

                                    // Infla el diseño personalizado
                                    LayoutInflater inflater = getLayoutInflater();
                                    View dialogView = inflater.inflate(R.layout.dialog_reserva, null);

                                    // Configura los TextViews en el diseño personalizado
                                    TextView fechaView = dialogView.findViewById(R.id.fecha);
                                    fechaView.setText("Fecha: " + clickedReserva.getFecha());

                                    TextView pagoView = dialogView.findViewById(R.id.pago);
                                    pagoView.setText("Pago: " + clickedReserva.getPago() + "€");

                                    TextView metodoPagoView = dialogView.findViewById(R.id.metodoPago);
                                    metodoPagoView.setText("Método de pago: " + clickedReserva.getMetodoPago());

                                    TextView observacionesView = dialogView.findViewById(R.id.observaciones);
                                    observacionesView.setText("Observaciones: " + clickedReserva.getObservaciones());

                                    Button btnOk = dialogView.findViewById(R.id.btn_ok);

                                    ImageView imageView = dialogView.findViewById(R.id.imageView);
                                    imageView.setOnClickListener(v1 -> {
                                        // Comprueba si ya tienes el permiso
                                        if (ContextCompat.checkSelfPermission(Reservas.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                            // Si no tienes el permiso, solicítalo
                                            ActivityCompat.requestPermissions(Reservas.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                                        } else {
                                            // Si ya tienes el permiso, continúa con la descarga del PDF
                                            downloadPdf(clickedReserva);
                                        }
                                    });

                                    // Establece el diseño personalizado como la vista del AlertDialog
                                    builder.setView(dialogView);

                                    AlertDialog dialog = builder.create();
                                    if (dialog.getWindow() != null) {
                                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    }
                                    dialog.show();
                                    btnOk.setOnClickListener(v1 -> dialog.dismiss());
                                });
                                tableLayout.addView(row);
                            }
                        });


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * Este método se utiliza para descargar el PDF de la reserva.
     * @param clickedReserva La reserva para la que se descargará el PDF.
     */
    private void downloadPdf(Reserva clickedReserva) {
        // Tu código para descargar el PDF va aquí
        // Obtén el ID de la reserva
        int reservaId = clickedReserva.getIdReserva();

        // Crea el cuerpo de la solicitud
        RequestBody formBody = new FormBody.Builder()
                .add("token_auth", "0de6e41e85570bfcf0afc59179b6f480") // Reemplaza con tu token de autenticación
                .add("idReserva", String.valueOf(reservaId))
                .build();

        // Crea la solicitud
        Request request = new Request.Builder()
                .url("http://34.175.164.212/hotel/descargar-factura.php")
                .post(formBody)
                .build();

        // Realiza la solicitud
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                // Muestra un mensaje de error
                runOnUiThread(() -> Toast.makeText(Reservas.this, "Error al descargar la factura", Toast.LENGTH_SHORT).show());
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Obtén el cuerpo de la respuesta como un array de bytes
                    if ("application/pdf".equals(response.header("Content-Type"))) {
                        // Obtén el cuerpo de la respuesta como un array de bytes
                        byte[] facturaBytes = response.body().bytes();

                        // Crea un archivo en el directorio de descargas
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            ContentValues values = new ContentValues();
                            values.put(MediaStore.Downloads.DISPLAY_NAME, "factura.pdf");
                            values.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
                            values.put(MediaStore.Downloads.IS_PENDING, 1);

                            Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

                            if (uri != null) {
                                try (OutputStream os = getContentResolver().openOutputStream(uri)) {
                                    if (os != null) {
                                        os.write(facturaBytes);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                values.clear();
                                values.put(MediaStore.Downloads.IS_PENDING, 0);
                                getContentResolver().update(uri, values, null, null);
                            }
                        } else {
                            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                            File pdfFile = new File(downloadsDir, "Factura.pdf");

                            try {
                                // Escribe los bytes en el archivo
                                FileOutputStream fos = new FileOutputStream(pdfFile);
                                fos.write(facturaBytes);
                                fos.close();
                            } catch (IOException e) {
                                // Muestra un mensaje de error
                                runOnUiThread(() -> Toast.makeText(Reservas.this, "Error al guardar la factura", Toast.LENGTH_SHORT).show());
                                e.printStackTrace();
                            }
                        }

                        // Muestra un mensaje de éxito
                        runOnUiThread(() -> Toast.makeText(Reservas.this, "Factura descargada correctamente", Toast.LENGTH_SHORT).show());
                    } else {
                        // Muestra un mensaje de error
                        runOnUiThread(() -> Toast.makeText(Reservas.this, "Error al descargar la factura", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(Reservas.this, "Error al descargar la factura", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    /**
     * Este metodo convierte los píxeles en dp.
     * @param dp
     * @return int
     */
    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }


    /**
     * Inicializa el contenido del menú de opciones estándar de la Actividad.
     * @param menu El menú de opciones en el que colocas tus elementos.
     * @return Debes devolver true para que se muestre el menú; si devuelves false, no se mostrará.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * Este hook se llama cada vez que se selecciona un elemento en tu menú de opciones.
     * @param item El elemento del menú que se seleccionó.
     * @return boolean Devuelve false para permitir que el procesamiento normal del menú continúe, true para consumirlo aquí.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_logout) {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_volver) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}