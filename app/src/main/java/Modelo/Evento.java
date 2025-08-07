package Modelo;

public class Evento extends Comunicado{
    private String lugar;
    private String fecha;
    public Evento(String tipo, String area, String titulo, String audiencia, String descripcion, String nombreArchivoImg, String lugar, String fecha) {
        super(tipo, area, titulo, audiencia, descripcion, nombreArchivoImg);
        this.fecha=fecha;
        this.lugar=lugar;
    }

    public String getLugar() {
        return lugar;
    }

    public String getFecha() {
        return fecha;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return super.toString() +
                "lugar='" + lugar + '\'' +
                ", fecha='" + fecha + '\'' +
                '}';
    }
}
