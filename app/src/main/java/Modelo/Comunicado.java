package Modelo;

import java.io.Serializable;

/**
 * Clase base abstracta para cualquier tipo de comunicado.
 * Implementa {@link Serializable} para permitir su serialización
 * y {@link Comparable} para ordenar por título.
 *
 * <p>Los campos modelan datos comunes a anuncios y eventos.
 * El identificador {@code idComunicado} se autoincrementa con {@code cont}.</p>
 *
 * @author
 * @since 1.0
 */
public abstract class Comunicado implements Serializable, Comparable<Comunicado> {

    /** Identificador interno del comunicado (autogenerado). */
    private int idComunicado;

    /** Tipo textual del comunicado (p. ej. "Anuncio" o "Evento"). */
    private String tipo;

    /** Área a la que pertenece el comunicado. */
    private String area;

    /** Título del comunicado. */
    private String titulo;

    /** Audiencia objetivo (p. ej. "Estudiantes;Profesores"). */
    private String audiencia;

    /** Cuerpo o descripción del comunicado. */
    private String descripcion;

    /** Nombre de archivo de la imagen asociada (si existe). */
    private String nombreArchivoImg;

    /** Contador estático usado para generar ids incrementales. */
    private static int cont = 0;

    /**
     * Constructor base de {@code Comunicado}.
     *
     * @param tipo             tipo textual
     * @param area             área del comunicado
     * @param titulo           título
     * @param audiencia        audiencia
     * @param descripcion      descripción
     * @param nombreArchivoImg nombre del archivo de imagen
     */
    //Contructor de clase Comunicado
    public Comunicado(String tipo, String area, String titulo, String audiencia,
                      String descripcion, String nombreArchivoImg) {
        this.idComunicado = cont;
        this.tipo = tipo;
        this.area = area;
        this.titulo = titulo;
        this.audiencia = audiencia;
        this.descripcion = descripcion;
        this.nombreArchivoImg = nombreArchivoImg;
        cont++;
    }

    /**
     * Orden natural por título en orden lexicográfico.
     * @param otro el comunicado a comparar
     * @return valor negativo, cero o positivo según el orden por título
     */
    @Override
    public int compareTo(Comunicado otro) {
        return this.titulo.compareTo(otro.getTitulo());
    }

    /** @return id interno del comunicado */
    public int getIdComunicado() { return idComunicado; }

    /** @return tipo textual del comunicado */
    public String getTipo() { return tipo; }

    /** @return área del comunicado */
    public String getArea() { return area; }

    /** @return título del comunicado */
    public String getTitulo() { return titulo; }

    /** @return audiencia objetivo */
    public String getAudiencia() { return audiencia; }

    /** @return descripción del comunicado */
    public String getDescripcion() { return descripcion; }

    /** @return nombre del archivo de imagen asociado */
    public String getNombreArchivoImg() { return nombreArchivoImg; }

    /**
     * Establece el tipo textual.
     * @param tipo tipo a asignar
     */
    public void setTipo(String tipo) { this.tipo = tipo; }

    /**
     * Establece el área.
     * @param area área a asignar
     */
    public void setArea(String area) { this.area = area; }

    /**
     * Establece el título.
     * @param titulo título a asignar
     */
    public void setTitulo(String titulo) { this.titulo = titulo; }

    /**
     * Establece la audiencia.
     * @param audiencia audiencia a asignar
     */
    public void setAudiencia(String audiencia) { this.audiencia = audiencia; }

    /**
     * Establece la descripción.
     * @param descripcion texto a asignar
     */
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    /**
     * Establece el nombre del archivo de imagen.
     * @param nombreArchivoImg nombre del archivo
     */
    public void setNombreArchivoImg(String nombreArchivoImg) { this.nombreArchivoImg = nombreArchivoImg; }

    /**
     * Representación textual del comunicado con sus campos base.
     * @return cadena con los atributos principales
     */
    @Override
    public String toString() {
        return "Comunicado{" +
                "idComunicado=" + idComunicado +
                ", tipo='" + tipo + '\'' +
                ", area='" + area + '\'' +
                ", titulo='" + titulo + '\'' +
                ", audiencia='" + audiencia + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", nombreArchivoImg='" + nombreArchivoImg + '\'' +
                '}';
    }
}
