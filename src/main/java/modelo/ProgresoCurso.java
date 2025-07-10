package modelo;

public class ProgresoCurso {
    private Usuario usuario;
    private Curso curso;
    private int numIntento;
    private double nota;
    private String estado;    

    public ProgresoCurso() {
    }

    public ProgresoCurso(Usuario usuario, Curso curso, int numIntento, double nota, String estado) {
        this.usuario = usuario;
        this.curso = curso;
        this.numIntento = numIntento;
        this.nota = nota;
        this.estado = estado;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public int getNumIntento() {
        return numIntento;
    }

    public void setNumIntento(int numIntento) {
        this.numIntento = numIntento;
    }

    public double getNota() {
        return nota;
    }

    public void setNota(double nota) {
        this.nota = nota;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

}
