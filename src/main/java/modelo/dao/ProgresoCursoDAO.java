package modelo.dao;

import modelo.ProgresoCurso;
import modelo.Usuario;
import modelo.Curso;
import config.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProgresoCursoDAO extends Conexion {

    public ProgresoCurso obtenerProgreso(int idCurso, int idUsuario) throws SQLException {
        ProgresoCurso progreso = null;
        Connection cn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT ID_CURSO, ID_USUARIO, NUM_INTENTO, NOTA, ESTADO FROM PROGRESO_CURSO WHERE ID_CURSO = ? AND ID_USUARIO = ?";
        try {
            cn = getConexion();
            stmt = cn.prepareStatement(sql);
            stmt.setInt(1, idCurso);
            stmt.setInt(2, idUsuario);
            rs = stmt.executeQuery();
            if (rs.next()) {
                progreso = new ProgresoCurso();
                Curso curso = new Curso();
                curso.setIdCurso(rs.getInt("ID_CURSO"));
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("ID_USUARIO"));
                progreso.setCurso(curso);
                progreso.setUsuario(usuario);
                progreso.setNumIntento(rs.getInt("NUM_INTENTO"));
                progreso.setNota(rs.getDouble("NOTA"));
                progreso.setEstado(rs.getString("ESTADO"));
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener progreso: " + e.getMessage());
            throw e;
        } finally {
            if (rs != null && !rs.isClosed()) rs.close();
            if (stmt != null && !stmt.isClosed()) stmt.close();
            if (cn != null && !cn.isClosed()) cn.close();
        }
        return progreso;
    }

    public boolean registrarOActualizarProgreso(ProgresoCurso progreso) throws SQLException {
        Connection cn = null;
        PreparedStatement stmt = null;
        String sql;
        ProgresoCurso existente = obtenerProgreso(progreso.getCurso().getIdCurso(), progreso.getUsuario().getIdUsuario());
        
        try {
            cn = getConexion();
            if (existente == null) {
                sql = "INSERT INTO PROGRESO_CURSO (ID_CURSO, ID_USUARIO, NUM_INTENTO, NOTA, ESTADO) VALUES (?, ?, ?, ?, ?)";
                stmt = cn.prepareStatement(sql);
                stmt.setInt(1, progreso.getCurso().getIdCurso());
                stmt.setInt(2, progreso.getUsuario().getIdUsuario());
                stmt.setInt(3, progreso.getNumIntento());
                stmt.setDouble(4, progreso.getNota());
                stmt.setString(5, progreso.getEstado());
            } else {
                sql = "UPDATE PROGRESO_CURSO SET NUM_INTENTO = ?, NOTA = ?, ESTADO = ? WHERE ID_CURSO = ? AND ID_USUARIO = ?";
                stmt = cn.prepareStatement(sql);
                stmt.setInt(1, progreso.getNumIntento());
                stmt.setDouble(2, progreso.getNota());
                stmt.setString(3, progreso.getEstado());
                stmt.setInt(4, progreso.getCurso().getIdCurso());
                stmt.setInt(5, progreso.getUsuario().getIdUsuario());
            }
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error al registrar/actualizar progreso: " + e.getMessage());
            throw e;
        } finally {
            if (stmt != null && !stmt.isClosed()) stmt.close();
            if (cn != null && !cn.isClosed()) cn.close();
        }
    }

public String verificarElegibilidadCertificado(int idUsuario) throws SQLException {
    Connection cn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    String sql = "SELECT c.ID_CURSO, pc.ESTADO " +
                 "FROM CURSO c LEFT JOIN PROGRESO_CURSO pc ON c.ID_CURSO = pc.ID_CURSO AND pc.ID_USUARIO = ?";
    try {
        cn = getConexion();
        stmt = cn.prepareStatement(sql);
        stmt.setInt(1, idUsuario);
        rs = stmt.executeQuery();
        boolean todosTomados = true;
        boolean todosAprobados = true;
        while (rs.next()) {
            String estado = rs.getString("ESTADO");
            if (estado == null) {
                todosTomados = false; // No ha tomado el examen de este curso
            } else if (!"APROBADO".equals(estado)) {
                todosAprobados = false; // Tomó el examen pero no aprobó
            }
        }
        if (!todosTomados) {
            return "Falta terminar todos los cursos para obtener el certificado.";
        } else if (!todosAprobados) {
            return "Debe aprobar todos los cursos para obtener el certificado.";
        } else {
            // All courses approved, set user estado to 3 (APPROVED)
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            Usuario usuario = usuarioDAO.obtenerPorId(idUsuario);
            if (usuario != null && usuario.getEstado() != 3) {
                usuario.setEstado(3);
                usuarioDAO.actualizar(usuario);
            }
            return "ELIGIBLE";
        }
    } catch (SQLException e) {
        System.out.println("Error al verificar elegibilidad para certificado: " + e.getMessage());
        throw e;
    } finally {
        if (rs != null && !rs.isClosed()) rs.close();
        if (stmt != null && !stmt.isClosed()) stmt.close();
        if (cn != null && !cn.isClosed()) cn.close();
    }
}
    
    // Método para listar todos los cursos con el progreso del usuario
    public List<ProgresoCurso> listarProgresoPorUsuario(int idUsuario) throws SQLException {
        List<ProgresoCurso> listaProgreso = new ArrayList<>();
        Connection cn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT c.ID_CURSO, c.NOMBRE_CURSO, c.NUM_PREGUNTAS, c.NUM_TEMAS, " +
                     "pc.NUM_INTENTO, pc.NOTA, pc.ESTADO " +
                     "FROM CURSO c LEFT JOIN PROGRESO_CURSO pc " +
                     "ON c.ID_CURSO = pc.ID_CURSO AND pc.ID_USUARIO = ?";

        try {
            cn = getConexion();
            stmt = cn.prepareStatement(sql);
            stmt.setInt(1, idUsuario);
            rs = stmt.executeQuery();

            while (rs.next()) {
                ProgresoCurso progreso = new ProgresoCurso();
                Curso curso = new Curso();
                curso.setIdCurso(rs.getInt("ID_CURSO"));
                curso.setNombreCurso(rs.getString("NOMBRE_CURSO"));
                curso.setNumPreguntas(rs.getInt("NUM_PREGUNTAS"));
                curso.setNumTemas(rs.getInt("NUM_TEMAS"));

                Usuario usuario = new Usuario();
                usuario.setIdUsuario(idUsuario);

                progreso.setCurso(curso);
                progreso.setUsuario(usuario);

                // Si no hay progreso, establecer valores por defecto
                if (rs.getObject("NUM_INTENTO") != null) {
                    progreso.setNumIntento(rs.getInt("NUM_INTENTO"));
                    progreso.setNota(rs.getDouble("NOTA"));
                    progreso.setEstado(rs.getString("ESTADO"));
                } else {
                    progreso.setNumIntento(0);
                    progreso.setNota(0.0);
                    progreso.setEstado("Aún no se realiza");
                }

                listaProgreso.add(progreso);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar progreso por usuario: " + e.getMessage());
            throw e;
        } finally {
            if (rs != null && !rs.isClosed()) rs.close();
            if (stmt != null && !stmt.isClosed()) stmt.close();
            if (cn != null && !cn.isClosed()) cn.close();
        }
        return listaProgreso;
    }
}