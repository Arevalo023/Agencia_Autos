package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrdenServicio {

    private int idOrden;
    private String folio;

    private Cliente cliente;
    private Vehiculo vehiculo;
    private Tecnico tecnico;
    private TipoServicio tipoServicio;

    private EstatusOrden estatus;

    private double manoObra;
    private double totalRefacciones; // se recalcula con las líneas
    private LocalDate proximoServicio;
    private String notas;

    private Usuario creadoPor;         // created_by
    private Usuario actualizadoPor;    // updated_by
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaCierre;

    // detalle de refacciones usadas en esta orden
    private List<OrdenRefaccion> refacciones = new ArrayList<>();

    public OrdenServicio() {}

    public int getIdOrden() {
        return idOrden;
    }

    public void setIdOrden(int idOrden) {
        this.idOrden = idOrden;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public Tecnico getTecnico() {
        return tecnico;
    }

    public void setTecnico(Tecnico tecnico) {
        this.tecnico = tecnico;
    }

    public TipoServicio getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(TipoServicio tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public EstatusOrden getEstatus() {
        return estatus;
    }

    public void setEstatus(EstatusOrden estatus) {
        this.estatus = estatus;
    }

    public double getManoObra() {
        return manoObra;
    }

    public void setManoObra(double manoObra) {
        this.manoObra = manoObra;
    }

    public double getTotalRefacciones() {
        return totalRefacciones;
    }

    public void setTotalRefacciones(double totalRefacciones) {
        this.totalRefacciones = totalRefacciones;
    }

    public LocalDate getProximoServicio() {
        return proximoServicio;
    }

    public void setProximoServicio(LocalDate proximoServicio) {
        this.proximoServicio = proximoServicio;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public Usuario getCreadoPor() {
        return creadoPor;
    }

    public void setCreadoPor(Usuario creadoPor) {
        this.creadoPor = creadoPor;
    }



public Usuario getActualizadoPor() {
    return actualizadoPor;
}

public void setActualizadoPor(Usuario actualizadoPor) {
    this.actualizadoPor = actualizadoPor;
}

public LocalDateTime getFechaCreacion() {
    return fechaCreacion;
}

public void setFechaCreacion(LocalDateTime fechaCreacion) {
    this.fechaCreacion = fechaCreacion;
}

public LocalDateTime getFechaCierre() {
    return fechaCierre;
}

public void setFechaCierre(LocalDateTime fechaCierre) {
    this.fechaCierre = fechaCierre;
}


    public List<OrdenRefaccion> getRefacciones() {
        return refacciones;
    }

    public void setRefacciones(List<OrdenRefaccion> refacciones) {
        this.refacciones = refacciones;
    }

    public double getTotalGeneral() {
        // mano de obra + suma de refacciones
        return manoObra + totalRefacciones;
    }
    
    private String entregadoPor;

    public String getEntregadoPor() {
        return entregadoPor;
    }
    public void setEntregadoPor(String entregadoPor) {
        this.entregadoPor = entregadoPor;
    }


    @Override
    public String toString() {
        String clienteTxt = (cliente != null)
                ? cliente.getNombre() + " " + cliente.getApellidos()
                : "(sin cliente)";

        String placaTxt = (vehiculo != null)
                ? vehiculo.getPlaca()
                : "(sin vehículo)";

        return "Folio " + folio + " | " + clienteTxt + " | " + placaTxt + " | " + estatus;
    }
}
