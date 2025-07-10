package modelo.dao;

import modelo.Curso;
import config.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CursoDAO extends Conexion {
    public boolean registrar(Curso curso) throws SQLException {
        Connection cn = null;
        PreparedStatement stmt = null;
        String sql = "INSERT INTO CURSO (NOMBRE_CURSO, NUM_PREGUNTAS, NUM_TEMAS) VALUES (?, 0, 0)";
        try {
            cn = getConexion();
            stmt = cn.prepareStatement(sql);
            stmt.setString(1, curso.getNombreCurso());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error al registrar curso: " + e.getMessage());
            throw e;
        } finally {
            if (stmt != null && !stmt.isClosed()) stmt.close();
            if (cn != null && !cn.isClosed()) cn.close();
        }
    }

    public boolean actualizar(Curso curso) throws SQLException {
        Connection cn = null;
        PreparedStatement stmt = null;
        String sql = "UPDATE CURSO SET NOMBRE_CURSO = ? WHERE ID_CURSO = ?";
        try {
            cn = getConexion();
            stmt = cn.prepareStatement(sql);
            stmt.setString(1, curso.getNombreCurso());
            stmt.setInt(2, curso.getIdCurso());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar curso: " + e.getMessage());
            throw e;
        } finally {
            if (stmt != null && !stmt.isClosed()) stmt.close();
            if (cn != null && !cn.isClosed()) cn.close();
        }
    }

    public boolean eliminar(int id) throws SQLException {
        Connection cn = null;
        PreparedStatement stmt = null;
        String sql = "DELETE FROM CURSO WHERE ID_CURSO = ?";
        try {
            cn = getConexion();
            stmt = cn.prepareStatement(sql);
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar curso: " + e.getMessage());
            throw e;
        } finally {
            if (stmt != null && !stmt.isClosed()) stmt.close();
            if (cn != null && !cn.isClosed()) cn.close();
        }
    }

    public List<Curso> listar() throws SQLException {
        List<Curso> cursos = new ArrayList<>();
        Connection cn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT c.ID_CURSO, c.NOMBRE_CURSO, " +
                     "(SELECT COUNT(*) FROM PREGUNTA p WHERE p.ID_CURSO = c.ID_CURSO) AS NUM_PREGUNTAS, " +
                     "(SELECT COUNT(*) FROM TEMA t WHERE t.ID_CURSO = c.ID_CURSO) AS NUM_TEMAS " +
                     "FROM CURSO c";
        try {
            cn = getConexion();
            stmt = cn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Curso curso = new Curso();
                curso.setIdCurso(rs.getInt("ID_CURSO"));
                curso.setNombreCurso(rs.getString("NOMBRE_CURSO"));
                curso.setNumPreguntas(rs.getInt("NUM_PREGUNTAS"));
                curso.setNumTemas(rs.getInt("NUM_TEMAS"));
                cursos.add(curso);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar cursos: " + e.getMessage());
            throw e;
        } finally {
            if (rs != null && !rs.isClosed()) rs.close();
            if (stmt != null && !stmt.isClosed()) stmt.close();
            if (cn != null && !cn.isClosed()) cn.close();
        }
        return cursos;
    }

    public Curso obtenerPorId(int id) throws SQLException {
        Curso curso = null;
        Connection cn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT c.ID_CURSO, c.NOMBRE_CURSO, " +
                     "(SELECT COUNT(*) FROM PREGUNTA p WHERE p.ID_CURSO = c.ID_CURSO) AS NUM_PREGUNTAS, " +
                     "(SELECT COUNT(*) FROM TEMA t WHERE t.ID_CURSO = c.ID_CURSO) AS NUM_TEMAS " +
                     "FROM CURSO c WHERE c.ID_CURSO = ?";
        try {
            cn = getConexion();
            stmt = cn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                curso = new Curso();
                curso.setIdCurso(rs.getInt("ID_CURSO"));
                curso.setNombreCurso(rs.getString("NOMBRE_CURSO"));
                curso.setNumPreguntas(rs.getInt("NUM_PREGUNTAS"));
                curso.setNumTemas(rs.getInt("NUM_TEMAS"));
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener curso: " + e.getMessage());
            throw e;
        } finally {
            if (rs != null && !rs.isClosed()) rs.close();
            if (stmt != null && !stmt.isClosed()) stmt.close();
            if (cn != null && !cn.isClosed()) cn.close();
        }
        return curso;
    }

    public void actualizarNumPreguntas(int idCurso) throws SQLException {
        Connection cn = null;
        PreparedStatement stmt = null;
        String sql = "UPDATE CURSO SET NUM_PREGUNTAS = (SELECT COUNT(*) FROM PREGUNTA WHERE ID_CURSO = ?) WHERE ID_CURSO = ?";
        try {
            cn = getConexion();
            stmt = cn.prepareStatement(sql);
            stmt.setInt(1, idCurso);
            stmt.setInt(2, idCurso);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al actualizar numPreguntas: " + e.getMessage());
            throw e;
        } finally {
            if (stmt != null && !stmt.isClosed()) stmt.close();
            if (cn != null && !cn.isClosed()) cn.close();
        }
    }

    public void actualizarNumTemas(int idCurso) throws SQLException {
        Connection cn = null;
        PreparedStatement stmt = null;
        String sql = "UPDATE CURSO SET NUM_TEMAS = (SELECT COUNT(*) FROM TEMA WHERE ID_CURSO = ?) WHERE ID_CURSO = ?";
        try {
            cn = getConexion();
            stmt = cn.prepareStatement(sql);
            stmt.setInt(1, idCurso);
            stmt.setInt(2, idCurso);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al actualizar numTemas: " + e.getMessage());
            throw e;
        } finally {
            if (stmt != null && !stmt.isClosed()) stmt.close();
            if (cn != null && !cn.isClosed()) cn.close();
        }
    }
}