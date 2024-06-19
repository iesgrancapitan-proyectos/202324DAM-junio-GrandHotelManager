package com.example.hotelapp.Adapters;

import android.animation.AnimatorInflater;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelapp.Objetos.Habitacion;
import com.example.hotelapp.Objetos.Reserva;
import com.example.hotelapp.R;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HabitacionesAdapter es un adaptador personalizado para un RecyclerView que muestra una lista de habitaciones.
 */
public class HabitacionesAdapter extends RecyclerView.Adapter<HabitacionesAdapter.ViewHolder> {
    private List<Habitacion> habitaciones;
    private Context context;
    private List<Reserva> reservas = new ArrayList<>();
    private Map<Integer, Reserva> reservaPorHabitacion = new HashMap<>(); // Define reservaPorHabitacion here

    /**
     * Actualiza la lista de reservas y el mapa de reservas por habitación.
     *
     * @param reservas La nueva lista de reservas.
     */
    public void setReservas(List<Reserva> reservas) {
        this.reservas = reservas;
        // Actualiza reservaPorHabitacion con las nuevas reservas
        for (Reserva reserva : reservas) {
            reservaPorHabitacion.put(reserva.getnHabitacion(), reserva);
        }
    }

    /**
     * Constructor para HabitacionesAdapter.
     *
     * @param habitaciones Lista de habitaciones a mostrar.
     * @param context      Contexto en el que se utiliza el adaptador.
     */
    public HabitacionesAdapter(List<Habitacion> habitaciones, Context context) {
        this.habitaciones = habitaciones;
        this.context = context;
    }

    /**
     * Actualiza la lista de habitaciones.
     *
     * @param habitaciones La nueva lista de habitaciones.
     */
    public void setHabitaciones(List<Habitacion> habitaciones) {
        this.habitaciones = habitaciones;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.habitacion_item, parent, false);
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
        Habitacion habitacion = habitaciones.get(position);
        holder.bind(habitacion);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.itemView.setStateListAnimator(AnimatorInflater.loadStateListAnimator(context, R.animator.scale_on_press));
        }
    }

    /**
     * Este método devuelve el número total de elementos en el conjunto de datos que posee el adaptador.
     * @return El número total de elementos en este adaptador.
     */
    @Override
    public int getItemCount() {
        return habitaciones.size();
    }

    /**
     * Proporciona una referencia directa a cada uno de los elementos de datos en un elemento de vista para un acceso fácil a los datos que puede actualizar la interfaz de usuario.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nhabitacion;
        ImageView cama;
        TextView estado;
        RelativeLayout itemView;

        /**
         * Constructor para ViewHolder.
         * @param itemView La vista que se utilizará para representar este ViewHolder.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nhabitacion = itemView.findViewById(R.id.tv_nhabitacion);
            cama = itemView.findViewById(R.id.iv_cama);
            estado = itemView.findViewById(R.id.tv_estado);
            this.itemView = itemView.findViewById(R.id.item_view);
        }

        /**
         * Vincula los datos de la habitación a los elementos de la vista.
         * @param habitacion Los datos de la habitación.
         */
        @SuppressLint("SetTextI18n")
        public void bind(Habitacion habitacion) {
            GradientDrawable gradient;
            nhabitacion.setText(String.valueOf(habitacion.getNhabitacion()));
            // Habitación disponible
            estado.setText("Disponible");
            gradient = new GradientDrawable(
                    GradientDrawable.Orientation.BOTTOM_TOP,
                    new int[] {Color.parseColor("#d0b05d"), Color.GREEN});
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDate hoy = LocalDate.now();
                for (Reserva reserva : reservas) {
                    if (reserva.getnHabitacion() == habitacion.getNhabitacion()) {
                        System.out.println("1.-" + reserva.getnHabitacion() + "2.-" + habitacion.getNhabitacion());
                        if ((hoy.isAfter(reserva.getFecha_entrada()) || hoy.isEqual(reserva.getFecha_entrada())) && (hoy.isBefore(reserva.getFecha_salida()) || hoy.isEqual(reserva.getFecha_salida()))) {
                            // Habitación ocupada
                            estado.setText("Ocupado");
                            gradient = new GradientDrawable(
                                    GradientDrawable.Orientation.BOTTOM_TOP,
                                    new int[] {Color.parseColor("#d0b05d"), Color.RED});
                        }
                    }

                }
                gradient.setCornerRadius(30f);
                this.itemView.setBackgroundDrawable(gradient);
            }

            itemView.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                // Infla el layout personalizado
                View view = LayoutInflater.from(context).inflate(R.layout.dialog_habitacion, null);

                // Configura los TextViews y el Button del layout personalizado
                TextView tvTitle = view.findViewById(R.id.tv_title);
                TextView tvNhabitacion = view.findViewById(R.id.tv_nhabitacion);
                TextView tvNpersonas = view.findViewById(R.id.tv_npersonas);
                TextView tvNcamas = view.findViewById(R.id.tv_ncamas);
                TextView tvMetroscuadrados = view.findViewById(R.id.tv_metroscuadrados);
                TextView tvPrecio = view.findViewById(R.id.tv_precio);
                Button btnOk = view.findViewById(R.id.btn_ok);

                tvTitle.setText("Información de la habitación");
                tvNhabitacion.setText("Número de habitación: " + habitacion.getNhabitacion());
                tvNpersonas.setText("Número de personas: " + habitacion.getNpersonas());
                tvNcamas.setText("Número de camas: " + habitacion.getNcamas());
                tvMetroscuadrados.setText("Metros cuadrados: " + habitacion.getMetroscuadrados() + " m²");
                tvPrecio.setText("Precio: " + habitacion.getPrecio() +"€");

                // Configura el AlertDialog para usar el layout personalizado
                builder.setView(view);

                // Crea y muestra el AlertDialog
                AlertDialog dialog = builder.create();
                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }
                dialog.show();

                // Configura el botón de OK para cerrar el AlertDialog cuando se presione
                btnOk.setOnClickListener(v1 -> dialog.dismiss());

            });
        }

    }
}