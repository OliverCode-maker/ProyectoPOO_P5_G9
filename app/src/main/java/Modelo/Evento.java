package Modelo;

/**
 * Representa un comunicado de tipo Evento.
 * Extiende {@link Comunicado} agregando lugar y fecha.
 *
 * <p>Útil para publicar actividades con una localización y una fecha concreta.</p>
 */
public class Evento extends Comunicado {

    /** Lugar donde se realizará el evento. */
    private String lugar;

    /** Fecha del evento en formato de texto (p. ej. "25/12/2025"). */
    private String fecha;

    /**
     * Crea un evento.
     *
     * @param tipo             tipo textual (p. ej. "Evento")
     * @param area             área del comunicado
     * @param titulo           título del evento
     * @param audiencia        audiencia objetivo
     * @param descripcion      descripción del evento
     * @param nombreArchivoImg nombre de archivo de imagen (opcional)
     * @param lugar            lugar donde ocurre
     * @param fecha            fecha del evento (formato esperado dd/MM/yyyy)
     */
    public Evento(String tipo, String area, String titulo, String audiencia,
                  String descripcion, String nombreArchivoImg, String lugar, String fecha) {
        super(tipo, area, titulo, audiencia, descripcion, nombreArchivoImg);
        this.fecha = fecha;
        this.lugar = lugar;
    }

    /** @return el lugar del evento */
    public String getLugar() { return lugar; }

    /** @return la fecha del evento en cadena */
    public String getFecha() { return fecha; }

    /**
     * Establece el lugar del evento.
     * @param lugar texto con la localización
     */
    public void setLugar(String lugar) { this.lugar = lugar; }

    /**
     * Establece la fecha del evento (como texto).
     * @param fecha cadena de fecha (p. ej. "dd/MM/yyyy")
     */
    public void setFecha(String fecha) { this.fecha = fecha; }

    /**
     * Representación legible del evento.
     * <p><b>Nota:</b> concatena el {@code toString()} del padre y añade campos propios.</p>
     */
    @Override
    public String toString() {
        return super.toString() +
                "lugar='" + lugar + '\'' +
                ", fecha='" + fecha + '\'' +
                '}';
    }
}
