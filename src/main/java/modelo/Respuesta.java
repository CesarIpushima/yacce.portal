package modelo;

public class Respuesta {
    private int idRespuesta;
    private String respuesta;
    private boolean correcta;
    private Pregunta pregunta;

    public Respuesta() {
    }

    public Respuesta(int idRespuesta, String respuesta, boolean correcta, Pregunta pregunta) {
        this.idRespuesta = idRespuesta;
        this.respuesta = respuesta;
        this.correcta = correcta;
        this.pregunta = pregunta;
    }

    public int getIdRespuesta() {
        return idRespuesta;
    }

    public void setIdRespuesta(int idRespuesta) {
        this.idRespuesta = idRespuesta;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public boolean isCorrecta() {
        return correcta;
    }

    public void setCorrecta(boolean correcta) {
        this.correcta = correcta;
    }

    public Pregunta getPregunta() {
        return pregunta;
    }

    public void setPregunta(Pregunta pregunta) {
        this.pregunta = pregunta;
    }
    
    
}
