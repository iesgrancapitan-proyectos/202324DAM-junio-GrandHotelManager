package com.example.hotelapp.Adapters;

import android.animation.AnimatorInflater;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelapp.Objetos.Habitacion;
import com.example.hotelapp.Objetos.Reserva;
import com.example.hotelapp.R;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Clase adaptadora para manejar la visualización e interacción de una lista de objetos Habitacion en un RecyclerView.
 */
public class HabitacionesLimpiezaAdapter extends RecyclerView.Adapter<HabitacionesLimpiezaAdapter.ViewHolder> {
    private List<Habitacion> habitaciones;
    private Context context;
    private List<Reserva> reservas = new ArrayList<>();
    private Map<Integer, Reserva> reservaPorHabitacion = new HashMap<>(); // Define reservaPorHabitacion aquí
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    private OnItemClickListener listener;

    /**
     * Establece la lista de objetos Reserva y actualiza el mapa reservaPorHabitacion.
     * @param reservas Lista de objetos Reserva.
     */
    public void setReservas(List<Reserva> reservas) {
        this.reservas = reservas;
        // Actualiza reservaPorHabitacion con las nuevas reservas
        for (Reserva reserva : reservas) {
            reservaPorHabitacion.put(reserva.getnHabitacion(), reserva);
        }
    }

    /**
     * Constructor para la clase HabitacionesLimpiezaAdapter.
     * @param habitaciones Lista de objetos Habitacion.
     * @param context Contexto en el que se utiliza el adaptador.
     */
    public HabitacionesLimpiezaAdapter(List<Habitacion> habitaciones, Context context) {
        this.habitaciones = habitaciones;
        this.context = context;
    }

    /**
     * Establece la lista de objetos Habitacion.
     * @param habitaciones Lista de objetos Habitacion.
     */
    public void setHabitaciones(List<Habitacion> habitaciones) {
        this.habitaciones = habitaciones;
    }

    /**
     * Llamado cuando RecyclerView necesita un nuevo ViewHolder del tipo dado para representar un elemento.
     * @param parent El ViewGroup en el que se agregará la nueva Vista después de que se vincule a una posición de adaptador.
     * @param viewType El tipo de vista de la nueva Vista.
     * @return Un nuevo ViewHolder que contiene una Vista del tipo de vista dado.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.habitacion_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Llamado por RecyclerView para mostrar los datos en la posición especificada.
     * @param holder El ViewHolder que debe actualizarse para representar el contenido del elemento en la posición dada en el conjunto de datos.
     * @param position La posición del elemento dentro del conjunto de datos del adaptador.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Habitacion habitacion = habitaciones.get(position);
        holder.bind(habitacion);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.itemView.setStateListAnimator(AnimatorInflater.loadStateListAnimator(context, R.animator.scale_on_press));
        }
    }

    /**
     * Devuelve el número total de elementos en el conjunto de datos que mantiene el adaptador.
     * @return El número total de elementos en este adaptador.
     */
    @Override
    public int getItemCount() {
        return habitaciones.size();
    }

    /**
     * Interfaz para manejar eventos de clic en el elemento.
     */
    public interface OnItemClickListener {
        void onItemClick(int nhabitacion);
    }

    /**
     * Establece el OnItemClickListener.
     * @param listener El oyente que será notificado de los eventos de clic en el elemento.
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * Un ViewHolder describe una vista de elemento y metadatos sobre su lugar dentro del RecyclerView.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nhabitacion;
        ImageView cama;
        TextView estado;
        RelativeLayout itemView;

        /**
         * Constructor para la clase ViewHolder.
         * @param itemView La vista que este ViewHolder administrará.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.nhabitacion = itemView.findViewById(R.id.tv_nhabitacion);
            this.itemView = itemView.findViewById(R.id.item_view);
        }

        /**
         * Vincula un objeto Habitacion al ViewHolder.
         * @param habitacion El objeto Habitacion a vincular.
         */
        @SuppressLint("SetTextI18n")
        public void bind(Habitacion habitacion) {
            nhabitacion.setText("Habitación " + habitacion.getNhabitacion());
            // Cambia el color del gradiente en función del estado de la habitación
            GradientDrawable gradient;
            if ("Limpia".equals(habitacion.getEstado())) {
                gradient = new GradientDrawable(
                        GradientDrawable.Orientation.BOTTOM_TOP,
                        new int[]{Color.parseColor("#d0b05d"), Color.GREEN});
            } else {
                gradient = new GradientDrawable(
                        GradientDrawable.Orientation.BOTTOM_TOP,
                        new int[]{Color.parseColor("#d0b05d"), Color.RED});
            }
            gradient.setCornerRadius(30f);
            this.itemView.setBackgroundDrawable(gradient);

            // Agrega un OnClickListener para cambiar el estado de la habitación
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(habitacion.getNhabitacion());
                }
                if ("Sucia".equals(habitacion.getEstado())) {
                    habitacion.setEstado("Limpia");

                    // Crear el cuerpo de la solicitud
                    RequestBody formBody = new FormBody.Builder()
                            .add("token_auth", "0de6e41e85570bfcf0afc59179b6f480") // Reemplaza con tu token de autenticación
                            .add("nhabitacion", String.valueOf(habitacion.getNhabitacion()))
                            .add("estado", "Limpia")
                            .build();

                    // Crear la solicitud
                    Request request = new Request.Builder()
                            .url("http://34.175.164.212/hotel/cambiar-estado-hab.php") // Reemplaza con la URL de tu API
                            .post(formBody)
                            .build();

                    // Realizar la solicitud
                    OkHttpClient client = new OkHttpClient();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            if (response.isSuccessful()) {
                                // Después de actualizar la base de datos, cambia el color del gradiente a verde
                                GradientDrawable newGradient = new GradientDrawable(
                                        GradientDrawable.Orientation.BOTTOM_TOP,
                                        new int[]{Color.parseColor("#d0b05d"), Color.GREEN});
                                newGradient.setCornerRadius(30f);
                                itemView.post(() -> itemView.setBackgroundDrawable(newGradient));
                            }
                        }
                    });
                }
            });
        }
    }
}