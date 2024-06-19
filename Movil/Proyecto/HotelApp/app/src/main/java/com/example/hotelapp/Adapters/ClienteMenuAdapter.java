package com.example.hotelapp.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelapp.Lista;
import com.example.hotelapp.Objetos.MenuOption;
import com.example.hotelapp.R;
import com.example.hotelapp.Resenas;

import java.util.List;

/**
 * ClienteMenuAdapter es un adaptador personalizado para un RecyclerView que muestra una lista de opciones de menú.
 */
public class ClienteMenuAdapter extends RecyclerView.Adapter<ClienteMenuAdapter.ViewHolder> {
    private List<MenuOption> menuOptions;
    private Context context;
    private Animation scaleUp;
    private Animation scaleDown;

    /**
     * Constructor para ClienteMenuAdapter.
     *
     * @param menuOptions Lista de opciones de menú a mostrar.
     * @param context     Contexto en el que se utiliza el adaptador.
     */
    @SuppressLint("ResourceType")
    public ClienteMenuAdapter(List<MenuOption> menuOptions, Context context) {
        this.menuOptions = menuOptions;
        this.context = context;

        scaleUp = AnimationUtils.loadAnimation(context, R.animator.scale_up);
        scaleDown = AnimationUtils.loadAnimation(context, R.animator.scale_down);
    }

    /**
     * Este método se llama cuando RecyclerView necesita un nuevo ViewHolder del tipo dado para representar un elemento.
     * Este nuevo ViewHolder debe ser construido con una nueva vista que puede representar los elementos del tipo dado.
     * @param parent El ViewGroup en el que se agregará la nueva vista después de que esté vinculada a una posición de adaptador.
     * @param viewType El tipo de vista de la nueva vista.
     * @return Un nuevo ViewHolder que contiene una vista del tipo de vista dado.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Este método se llama por RecyclerView para mostrar los datos en la posición especificada.
     * Este método debe actualizar el contenido de itemView para reflejar el elemento en la posición dada.
     * @param holder El ViewHolder que debe actualizarse para representar el contenido del elemento en la posición dada en el conjunto de datos.
     * @param position La posición del elemento dentro del conjunto de datos del adaptador.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MenuOption menuOption = menuOptions.get(position);
        holder.title.setText(menuOption.getTitle());
        holder.image.setImageResource(menuOption.getImageResId());

        // Crea un OnTouchListener
        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.startAnimation(scaleUp);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.startAnimation(scaleDown);
                }
                return false;
            }
        };

        // Asigna el OnTouchListener a cada elemento del RecyclerView
        holder.itemView.setOnTouchListener(onTouchListener);

        holder.itemView.setOnClickListener(v -> {
            if (menuOption.getTitle().equals("Pedir")) {
                Intent intent = new Intent(context, Lista.class);
                context.startActivity(intent);
            }
            else if(menuOption.getTitle().equals("Reseñas")){
                Intent intent = new Intent(context, Resenas.class);
                context.startActivity(intent);
            }
            /*else if(menuOption.getTitle().equals("Reservas")){
                Intent intent = new Intent(context, Reservas.class);
                context.startActivity(intent);
            }*/
        });
    }

    /**
     * Este método devuelve el número total de elementos en el conjunto de datos que posee el adaptador.
     * @return El número total de elementos en este adaptador.
     */
    @Override
    public int getItemCount() {
        return menuOptions.size();
    }

    /**
     * Proporciona una referencia directa a cada uno de los elementos de datos en un elemento de vista
     * para un acceso fácil a los datos que puede actualizar la interfaz de usuario.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView image;

        /**
         * Constructor para ViewHolder.
         * @param itemView La vista que se utilizará para representar este ViewHolder.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_title);
            image = itemView.findViewById(R.id.item_image);
        }
    }
}