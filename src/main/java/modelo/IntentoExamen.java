package modelo;

import java.sql.Timestamp;

public class IntentoExamen {
    private int idIntento;
    private Usuario usuario;
    private Curso curso;
    private double nota;
    private java.sql.Timestamp fechaHora;

    public IntentoExamen() {
    }

    public IntentoExamen(int idIntento, Usuario usuario, Curso curso, double nota, Timestamp fechaHora) {
        this.idIntento = idIntento;
        this.usuario = usuario;
        this.curso = curso;
        this.nota = nota;
        this.fechaHora = fechaHora;
    }

    public int getIdIntento() {
        return idIntento;
    }

    public void setIdIntento(int idIntento) {
        this.idIntento = idIntento;
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

    public double getNota() {
        return nota;
    }

    public void setNota(double nota) {
        this.nota = nota;
    }

    public Timestamp getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(Timestamp fechaHora) {
        this.fechaHora = fechaHora;
    }
    
}
