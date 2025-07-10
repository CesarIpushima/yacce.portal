package controlador;

import modelo.Usuario;
import modelo.Rol;
import modelo.dao.UsuarioDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;
import modelo.ProgresoCurso;
import modelo.dao.ProgresoCursoDAO;

@WebServlet(name = "UsuarioServlet", urlPatterns = {"/UsuarioServlet"})
public class UsuarioServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String accion = request.getParameter("accion");
        System.out.println("Acción recibida: " + accion); // Depuración

        try {
            // Cargar roles para el combo box
            UsuarioDAO dao = new UsuarioDAO();
            List<Rol> roles = dao.listarRoles();
            request.setAttribute("roles", roles);

            if (accion != null) {
                switch (accion) {
                    case "editar":
                        System.out.println("Editando usuario con id: " + request.getParameter("id"));
                        cargarUsuarioParaEditar(request, response);
                        break;
                    case "eliminar":
                        System.out.println("Bloqueando usuario con id: " + request.getParameter("id"));
                        bloquearUsuario(request, response);
                        break;
                    case "desbloquear":
                        System.out.println("Desbloqueando usuario con id: " + request.getParameter("id"));
                        desbloquearUsuario(request, response);
                        break;
                    case "insertar":
                        System.out.println("Insertando usuario: " + request.getParameter("user"));
                        insertarUsuario(request, response);
                        break;
                    case "actualizar":
                        System.out.println("Actualizando usuario con id: " + request.getParameter("idUsuario"));
                        actualizarUsuario(request, response);
                        break;
                    case "reporteGeneral":
                        System.out.println("Mostrando reporte general de usuarios");
                        reporteGeneral(request, response);
                        break;
                    case "verReporteDetalle":
                        System.out.println("Mostrando detalle del usuario con id: " + request.getParameter("idUsuario"));
                        verReporteDetalle(request, response);
                        break;
                    default:
                        System.out.println("Listando usuarios por acción no reconocida");
                        listarUsuarios(request, response);
                }
            } else {
                System.out.println("Listando usuarios por acción nula");
                listarUsuarios(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error en UsuarioServlet: " + e.getMessage());
            request.setAttribute("msje", "Error: " + e.getMessage());
            listarUsuarios(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    private void listarUsuarios(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UsuarioDAO dao = new UsuarioDAO();
        try {
            List<Usuario> usuarios = dao.listar();
            System.out.println("Usuarios listados: " + (usuarios != null ? usuarios.size() : "null")); // Depuración
            request.setAttribute("usuarios", usuarios);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al listar usuarios: " + e.getMessage());
            request.setAttribute("msje", "Error al listar usuarios: " + e.getMessage());
        }
        request.getRequestDispatcher("/vista/admin/gestionUsuarios.jsp").forward(request, response);
    }

    private void cargarUsuarioParaEditar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            UsuarioDAO dao = new UsuarioDAO();
            Usuario usuario = dao.obtenerPorId(id);
            System.out.println("Usuario cargado para editar: " + (usuario != null ? usuario.getUser() : "null")); // Depuración
            request.setAttribute("usuario", usuario);
        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            System.out.println("Error al cargar usuario: " + e.getMessage());
            request.setAttribute("msje", "Error al cargar usuario: " + e.getMessage());
        }
        listarUsuarios(request, response);
    }

    private void insertarUsuario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Usuario usuario = new Usuario();
            usuario.setUser(request.getParameter("user"));
            usuario.setPass(request.getParameter("pass"));
            usuario.setEmail(request.getParameter("email"));
            usuario.setEstado(1); // Estado predeterminado: Sin iniciar
            Rol rol = new Rol();
            rol.setIdRol(Integer.parseInt(request.getParameter("idRol")));
            usuario.setRol(rol);
            UsuarioDAO dao = new UsuarioDAO();
            boolean exito = dao.registrar(usuario);
            System.out.println("Usuario insertado: " + usuario.getUser() + ", Éxito: " + exito); // Depuración
            request.setAttribute("msje", exito ? "Usuario insertado correctamente" : "Error al insertar usuario");
        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            System.out.println("Error al insertar usuario: " + e.getMessage());
            request.setAttribute("msje", "Error al insertar usuario: " + e.getMessage());
        }
        listarUsuarios(request, response);
    }

    private void actualizarUsuario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Usuario usuario = new Usuario();
            usuario.setIdUsuario(Integer.parseInt(request.getParameter("idUsuario")));
            usuario.setUser(request.getParameter("user"));
            usuario.setPass(request.getParameter("pass"));
            usuario.setEmail(request.getParameter("email"));
            // Mantener el estado existente, ya que no se envía desde el formulario
            UsuarioDAO dao = new UsuarioDAO();
            Usuario usuarioExistente = dao.obtenerPorId(usuario.getIdUsuario());
            usuario.setEstado(usuarioExistente.getEstado());
            Rol rol = new Rol();
            rol.setIdRol(Integer.parseInt(request.getParameter("idRol")));
            usuario.setRol(rol);
            boolean exito = dao.actualizar(usuario);
            System.out.println("Usuario actualizado: " + usuario.getUser() + ", Éxito: " + exito); // Depuración
            request.setAttribute("msje", exito ? "Usuario actualizado correctamente" : "Error al actualizar usuario");
        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            System.out.println("Error al actualizar usuario: " + e.getMessage());
            request.setAttribute("msje", "Error al actualizar usuario: " + e.getMessage());
        }
        listarUsuarios(request, response);
    }

    private void bloquearUsuario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            UsuarioDAO dao = new UsuarioDAO();
            boolean exito = dao.bloquear(id);
            System.out.println("Usuario bloqueado con id: " + id + ", Éxito: " + exito); // Depuración
            request.setAttribute("msje", exito ? "Usuario bloqueado correctamente" : "Error al bloquear usuario");
        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            System.out.println("Error al bloquear usuario: " + e.getMessage());
            request.setAttribute("msje", "Error al bloquear usuario: " + e.getMessage());
        }
        listarUsuarios(request, response);
    }

    private void desbloquearUsuario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            UsuarioDAO dao = new UsuarioDAO();
            boolean exito = dao.desbloquear(id);
            System.out.println("Usuario desbloqueado con id: " + id + ", Éxito: " + exito); // Depuración
            request.setAttribute("msje", exito ? "Usuario desbloqueado correctamente" : "Error al desbloquear usuario");
        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            System.out.println("Error al desbloquear usuario: " + e.getMessage());
            request.setAttribute("msje", "Error al desbloquear usuario: " + e.getMessage());
        }
        listarUsuarios(request, response);
    }

    private void reporteGeneral(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Verificar que el usuario sea administrador
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null || !"ADMINISTRADOR".equals(session.getAttribute("role"))) {
            System.out.println("Acceso denegado: Solo administradores pueden ver el reporte general");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        UsuarioDAO dao = new UsuarioDAO();
        try {
            List<Usuario> usuarios = dao.listarPostulantes();
            System.out.println("Usuarios listados para reporte: " + (usuarios != null ? usuarios.size() : "null")); // Depuración
            request.setAttribute("usuarios", usuarios);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al listar usuarios para reporte: " + e.getMessage());
            request.setAttribute("msje", "Error al listar usuarios para reporte: " + e.getMessage());
        }
        System.out.println("Redirigiendo a reporteGeneral.jsp");
        request.getRequestDispatcher("/vista/admin/reporteGeneral.jsp").forward(request, response);
    }

private void verReporteDetalle(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    // Verificar que el usuario sea administrador
    HttpSession session = request.getSession(false);
    if (session == null || session.getAttribute("username") == null || !"ADMINISTRADOR".equals(session.getAttribute("role"))) {
        System.out.println("Acceso denegado: Solo administradores pueden ver el detalle del reporte");
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    String idUsuarioParam = request.getParameter("idUsuario");
    if (idUsuarioParam == null || idUsuarioParam.trim().isEmpty()) {
        System.out.println("idUsuario no proporcionado o vacío.");
        request.setAttribute("msje", "ID de usuario no proporcionado.");
        reporteGeneral(request, response);
        return;
    }

    int idUsuario;
    try {
        idUsuario = Integer.parseInt(idUsuarioParam);
    } catch (NumberFormatException e) {
        System.out.println("idUsuario no es un número válido: " + idUsuarioParam);
        request.setAttribute("msje", "ID de usuario inválido.");
        reporteGeneral(request, response);
        return;
    }

    UsuarioDAO dao = new UsuarioDAO();
    ProgresoCursoDAO progresoDao = new ProgresoCursoDAO();
    try {
        Usuario usuario = dao.obtenerPorId(idUsuario);
        if (usuario == null) {
            System.out.println("Usuario no encontrado para id: " + idUsuario);
            request.setAttribute("msje", "Usuario no encontrado.");
            reporteGeneral(request, response);
            return;
        }
        List<ProgresoCurso> progresos = progresoDao.listarProgresoPorUsuario(idUsuario);
        System.out.println("Usuario cargado para detalle: " + usuario.getUser() + ", Progresos: " + progresos.size()); // Depuración
        request.setAttribute("usuario", usuario);
        request.setAttribute("progresos", progresos);
    } catch (SQLException e) {
        e.printStackTrace();
        System.out.println("Error al cargar detalle del usuario o progresos: " + e.getMessage());
        request.setAttribute("msje", "Error al cargar detalle del usuario: " + e.getMessage());
    }
    System.out.println("Redirigiendo a reporteDetallado.jsp con idUsuario: " + idUsuario);
    request.getRequestDispatcher("/vista/admin/reporteDetallado.jsp").forward(request, response);
}

    @Override
    public String getServletInfo() {
        return "Servlet para gestión de usuarios";
    }
}