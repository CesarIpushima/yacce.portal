package modelo.dao;

import modelo.ProgresoTema;
import modelo.Usuario;
import modelo.Tema;
import config.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ProgresoTemaDAO extends Conexion {

    public ProgresoTema obtenerProgreso(int idTema, int idUsuario) throws SQLException {
        ProgresoTema progreso = null;
        Connection cn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT ID_TEMA, ID_USUARIO, ESTADO FROM PROGRESO_TEMA WHERE ID_TEMA = ? AND ID_USUARIO = ?";
        try {
            cn = getConexion();
            stmt = cn.prepareStatement(sql);
            stmt.setInt(1, idTema);
            stmt.setInt(2, idUsuario);
            rs = stmt.executeQuery();
            if (rs.next()) {
                progreso = new ProgresoTema();
                Tema tema = new Tema();
                tema.setIdTema(rs.getInt("ID_TEMA"));
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("ID_USUARIO"));
                progreso.setTema(tema);
                progreso.setUsuario(usuario);
                progreso.setEstado(rs.getString("ESTADO"));
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener progreso tema: " + e.getMessage());
            throw e;
        } finally {
            if (rs != null && !rs.isClosed()) rs.close();
            if (stmt != null && !stmt.isClosed()) stmt.close();
            if (cn != null && !cn.isClosed()) cn.close();
        }
        return progreso;
    }

    public boolean registrarOActualizarProgreso(ProgresoTema progreso) throws SQLException {
        Connection cn = null;
        PreparedStatement stmt = null;
        String sql;
        ProgresoTema existente = obtenerProgreso(progreso.getTema().getIdTema(), progreso.getUsuario().getIdUsuario());
        
        try {
            cn = getConexion();
            if (existente == null) {
                sql = "INSERT INTO PROGRESO_TEMA (ID_TEMA, ID_USUARIO, ESTADO) VALUES (?, ?, ?)";
                stmt = cn.prepareStatement(sql);
                stmt.setInt(1, progreso.getTema().getIdTema());
                stmt.setInt(2, progreso.getUsuario().getIdUsuario());
                stmt.setString(3, progreso.getEstado());
            } else {
                sql = "UPDATE PROGRESO_TEMA SET ESTADO = ? WHERE ID_TEMA = ? AND ID_USUARIO = ?";
                stmt = cn.prepareStatement(sql);
                stmt.setString(1, progreso.getEstado());
                stmt.setInt(2, progreso.getTema().getIdTema());
                stmt.setInt(3, progreso.getUsuario().getIdUsuario());
            }
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error al registrar/actualizar progreso tema: " + e.getMessage());
            throw e;
        } finally {
            if (stmt != null && !stmt.isClosed()) stmt.close();
            if (cn != null && !cn.isClosed()) cn.close();
        }
    }

    public boolean todosTemasCompletados(int idCurso, int idUsuario) throws SQLException {
        Connection cn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT t.ID_TEMA " +
                     "FROM TEMA t LEFT JOIN PROGRESO_TEMA pt ON t.ID_TEMA = pt.ID_TEMA AND pt.ID_USUARIO = ? " +
                     "WHERE t.ID_CURSO = ? AND (pt.ESTADO IS NULL OR pt.ESTADO != 'FINALIZADO')";
        try {
            cn = getConexion();
            stmt = cn.prepareStatement(sql);
            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idCurso);
            rs = stmt.executeQuery();
            return !rs.next(); // True if no incomplete themes exist
        } catch (SQLException e) {
            System.out.println("Error al verificar temas completados: " + e.getMessage());
            throw e;
        } finally {
            if (rs != null && !rs.isClosed()) rs.close();
            if (stmt != null && !stmt.isClosed()) stmt.close();
            if (cn != null && !cn.isClosed()) cn.close();
        }
    }
    
    public Map<Integer, String> obtenerEstadosPorCurso(int idCurso, int idUsuario) throws SQLException {
    Map<Integer, String> temaEstados = new HashMap<>();
    Connection cn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    String sql = "SELECT ID_TEMA, ESTADO FROM PROGRESO_TEMA WHERE ID_USUARIO = ? AND ID_TEMA IN (SELECT ID_TEMA FROM TEMA WHERE ID_CURSO = ?)";
    try {
        cn = getConexion();
        stmt = cn.prepareStatement(sql);
        stmt.setInt(1, idUsuario);
        stmt.setInt(2, idCurso);
        rs = stmt.executeQuery();
        while (rs.next()) {
            temaEstados.put(rs.getInt("ID_TEMA"), rs.getString("ESTADO"));
        }
    } catch (SQLException e) {
        System.out.println("Error al obtener estados de temas: " + e.getMessage());
        throw e;
    } finally {
        if (rs != null && !rs.isClosed()) rs.close();
        if (stmt != null && !stmt.isClosed()) stmt.close();
        if (cn != null && !cn.isClosed()) cn.close();
    }
    return temaEstados;
}
}