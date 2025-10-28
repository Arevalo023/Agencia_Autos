package model;

public class Refaccion {

    private int idRefaccion;
    private String clave;
    private String descripcion;
    private double precioUnitario;
    private boolean activo;

    public Refaccion() {
    }

    public Refaccion(int idRefaccion, String clave,
                     String descripcion, double precioUnitario,
                     boolean activo) {
        this.idRefaccion = idRefaccion;
        this.clave = clave;
        this.descripcion = descripcion;
        this.precioUnitario = precioUnitario;
        this.activo = activo;
    }

    public int getIdRefaccion() {
        return idRefaccion;
    }

    public void setIdRefaccion(int idRefaccion) {
        this.idRefaccion = idRefaccion;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        // Ejemplo: "BOMBA-ACEITE - $350.00"
        return clave + " - " + descripcion;
    }
}
