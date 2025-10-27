package model;
 
public class TipoServicio {
 
    private int idTipoServicio;

    private String nombre;

    private String descripcion;

    private boolean activo;
 
    public TipoServicio() {

    }
 
    public TipoServicio(int idTipoServicio, String nombre,

                        String descripcion, boolean activo) {

        this.idTipoServicio = idTipoServicio;

        this.nombre = nombre;

        this.descripcion = descripcion;

        this.activo = activo;

    }
 
    public int getIdTipoServicio() {

        return idTipoServicio;

    }
 
    public void setIdTipoServicio(int idTipoServicio) {

        this.idTipoServicio = idTipoServicio;

    }
 
    public String getNombre() {

        return nombre;

    }
 
    public void setNombre(String nombre) {

        this.nombre = nombre;

    }
 
    public String getDescripcion() {

        return descripcion;

    }
 
    public void setDescripcion(String descripcion) {

        this.descripcion = descripcion;

    }
 
    public boolean isActivo() {

        return activo;

    }
 
    public void setActivo(boolean activo) {

        this.activo = activo;

    }
 
    @Override

    public String toString() {

        return nombre;

    }

}

 