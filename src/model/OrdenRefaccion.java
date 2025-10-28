package model;

public class OrdenRefaccion {

    private int idOrden;        // FK a ordenes.id_orden
    private Refaccion refaccion;
    private int cantidad;
    private double precioUnitario;

    public OrdenRefaccion() {}

    public OrdenRefaccion(int idOrden, Refaccion refaccion, int cantidad, double precioUnitario) {
        this.idOrden = idOrden;
        this.refaccion = refaccion;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public int getIdOrden() {
        return idOrden;
    }

    public void setIdOrden(int idOrden) {
        this.idOrden = idOrden;
    }

    public Refaccion getRefaccion() {
        return refaccion;
    }

    public void setRefaccion(Refaccion refaccion) {
        this.refaccion = refaccion;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public double getSubtotal() {
        return cantidad * precioUnitario;
    }

	public void setSubtotal(double double1) {
		// TODO Auto-generated method stub
		return;
	}
}
