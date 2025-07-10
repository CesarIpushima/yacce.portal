package controlador;

import modelo.Usuario;
import modelo.dao.UsuarioDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.sql.Timestamp;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String accion = request.getParameter("accion");
        System.out.println("Acción recibida: " + accion); // Depuración

        try {
            if (accion != null) {
                switch (accion) {
                    case "login":
                        System.out.println("Procesando inicio de sesión para: " + request.getParameter("username"));
                        procesarLogin(request, response);
                        break;
                    default:
                        System.out.println("Redirigiendo a login por acción no reconocida");
                        request.getRequestDispatcher("/vista/login.jsp").forward(request, response);
                }
            } else {
                System.out.println("Redirigiendo a login por acción nula");
                request.getRequestDispatcher("/vista/login.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error en LoginServlet: " + e.getMessage());
            request.setAttribute("msje", "Error: " + e.getMessage());
            request.setAttribute("mensajeTipo", "danger");
            request.getRequestDispatcher("/vista/login.jsp").forward(request, response);
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

    private void procesarLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String user = request.getParameter("username");
        String pass = request.getParameter("password");
        UsuarioDAO dao = new UsuarioDAO();

        try {
            Usuario usuario = dao.obtenerPorUser(user);
            System.out.println("Usuario encontrado: " + (usuario != null ? usuario.getUser() : "null")); // Depuración

            if (usuario != null && pass.equals(usuario.getPass())) { // Comparación simple
                int estado = usuario.getEstado();

                if (estado == 0) {
                    System.out.println("Usuario bloqueado: " + user);
                    request.setAttribute("msje", "Usuario bloqueado. Contacte al administrador.");
                    request.setAttribute("mensajeTipo", "danger");
                } else if (estado == 1) {
                    // Primer ingreso: actualizar a estado 2 y registrar fecha
                    System.out.println("Primer ingreso para: " + user);
                    usuario.setEstado(2);
                    usuario.setFechaInicio(new Timestamp(System.currentTimeMillis()));
                    boolean exito = dao.actualizar(usuario);
                    System.out.println("Actualización de estado y fecha: " + exito); // Depuración
                    if (exito) {
                        HttpSession session = request.getSession();
                        session.setAttribute("username", user);
                        session.setAttribute("role", usuario.getRol().getNombreRol());
                        if ("ADMINISTRADOR".equals(usuario.getRol().getNombreRol())) {
                            response.sendRedirect(request.getContextPath() + "/CursoServlet");
                        } else if ("POSTULANTE".equals(usuario.getRol().getNombreRol())) {
                            response.sendRedirect(request.getContextPath() + "/CursoServlet");
                        }
                    } else {
                        request.setAttribute("msje", "Error al procesar primer ingreso.");
                        request.setAttribute("mensajeTipo", "danger");
                    }
                } else if (estado == 2 || estado == 3) {
                    // Usuario en proceso
                    System.out.println("Usuario en proceso: " + user);
                    
                    if (estado == 2 && usuario.getFechaInicio() != null && "POSTULANTE".equals(usuario.getRol().getNombreRol())) {
                        long milisegundosActuales = System.currentTimeMillis();
                        long milisegundosInicio = usuario.getFechaInicio().getTime();
                        long diferenciaDias = (milisegundosActuales - milisegundosInicio) / (1000 * 60 * 60 * 24);

                        if (diferenciaDias > 7) {
                            // Bloquear al usuario automáticamente
                            usuario.setEstado(0);
                            dao.actualizar(usuario);
                            request.setAttribute("msje", "Su cuenta ha sido bloqueada automáticamente por superar los 7 días permitidos sin completar el proceso.");
                            request.setAttribute("mensajeTipo", "danger");
                            request.getRequestDispatcher("/vista/login.jsp").forward(request, response);
                            return;
                        } else {
                            long diasRestantes = 7 - diferenciaDias;
                            request.setAttribute("msje", "Advertencia: Le quedan " + diasRestantes + " día(s) para completar el proceso antes de ser bloqueado.");
                            request.setAttribute("mensajeTipo", "warning");
                        }
                    }
                    HttpSession session = request.getSession();
                    session.setAttribute("username", user);
                    session.setAttribute("role", usuario.getRol().getNombreRol());
                    if ("ADMINISTRADOR".equals(usuario.getRol().getNombreRol())) {
                        response.sendRedirect(request.getContextPath() + "/CursoServlet");
                    } else if ("POSTULANTE".equals(usuario.getRol().getNombreRol())) {
                        response.sendRedirect(request.getContextPath() + "/CursoServlet");
                    }
                }
            } else {
                System.out.println("Credenciales incorrectas para: " + user);
                request.setAttribute("msje", "Usuario o contraseña incorrectos.");
                request.setAttribute("mensajeTipo", "danger");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al procesar login: " + e.getMessage());
            request.setAttribute("msje", "Error al procesar login: " + e.getMessage());
            request.setAttribute("mensajeTipo", "danger");
        }

        if (request.getAttribute("msje") != null) {
            request.getRequestDispatcher("/vista/login.jsp").forward(request, response);
        }
    }
    private void cerrarSesion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            System.out.println("Sesión encontrada, invalidando..."); // Depuración
            session.invalidate(); // Invalidar la sesión
        } else {
            System.out.println("No hay sesión activa para cerrar"); // Depuración
        }
        response.sendRedirect(request.getContextPath() + "/vista/login.jsp");
    }
    @Override
    public String getServletInfo() {
        return "Servlet para gestionar el inicio de sesión";
    }
}