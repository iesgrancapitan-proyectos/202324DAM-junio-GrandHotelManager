package com.example.hotelapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelapp.Adapters.HabitacionesLimpiezaAdapter;
import com.example.hotelapp.Objetos.Habitacion;
import com.example.hotelapp.Objetos.Reserva;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Esta clase representa la actividad de Limpieza de Habitaciones en la aplicación.
 * Extiende AppCompatActivity, lo que indica que esta es una clase de Actividad.
 */
public class HabitacionesLimpieza extends AppCompatActivity {
    private HabitacionesLimpiezaAdapter adapter; // Adaptador para el RecyclerView
    private int nhabitacion; // Add this to store the room number

    /**
     * Este método se llama cuando la actividad está iniciando.
     * Aquí es donde ocurre la mayoría de la inicialización.
     * @param savedInstanceState Si la actividad se está reinicializando después de haber sido cerrada previamente, este Bundle contiene los datos que suministró más recientemente en onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.habitaciones_limpieza);

        // Solicita el permiso de la cámara
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, HabitacionesLimpiezaAdapter.REQUEST_IMAGE_CAPTURE);
        }

        RecyclerView recyclerView = findViewById(R.id.recycler_view); // Asegúrate de que este ID esté en tu layout
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Configura la Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        adapter = new HabitacionesLimpiezaAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(nhabitacion -> {
            this.nhabitacion = nhabitacion; // Store the room number
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, HabitacionesLimpiezaAdapter.REQUEST_IMAGE_CAPTURE);
            }
        });

        fetchHabitaciones();
        fetchReservas();
    }

    /**
     * Este método se utiliza para obtener la información de las habitaciones desde el servidor.
     */
    private void fetchHabitaciones() {
        OkHttpClient client = new OkHttpClient();

        // Crear el cuerpo de la solicitud
        RequestBody formBodyH = new FormBody.Builder()
                .add("token_auth", "0de6e41e85570bfcf0afc59179b6f480") // Asegúrate de que este es tu token de autenticación correcto
                .add("tiposPeticion", "infoHabitaciones")
                .build();

        // Crear la solicitud
        Request requestH = new Request.Builder()
                .url("http://34.175.164.212/hotel/obtener-info.php") // Reemplaza con la URL de tu API
                .post(formBodyH)
                .build();


        // Hacer la solicitud
        client.newCall(requestH).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    System.out.println("Response from server: " + responseBody);
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        JSONArray jsonArray = jsonResponse.getJSONArray("infoHabitaciones");
                        List<Habitacion> habitaciones = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Habitacion habitacion = new Habitacion();
                            habitacion.setNhabitacion(jsonObject.getInt("nhabitacion"));
                            habitacion.setNpersonas(jsonObject.getInt("npersonas"));
                            habitacion.setNcamas(jsonObject.getInt("ncamas"));
                            habitacion.setMetroscuadrados(jsonObject.getInt("metroscuadrados"));
                            habitacion.setPrecio(jsonObject.getInt("precio"));
                            habitacion.setEstado(jsonObject.getString("estado"));
                            habitaciones.add(habitacion);
                        }

                        // Actualiza tu RecyclerView con los datos de las habitaciones
                        new Handler(Looper.getMainLooper()).post(() -> {
                            adapter.setHabitaciones(habitaciones);
                            adapter.notifyDataSetChanged();
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * Este método se utiliza para obtener la información de las reservas desde el servidor.
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
                .url("http://34.175.164.212/hotel/obtener-info.php")
                .post(formBodyR)
                .build();

        // Hacer la solicitud
        client.newCall(requestR).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

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
                                reserva.setFecha_entrada(LocalDate.parse(jsonObjectReserva.getString("fechaentrada")));
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                reserva.setFecha_salida(LocalDate.parse(jsonObjectReserva.getString("fechasalida")));
                            }
                            reservas.add(reserva);
                        }


                        // Actualiza tu RecyclerView con los datos de las habitaciones
                        new Handler(Looper.getMainLooper()).post(() -> {
                            adapter.setReservas(reservas);
                            adapter.notifyDataSetChanged();
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * Este método se llama cuando una actividad que lanzaste sale, dándote el requestCode con el que la iniciaste, el resultCode que devolvió, y cualquier dato adicional de ella.
     * @param requestCode El código de solicitud entero originalmente suministrado a startActivityForResult(), permitiéndote identificar de quién viene este resultado.
     * @param resultCode El código de resultado entero devuelto por la actividad hija a través de su setResult().
     * @param data Un Intent, que puede devolver datos de resultado al llamador (varios datos pueden adjuntarse a Intent "extras").
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == HabitacionesLimpiezaAdapter.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            String imageBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            uploadImage(imageBase64, nhabitacion);
        }
    }

    /**
     * Este método se utiliza para subir la imagen al servidor.
     * @param imageBase64 La representación base64 de la imagen.
     * @param nhabitacion El número de habitación.
     */
    private void uploadImage(String imageBase64, int nhabitacion) {
        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("token_auth", "0de6e41e85570bfcf0afc59179b6f480")
                .add("nhabitacion", String.valueOf(nhabitacion))
                .add("foto", imageBase64)
                .build();

        Request request = new Request.Builder()
                .url("http://34.175.164.212/hotel/subir-foto-hab.php")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(HabitacionesLimpieza.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    new Handler(Looper.getMainLooper()).post(() ->
                            Toast.makeText(HabitacionesLimpieza.this, "Imagen subida correctamente", Toast.LENGTH_SHORT).show()
                    );
                    Log.d("Image Upload", "Image uploaded successfully");
                    Log.d("Server Response", "Response from server: " + response.body().string());
                    response.body().close();

                } else {
                    Log.e("Image Upload", "Image upload failed: " + response.message());
                    Log.e("Server Response", "Error from server: " + response.body().string());
                    response.body().close();
                }
            }
        });
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
