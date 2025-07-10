package modelo;

import java.sql.Timestamp;

public class Usuario {   
    private int idUsuario;
    private String user;
    private String pass;
    private String email;
    private int estado;
    private java.sql.Timestamp fechaInicio;
    private Rol rol;

    public Usuario() {
    }

    public Usuario(int idUsuario, String user, String pass, String email, int estado, Timestamp fechaInicio, Rol rol) {
        this.idUsuario = idUsuario;
        this.user = user;
        this.pass = pass;
        this.email = email;
        this.estado = estado;
        this.fechaInicio = fechaInicio;
        this.rol = rol;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public Timestamp getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Timestamp fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
  
}
