package Modelo;

public class Anuncio extends Comunicado{
    private String nivelUrgencia;

    public Anuncio(String tipo, String area, String titulo, String audiencia, String descripcion, String nombreArchivoImg, String nivelUrgencia) {
        super(tipo, area, titulo, audiencia, descripcion, nombreArchivoImg);
        this.nivelUrgencia = nivelUrgencia;
    }

    public String getNivelUrgencia() {
        return nivelUrgencia;
    }

    public void setNivelUrgencia(String nivelUrgencia) {
        this.nivelUrgencia = nivelUrgencia;
    }

    @Override
    public String toString() {
        return super.toString() +
                "nivelUrgencia='" + nivelUrgencia + '\'' +
                '}';
    }
}
