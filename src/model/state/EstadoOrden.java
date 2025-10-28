package model.state;
 
import model.EstatusOrden;
 
/**

* EstadoOrden

* 

* Cada implementaci�n representa un estado posible de la orden

* (EN_ESPERA, EN_PROCESO, FINALIZADO)

*

* Cada estado define qu� transiciones son v�lidas.

*/

public interface EstadoOrden {
 
    /**

     * Devuelve el estatus l�gico que representa este objeto

     * (por ejemplo EstatusOrden.EN_ESPERA).

     */

    EstatusOrden getEstatus();
 
    /**

     * �Puedo pasar a EN_PROCESO desde el estado actual?

     */

    boolean puedePasarAEnProceso();
 
    /**

     * �Puedo pasar a FINALIZADO desde el estado actual?

     */

    boolean puedePasarAFinalizado();
 
    /**

     * Mensaje amigable si intento una transici�n inv�lida.

     */

    String getMensajeErrorTransicion();

}

 