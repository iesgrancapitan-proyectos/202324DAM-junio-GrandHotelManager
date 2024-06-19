/**
 * Nombre del Proyecto: Grand Hotel Manager
 * Última Modificación: 15/06/2024
 *
 * @author: Antonio Castro Gómez y Hugo Salamanca Nuñez
 * @version: 1.0
 *
 * Descripción: Este proyecto es una aplicación de hotel que permite a los usuarios iniciar sesión,
 * escanear códigos QR, y acceder a diferentes menús dependiendo de su tipo de usuario.
 */
package com.example.hotelapp;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hotelapp.Objetos.PasswordEncryptor;
import com.example.hotelapp.Objetos.UserSession;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Esta clase representa la actividad de inicio de sesión en la aplicación.
 * Extiende AppCompatActivity, lo que indica que esta es una clase de Actividad.
 */
public class Login extends AppCompatActivity {
    EditText ettUsername, ettPassword;
    ImageView ivQr;
    Button bLogin;

    /**
     * Este método se llama cuando la actividad está iniciando.
     * Aquí es donde ocurre la mayoría de la inicialización.
     * @param savedInstanceState Si la actividad se está reinicializando después de haber sido cerrada previamente, este Bundle contiene los datos que suministró más recientemente en onSaveInstanceState(Bundle).
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ettUsername = findViewById(R.id.ettNombre);
        ettPassword = findViewById(R.id.ettContra);
        ivQr = findViewById(R.id.ivQr);
        bLogin = findViewById(R.id.bLogin);

        // Establecer los drawables con margen desde el principio
        if (ettPassword.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
            ettPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye_off_with_margin, 0);
        } else {
            ettPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye_on_with_margin, 0);
        }

        TextView tvTerms = findViewById(R.id.tvTerms);
        String termsText = getResources().getString(R.string.terms);
        SpannableString spannableString = new SpannableString(termsText);

        // Cambiar el color de "Términos de Servicio" y hacerlo clickeable
        int start = termsText.indexOf("Términos de Servicio");
        int end = start + "Términos de Servicio".length();
        spannableString.setSpan(new ForegroundColorSpan(Color.YELLOW), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                showDialog("Términos de Servicio", "Al utilizar nuestros servicios, usted acepta estos términos. Por favor, léalos cuidadosamente.");
            }
        }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Cambiar el color de "Política de Privacidad" y hacerlo clickeable
        start = termsText.indexOf("Política de Privacidad");
        end = start + "Política de Privacidad".length();
        spannableString.setSpan(new ForegroundColorSpan(Color.YELLOW), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                showDialog("Política de Privacidad", "Nosotros respetamos su privacidad y nos comprometemos a protegerla a través de nuestra conformidad con esta política.");
            }
        }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvTerms.setText(spannableString);
        tvTerms.setMovementMethod(LinkMovementMethod.getInstance());

        bLogin.setOnClickListener(v -> {
           login();
        });
        ivQr.setOnClickListener(v -> {
            new IntentIntegrator(Login.this)
                    .setOrientationLocked(false)
                    .setPrompt("Scan the QR code")
                    .initiateScan();
        });

        ettPassword.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;

            if(event.getAction() == MotionEvent.ACTION_UP) {
                if(event.getRawX() >= (ettPassword.getRight() - ettPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    // Cuando el usuario toca el icono, cambia la visibilidad de la contraseña
                    if (ettPassword.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
                        // Mostrar la contraseña
                        ettPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        ettPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye_on_with_margin, 0);
                    } else {
                        // Ocultar la contraseña
                        ettPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        ettPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye_off_with_margin, 0);
                    }
                    return true;
                }
            }
            return false;
        });

    }

    /**
     * Este método se llama después de que se inicia la actividad.
     * Se utiliza para obtener el resultado de la actividad de escaneo de QR.
     *
     * @param requestCode El código de solicitud original que se pasó a startActivityForResult().
     * @param resultCode El código de resultado devuelto por la actividad hijo a través de su setResult().
     * @param data Un Intent que lleva los datos de resultado.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                // Supongamos que el contenido del QR es "tonicg1 toni1"
                String[] qrContents = result.getContents().split(" ");
                ettUsername.setText(qrContents[0]);
                ettPassword.setText(qrContents[1]);
                login();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Este método se utiliza para mostrar un diálogo con un título y un mensaje.
     */
    private void showDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    /**
     * Este método se utiliza para iniciar sesión en el usuario.
     */
    private void login() {
        String username = ettUsername.getText().toString();
        String password = ettPassword.getText().toString();

        String passwordHash = PasswordEncryptor.encrypt(password);
        System.out.println("Contra: " + passwordHash);
        if(passwordHash == null) {
            System.out.println("Error al encriptar la contraseña");
            return;
        }

        OkHttpClient client = new OkHttpClient();

        // Crear el cuerpo de la solicitud
        RequestBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("password", passwordHash)
                .build();

        // Crear la solicitud
        Request request = new Request.Builder()
                .url("http://34.175.164.212/hotel/login.php")
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
                    // Parsear la respuesta como JSON
                    String responseBody = response.body().string();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        String status = jsonResponse.getString("status");
                        System.out.println(status);
                        if ("SUCCESS".equals(status)) {
                            // Aquí puedes proceder con el inicio de sesión
                            System.out.println("Inicio de sesión exitoso");

                            // Guarda el token en la sesión
                            String token = jsonResponse.getString("token");
                            UserSession.setToken(token);

                            // Hacer una solicitud a usersession.php para obtener los datos de la sesión
                            Request sessionRequest = new Request.Builder()
                                    .url("http://34.175.164.212/hotel/usersession.php?token=" + UserSession.getToken())
                                    .build();

                            client.newCall(sessionRequest).enqueue(new Callback() {
                                @Override
                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                    e.printStackTrace();
                                }

                                @Override
                                public void onResponse(@NotNull Call call, @NotNull Response sessionResponse) throws IOException {
                                    if (sessionResponse.isSuccessful()) {
                                        try {
                                            String sessionResponseBody = sessionResponse.body().string();
                                            JSONObject sessionJson = new JSONObject(sessionResponseBody);
                                            String sessionUsername = sessionJson.getString("usuario");
                                            int userType = sessionJson.getInt("tipo");

                                            // Guarda el nombre de usuario y el tipo de usuario en la sesión
                                            UserSession.setUsername(sessionUsername);
                                            UserSession.setUserType(userType);

                                            if(userType == 2) {
                                                Intent intent = new Intent(Login.this, ClienteMenu.class);
                                                startActivity(intent);
                                            } else if(userType == 0 || userType == 1) {
                                                Intent intent = new Intent(Login.this, MainMenu.class);
                                                startActivity(intent);
                                            } else if(userType == 3) {
                                                Intent intent = new Intent(Login.this, LimpiezaMenu.class);
                                                startActivity(intent);
                                            }
                                        }
                                        catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                        } else {
                            runOnUiThread(() -> {
                                Toast.makeText(Login.this, "Nombre de usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

}