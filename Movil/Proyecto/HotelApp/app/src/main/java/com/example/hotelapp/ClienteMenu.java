package com.example.hotelapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelapp.Adapters.ClienteMenuAdapter;
import com.example.hotelapp.Objetos.MenuOption;

import java.util.ArrayList;
import java.util.List;

/**
 * Esta clase representa la actividad del menú del cliente en la aplicación.
 * Extiende AppCompatActivity, lo que indica que esta es una clase de Actividad.
 */
public class ClienteMenu extends AppCompatActivity {
    private RecyclerView recyclerView; // RecyclerView para mostrar los elementos del menú
    private ClienteMenuAdapter adapter; // Adaptador para el RecyclerView
    private List<MenuOption> menuItems; // Lista de elementos del menú
    private Toolbar toolbar; // Toolbar de la actividad
    private TextView toolbarTitle; // Título de la Toolbar

    /**
     * Este método se llama cuando la actividad está iniciando.
     * Aquí es donde ocurre la mayoría de la inicialización.
     * @param savedInstanceState Si la actividad se está reinicializando después de haber sido cerrada previamente, este Bundle contiene los datos que suministró más recientemente en onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.menu_cliente);

        // Configura la Toolbar
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title);

        // Agrega un margen superior a la Toolbar igual a la altura de la barra de estado
        int statusBarHeight = getResources().getDimensionPixelSize(
                getResources().getIdentifier("status_bar_height", "dimen", "android"));
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) toolbar.getLayoutParams();
        params.setMargins(0, statusBarHeight, 0, 0);
        toolbar.setLayoutParams(params);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Inicializa la lista de elementos del menú
        menuItems = new ArrayList<>();
        menuItems.add(new MenuOption("Pedir", R.drawable.pedir));
        menuItems.add(new MenuOption("Reseñas", R.drawable.resena));

        // Configura el RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ClienteMenuAdapter(menuItems, this);
        recyclerView.setAdapter(adapter);
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