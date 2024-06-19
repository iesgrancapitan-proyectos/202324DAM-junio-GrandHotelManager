package es.iesgrancapitan.proyectohotelfx;

/**
 * Clase Habitacion
 */
public class Habitacion {
    private int numero;
    private int nPersonas;
    private int nCamas;
    private int mCuadrados;
    private float precio;
    private String estado;

    /**
     * Constructor de la clase Habitacion
     * @param numero
     * @param nPersonas
     * @param nCamas
     * @param mCuadrados
     * @param precio
     * @param estado
     */
    public Habitacion(int numero, int nPersonas, int nCamas, int mCuadrados, float precio, String estado) {
        this.numero = numero;
        this.nPersonas = nPersonas;
        this.nCamas = nCamas;
        this.mCuadrados = mCuadrados;
        this.precio = precio;
        this.estado = estado;
    }

    public int getNumero() {
        return numero;
    }

    public int getnPersonas() {
        return nPersonas;
    }

    public int getnCamas() {
        return nCamas;
    }

    public int getmCuadrados() {
        return mCuadrados;
    }
    public float getPrecio() {
        return precio;
    }
    public String getEstado() {
        return estado;
    }
}