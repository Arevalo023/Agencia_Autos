package model;
 
import java.time.LocalDateTime;
 
public class Cliente {
 
    private int idCliente;
    private String nombre;
    private String apellidos;
    private String telefono;
    private String email;
    private boolean activo;
    private LocalDateTime creadoEn;
 
    public Cliente() {
    }
 
    public Cliente(int idCliente, String nombre, String apellidos,
                   String telefono, String email,
                   boolean activo, LocalDateTime creadoEn) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.email = email;
        this.activo = activo;
        this.creadoEn = creadoEn;
    }
 
    public int getIdCliente() {
        return idCliente;
    }
 
    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }
 
    public String getNombre() {
        return nombre;
    }
 
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
 
    public String getApellidos() {
        return apellidos;
    }
 
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
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
 
    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }
 
    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }
 
    @Override
    public String toString() {
        return nombre + " " + apellidos;
    }
}