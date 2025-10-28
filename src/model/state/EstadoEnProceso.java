package model.state;

import model.EstatusOrden;

public class EstadoEnProceso implements EstadoOrden {

   @Override
   public EstatusOrden getEstatus() {
       return EstatusOrden.EN_PROCESO;
   }

   @Override
   public boolean puedePasarAEnProceso() {
       // ya estoy en EN_PROCESO, no tiene sentido "pasar a EN_PROCESO otra vez"
       return false;
   }

   @Override
   public boolean puedePasarAFinalizado() {
       // EN_PROCESO -> FINALIZADO = válido
       return true;
   }

   @Override
   public String getMensajeErrorTransicion() {
       return "La orden ya está EN PROCESO. Solo puede avanzar a FINALIZADO.";
   }
}