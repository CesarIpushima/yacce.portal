package modelo.dao;

import modelo.Tema;
import modelo.Curso;
import config.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TemaDAO extends Conexion {
    public boolean registrar(Tema tema) throws SQLException {
        Connection cn = null;
        PreparedStatement stmt = null;
        String sql = "INSERT INTO TEMA (NOMBRE_TEMA, CONTENIDO, ID_CURSO) VALUES (?, ?, ?)";
        try {
            cn = getConexion();
            stmt = cn.prepareStatement(sql);
            stmt.setString(1, tema.getNombreTema());
            stmt.setString(2, tema.getContenido());
            stmt.setInt(3, tema.getCurso().getIdCurso());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error al registrar tema: " + e.getMessage());
            throw e;
        } finally {
            if (stmt != null && !stmt.isClosed()) stmt.close();
            if (cn != null && !cn.isClosed()) cn.close();
        }
    }

    public boolean actualizar(Tema tema) throws SQLException {
        Connection cn = null;
        PreparedStatement stmt = null;
        String sql = "UPDATE TEMA SET NOMBRE_TEMA = ?, CONTENIDO = ?, ID_CURSO = ? WHERE ID_TEMA = ?";
        try {
            cn = getConexion();
            stmt = cn.prepareStatement(sql);
            stmt.setString(1, tema.getNombreTema());
            stmt.setString(2, tema.getContenido());
            stmt.setInt(3, tema.getCurso().getIdCurso());
            stmt.setInt(4, tema.getIdTema());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar tema: " + e.getMessage());
            throw e;
        } finally {
            if (stmt != null && !stmt.isClosed()) stmt.close();
            if (cn != null && !cn.isClosed()) cn.close();
        }
    }

    public boolean eliminar(int id) throws SQLException {
        Connection cn = null;
        PreparedStatement stmt = null;
        String sql = "DELETE FROM TEMA WHERE ID_TEMA = ?";
        try {
            cn = getConexion();
            stmt = cn.prepareStatement(sql);
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar tema: " + e.getMessage());
            throw e;
        } finally {
            if (stmt != null && !stmt.isClosed()) stmt.close();
            if (cn != null && !cn.isClosed()) cn.close();
        }
    }

    public List<Tema> listarPorCurso(int idCurso) throws SQLException {
        List<Tema> temas = new ArrayList<>();
        Connection cn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT t.ID_TEMA, t.NOMBRE_TEMA, t.CONTENIDO, t.ID_CURSO, c.NOMBRE_CURSO " +
                     "FROM TEMA t JOIN CURSO c ON t.ID_CURSO = c.ID_CURSO WHERE t.ID_CURSO = ?";
        try {
            cn = getConexion();
            stmt = cn.prepareStatement(sql);
            stmt.setInt(1, idCurso);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Tema tema = new Tema();
                tema.setIdTema(rs.getInt("ID_TEMA"));
                tema.setNombreTema(rs.getString("NOMBRE_TEMA"));
                tema.setContenido(rs.getString("CONTENIDO"));
                Curso curso = new Curso();
                curso.setIdCurso(rs.getInt("ID_CURSO"));
                curso.setNombreCurso(rs.getString("NOMBRE_CURSO"));
                tema.setCurso(curso);
                temas.add(tema);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar temas: " + e.getMessage());
            throw e;
        } finally {
            if (rs != null && !rs.isClosed()) rs.close();
            if (stmt != null && !stmt.isClosed()) stmt.close();
            if (cn != null && !cn.isClosed()) cn.close();
        }
        return temas;
    }

    public Tema obtenerPorId(int id) throws SQLException {
        Tema tema = null;
        Connection cn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT t.ID_TEMA, t.NOMBRE_TEMA, t.CONTENIDO, t.ID_CURSO, c.NOMBRE_CURSO " +
                     "FROM TEMA t JOIN CURSO c ON t.ID_CURSO = c.ID_CURSO WHERE t.ID_TEMA = ?";
        try {
            cn = getConexion();
            stmt = cn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                tema = new Tema();
                tema.setIdTema(rs.getInt("ID_TEMA"));
                tema.setNombreTema(rs.getString("NOMBRE_TEMA"));
                tema.setContenido(rs.getString("CONTENIDO"));
                Curso curso = new Curso();
                curso.setIdCurso(rs.getInt("ID_CURSO"));
                curso.setNombreCurso(rs.getString("NOMBRE_CURSO"));
                tema.setCurso(curso);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener tema: " + e.getMessage());
            throw e;
        } finally {
            if (rs != null && !rs.isClosed()) rs.close();
            if (stmt != null && !stmt.isClosed()) stmt.close();
            if (cn != null && !cn.isClosed()) cn.close();
        }
        return tema;
    }
}