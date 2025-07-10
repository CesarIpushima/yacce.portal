package modelo.dao;

import modelo.Respuesta;
import modelo.Pregunta;
import config.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RespuestaDAO extends Conexion {
    public boolean registrar(Respuesta respuesta) throws SQLException {
        Connection cn = null;
        PreparedStatement stmt = null;
        String sql = "INSERT INTO RESPUESTA (RESPUESTA, CORRECTA, ID_PREGUNTA) VALUES (?, ?, ?)";
        try {
            cn = getConexion();
            stmt = cn.prepareStatement(sql);
            stmt.setString(1, respuesta.getRespuesta());
            stmt.setBoolean(2, respuesta.isCorrecta());
            stmt.setInt(3, respuesta.getPregunta().getIdPregunta());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error al registrar respuesta: " + e.getMessage());
            throw e;
        } finally {
            if (stmt != null && !stmt.isClosed()) stmt.close();
            if (cn != null && !cn.isClosed()) cn.close();
        }
    }

    public boolean actualizar(Respuesta respuesta) throws SQLException {
        Connection cn = null;
        PreparedStatement stmt = null;
        String sql = "UPDATE RESPUESTA SET RESPUESTA = ?, CORRECTA = ?, ID_PREGUNTA = ? WHERE ID_RESPUESTA = ?";
        try {
            cn = getConexion();
            stmt = cn.prepareStatement(sql);
            stmt.setString(1, respuesta.getRespuesta());
            stmt.setBoolean(2, respuesta.isCorrecta());
            stmt.setInt(3, respuesta.getPregunta().getIdPregunta());
            stmt.setInt(4, respuesta.getIdRespuesta());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar respuesta: " + e.getMessage());
            throw e;
        } finally {
            if (stmt != null && !stmt.isClosed()) stmt.close();
            if (cn != null && !cn.isClosed()) cn.close();
        }
    }

    public boolean eliminar(int id) throws SQLException {
        Connection cn = null;
        PreparedStatement stmt = null;
        String sql = "DELETE FROM RESPUESTA WHERE ID_RESPUESTA = ?";
        try {
            cn = getConexion();
            stmt = cn.prepareStatement(sql);
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar respuesta: " + e.getMessage());
            throw e;
        } finally {
            if (stmt != null && !stmt.isClosed()) stmt.close();
            if (cn != null && !cn.isClosed()) cn.close();
        }
    }

    public List<Respuesta> listarPorPregunta(int idPregunta) throws SQLException {
        List<Respuesta> respuestas = new ArrayList<>();
        Connection cn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT r.ID_RESPUESTA, r.RESPUESTA, r.CORRECTA, r.ID_PREGUNTA, p.PREGUNTA " +
                     "FROM RESPUESTA r JOIN PREGUNTA p ON r.ID_PREGUNTA = p.ID_PREGUNTA WHERE r.ID_PREGUNTA = ?";
        try {
            cn = getConexion();
            stmt = cn.prepareStatement(sql);
            stmt.setInt(1, idPregunta);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Respuesta respuesta = new Respuesta();
                respuesta.setIdRespuesta(rs.getInt("ID_RESPUESTA"));
                respuesta.setRespuesta(rs.getString("RESPUESTA"));
                respuesta.setCorrecta(rs.getBoolean("CORRECTA"));
                Pregunta pregunta = new Pregunta();
                pregunta.setIdPregunta(rs.getInt("ID_PREGUNTA"));
                pregunta.setPregunta(rs.getString("PREGUNTA"));
                respuesta.setPregunta(pregunta);
                respuestas.add(respuesta);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar respuestas: " + e.getMessage());
            throw e;
        } finally {
            if (rs != null && !rs.isClosed()) rs.close();
            if (stmt != null && !stmt.isClosed()) stmt.close();
            if (cn != null && !cn.isClosed()) cn.close();
        }
        return respuestas;
    }

    public Respuesta obtenerPorId(int id) throws SQLException {
        Respuesta respuesta = null;
        Connection cn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT r.ID_RESPUESTA, r.RESPUESTA, r.CORRECTA, r.ID_PREGUNTA, p.PREGUNTA " +
                     "FROM RESPUESTA r JOIN PREGUNTA p ON r.ID_PREGUNTA = p.ID_PREGUNTA WHERE r.ID_RESPUESTA = ?";
        try {
            cn = getConexion();
            stmt = cn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                respuesta = new Respuesta();
                respuesta.setIdRespuesta(rs.getInt("ID_RESPUESTA"));
                respuesta.setRespuesta(rs.getString("RESPUESTA"));
                respuesta.setCorrecta(rs.getBoolean("CORRECTA"));
                Pregunta pregunta = new Pregunta();
                pregunta.setIdPregunta(rs.getInt("ID_PREGUNTA"));
                pregunta.setPregunta(rs.getString("PREGUNTA"));
                respuesta.setPregunta(pregunta);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener respuesta: " + e.getMessage());
            throw e;
        } finally {
            if (rs != null && !rs.isClosed()) rs.close();
            if (stmt != null && !stmt.isClosed()) stmt.close();
            if (cn != null && !cn.isClosed()) cn.close();
        }
        return respuesta;
    }
}