package modelo;

public class Curso {
    private int idCurso;
    private String nombreCurso;
    private int numPreguntas;
    private int numTemas;

    public Curso() {
    }

    public Curso(int idCurso, String nombreCurso, int numPreguntas, int numTemas) {
        this.idCurso = idCurso;
        this.nombreCurso = nombreCurso;
        this.numPreguntas = numPreguntas;
        this.numTemas = numTemas;
    }

    public int getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(int idCurso) {
        this.idCurso = idCurso;
    }

    public String getNombreCurso() {
        return nombreCurso;
    }

    public void setNombreCurso(String nombreCurso) {
        this.nombreCurso = nombreCurso;
    }

    public int getNumPreguntas() {
        return numPreguntas;
    }

    public void setNumPreguntas(int numPreguntas) {
        this.numPreguntas = numPreguntas;
    }
    
    public int getNumTemas() {
        return numTemas;
    }

    public void setNumTemas(int numTemas) {
        this.numTemas = numTemas;
    }
}
