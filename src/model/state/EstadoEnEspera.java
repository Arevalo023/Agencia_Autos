package model.state;
 
import model.EstatusOrden;
 
public class EstadoEnEspera implements EstadoOrden {
 
    @Override

    public EstatusOrden getEstatus() {

        return EstatusOrden.EN_ESPERA;

    }
 
    @Override

    public boolean puedePasarAEnProceso() {

        // EN_ESPERA -> EN_PROCESO = v�lido

        return true;

    }
 
    @Override

    public boolean puedePasarAFinalizado() {

        // EN_ESPERA -> FINALIZADO = NO, brincarse el proceso no es v�lido

        return false;

    }
 
    @Override

    public String getMensajeErrorTransicion() {

        return "La orden est� EN ESPERA. Primero debe pasar a EN PROCESO antes de finalizar.";

    }

}

 