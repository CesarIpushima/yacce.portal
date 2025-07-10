package controlador;

import modelo.Curso;
import modelo.dao.CursoDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "CursoServlet", urlPatterns = {"/CursoServlet"})
public class CursoServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String accion = request.getParameter("accion");
        System.out.println("Acción recibida: " + accion); // Depuración

        try {
            if (accion != null) {
                switch (accion) {
                    case "editar":
                        System.out.println("Editando curso con id: " + request.getParameter("id"));
                        cargarCursoParaEditar(request, response);
                        break;
                    case "eliminar":
                        System.out.println("Eliminando curso con id: " + request.getParameter("id"));
                        eliminarCurso(request, response);
                        break;
                    case "insertar":
                        System.out.println("Insertando curso: " + request.getParameter("nombreCurso"));
                        insertarCurso(request, response);
                        break;
                    case "actualizar":
                        System.out.println("Actualizando curso con id: " + request.getParameter("idCurso"));
                        actualizarCurso(request, response);
                        break;
                    default:
                        System.out.println("Listando cursos por acción no reconocida");
                        listarCursos(request, response);
                }
            } else {
                System.out.println("Listando cursos por acción nula");
                listarCursos(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error en CursoServlet: " + e.getMessage());
            request.setAttribute("msje", "Error: " + e.getMessage());
            listarCursos(request, response);
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

    private void listarCursos(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        CursoDAO dao = new CursoDAO();
        try {
            // Verificar la sesión y el rol
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("username") == null) {
                System.out.println("Sin sesión activa, redirigiendo a login.jsp");
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }

            String role = (String) session.getAttribute("role");
            System.out.println("Rol del usuario: " + (role != null ? role : "null")); // Depuración

            // Obtener la lista de cursos
            List<Curso> cursos = dao.listar();
            System.out.println("Cursos listados: " + (cursos != null ? cursos.size() : "null")); // Depuración
            if (cursos != null) {
                for (Curso curso : cursos) {
                    System.out.println("Curso: " + curso.getIdCurso() + " - " + curso.getNombreCurso()); // Depuración detallada
                }
            } else {
                System.out.println("No se encontraron cursos.");
            }
            request.setAttribute("cursos", cursos);

            // Redirigir según el rol (en mayúsculas)
            if ("ADMINISTRADOR".equals(role)) {
                request.getRequestDispatcher("/vista/admin/gestionCursos.jsp").forward(request, response);
            } else if ("POSTULANTE".equals(role)) {
                request.getRequestDispatcher("/vista/postulante/indexPostulante.jsp").forward(request, response);
            } else {
                System.out.println("Rol no reconocido: " + role + ", redirigiendo a login.jsp");
                response.sendRedirect(request.getContextPath() + "/login.jsp");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al listar cursos: " + e.getMessage());
            request.setAttribute("msje", "Error al listar cursos: " + e.getMessage());
            // Redirigir al JSP del postulante en caso de error, si es postulante
            String role = (String) request.getSession(false).getAttribute("role");
            if ("POSTULANTE".equals(role)) {
                request.getRequestDispatcher("/vista/postulante/indexPostulante.jsp").forward(request, response);
            } else {
                request.getRequestDispatcher("/vista/admin/gestionCursos.jsp").forward(request, response);
            }
        }
    }

    private void cargarCursoParaEditar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            CursoDAO dao = new CursoDAO();
            Curso curso = dao.obtenerPorId(id);
            System.out.println("Curso cargado para editar: " + (curso != null ? curso.getNombreCurso() : "null")); // Depuración
            request.setAttribute("curso", curso);
        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            System.out.println("Error al cargar curso: " + e.getMessage());
            request.setAttribute("msje", "Error al cargar curso: " + e.getMessage());
        }
        listarCursos(request, response);
    }

    private void insertarCurso(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Curso curso = new Curso();
            curso.setNombreCurso(request.getParameter("nombreCurso"));
            CursoDAO dao = new CursoDAO();
            boolean exito = dao.registrar(curso);
            System.out.println("Curso insertado: " + curso.getNombreCurso() + ", Éxito: " + exito); // Depuración
            request.setAttribute("msje", exito ? "Curso insertado correctamente" : "Error al insertar curso");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al insertar curso: " + e.getMessage());
            request.setAttribute("msje", "Error al insertar curso: " + e.getMessage());
        }
        listarCursos(request, response);
    }

    private void actualizarCurso(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Curso curso = new Curso();
            curso.setIdCurso(Integer.parseInt(request.getParameter("idCurso")));
            curso.setNombreCurso(request.getParameter("nombreCurso"));
            CursoDAO dao = new CursoDAO();
            boolean exito = dao.actualizar(curso);
            System.out.println("Curso actualizado: " + curso.getNombreCurso() + ", Éxito: " + exito); // Depuración
            request.setAttribute("msje", exito ? "Curso actualizado correctamente" : "Error al actualizar curso");
        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            System.out.println("Error al actualizar curso: " + e.getMessage());
            request.setAttribute("msje", "Error al actualizar curso: " + e.getMessage());
        }
        listarCursos(request, response);
    }

    private void eliminarCurso(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            CursoDAO dao = new CursoDAO();
            boolean exito = dao.eliminar(id);
            System.out.println("Curso eliminado con id: " + id + ", Éxito: " + exito); // Depuración
            request.setAttribute("msje", exito ? "Curso eliminado correctamente" : "Error al eliminar curso");
        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            System.out.println("Error al eliminar curso: " + e.getMessage());
            request.setAttribute("msje", "Error al eliminar curso: " + e.getMessage());
        }
        listarCursos(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Servlet para gestión de cursos";
    }
}