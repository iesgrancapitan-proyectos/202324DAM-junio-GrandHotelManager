package es.iesgrancapitan.proyectohotelfx;

import java.time.LocalDate;

/*+
    * Clase Reserva

 */
public class Reserva {
    private int idReserva;
    private int nHabitacion;
    private LocalDate fecha;
    private LocalDate fechaEntrada;
    private LocalDate fechaSalida;
    private String estado;
    private float pago;
    private String metodopago;
    private String observaciones;

    /**
     * Constructor de la clase Reserva
     * @param idReserva
     * @param nHabitacion
     * @param fecha
     * @param fechaEntrada
     * @param fechaSalida
     * @param pago
     * @param metodopago
     * @param observaciones
     */
    public Reserva(int idReserva, int nHabitacion, LocalDate fecha,LocalDate fechaEntrada, LocalDate fechaSalida, float pago, String metodopago, String observaciones) {
        this.idReserva = idReserva;
        this.nHabitacion = nHabitacion;
        this.fecha = fecha;
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.pago = pago;
        this.metodopago = metodopago;
        this.observaciones = observaciones;
    }

    public int getIdReserva() {
        return idReserva;
    }

    public int getNHabitacion() {
        return nHabitacion;
    }
    public LocalDate getFecha() {
        return fecha;
    }

    public LocalDate getFechaEntrada() {
        return fechaEntrada;
    }

    public LocalDate getFechaSalida() {
        return fechaSalida;
    }

    public String getEstado() {
        return estado;
    }

    public float getPago() {
        return pago;
    }

    public String getMetodopago() {
        return metodopago;
    }

    public String getObservaciones() {
        return observaciones;
    }


}