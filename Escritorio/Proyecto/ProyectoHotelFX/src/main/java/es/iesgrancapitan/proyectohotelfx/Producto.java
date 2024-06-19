package es.iesgrancapitan.proyectohotelfx;

/*+
    * Clase Producto
 */
public class Producto {
    private int idProducto;
    private String nombre;
    private String descripcion;
    private float precio;
    private int cantidad;

    /**
     * Constructor de la clase Producto
     * @param idProducto
     * @param nombre
     * @param descripcion
     * @param precio
     * @param cantidad
     */
    public Producto(int idProducto, String nombre, String descripcion, float precio, int cantidad) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.cantidad = cantidad;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public float getPrecio() {
        return precio;
    }

    public int getCantidad() {
        return cantidad;
    }
}
