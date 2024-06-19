package com.example.hotelapp.Objetos;

/**
 * Esta clase representa un Producto en la aplicación.
 * Contiene campos para el id, nombre, descripción, precio e imagen del producto.
 */
public class Producto {
    private int id; // El ID del producto
    private String nombre; // El nombre del producto
    private String descripcion; // La descripción del producto
    private float precio; // El precio del producto
    private String imagenNombre; // El nombre de la imagen del producto

    /**
     * Constructor de la clase Producto.
     * @param id El ID del producto.
     * @param nombre El nombre del producto.
     * @param descripcion La descripción del producto.
     * @param precio El precio del producto.
     * @param imagenNombre El nombre de la imagen del producto.
     */
    public Producto(int id, String nombre, String descripcion, float precio, String imagenNombre) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.imagenNombre = imagenNombre;
    }

    /**
     * Constructor vacío de la clase Producto.
     */
    public Producto(){

    }

    /**
     * Método getter para el ID del producto.
     * @return El ID del producto.
     */
    public int getId() {
        return id;
    }

    /**
     * Método setter para el ID del producto.
     * @param id El nuevo ID del producto.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Método getter para el nombre del producto.
     * @return El nombre del producto.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Método setter para el nombre del producto.
     * @param nombre El nuevo nombre del producto.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Método getter para la descripción del producto.
     * @return La descripción del producto.
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Método setter para la descripción del producto.
     * @param descripcion La nueva descripción del producto.
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Método getter para el precio del producto.
     * @return El precio del producto.
     */
    public float getPrecio() {
        return precio;
    }

    /**
     * Método setter para el precio del producto.
     * @param precio El nuevo precio del producto.
     */
    public void setPrecio(float precio) {
        this.precio = precio;
    }

    /**
     * Método getter para el nombre de la imagen del producto.
     * @return El nombre de la imagen del producto.
     */
    public String getImagenNombre() {
        return imagenNombre;
    }

    /**
     * Método setter para el nombre de la imagen del producto.
     * @param imagenNombre El nuevo nombre de la imagen del producto.
     */
    public void setImagenNombre(String imagenNombre) {
        this.imagenNombre = imagenNombre;
    }
}