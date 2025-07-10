package controlador;

import modelo.Respuesta;
import modelo.Pregunta;
import modelo.Curso;
import modelo.dao.RespuestaDAO;
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

@WebServlet(name = "RespuestaServlet", urlPatterns = {"/RespuestaServlet"})
public class RespuestaServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String accion = request.getParameter("accion");
        int idPregunta = Integer.parseInt(request.getParameter("idPregunta"));
        int idCurso = Integer.parseInt(request.getParameter("idCurso"));
        System.out.println("Acción recibida: " + accion + ", idPregunta: " + idPregunta + ", idCurso: " + idCurso); // Depuración

        try {
            // Cargar la pregunta para mostrar su texto
            PreguntaDAO preguntaDAO = new PreguntaDAO();
            Pregunta pregunta = preguntaDAO.obtenerPorId(idPregunta);
            request.setAttribute("pregunta", pregunta);

            if (accion != null) {
                switch (accion) {
                    case "editar":
                        System.out.println("Editando respuesta con id: " + request.getParameter("id"));
                        cargarRespuestaParaEditar(request, response);
                        break;
                    case "eliminar":
                        System.out.println("Eliminando respuesta con id: " + request.getParameter("id"));
                        eliminarRespuesta(request, response);
                        break;
                    case "insertar":
                        System.out.println("Insertando respuesta: " + request.getParameter("respuesta"));
                        insertarRespuesta(request, response);
                        break;
                    case "actualizar":
                        System.out.println("Actualizando respuesta con id: " + request.getParameter("idRespuesta"));
                        actualizarRespuesta(request, response);
                        break;
                    default:
                        System.out.println("Listando respuestas por acción no reconocida");
                        listarRespuestas(request, response);
                }
            } else {
                System.out.println("Listando respuestas por acción nula");
                listarRespuestas(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error en RespuestaServlet: " + e.getMessage());
            request.setAttribute("msje", "Error: " + e.getMessage());
            listarRespuestas(request, response);
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

    private void listarRespuestas(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int idPregunta = Integer.parseInt(request.getParameter("idPregunta"));
        RespuestaDAO dao = new RespuestaDAO();
        try {
            List<Respuesta> respuestas = dao.listarPorPregunta(idPregunta);
            System.out.println("Respuestas listadas: " + (respuestas != null ? respuestas.size() : "null")); // Depuración
            request.setAttribute("respuestas", respuestas);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al listar respuestas: " + e.getMessage());
            request.setAttribute("msje", "Error al listar respuestas: " + e.getMessage());
        }
        request.getRequestDispatcher("/vista/admin/gestionRespuestas.jsp").forward(request, response);
    }

    private void cargarRespuestaParaEditar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            RespuestaDAO dao = new RespuestaDAO();
            Respuesta respuesta = dao.obtenerPorId(id);
            System.out.println("Respuesta cargada para editar: " + (respuesta != null ? respuesta.getRespuesta() : "null")); // Depuración
            request.setAttribute("respuesta", respuesta);
        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            System.out.println("Error al cargar respuesta: " + e.getMessage());
            request.setAttribute("msje", "Error al cargar respuesta: " + e.getMessage());
        }
        listarRespuestas(request, response);
    }

    private void insertarRespuesta(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Respuesta respuesta = new Respuesta();
            respuesta.setRespuesta(request.getParameter("respuesta"));
            String correctaParam = request.getParameter("correcta");
            respuesta.setCorrecta("true".equals(correctaParam));
            Pregunta pregunta = new Pregunta();
            pregunta.setIdPregunta(Integer.parseInt(request.getParameter("idPregunta")));
            respuesta.setPregunta(pregunta);
            RespuestaDAO dao = new RespuestaDAO();
            boolean exito = dao.registrar(respuesta);
            System.out.println("Respuesta insertada: " + respuesta.getRespuesta() + ", Éxito: " + exito); // Depuración
            request.setAttribute("msje", exito ? "Respuesta insertada correctamente" : "Error al insertar respuesta");
        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            System.out.println("Error al insertar respuesta: " + e.getMessage());
            request.setAttribute("msje", "Error al insertar respuesta: " + e.getMessage());
        }
        listarRespuestas(request, response);
    }

    private void actualizarRespuesta(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Respuesta respuesta = new Respuesta();
            respuesta.setIdRespuesta(Integer.parseInt(request.getParameter("idRespuesta")));
            respuesta.setRespuesta(request.getParameter("respuesta"));
            String correctaParam = request.getParameter("correcta");
            respuesta.setCorrecta("true".equals(correctaParam));
            Pregunta pregunta = new Pregunta();
            pregunta.setIdPregunta(Integer.parseInt(request.getParameter("idPregunta")));
            respuesta.setPregunta(pregunta);
            RespuestaDAO dao = new RespuestaDAO();
            boolean exito = dao.actualizar(respuesta);
            System.out.println("Respuesta actualizada: " + respuesta.getRespuesta() + ", Éxito: " + exito); // Depuración
            request.setAttribute("msje", exito ? "Respuesta actualizada correctamente" : "Error al actualizar respuesta");
        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            System.out.println("Error al actualizar respuesta: " + e.getMessage());
            request.setAttribute("msje", "Error al actualizar respuesta: " + e.getMessage());
        }
        listarRespuestas(request, response);
    }

    private void eliminarRespuesta(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            RespuestaDAO dao = new RespuestaDAO();
            boolean exito = dao.eliminar(id);
            System.out.println("Respuesta eliminada con id: " + id + ", Éxito: " + exito); // Depuración
            request.setAttribute("msje", exito ? "Respuesta eliminada correctamente" : "Error al eliminar respuesta");
        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            System.out.println("Error al eliminar respuesta: " + e.getMessage());
            request.setAttribute("msje", "Error al eliminar respuesta: " + e.getMessage());
        }
        listarRespuestas(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Servlet para gestión de respuestas";
    }
}