package com.example.hotelapp.Objetos;

import java.time.LocalDate;

/**
 * Esta clase representa una Reserva en la aplicación.
 * Contiene campos para el id de la reserva, número de habitación, fechas, pago, método de pago, observaciones y factura.
 */
public class Reserva {
    private int idReserva; // El ID de la reserva
    private int nHabitacion; // El número de la habitación
    private LocalDate fecha; // La fecha de la reserva
    private LocalDate fecha_entrada; // La fecha de entrada
    private LocalDate fecha_salida; // La fecha de salida
    private int pago; // El pago de la reserva
    private String metodoPago; // El método de pago
    private String observaciones; // Las observaciones de la reserva
    private byte[] factura; // La factura de la reserva

    /**
     * Constructor de la clase Reserva.
     * @param idReserva El ID de la reserva.
     * @param nHabitacion El número de la habitación.
     * @param fecha La fecha de la reserva.
     * @param fecha_entrada La fecha de entrada.
     * @param fecha_salida La fecha de salida.
     * @param pago El pago de la reserva.
     * @param metodoPago El método de pago.
     * @param observaciones Las observaciones de la reserva.
     * @param factura La factura de la reserva.
     */
    public Reserva(int idReserva, int nHabitacion, LocalDate fecha, LocalDate fecha_entrada, LocalDate fecha_salida, int pago, String metodoPago, String observaciones, byte[] factura) {
        this.idReserva = idReserva;
        this.nHabitacion = nHabitacion;
        this.fecha = fecha;
        this.fecha_entrada = fecha_entrada;
        this.fecha_salida = fecha_salida;
        this.pago = pago;
        this.metodoPago = metodoPago;
        this.observaciones = observaciones;
        this.factura = factura;
    }

    /**
     * Constructor vacío de la clase Reserva.
     */
    public Reserva() {

    }

    // Métodos getter y setter para los campos de la clase Reserva.

    public int getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(int idReserva) {
        this.idReserva = idReserva;
    }

    public int getnHabitacion() {
        return nHabitacion;
    }

    public void setnHabitacion(int nHabitacion) {
        this.nHabitacion = nHabitacion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalDate getFecha_entrada() {
        return fecha_entrada;
    }

    public void setFecha_entrada(LocalDate fecha_entrada) {
        this.fecha_entrada = fecha_entrada;
    }

    public LocalDate getFecha_salida() {
        return fecha_salida;
    }

    public void setFecha_salida(LocalDate fecha_salida) {
        this.fecha_salida = fecha_salida;
    }

    public int getPago() {
        return pago;
    }

    public void setPago(int pago) {
        this.pago = pago;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public byte[] getFactura() {
        return factura;
    }

    public void setFactura(byte[] factura) {
        this.factura = factura;
    }
}