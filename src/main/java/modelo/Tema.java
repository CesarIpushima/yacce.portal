package modelo;

public class Tema {
    private int idTema;
    private String nombreTema;
    private String contenido;
    private Curso curso;

    public Tema() {
    }

    public Tema(int idTema, String nombreTema, String contenido, Curso curso) {
        this.idTema = idTema;
        this.nombreTema = nombreTema;
        this.contenido = contenido;
        this.curso = curso;
    }

    public int getIdTema() {
        return idTema;
    }

    public void setIdTema(int idTema) {
        this.idTema = idTema;
    }

    public String getNombreTema() {
        return nombreTema;
    }

    public void setNombreTema(String nombreTema) {
        this.nombreTema = nombreTema;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    
    
}
