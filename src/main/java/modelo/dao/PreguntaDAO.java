package modelo.dao;

import modelo.Pregunta;
import modelo.Curso;
import config.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PreguntaDAO extends Conexion {
    public boolean registrar(Pregunta pregunta) throws SQLException {
        Connection cn = null;
        PreparedStatement stmt = null;
        String sql = "INSERT INTO PREGUNTA (PREGUNTA, ID_CURSO) VALUES (?, ?)";
        try {
            cn = getConexion();
            stmt = cn.prepareStatement(sql);
            stmt.setString(1, pregunta.getPregunta());
            stmt.setInt(2, pregunta.getCurso().getIdCurso());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                CursoDAO cursoDAO = new CursoDAO();
                cursoDAO.actualizarNumPreguntas(pregunta.getCurso().getIdCurso());
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.out.println("Error al registrar pregunta: " + e.getMessage());
            throw e;
        } finally {
            if (stmt != null && !stmt.isClosed()) stmt.close();
            if (cn != null && !cn.isClosed()) cn.close();
        }
    }

    public boolean actualizar(Pregunta pregunta) throws SQLException {
        Connection cn = null;
        PreparedStatement stmt = null;
        String sql = "UPDATE PREGUNTA SET PREGUNTA = ?, ID_CURSO = ? WHERE ID_PREGUNTA = ?";
        try {
            cn = getConexion();
            stmt = cn.prepareStatement(sql);
            stmt.setString(1, pregunta.getPregunta());
            stmt.setInt(2, pregunta.getCurso().getIdCurso());
            stmt.setInt(3, pregunta.getIdPregunta());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                CursoDAO cursoDAO = new CursoDAO();
                cursoDAO.actualizarNumPreguntas(pregunta.getCurso().getIdCurso());
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.out.println("Error al actualizar pregunta: " + e.getMessage());
            throw e;
        } finally {
            if (stmt != null && !stmt.isClosed()) stmt.close();
            if (cn != null && !cn.isClosed()) cn.close();
        }
    }

    public boolean eliminar(int id) throws SQLException {
        Connection cn = null;
        PreparedStatement stmt = null;
        String sql = "DELETE FROM PREGUNTA WHERE ID_PREGUNTA = ?";
        try {
            cn = getConexion();
            stmt = cn.prepareStatement(sql);
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                // Obtener ID_CURSO antes de eliminar (o pasar idCurso como parámetro)
                String sqlCurso = "SELECT ID_CURSO FROM PREGUNTA WHERE ID_PREGUNTA = ?";
                PreparedStatement stmtCurso = cn.prepareStatement(sqlCurso);
                stmtCurso.setInt(1, id);
                ResultSet rs = stmtCurso.executeQuery();
                if (rs.next()) {
                    int idCurso = rs.getInt("ID_CURSO");
                    CursoDAO cursoDAO = new CursoDAO();
                    cursoDAO.actualizarNumPreguntas(idCurso);
                }
                rs.close();
                stmtCurso.close();
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.out.println("Error al eliminar pregunta: " + e.getMessage());
            throw e;
        } finally {
            if (stmt != null && !stmt.isClosed()) stmt.close();
            if (cn != null && !cn.isClosed()) cn.close();
        }
    }

    public List<Pregunta> listarPorCurso(int idCurso) throws SQLException {
        List<Pregunta> preguntas = new ArrayList<>();
        Connection cn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT p.ID_PREGUNTA, p.PREGUNTA, p.ID_CURSO, c.NOMBRE_CURSO " +
                     "FROM PREGUNTA p JOIN CURSO c ON p.ID_CURSO = c.ID_CURSO WHERE p.ID_CURSO = ?";
        try {
            cn = getConexion();
            stmt = cn.prepareStatement(sql);
            stmt.setInt(1, idCurso);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Pregunta pregunta = new Pregunta();
                pregunta.setIdPregunta(rs.getInt("ID_PREGUNTA"));
                pregunta.setPregunta(rs.getString("PREGUNTA"));
                Curso curso = new Curso();
                curso.setIdCurso(rs.getInt("ID_CURSO"));
                curso.setNombreCurso(rs.getString("NOMBRE_CURSO"));
                pregunta.setCurso(curso);
                preguntas.add(pregunta);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar preguntas: " + e.getMessage());
            throw e;
        } finally {
            if (rs != null && !rs.isClosed()) rs.close();
            if (stmt != null && !stmt.isClosed()) stmt.close();
            if (cn != null && !cn.isClosed()) cn.close();
        }
        return preguntas;
    }

    public Pregunta obtenerPorId(int id) throws SQLException {
        Pregunta pregunta = null;
        Connection cn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT p.ID_PREGUNTA, p.PREGUNTA, p.ID_CURSO, c.NOMBRE_CURSO " +
                     "FROM PREGUNTA p JOIN CURSO c ON p.ID_CURSO = c.ID_CURSO WHERE p.ID_PREGUNTA = ?";
        try {
            cn = getConexion();
            stmt = cn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                pregunta = new Pregunta();
                pregunta.setIdPregunta(rs.getInt("ID_PREGUNTA"));
                pregunta.setPregunta(rs.getString("PREGUNTA"));
                Curso curso = new Curso();
                curso.setIdCurso(rs.getInt("ID_CURSO"));
                curso.setNombreCurso(rs.getString("NOMBRE_CURSO"));
                pregunta.setCurso(curso);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener pregunta: " + e.getMessage());
            throw e;
        } finally {
            if (rs != null && !rs.isClosed()) rs.close();
            if (stmt != null && !stmt.isClosed()) stmt.close();
            if (cn != null && !cn.isClosed()) cn.close();
        }
        return pregunta;
    }
}