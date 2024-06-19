package es.iesgrancapitan.proyectohotelfx;

/**
 * Clase Cliente
 */
public class Cliente {
    private String nif;
    private String tipoDocumento;
    private String nombre;
    private String apellido1;
    private String apellido2;
    private String sexo;
    private String nacimiento;
    private String nacionalidad;

    /**
     * Constructor de la clase Cliente
     * @param nif
     * @param tipoDocumento
     * @param nombre
     * @param apellido1
     * @param apellido2
     * @param sexo
     * @param nacimiento
     * @param nacionalidad
     */
    public Cliente(String nif, String tipoDocumento, String nombre, String apellido1, String apellido2, String sexo, String nacimiento, String nacionalidad) {
        this.nif = nif;
        this.tipoDocumento = tipoDocumento;
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
        this.sexo = sexo;
        this.nacimiento = nacimiento;
        this.nacionalidad = nacionalidad;
    }

    public String getNIF() {
        return nif;
    }

    public String getTipodocumento() {
        return tipoDocumento;
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

    public String getSexo() {
        return sexo;
    }

    public String getNacimiento() {
        return nacimiento;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    @Override
    public String toString() {
        return nif + " | " + nombre + " " + apellido1 + " " + apellido2;
    }
}
