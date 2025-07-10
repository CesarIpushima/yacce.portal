package modelo.dao;

import modelo.Usuario;
import modelo.Rol;
import config.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO extends Conexion {
    public boolean registrar(Usuario usuario) throws SQLException {
        Connection cn = null;
        PreparedStatement stmt = null;
        String sql = "INSERT INTO USUARIO (USER, PASS, EMAIL, ESTADO, ID_ROL) VALUES (?, ?, ?, ?, ?)";
        try {
            cn = getConexion();
            stmt = cn.prepareStatement(sql);
            stmt.setString(1, usuario.getUser());
            stmt.setString(2, usuario.getPass());
            stmt.setString(3, usuario.getEmail());
            stmt.setInt(4, usuario.getEstado());
            stmt.setInt(5, usuario.getRol().getIdRol());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error al registrar usuario: " + e.getMessage());
            throw e;
        } finally {
            if (stmt != null && !stmt.isClosed()) stmt.close();
            if (cn != null && !cn.isClosed()) cn.close();
        }
    }

    public boolean actualizar(Usuario usuario) throws SQLException {
        Connection cn = null;
        PreparedStatement stmt = null;
        String sql = "UPDATE USUARIO SET USER = ?, PASS = ?, EMAIL = ?, ESTADO = ?, ID_ROL = ?, FECHA_INICIO = ? WHERE ID_USUARIO = ?";
        try {
            cn = getConexion();
            stmt = cn.prepareStatement(sql);
            stmt.setString(1, usuario.getUser());
            stmt.setString(2, usuario.getPass());
            stmt.setString(3, usuario.getEmail());
            stmt.setInt(4, usuario.getEstado());
            stmt.setInt(5, usuario.getRol().getIdRol());
            stmt.setTimestamp(6, usuario.getFechaInicio());
            stmt.setInt(7, usuario.getIdUsuario());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar usuario: " + e.getMessage());
            throw e;
        } finally {
            if (stmt != null && !stmt.isClosed()) stmt.close();
            if (cn != null && !cn.isClosed()) cn.close();
        }
    }

    public boolean bloquear(int id) throws SQLException {
        Connection cn = null;
        PreparedStatement stmt = null;
        String sql = "UPDATE USUARIO SET ESTADO = 0 WHERE ID_USUARIO = ? AND ESTADO != 0";
        try {
            cn = getConexion();
            stmt = cn.prepareStatement(sql);
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error al bloquear usuario: " + e.getMessage());
            throw e;
        } finally {
            if (stmt != null && !stmt.isClosed()) stmt.close();
            if (cn != null && !cn.isClosed()) cn.close();
        }
    }

    public boolean desbloquear(int id) throws SQLException {
        Connection cn = null;
        PreparedStatement stmt = null;
        String sql = "UPDATE USUARIO SET ESTADO = 1 WHERE ID_USUARIO = ? AND ESTADO = 0";
        try {
            cn = getConexion();
            stmt = cn.prepareStatement(sql);
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error al desbloquear usuario: " + e.getMessage());
            throw e;
        } finally {
            if (stmt != null && !stmt.isClosed()) stmt.close();
            if (cn != null && !cn.isClosed()) cn.close();
        }
    }

    public List<Usuario> listar() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        Connection cn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT u.ID_USUARIO, u.USER, u.PASS, u.EMAIL, u.ESTADO, u.FECHA_INICIO, u.ID_ROL, r.NOMBRE_ROL " +
                     "FROM USUARIO u LEFT JOIN ROL r ON u.ID_ROL = r.ID_ROL";
        try {
            cn = getConexion();
            stmt = cn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("ID_USUARIO"));
                usuario.setUser(rs.getString("USER"));
                usuario.setPass(rs.getString("PASS"));
                usuario.setEmail(rs.getString("EMAIL"));
                usuario.setEstado(rs.getInt("ESTADO"));
                usuario.setFechaInicio(rs.getTimestamp("FECHA_INICIO"));
                Rol rol = new Rol();
                rol.setIdRol(rs.getInt("ID_ROL"));
                rol.setNombreRol(rs.getString("NOMBRE_ROL"));
                usuario.setRol(rol);
                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar usuarios: " + e.getMessage());
            throw e;
        } finally {
            if (rs != null && !rs.isClosed()) rs.close();
            if (stmt != null && !stmt.isClosed()) stmt.close();
            if (cn != null && !cn.isClosed()) cn.close();
        }
        return usuarios;
    }

        public List<Usuario> listarPostulantes() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        Connection cn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT u.ID_USUARIO, u.USER, u.PASS, u.EMAIL, u.ESTADO, u.FECHA_INICIO, u.ID_ROL, r.NOMBRE_ROL " +
                     "FROM USUARIO u LEFT JOIN ROL r ON u.ID_ROL = r.ID_ROL WHERE r.NOMBRE_ROL = 'POSTULANTE'";
        try {
            cn = getConexion();
            stmt = cn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("ID_USUARIO"));
                usuario.setUser(rs.getString("USER"));
                usuario.setPass(rs.getString("PASS"));
                usuario.setEmail(rs.getString("EMAIL"));
                usuario.setEstado(rs.getInt("ESTADO"));
                usuario.setFechaInicio(rs.getTimestamp("FECHA_INICIO"));
                Rol rol = new Rol();
                rol.setIdRol(rs.getInt("ID_ROL"));
                rol.setNombreRol(rs.getString("NOMBRE_ROL"));
                usuario.setRol(rol);
                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar usuarios: " + e.getMessage());
            throw e;
        } finally {
            if (rs != null && !rs.isClosed()) rs.close();
            if (stmt != null && !stmt.isClosed()) stmt.close();
            if (cn != null && !cn.isClosed()) cn.close();
        }
        return usuarios;
    }
    
    public Usuario obtenerPorId(int id) throws SQLException {
        Usuario usuario = null;
        Connection cn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT u.ID_USUARIO, u.USER, u.PASS, u.EMAIL, u.ESTADO, u.FECHA_INICIO, u.ID_ROL, r.NOMBRE_ROL " +
                     "FROM USUARIO u LEFT JOIN ROL r ON u.ID_ROL = r.ID_ROL WHERE u.ID_USUARIO = ?";
        try {
            cn = getConexion();
            stmt = cn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("ID_USUARIO"));
                usuario.setUser(rs.getString("USER"));
                usuario.setPass(rs.getString("PASS"));
                usuario.setEmail(rs.getString("EMAIL"));
                usuario.setEstado(rs.getInt("ESTADO"));
                usuario.setFechaInicio(rs.getTimestamp("FECHA_INICIO"));
                Rol rol = new Rol();
                rol.setIdRol(rs.getInt("ID_ROL"));
                rol.setNombreRol(rs.getString("NOMBRE_ROL"));
                usuario.setRol(rol);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener usuario: " + e.getMessage());
            throw e;
        } finally {
            if (rs != null && !rs.isClosed()) rs.close();
            if (stmt != null && !stmt.isClosed()) stmt.close();
            if (cn != null && !cn.isClosed()) cn.close();
        }
        return usuario;
    }

    public Usuario obtenerPorUser(String user) throws SQLException {
        Usuario usuario = null;
        Connection cn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT u.ID_USUARIO, u.USER, u.PASS, u.EMAIL, u.ESTADO, u.FECHA_INICIO, u.ID_ROL, r.NOMBRE_ROL " +
                     "FROM USUARIO u LEFT JOIN ROL r ON u.ID_ROL = r.ID_ROL WHERE u.USER = ?";
        try {
            cn = getConexion();
            stmt = cn.prepareStatement(sql);
            stmt.setString(1, user);
            rs = stmt.executeQuery();
            if (rs.next()) {
                usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("ID_USUARIO"));
                usuario.setUser(rs.getString("USER"));
                usuario.setPass(rs.getString("PASS"));
                usuario.setEmail(rs.getString("EMAIL"));
                usuario.setEstado(rs.getInt("ESTADO"));
                usuario.setFechaInicio(rs.getTimestamp("FECHA_INICIO"));
                Rol rol = new Rol();
                rol.setIdRol(rs.getInt("ID_ROL"));
                rol.setNombreRol(rs.getString("NOMBRE_ROL"));
                usuario.setRol(rol);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener usuario por user: " + e.getMessage());
            throw e;
        } finally {
            if (rs != null && !rs.isClosed()) rs.close();
            if (stmt != null && !stmt.isClosed()) stmt.close();
            if (cn != null && !cn.isClosed()) cn.close();
        }
        return usuario;
    }

    public List<Rol> listarRoles() throws SQLException {
        List<Rol> roles = new ArrayList<>();
        Connection cn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT ID_ROL, NOMBRE_ROL FROM ROL";
        try {
            cn = getConexion();
            stmt = cn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Rol rol = new Rol();
                rol.setIdRol(rs.getInt("ID_ROL"));
                rol.setNombreRol(rs.getString("NOMBRE_ROL"));
                roles.add(rol);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar roles: " + e.getMessage());
            throw e;
        } finally {
            if (rs != null && !rs.isClosed()) rs.close();
            if (stmt != null && !stmt.isClosed()) stmt.close();
            if (cn != null && !cn.isClosed()) cn.close();
        }
        return roles;
    }
}