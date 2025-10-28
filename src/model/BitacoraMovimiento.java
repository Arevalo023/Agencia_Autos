package model;

import java.time.LocalDateTime;

public class BitacoraMovimiento {

    private long idBitacora;
    private OrdenServicio orden;
    private Usuario usuario;          // quién hizo el cambio
    private String estatusAnterior;   // puede ser null la primera vez
    private String estatusNuevo;
    private LocalDateTime fechaEvento;
    private String nota;

    public BitacoraMovimiento() {
    }

    public BitacoraMovimiento(long idBitacora,
                              OrdenServicio orden,
                              Usuario usuario,
                              String estatusAnterior,
                              String estatusNuevo,
                              LocalDateTime fechaEvento,
                              String nota) {
        this.idBitacora = idBitacora;
        this.orden = orden;
        this.usuario = usuario;
        this.estatusAnterior = estatusAnterior;
        this.estatusNuevo = estatusNuevo;
        this.fechaEvento = fechaEvento;
        this.nota = nota;
    }

    public long getIdBitacora() {
        return idBitacora;
    }

    public void setIdBitacora(long idBitacora) {
        this.idBitacora = idBitacora;
    }

    public OrdenServicio getOrden() {
        return orden;
    }

    public void setOrden(OrdenServicio orden) {
        this.orden = orden;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getEstatusAnterior() {
        return estatusAnterior;
    }

    public void setEstatusAnterior(String estatusAnterior) {
        this.estatusAnterior = estatusAnterior;
    }

    public String getEstatusNuevo() {
        return estatusNuevo;
    }

    public void setEstatusNuevo(String estatusNuevo) {
        this.estatusNuevo = estatusNuevo;
    }

    public LocalDateTime getFechaEvento() {
        return fechaEvento;
    }

    public void setFechaEvento(LocalDateTime fechaEvento) {
        this.fechaEvento = fechaEvento;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    @Override
    public String toString() {
        // Ejemplo: "EN_PROCESO -> FINALIZADO (2025-10-25 13:40)"
        return estatusAnterior + " -> " + estatusNuevo + " (" + fechaEvento + ")";
    }
}
