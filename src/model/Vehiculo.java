package model;
 
public class Vehiculo {
 
    private int idVehiculo;

    private Cliente cliente;          // id_cliente

    private ModeloAuto modelo;        // id_modelo

    private AnioModelo anioModelo;    // id_anio_modelo

    private String placa;

    private String vin;

    private String color;

    private Integer kilometraje;      // puede venir null en BD

    private boolean activo;
 
    public Vehiculo() {

    }
 
    public Vehiculo(int idVehiculo,

                    Cliente cliente,

                    ModeloAuto modelo,

                    AnioModelo anioModelo,

                    String placa,

                    String vin,

                    String color,

                    Integer kilometraje,

                    boolean activo) {

        this.idVehiculo = idVehiculo;

        this.cliente = cliente;

        this.modelo = modelo;

        this.anioModelo = anioModelo;

        this.placa = placa;

        this.vin = vin;

        this.color = color;

        this.kilometraje = kilometraje;

        this.activo = activo;

    }
 
    public int getIdVehiculo() {

        return idVehiculo;

    }
 
    public void setIdVehiculo(int idVehiculo) {

        this.idVehiculo = idVehiculo;

    }
 
    public Cliente getCliente() {

        return cliente;

    }
 
    public void setCliente(Cliente cliente) {

        this.cliente = cliente;

    }
 
    public ModeloAuto getModelo() {

        return modelo;

    }
 
    public void setModelo(ModeloAuto modelo) {

        this.modelo = modelo;

    }
 
    public AnioModelo getAnioModelo() {

        return anioModelo;

    }
 
    public void setAnioModelo(AnioModelo anioModelo) {

        this.anioModelo = anioModelo;

    }
 
    public String getPlaca() {

        return placa;

    }
 
    public void setPlaca(String placa) {

        this.placa = placa;

    }
 
    public String getVin() {

        return vin;

    }
 
    public void setVin(String vin) {

        this.vin = vin;

    }
 
    public String getColor() {

        return color;

    }
 
    public void setColor(String color) {

        this.color = color;

    }
 
    public Integer getKilometraje() {

        return kilometraje;

    }
 
    public void setKilometraje(Integer kilometraje) {

        this.kilometraje = kilometraje;

    }
 
    public boolean isActivo() {

        return activo;

    }
 
    public void setActivo(boolean activo) {

        this.activo = activo;

    }
 
// model/Vehiculo.java

    @Override

    public String toString() {

        String modeloTxt = (modelo != null ? modelo.getNombre() : "Modelo?");

        String anioTxt   = (anioModelo != null ? String.valueOf(anioModelo.getAnio()) : "Año?");

        return placa + " • " + modeloTxt + " " + anioTxt;

    }
 
}

 