package com.example.hotelapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Esta clase representa la actividad de Información en la aplicación.
 * Extiende AppCompatActivity, lo que indica que esta es una clase de Actividad.
 */
public class Informacion extends AppCompatActivity {
    private Toolbar toolbar;

    /**
     * Este método se llama cuando la actividad está iniciando.
     * Aquí es donde ocurre la mayoría de la inicialización.
     * @param savedInstanceState Si la actividad se está reinicializando después de haber sido cerrada previamente, este Bundle contiene los datos que suministró más recientemente en onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.informacion);

        // Configura la Toolbar
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        fetchTotalClientes();
        fetchTotalEmpleados();
        fetchHabDisponibles();
        fetchTotalHabitaciones();
        fetchHabOcupadas();
        fetchHabReservadas();

        @SuppressLint("ResourceType")
        final Animation scaleUp = AnimationUtils.loadAnimation(this, R.animator.scale_up);
        @SuppressLint("ResourceType")
        final Animation scaleDown = AnimationUtils.loadAnimation(this, R.animator.scale_down);

        // Obtén una referencia a cada CardView
        CardView cardView1 = findViewById(R.id.card_view1);
        CardView cardView2 = findViewById(R.id.card_view2);
        CardView cardView3 = findViewById(R.id.card_view3);
        CardView cardView4 = findViewById(R.id.card_view4);
        CardView cardView5 = findViewById(R.id.card_view5);
        CardView cardView6 = findViewById(R.id.card_view6);

        // Crea un OnTouchListener
        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.startAnimation(scaleUp);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.startAnimation(scaleDown);
                }
                return true;
            }
        };

        // Asigna el OnTouchListener a cada CardView
        cardView1.setOnTouchListener(onTouchListener);
        cardView2.setOnTouchListener(onTouchListener);
        cardView3.setOnTouchListener(onTouchListener);
        cardView4.setOnTouchListener(onTouchListener);
        cardView5.setOnTouchListener(onTouchListener);
        cardView6.setOnTouchListener(onTouchListener);
    }

    /**
     * Este método se utiliza para obtener el total de clientes desde el servidor.
     */
    private void fetchTotalClientes() {
        OkHttpClient client = new OkHttpClient();

        // Crear el cuerpo de la solicitud
        RequestBody formBody = new FormBody.Builder()
                .add("token_auth", "0de6e41e85570bfcf0afc59179b6f480") // Asegúrate de que este es tu token de autenticación correcto
                .add("tiposPeticion", "totalClientes")
                .build();

        // Crear la solicitud
        Request request = new Request.Builder()
                .url("http://34.175.164.212/hotel/obtener-info.php") // Reemplaza con la URL de tu API
                .post(formBody)
                .build();

        // Hacer la solicitud
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        Object json = new JSONTokener(responseBody).nextValue();
                        if (json instanceof JSONObject) {
                            JSONObject jsonResponse = new JSONObject(responseBody);
                            if(jsonResponse.has("totalClientes")) {
                                int totalClientes = jsonResponse.getInt("totalClientes");

                                // Aquí puedes hacer lo que necesites con el total de clientes
                                // Por ejemplo, puedes guardarlo en una variable de instancia
                                // y luego usarlo para actualizar tu TextView
                                runOnUiThread(() -> {
                                    TextView tvTotalClientes = findViewById(R.id.tv_total_clientes);
                                    tvTotalClientes.setText(String.valueOf(totalClientes));
                                });
                            }
                        } else if (json instanceof JSONArray) {
                            // La respuesta es un JSONArray, maneja esto aquí
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * Este método se utiliza para obtener el total de empleados desde el servidor.
     */
    private void fetchTotalEmpleados() {
        OkHttpClient client = new OkHttpClient();

        // Crear el cuerpo de la solicitud
        RequestBody formBody = new FormBody.Builder()
                .add("token_auth", "0de6e41e85570bfcf0afc59179b6f480") // Asegúrate de que este es tu token de autenticación correcto
                .add("tiposPeticion", "totalEmpleados")
                .build();

        // Crear la solicitud
        Request request = new Request.Builder()
                .url("http://34.175.164.212/hotel/obtener-info.php") // Reemplaza con la URL de tu API
                .post(formBody)
                .build();

        // Hacer la solicitud
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        Object json = new JSONTokener(responseBody).nextValue();
                        if (json instanceof JSONObject) {
                            JSONObject jsonResponse = new JSONObject(responseBody);
                            if(jsonResponse.has("totalEmpleados")) {
                                int totalEmpleados = jsonResponse.getInt("totalEmpleados");
                                runOnUiThread(() -> {
                                    TextView tvTotalEmpleados = findViewById(R.id.tv_empleados_totales);
                                    tvTotalEmpleados.setText(String.valueOf(totalEmpleados));
                                });
                            }
                        } else if (json instanceof JSONArray) {
                            // La respuesta es un JSONArray, maneja esto aquí
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * Este método se utiliza para obtener las habitaciones disponibles desde el servidor.
     */
    private void fetchHabDisponibles() {
        OkHttpClient client = new OkHttpClient();

        // Crear el cuerpo de la solicitud
        RequestBody formBody = new FormBody.Builder()
                .add("token_auth", "0de6e41e85570bfcf0afc59179b6f480") // Asegúrate de que este es tu token de autenticación correcto
                .add("tiposPeticion", "habDisponibles")
                .build();

        // Crear la solicitud
        Request request = new Request.Builder()
                .url("http://34.175.164.212/hotel/obtener-info.php") // Reemplaza con la URL de tu API
                .post(formBody)
                .build();

        // Hacer la solicitud
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        Object json = new JSONTokener(responseBody).nextValue();
                        if (json instanceof JSONObject) {
                            JSONObject jsonResponse = new JSONObject(responseBody);
                            if(jsonResponse.has("habDisponibles")) {
                                int habDisponibles = jsonResponse.getInt("habDisponibles");
                                runOnUiThread(() -> {
                                    TextView tvHabDisponibles = findViewById(R.id.tv_habitaciones_disponibles);
                                    tvHabDisponibles.setText(String.valueOf(habDisponibles));
                                });
                            }
                        } else if (json instanceof JSONArray) {
                            // La respuesta es un JSONArray, maneja esto aquí
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * Este método se utiliza para obtener el total de habitaciones desde el servidor.
     */
    private void fetchTotalHabitaciones() {
        OkHttpClient client = new OkHttpClient();

        // Crear el cuerpo de la solicitud
        RequestBody formBody = new FormBody.Builder()
                .add("token_auth", "0de6e41e85570bfcf0afc59179b6f480") // Asegúrate de que este es tu token de autenticación correcto
                .add("tiposPeticion", "totalHabitaciones")
                .build();

        // Crear la solicitud
        Request request = new Request.Builder()
                .url("http://34.175.164.212/hotel/obtener-info.php") // Reemplaza con la URL de tu API
                .post(formBody)
                .build();

        // Hacer la solicitud
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        Object json = new JSONTokener(responseBody).nextValue();
                        if (json instanceof JSONObject) {
                            JSONObject jsonResponse = new JSONObject(responseBody);
                            if(jsonResponse.has("totalHabitaciones")) {
                                int habTotales = jsonResponse.getInt("totalHabitaciones");

                                runOnUiThread(() -> {
                                    TextView tvHabTotales = findViewById(R.id.tv_habitaciones_totales);
                                    tvHabTotales.setText(String.valueOf(habTotales));
                                });
                            }
                        } else if (json instanceof JSONArray) {
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * Este método se utiliza para obtener las habitaciones ocupadas desde el servidor.
     */
    private void fetchHabOcupadas() {
        OkHttpClient client = new OkHttpClient();

        // Crear el cuerpo de la solicitud
        RequestBody formBody = new FormBody.Builder()
                .add("token_auth", "0de6e41e85570bfcf0afc59179b6f480") // Asegúrate de que este es tu token de autenticación correcto
                .add("tiposPeticion", "habOcupadas")
                .build();

        // Crear la solicitud
        Request request = new Request.Builder()
                .url("http://34.175.164.212/hotel/obtener-info.php") // Reemplaza con la URL de tu API
                .post(formBody)
                .build();

        // Hacer la solicitud
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        Object json = new JSONTokener(responseBody).nextValue();
                        if (json instanceof JSONObject) {
                            JSONObject jsonResponse = new JSONObject(responseBody);
                            if(jsonResponse.has("habOcupadas")) {
                                int habOcupadas = jsonResponse.getInt("habOcupadas");

                                // Aquí puedes hacer lo que necesites con el total de clientes
                                // Por ejemplo, puedes guardarlo en una variable de instancia
                                // y luego usarlo para actualizar tu TextView
                                runOnUiThread(() -> {
                                    TextView tvHabOcupadas = findViewById(R.id.tv_habitaciones_ocupadas);
                                    tvHabOcupadas.setText(String.valueOf(habOcupadas));
                                });
                            }
                        } else if (json instanceof JSONArray) {
                            // La respuesta es un JSONArray, maneja esto aquí
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * Este método se utiliza para obtener las habitaciones reservadas desde el servidor.
     */
    private void fetchHabReservadas() {
        OkHttpClient client = new OkHttpClient();

        // Crear el cuerpo de la solicitud
        RequestBody formBody = new FormBody.Builder()
                .add("token_auth", "0de6e41e85570bfcf0afc59179b6f480") // Asegúrate de que este es tu token de autenticación correcto
                .add("tiposPeticion", "habReservadas")
                .build();

        // Crear la solicitud
        Request request = new Request.Builder()
                .url("http://34.175.164.212/hotel/obtener-info.php") // Reemplaza con la URL de tu API
                .post(formBody)
                .build();

        // Hacer la solicitud
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        Object json = new JSONTokener(responseBody).nextValue();
                        if (json instanceof JSONObject) {
                            JSONObject jsonResponse = new JSONObject(responseBody);
                            if(jsonResponse.has("habReservadas")) {
                                int habReservadas = jsonResponse.getInt("habReservadas");

                                // Aquí puedes hacer lo que necesites con el total de clientes
                                // Por ejemplo, puedes guardarlo en una variable de instancia
                                // y luego usarlo para actualizar tu TextView
                                runOnUiThread(() -> {
                                    TextView tvHabreservadas = findViewById(R.id.tv_habitaciones_reservadas);
                                    tvHabreservadas.setText(String.valueOf(habReservadas));
                                });
                            }
                        } else if (json instanceof JSONArray) {
                            // La respuesta es un JSONArray, maneja esto aquí
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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