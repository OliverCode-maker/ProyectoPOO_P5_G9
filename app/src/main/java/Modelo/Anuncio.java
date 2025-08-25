package Modelo;

/**
 * Representa un comunicado de tipo Anuncio.
 * Extiende {@link Comunicado} agregando el nivel de urgencia.
 *
 * <p>Se usa cuando el comunicado no es un evento con lugar/fecha,
 * sino un aviso dirigido a una audiencia con una urgencia asociada.</p>
 *
 * @author Tu Nombre
 * @since 1.0
 */
public class Anuncio extends Comunicado {

    /** Nivel de urgencia del anuncio (por ejemplo: "BAJA", "MEDIA", "ALTA"). */
    private String nivelUrgencia;

    /**
     * Crea un Anuncio.
     *
     * @param tipo             tipo textual del comunicado (p. ej. "Anuncio")
     * @param area             área a la que pertenece
     * @param titulo           título del comunicado
     * @param audiencia        audiencia objetivo (p. ej. "Estudiantes;Profesores")
     * @param descripcion      contenido o cuerpo del anuncio
     * @param nombreArchivoImg nombre del archivo de imagen asociado (opcional)
     * @param nivelUrgencia    urgencia del anuncio
     */
    public Anuncio(String tipo, String area, String titulo, String audiencia,
                   String descripcion, String nombreArchivoImg, String nivelUrgencia) {
        super(tipo, area, titulo, audiencia, descripcion, nombreArchivoImg);
        this.nivelUrgencia = nivelUrgencia;
    }

    /** @return el nivel de urgencia del anuncio */
    public String getNivelUrgencia() {
        return nivelUrgencia;
    }

    /**
     * Establece el nivel de urgencia del anuncio.
     * @param nivelUrgencia texto que describe la urgencia (no nulo)
     */
    public void setNivelUrgencia(String nivelUrgencia) {
        this.nivelUrgencia = nivelUrgencia;
    }

    /**
     * Representación legible del anuncio.
     * <p><b>Nota:</b> concatena el {@code toString()} del padre y añade el campo propio.</p>
     */
    @Override
    public String toString() {
        return super.toString() +
                "nivelUrgencia='" + nivelUrgencia + '\'' +
                '}';
    }
}
