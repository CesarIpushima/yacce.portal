package modelo.dao;

import modelo.IntentoExamen;
import config.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class IntentoExamenDAO extends Conexion {

    public boolean registrarIntento(IntentoExamen intento) throws SQLException {
        Connection cn = null;
        PreparedStatement stmt = null;
        String sql = "INSERT INTO INTENTO_EXAMEN (ID_CURSO, ID_USUARIO, NOTA, FECHA) VALUES (?, ?, ?, ?)";
        try {
            cn = getConexion();
            stmt = cn.prepareStatement(sql);
            stmt.setInt(1, intento.getCurso().getIdCurso());
            stmt.setInt(2, intento.getUsuario().getIdUsuario());
            stmt.setDouble(3, intento.getNota());
            stmt.setTimestamp(4, intento.getFechaHora());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error al registrar intento de examen: " + e.getMessage());
            throw e;
        } finally {
            if (stmt != null && !stmt.isClosed()) stmt.close();
            if (cn != null && !cn.isClosed()) cn.close();
        }
    }
}