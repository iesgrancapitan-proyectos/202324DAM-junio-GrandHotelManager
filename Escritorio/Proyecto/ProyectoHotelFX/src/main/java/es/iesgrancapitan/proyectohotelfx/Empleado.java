package es.iesgrancapitan.proyectohotelfx;

/**
 * Clase Empleado
 *
 */
public class Empleado {
    private int idempleado;
    private String NIF;
    private String nombre;
    private String apellido1;
    private String apellido2;
    private String usuario;


    /**
     * Constructor de la clase Empleado
     * @param idempleado
     * @param NIF
     * @param nombre
     * @param apellido1
     * @param apellido2
     * @param usuario
     */
    public Empleado(int idempleado, String NIF, String nombre, String apellido1, String apellido2, String usuario) {
        this.idempleado = idempleado;
        this.NIF = NIF;
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
        this.usuario = usuario;
    }

    public int getIdempleado() {
        return idempleado;
    }

    public String getNIF() {
        return NIF;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido1() {
        return apellido1;
    }

    public String getApellido2() {
        return apellido2;
    }

    public String getUsuario() {
        return usuario;
    }
}