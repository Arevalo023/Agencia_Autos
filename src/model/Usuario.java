package model;
 
import java.time.LocalDateTime;
 
public class Usuario {
 
    private int idUsuario;
    private String username;
    private String passwordHash;
    private String nombreCompleto;
    private String email;
    private String rol;        // 'ADMIN', 'TECNICO', 'CONSULTA'
    private boolean activo;
    private LocalDateTime creadoEn;
 
    public Usuario() {
    }
 
    public Usuario(int idUsuario, String username, String passwordHash,
                   String nombreCompleto, String email, String rol,
                   boolean activo, LocalDateTime creadoEn) {
        this.idUsuario = idUsuario;
        this.username = username;
        this.passwordHash = passwordHash;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.rol = rol;
        this.activo = activo;
        this.creadoEn = creadoEn;
    }
 
    public int getIdUsuario() {
        return idUsuario;
    }
 
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
 
    public String getUsername() {
        return username;
    }
 
    public void setUsername(String username) {
        this.username = username;
    }
 
    public String getPasswordHash() {
        return passwordHash;
    }
 
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
 
    public String getNombreCompleto() {
        return nombreCompleto;
    }
 
    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }
 
    public String getEmail() {
        return email;
    }
 
    public void setEmail(String email) {
        this.email = email;
    }
 
    public String getRol() {
        return rol;
    }
 
    public void setRol(String rol) {
        this.rol = rol;
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
        return nombreCompleto + " (" + rol + ")";
    }
}