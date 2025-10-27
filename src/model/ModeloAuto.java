package model;

public class ModeloAuto {

    private int idModelo;
    private Marca marca;  // relación con Marca
    private String nombre;
    private boolean activo;

    public ModeloAuto() {
    }

    public ModeloAuto(int idModelo, Marca marca,
                      String nombre, boolean activo) {
        this.idModelo = idModelo;
        this.marca = marca;
        this.nombre = nombre;
        this.activo = activo;
    }

    public int getIdModelo() {
        return idModelo;
    }

    public void setIdModelo(int idModelo) {
        this.idModelo = idModelo;
    }

    public Marca getMarca() {
        return marca;
    }

    public void setMarca(Marca marca) {
        this.marca = marca;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        // Ejemplo: "Nissan - Versa"
        return (marca != null ? marca.getNombre() + " - " : "") + nombre;
    }
}

