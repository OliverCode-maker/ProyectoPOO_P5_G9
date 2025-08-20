package Modelo;

import java.io.Serializable;

public abstract class Comunicado implements Serializable, Comparable<Comunicado> {
    private int idComunicado;
    private String tipo;
    private String area;
    private String titulo;
    private String audiencia;
    private String descripcion;
    private String nombreArchivoImg;
    private static int cont=0;

    //Contructor de clase Comunicado
    public Comunicado(String tipo, String area, String titulo, String audiencia, String descripcion, String nombreArchivoImg) {
        this.idComunicado=cont;
        this.tipo = tipo;
        this.area = area;
        this.titulo = titulo;
        this.audiencia = audiencia;
        this.descripcion = descripcion;
        this.nombreArchivoImg = nombreArchivoImg;

        cont++;
    }

    @Override
    public int compareTo(Comunicado otro) {
        return this.titulo.compareTo(otro.getTitulo());
    }

    public int getIdComunicado() {
        return idComunicado;
    }

    public String getTipo() {
        return tipo;
    }

    public String getArea() {
        return area;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAudiencia() {
        return audiencia;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getNombreArchivoImg() {
        return nombreArchivoImg;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setAudiencia(String audiencia) {
        this.audiencia = audiencia;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setNombreArchivoImg(String nombreArchivoImg) {
        this.nombreArchivoImg = nombreArchivoImg;
    }

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
