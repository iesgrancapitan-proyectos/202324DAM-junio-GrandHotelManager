package com.example.hotelapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Esta clase representa la actividad de Reseñas en la aplicación.
 * Extiende AppCompatActivity, lo que indica que esta es una clase de Actividad.
 */
public class Resenas extends AppCompatActivity {

    private EditText reviewText;
    private RatingBar reviewRating;

    /**
     * Este método se llama cuando la actividad está iniciando.
     * Aquí es donde ocurre la mayoría de la inicialización.
     * @param savedInstanceState Si la actividad se está reinicializando después de haber sido cerrada previamente, este Bundle contiene los datos que suministró más recientemente en onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resenas);

        // Configura la Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        reviewText = findViewById(R.id.review_text);
        reviewRating = findViewById(R.id.review_rating);

        reviewRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
            }
        });

        // Agrega un OnClickListener al botón bEnviar
        Button bEnviar = findViewById(R.id.bEnviar);
        bEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarResena();
            }
        });
    }

    /**
     * Este método se utiliza para enviar la reseña.
     * Obtiene la calificación y el texto de la UI, crea una solicitud y la envía al servidor.
     */
    public void enviarResena() {
        float rating = reviewRating.getRating();

        String text = reviewText.getText().toString();

        OkHttpClient client = new OkHttpClient();

        // Crea el cuerpo de la solicitud
        RequestBody formBody = new FormBody.Builder()
                .add("token_auth", "0de6e41e85570bfcf0afc59179b6f480") // Asegúrate de que este es tu token de autenticación correcto
                .add("valoracion", Float.toString(rating))
                .add("observaciones", text)
                .build();

        // Crea la solicitud
        Request request = new Request.Builder()
                .url("http://34.175.164.212/hotel/valoracion.php") // Reemplaza con la URL de tu API
                .post(formBody)
                .build();

        // Realiza la solicitud
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(Resenas.this, "Reseña enviada con éxito", Toast.LENGTH_SHORT).show());
                } else {
                    runOnUiThread(() -> Toast.makeText(Resenas.this, "Error al enviar la reseña", Toast.LENGTH_SHORT).show());
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