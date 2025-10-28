package model.state;
 
import model.EstatusOrden;
 
public class EstadoFinalizado implements EstadoOrden {
 
    @Override

    public EstatusOrden getEstatus() {

        return EstatusOrden.FINALIZADO;

    }
 
    @Override

    public boolean puedePasarAEnProceso() {

        // FINALIZADO -> EN_PROCESO = NO

        return false;

    }
 
    @Override

    public boolean puedePasarAFinalizado() {

        // ya está finalizado, no avanza más

        return false;

    }
 
    @Override

    public String getMensajeErrorTransicion() {

        return "La orden está FINALIZADA. No se puede reabrir ni modificar estatus.";

    }

}

 