package controlador;

import modelo.Pregunta;
import modelo.Curso;
import modelo.dao.PreguntaDAO;
import modelo.dao.CursoDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "PreguntaServlet", urlPatterns = {"/PreguntaServlet"})
public class PreguntaServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String accion = request.getParameter("accion");
        int idCurso = Integer.parseInt(request.getParameter("idCurso"));
        System.out.println("Acción recibida: " + accion + ", idCurso: " + idCurso); // Depuración

        try {
            // Cargar el curso para mostrar su nombre
            CursoDAO cursoDAO = new CursoDAO();
            Curso curso = cursoDAO.obtenerPorId(idCurso);
            request.setAttribute("curso", curso);

            if (accion != null) {
                switch (accion) {
                    case "editar":
                        System.out.println("Editando pregunta con id: " + request.getParameter("id"));
                        cargarPreguntaParaEditar(request, response);
                        break;
                    case "eliminar":
                        System.out.println("Eliminando pregunta con id: " + request.getParameter("id"));
                        eliminarPregunta(request, response);
                        break;
                    case "insertar":
                        System.out.println("Insertando pregunta: " + request.getParameter("pregunta"));
                        insertarPregunta(request, response);
                        break;
                    case "actualizar":
                        System.out.println("Actualizando pregunta con id: " + request.getParameter("idPregunta"));
                        actualizarPregunta(request, response);
                        break;
                    default:
                        System.out.println("Listando preguntas por acción no reconocida");
                        listarPreguntas(request, response);
                }
            } else {
                System.out.println("Listando preguntas por acción nula");
                listarPreguntas(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error en PreguntaServlet: " + e.getMessage());
            request.setAttribute("msje", "Error: " + e.getMessage());
            listarPreguntas(request, response);
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

    private void listarPreguntas(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int idCurso = Integer.parseInt(request.getParameter("idCurso"));
        PreguntaDAO dao = new PreguntaDAO();
        try {
            List<Pregunta> preguntas = dao.listarPorCurso(idCurso);
            System.out.println("Preguntas listadas: " + (preguntas != null ? preguntas.size() : "null")); // Depuración
            request.setAttribute("preguntas", preguntas);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al listar preguntas: " + e.getMessage());
            request.setAttribute("msje", "Error al listar preguntas: " + e.getMessage());
        }
        request.getRequestDispatcher("/vista/admin/gestionPreguntas.jsp").forward(request, response);
    }

    private void cargarPreguntaParaEditar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            PreguntaDAO dao = new PreguntaDAO();
            Pregunta pregunta = dao.obtenerPorId(id);
            System.out.println("Pregunta cargada para editar: " + (pregunta != null ? pregunta.getPregunta() : "null")); // Depuración
            request.setAttribute("pregunta", pregunta);
        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            System.out.println("Error al cargar pregunta: " + e.getMessage());
            request.setAttribute("msje", "Error al cargar pregunta: " + e.getMessage());
        }
        listarPreguntas(request, response);
    }

    private void insertarPregunta(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Pregunta pregunta = new Pregunta();
            pregunta.setPregunta(request.getParameter("pregunta"));
            Curso curso = new Curso();
            curso.setIdCurso(Integer.parseInt(request.getParameter("idCurso")));
            pregunta.setCurso(curso);
            PreguntaDAO dao = new PreguntaDAO();
            boolean exito = dao.registrar(pregunta);
            System.out.println("Pregunta insertada: " + pregunta.getPregunta() + ", Éxito: " + exito); // Depuración
            request.setAttribute("msje", exito ? "Pregunta insertada correctamente" : "Error al insertar pregunta");
        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            System.out.println("Error al insertar pregunta: " + e.getMessage());
            request.setAttribute("msje", "Error al insertar pregunta: " + e.getMessage());
        }
        listarPreguntas(request, response);
    }

    private void actualizarPregunta(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Pregunta pregunta = new Pregunta();
            pregunta.setIdPregunta(Integer.parseInt(request.getParameter("idPregunta")));
            pregunta.setPregunta(request.getParameter("pregunta"));
            Curso curso = new Curso();
            curso.setIdCurso(Integer.parseInt(request.getParameter("idCurso")));
            pregunta.setCurso(curso);
            PreguntaDAO dao = new PreguntaDAO();
            boolean exito = dao.actualizar(pregunta);
            System.out.println("Pregunta actualizada: " + pregunta.getPregunta() + ", Éxito: " + exito); // Depuración
            request.setAttribute("msje", exito ? "Pregunta actualizada correctamente" : "Error al actualizar pregunta");
        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            System.out.println("Error al actualizar pregunta: " + e.getMessage());
            request.setAttribute("msje", "Error al actualizar pregunta: " + e.getMessage());
        }
        listarPreguntas(request, response);
    }

    private void eliminarPregunta(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            PreguntaDAO dao = new PreguntaDAO();
            boolean exito = dao.eliminar(id);
            System.out.println("Pregunta eliminada con id: " + id + ", Éxito: " + exito); // Depuración
            request.setAttribute("msje", exito ? "Pregunta eliminada correctamente" : "Error al eliminar pregunta");
        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            System.out.println("Error al eliminar pregunta: " + e.getMessage());
            request.setAttribute("msje", "Error al eliminar pregunta: " + e.getMessage());
        }
        listarPreguntas(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Servlet para gestión de preguntas";
    }
}