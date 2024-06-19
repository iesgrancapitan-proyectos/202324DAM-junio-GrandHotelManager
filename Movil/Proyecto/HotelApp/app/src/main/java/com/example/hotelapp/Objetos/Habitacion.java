package com.example.hotelapp.Objetos;

/**
 * La clase Habitacion representa una habitación en un hotel.
 */
public class Habitacion {
    private int nhabitacion;
    private int npersonas;
    private int ncamas;
    private int metroscuadrados;
    private int precio;
    private String estado;
    private String usuario;

    /**
     * Constructor para la clase Habitacion.
     *
     * @param nhabitacion El número de la habitación.
     * @param npersonas El número de personas que pueden alojarse en la habitación.
     * @param ncamas El número de camas en la habitación.
     * @param metroscuadrados El tamaño de la habitación en metros cuadrados.
     * @param precio El precio de la habitación por noche.
     * @param estado El estado actual de la habitación (por ejemplo, "disponible", "ocupado").
     * @param usuario El usuario que ha reservado la habitación.
     */
    public Habitacion(int nhabitacion, int npersonas, int ncamas, int metroscuadrados, int precio, String estado, String usuario) {
        this.nhabitacion = nhabitacion;
        this.npersonas = npersonas;
        this.ncamas = ncamas;
        this.metroscuadrados = metroscuadrados;
        this.precio = precio;
        this.estado = estado;
        this.usuario = usuario;
    }

    /**
     * Constructor vacío para la clase Habitacion.
     */
    public Habitacion(){

    }

    // Getters y setters para los atributos de la clase Habitacion.

    public int getNhabitacion() {
        return nhabitacion;
    }

    public void setNhabitacion(int nhabitacion) {
        this.nhabitacion = nhabitacion;
    }

    public int getNpersonas() {
        return npersonas;
    }

    public void setNpersonas(int npersonas) {
        this.npersonas = npersonas;
    }

    public int getNcamas() {
        return ncamas;
    }

    public void setNcamas(int ncamas) {
        this.ncamas = ncamas;
    }

    public int getMetroscuadrados() {
        return metroscuadrados;
    }

    public void setMetroscuadrados(int metroscuadrados) {
        this.metroscuadrados = metroscuadrados;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getUsuario(){
        return usuario;
    }

    public void setUsuario(String usuario){
        this.usuario = usuario;
    }
}