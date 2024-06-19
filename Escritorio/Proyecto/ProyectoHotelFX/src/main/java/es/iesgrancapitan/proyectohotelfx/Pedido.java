package es.iesgrancapitan.proyectohotelfx;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Clase Pedido
 */
public class Pedido {
    private int idPedido;
    private int nHabitacion;
    private LocalDate fechaPedido;
    private String estado;
    private float total;

    /**
     * Constructor de la clase Pedido
     * @param idPedido
     * @param nHabitacion
     * @param fechaPedido
     * @param estado
     * @param total
     */
    public Pedido(int idPedido, int nHabitacion, LocalDateTime fechaPedido, String estado, float total) {
        this.idPedido = idPedido;
        this.nHabitacion = nHabitacion;
        this.fechaPedido = LocalDate.from(fechaPedido);
        this.estado = estado;
        this.total = total;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public int getNHabitacion() {
        return nHabitacion;
    }

    public LocalDate getFechaPedido() {
        return fechaPedido;
    }

    public String getEstado() {
        return estado;
    }

    public float getTotal() {
        return total;
    }

}