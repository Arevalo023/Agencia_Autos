package model;

public class AnioModelo {

    private int idAnioModelo;
    private ModeloAuto modelo; // referencia al modelo
    private int anio;
    private boolean activo;

    public AnioModelo() {
    }

    public AnioModelo(int idAnioModelo, ModeloAuto modelo,
                      int anio, boolean activo) {
        this.idAnioModelo = idAnioModelo;
        this.modelo = modelo;
        this.anio = anio;
        this.activo = activo;
    }

    public int getIdAnioModelo() {
        return idAnioModelo;
    }

    public void setIdAnioModelo(int idAnioModelo) {
        this.idAnioModelo = idAnioModelo;
    }

    public ModeloAuto getModelo() {
        return modelo;
    }

    public void setModelo(ModeloAuto modelo) {
        this.modelo = modelo;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        // Ejemplo: "Versa 2022"
        return (modelo != null ? modelo.getNombre() + " " : "") + anio;
    }
}
