package modelo;

public class ProgresoTema {
    private Usuario usuario;
    private Tema tema;
    private String estado;

    public ProgresoTema() {
    }

    public ProgresoTema(Usuario usuario, Tema tema, String estado) {
        this.usuario = usuario;
        this.tema = tema;
        this.estado = estado;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Tema getTema() {
        return tema;
    }

    public void setTema(Tema tema) {
        this.tema = tema;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    
}
