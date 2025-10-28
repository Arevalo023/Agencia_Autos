package model;

public class Tecnico {

    private int idTecnico;
    private String noEmpleado;
    private String nombre;
    private String telefono;
    private String email;
    private boolean activo;

    public Tecnico() {
    }

    public Tecnico(int idTecnico, String noEmpleado,
                   String nombre, String telefono,
                   String email, boolean activo) {
        this.idTecnico = idTecnico;
        this.noEmpleado = noEmpleado;
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
        this.activo = activo;
    }

    public int getIdTecnico() {
        return idTecnico;
    }

    public void setIdTecnico(int idTecnico) {
        this.idTecnico = idTecnico;
    }

    public String getNoEmpleado() {
        return noEmpleado;
    }

    public void setNoEmpleado(String noEmpleado) {
        this.noEmpleado = noEmpleado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        // Ejemplo: "TEC123 - Juan Pérez"
        return noEmpleado + " - " + nombre;
    }
}
