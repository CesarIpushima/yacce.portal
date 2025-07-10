package modelo;

public class Pregunta {
    private int idPregunta;
    private String pregunta;
    private Curso curso;

    public Pregunta() {
    }

    public Pregunta(int idPregunta, String pregunta, Curso curso) {
        this.idPregunta = idPregunta;
        this.pregunta = pregunta;
        this.curso = curso;
    }

    public int getIdPregunta() {
        return idPregunta;
    }

    public void setIdPregunta(int idPregunta) {
        this.idPregunta = idPregunta;
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }
    
}
