package com.example.hotelapp.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelapp.Objetos.Producto;
import com.example.hotelapp.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ProductoAdapter es un adaptador personalizado para un RecyclerView que muestra una lista de productos.
 */
public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ViewHolder> {
    private List<Producto> productos;
    private Map<Integer, Integer> cesta = new HashMap<>();
    private OnQuantityChangeListener listener;

    /**
     * Establece el listener para cambios en la cantidad de productos.
     *
     * @param listener El listener para cambios en la cantidad de productos.
     */
    public void setOnQuantityChangeListener(OnQuantityChangeListener listener) {
        this.listener = listener;
    }

    /**
     * Constructor para ProductoAdapter.
     *
     * @param productos Lista de productos a mostrar.
     */
    public ProductoAdapter(List<Producto> productos) {
        this.productos = productos;
    }

    /**
     * Establece la lista de productos a mostrar.
     *
     * @param productos Lista de productos a mostrar.
     */
    public void setProductos(List<Producto> productos) {
        this.productos = productos;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.producto_item, parent, false);
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
        Producto producto = productos.get(position);
        holder.bind(producto);
    }

    /**
     * Este método devuelve el número total de elementos en el conjunto de datos que posee el adaptador.
     * @return El número total de elementos en este adaptador.
     */
    @Override
    public int getItemCount() {
        return productos.size();
    }

    /**
     * Devuelve la cesta de la compra actual.
     *
     * @return La cesta de la compra actual.
     */
    public Map<Integer, Integer> getCesta() {
        return cesta;
    }

    /**
     * Proporciona una referencia directa a cada uno de los elementos de datos en un elemento de vista
     * para un acceso fácil a los datos que puede actualizar la interfaz de usuario.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombreProducto;
        TextView precioProducto;
        ImageView imgAñadir;
        ImageView imgRestar;
        TextView cantidadProducto;
        ImageView imagenProducto;

        /**
         * Constructor para ViewHolder.
         * @param itemView La vista que se utilizará para representar este ViewHolder.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreProducto = itemView.findViewById(R.id.nombreProducto);
            precioProducto = itemView.findViewById(R.id.precioProducto);
            imgAñadir = itemView.findViewById(R.id.imgAñadir);
            imgRestar = itemView.findViewById(R.id.imgRestar);
            cantidadProducto = itemView.findViewById(R.id.cantidadProducto);
            imagenProducto = itemView.findViewById(R.id.imagenProducto);
        }

        /**
         * Vincula los datos del producto a los elementos de la vista.
         *
         * @param producto El producto a vincular.
         */
        public void bind(Producto producto) {
            nombreProducto.setText(producto.getNombre());
            precioProducto.setText(String.valueOf(producto.getPrecio()));

            String imagenNombre = producto.getImagenNombre();
            if (imagenNombre != null && !imagenNombre.isEmpty()) {
                int imagenResId = itemView.getContext().getResources().getIdentifier(imagenNombre, "drawable", itemView.getContext().getPackageName());
                Log.d("ProductoAdapter", "Nombre de la imagen: " + imagenNombre + ", ID del recurso: " + imagenResId);
                if (imagenResId != 0) {
                    imagenProducto.setImageResource(imagenResId);
                } else {
                    imagenProducto.setImageResource(R.drawable.default_image);
                    Log.d("ProductoAdapter", "Imagen no encontrada para: " + imagenNombre + ", usando imagen por defecto.");
                }
            } else {
                imagenProducto.setImageResource(R.drawable.default_image);
                Log.d("ProductoAdapter", "Nombre de imagen vacío o nulo, usando imagen por defecto.");
            }

            cantidadProducto.setText(String.valueOf(cesta.getOrDefault(producto.getId(), 0)));

            imgAñadir.setOnClickListener(v -> {
                int cantidad = cesta.getOrDefault(producto.getId(), 0) + 1;
                cesta.put(producto.getId(), cantidad);
                cantidadProducto.setText(String.valueOf(cantidad));
                if (listener != null) {
                    listener.onQuantityChange();
                }
            });

            imgRestar.setOnClickListener(v -> {
                int cantidad = Math.max(0, cesta.getOrDefault(producto.getId(), 0) - 1);
                cesta.put(producto.getId(), cantidad);
                cantidadProducto.setText(String.valueOf(cantidad));
                if (listener != null) {
                    listener.onQuantityChange();
                }
            });
        }

    }

    /**
     * Busca un producto por su ID.
     *
     * @param id El ID del producto a buscar.
     * @return El producto con el ID dado, o null si no se encuentra.
     */
    public Producto findProductoById(int id) {
        for (Producto producto : productos) {
            if (producto.getId() == id) {
                return producto;
            }
        }
        return null;
    }

    /**
     * Calcula el total de la cesta de la compra.
     *
     * @return El total de la cesta de la compra.
     */
    public float calcularTotal() {
        float total = 0;
        for (Map.Entry<Integer, Integer> entry : cesta.entrySet()) {
            Producto producto = findProductoById(entry.getKey());
            total += producto.getPrecio() * entry.getValue();
        }
        return total;
    }

    /**
     * Interfaz para escuchar cambios en la cantidad de productos.
     */
    public interface OnQuantityChangeListener {
        void onQuantityChange();
    }

}