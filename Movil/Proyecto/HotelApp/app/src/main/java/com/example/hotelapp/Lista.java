package com.example.hotelapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelapp.Adapters.ProductoAdapter;
import com.example.hotelapp.Objetos.Producto;
import com.example.hotelapp.Objetos.UserSession;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
 * Esta clase representa la actividad de la lista de productos en la aplicación.
 * Extiende AppCompatActivity, lo que indica que esta es una clase de Actividad.
 */
public class Lista extends AppCompatActivity implements ProductoAdapter.OnQuantityChangeListener {
    private ProductoAdapter adapter; // Adaptador para el RecyclerView

    /**
     * Este método se llama cuando la actividad está iniciando.
     * Aquí es donde ocurre la mayoría de la inicialización.
     * @param savedInstanceState Si la actividad se está reinicializando después de haber sido cerrada previamente, este Bundle contiene los datos que suministró más recientemente en onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Crear los objetos Producto
        Producto producto1 = new Producto(1, "Agua", "1L", 1.5f, "botella_agua");
        Producto producto2 = new Producto(2, "Toalla", "1.40m x 0.70m", 7.99f, "toalla");
        Producto producto3 = new Producto(3, "Champagne", "1L", 49.99f, "champagne");
        Producto producto4 = new Producto(4, "Cargador", "Tipo C", 4.99f, "cargador");
        Producto producto5 = new Producto(5, "Almohada", "Mediana", 3.99f, "almohada");
        Producto producto6 = new Producto(6, "Secadora", "Simple", 9.99f, "secadora");
        Producto producto7 = new Producto(7, "Kindle", "Pequeña", 11.99f, "kindle");
        Producto producto8 = new Producto(7, "Dentrifico", "Mediana", 2.50f, "dentrifico");

        // Agregar los productos a la lista
        List<Producto> productos = new ArrayList<>();
        productos.add(producto1);
        productos.add(producto2);
        productos.add(producto3);
        productos.add(producto4);
        productos.add(producto5);
        productos.add(producto6);
        productos.add(producto7);
        productos.add(producto8);

        // Configurar el adaptador con la lista de productos
        adapter = new ProductoAdapter(productos);
        adapter.setOnQuantityChangeListener(this);
        recyclerView.setAdapter(adapter);

        // Llama a fetchProductos() para obtener los productos de la base de datos
        fetchProductos();
        actualizarTotal();

        Button btnPedir = findViewById(R.id.btnPedir);
        btnPedir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarCesta();
            }
        });
    }

    /**
     * Este método se llama cuando cambia la cantidad de un producto en el carrito.
     */
    @Override
    public void onQuantityChange() {
        Log.d("Lista", "onQuantityChange called");
        actualizarTotal();
    }

    /**
     * Este método muestra el carrito de compras.
     */
    private void mostrarCesta() {
        obtenerHabitacionUsuario(new HabitacionCallback() {
            @Override
            public void onHabitacionObtenida(int nhabitacion) {
                // Aquí tienes el número de habitación
                // Ahora puedes crear el cuerpo de la solicitud y hacer la solicitud

                Map<Integer, Integer> cesta = adapter.getCesta();
                String totalStr = actualizarTotal();

                // Crear el JSONArray de detallespedido
                JSONArray jsonArray = new JSONArray();
                for (Map.Entry<Integer, Integer> entry : cesta.entrySet()) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("idproducto", entry.getKey());
                        jsonObject.put("cantidad", entry.getValue());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    jsonArray.put(jsonObject);
                }

                // Crear el cuerpo de la solicitud
                RequestBody body = new FormBody.Builder()
                        .add("token_auth", "0de6e41e85570bfcf0afc59179b6f480")
                        .add("nhabitacion", Integer.toString(nhabitacion))
                        .add("total", totalStr)
                        .add("detallespedido", jsonArray.toString()) // Enviar como cadena JSON
                        .build();

                // Crear la solicitud
                Request request = new Request.Builder()
                        .url("http://34.175.164.212/hotel/insertar-pedido.php") // Reemplaza con la URL de tu API
                        .post(body)
                        .build();

                // Hacer la solicitud
                OkHttpClient client = new OkHttpClient();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            runOnUiThread(() -> Toast.makeText(Lista.this, "Pedido realizado con éxito", Toast.LENGTH_SHORT).show());
                        } else {
                            runOnUiThread(() -> Toast.makeText(Lista.this, "Error al realizar el pedido", Toast.LENGTH_SHORT).show());
                        }
                    }
                });
            }
        });
    }

    /**
     * Este método busca un producto por su ID.
     * @param id El ID del producto.
     * @return El producto con el ID especificado.
     */
    private Producto findProductoById(int id) {
        // Aquí deberías buscar el producto con el ID especificado en tu base de datos
        // Este es solo un ejemplo y deberías reemplazarlo con tu propia lógica
        return new Producto();
    }

    /**
     * Este método se utiliza para obtener los productos desde el servidor.
     */
    private void fetchProductos() {
        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("token_auth", "0de6e41e85570bfcf0afc59179b6f480")
                .add("tiposPeticion", "infoProductos")
                .build();

        Request request = new Request.Builder()
                .url("http://34.175.164.212/hotel/obtener-info.php")
                .post(formBody)
                .build();

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
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        JSONArray jsonArray = jsonResponse.getJSONArray("infoProductos");
                        List<Producto> productos = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Producto producto = new Producto();
                            producto.setId(jsonObject.getInt("idproducto"));
                            producto.setNombre(jsonObject.getString("nombre"));
                            producto.setDescripcion(jsonObject.getString("descripcion"));
                            producto.setPrecio((float) jsonObject.getDouble("precio"));

                            // Asigna el nombre de la imagen basado en el nombre del producto
                            String nombreProducto = jsonObject.getString("nombre").toLowerCase().replace(" ", "_");
                            producto.setImagenNombre(nombreProducto);

                            productos.add(producto);
                        }

                        new Handler(Looper.getMainLooper()).post(() -> {
                            adapter.setProductos(productos);
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
     * Este método actualiza el total del carrito de compras.
     * @return El total del carrito de compras como una cadena.
     */
    public String actualizarTotal() {
        float total = adapter.calcularTotal();
        Log.d("Lista", "Total updated: " + total);
        TextView totalPrecio = findViewById(R.id.totalPrecio);
        // Formatea el total a un String con 2 decimales
        String totalStr = String.format("%.2f", total);
        runOnUiThread(() -> totalPrecio.setText("Total: " + totalStr + "€"));
        return totalStr;
    }

    /**
     * Esta interfaz define un callback para obtener el número de habitación.
     */
    public interface HabitacionCallback {
        void onHabitacionObtenida(int nhabitacion);
    }

    /**
     * Este método obtiene el número de habitación del usuario.
     * @param callback El callback a llamar cuando se obtiene el número de habitación.
     */
    public void obtenerHabitacionUsuario(HabitacionCallback callback) {
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
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        JSONArray jsonArray = jsonResponse.getJSONArray("infoHabitaciones");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String usuario = jsonObject.getString("usuario");
                            if (usuario.equals(UserSession.getUsername())) {
                                int nhabitacion = jsonObject.getInt("nhabitacion");
                                callback.onHabitacionObtenida(nhabitacion);
                                break;
                            }
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