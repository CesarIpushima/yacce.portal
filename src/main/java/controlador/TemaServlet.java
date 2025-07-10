package controlador;

import modelo.Tema;
import modelo.Curso;
import modelo.Usuario;
import modelo.ProgresoTema;
import modelo.dao.TemaDAO;
import modelo.dao.CursoDAO;
import modelo.dao.UsuarioDAO;
import modelo.dao.ProgresoTemaDAO;
import modelo.Pregunta;
import modelo.Respuesta;
import modelo.dao.PreguntaDAO;
import modelo.dao.RespuestaDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "TemaServlet", urlPatterns = {"/TemaServlet"})
public class TemaServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "listar"; // Acción por defecto
        }

        // Manejo del parámetro idCurso (solo si no es obtenerCertificado)
        int idCurso = 1; // Valor por defecto
        if (!accion.equals("obtenerCertificado")) {
            String idCursoStr = request.getParameter("idCurso");
            if (idCursoStr != null && !idCursoStr.trim().isEmpty()) {
                try {
                    idCurso = Integer.parseInt(idCursoStr);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    request.setAttribute("msje", "ID de curso inválido.");
                    listarTemas(request, response);
                    return;
                }
            } else {
                request.setAttribute("msje", "ID de curso no proporcionado.");
                listarTemas(request, response);
                return;
            }
        }

        try {
            // Verificar sesión
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("username") == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }
            String username = (String) session.getAttribute("username");
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            Usuario usuario = usuarioDAO.obtenerPorUser(username);
            if (usuario == null) {
                request.setAttribute("msje", "Usuario no encontrado.");
                listarTemas(request, response);
                return;
            }

            // Procesar acción
            switch (accion) {
                case "obtenerCertificado":
                    obtenerCertificado(request, response);
                    break;
                case "editar":
                    cargarTemaParaEditar(request, response);
                    break;
                case "eliminar":
                    eliminarTema(request, response);
                    break;
                case "insertar":
                    insertarTema(request, response);
                    break;
                case "actualizar":
                    actualizarTema(request, response);
                    break;
                case "verContenido":
                    verContenido(request, response, usuario);
                    break;
                case "verExamen":
                    verExamen(request, response, usuario);
                    break;
                case "finalizarTema":
                    finalizarTema(request, response, usuario);
                    break;
                default:
                    // Cargar curso solo para acciones que lo requieran
                    CursoDAO cursoDAO = new CursoDAO();
                    Curso curso = cursoDAO.obtenerPorId(idCurso);
                    request.setAttribute("curso", curso);
                    ProgresoTemaDAO progresoTemaDAO = new ProgresoTemaDAO();
                    boolean examenHabilitado = progresoTemaDAO.todosTemasCompletados(idCurso, usuario.getIdUsuario());
                    request.setAttribute("examenHabilitado", examenHabilitado);
                    listarTemas(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("msje", "Error: " + e.getMessage());
            listarTemas(request, response);
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
private void obtenerCertificado(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    HttpSession session = request.getSession(false);
    if (session == null || session.getAttribute("username") == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    String username = (String) session.getAttribute("username");
    UsuarioDAO usuarioDAO = new UsuarioDAO();
    try {
        Usuario usuario = usuarioDAO.obtenerPorUser(username);
        if (usuario == null || usuario.getEstado() != 3) {
            response.sendRedirect(request.getContextPath() + "/TemaServlet?accion=listar&idCurso=1"); // Redirect if not eligible
            return;
        }
        request.setAttribute("usuario", usuario); // Pass user object for name
        request.getRequestDispatcher("/vista/postulante/certificado.jsp").forward(request, response);
    } catch (SQLException e) {
        e.printStackTrace();
        request.setAttribute("msje", "Error al obtener certificado: " + e.getMessage());
        listarTemas(request, response);
    }
}
private void listarTemas(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    HttpSession session = request.getSession(false);
    if (session == null || session.getAttribute("username") == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    String role = (String) session.getAttribute("role");
    int idCurso = Integer.parseInt(request.getParameter("idCurso"));
    TemaDAO dao = new TemaDAO();
    try {
        List<Tema> temas = dao.listarPorCurso(idCurso);
        request.setAttribute("temas", temas);

        // Fetch user progress
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario usuario = usuarioDAO.obtenerPorUser((String) session.getAttribute("username"));
        if (usuario != null) {
            ProgresoTemaDAO progresoTemaDAO = new ProgresoTemaDAO();
           Map<Integer, String> temaEstados = progresoTemaDAO.obtenerEstadosPorCurso(idCurso, usuario.getIdUsuario());
            request.setAttribute("temaEstados", temaEstados);
            boolean examenHabilitado = progresoTemaDAO.todosTemasCompletados(idCurso, usuario.getIdUsuario());
            request.setAttribute("examenHabilitado", examenHabilitado);
        }
    } catch (SQLException e) {
        e.printStackTrace();
        request.setAttribute("msje", "Error al listar temas: " + e.getMessage());
    }

    if ("ADMINISTRADOR".equals(role)) {
        request.getRequestDispatcher("/vista/admin/gestionTemas.jsp").forward(request, response);
    } else if ("POSTULANTE".equals(role)) {
        request.getRequestDispatcher("/vista/postulante/temas.jsp").forward(request, response);
    } else {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }
}
    private void cargarTemaParaEditar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            TemaDAO dao = new TemaDAO();
            Tema tema = dao.obtenerPorId(id);
            request.setAttribute("tema", tema);
        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            request.setAttribute("msje", "Error al cargar tema: " + e.getMessage());
        }
        listarTemas(request, response);
    }

    private void insertarTema(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Tema tema = new Tema();
            tema.setNombreTema(request.getParameter("nombreTema"));
            tema.setContenido(request.getParameter("contenido"));
            Curso curso = new Curso();
            curso.setIdCurso(Integer.parseInt(request.getParameter("idCurso")));
            tema.setCurso(curso);
            TemaDAO dao = new TemaDAO();
            boolean exito = dao.registrar(tema);
            request.setAttribute("msje", exito ? "Tema insertado correctamente" : "Error al insertar tema");
        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            request.setAttribute("msje", "Error al insertar tema: " + e.getMessage());
        }
        listarTemas(request, response);
    }

    private void actualizarTema(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Tema tema = new Tema();
            tema.setIdTema(Integer.parseInt(request.getParameter("idTema")));
            tema.setNombreTema(request.getParameter("nombreTema"));
            tema.setContenido(request.getParameter("contenido"));
            Curso curso = new Curso();
            curso.setIdCurso(Integer.parseInt(request.getParameter("idCurso")));
            tema.setCurso(curso);
            TemaDAO dao = new TemaDAO();
            boolean exito = dao.actualizar(tema);
            request.setAttribute("msje", exito ? "Tema actualizado correctamente" : "Error al actualizar tema");
        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            request.setAttribute("msje", "Error al actualizar tema: " + e.getMessage());
        }
        listarTemas(request, response);
    }

    private void eliminarTema(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            TemaDAO dao = new TemaDAO();
            boolean exito = dao.eliminar(id);
            request.setAttribute("msje", exito ? "Tema eliminado correctamente" : "Error al eliminar tema");
        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            request.setAttribute("msje", "Error al eliminar tema: " + e.getMessage());
        }
        listarTemas(request, response);
    }

    private void verContenido(HttpServletRequest request, HttpServletResponse response, Usuario usuario)
            throws ServletException, IOException {
        int idTema = Integer.parseInt(request.getParameter("idTema"));
        int idCurso = Integer.parseInt(request.getParameter("idCurso"));
        TemaDAO dao = new TemaDAO();
        ProgresoTemaDAO progresoDAO = new ProgresoTemaDAO();
        try {
            Tema tema = dao.obtenerPorId(idTema);
            request.setAttribute("tema", tema);
            request.setAttribute("idCurso", idCurso);

            // Registrar estado INICIADO
            ProgresoTema progreso = new ProgresoTema();
            progreso.setTema(tema);
            progreso.setUsuario(usuario);
            progreso.setEstado("INICIADO");
            progresoDAO.registrarOActualizarProgreso(progreso);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("msje", "Error al cargar contenido del tema: " + e.getMessage());
        }
        request.getRequestDispatcher("/vista/postulante/contenidoTema.jsp").forward(request, response);
    }

    private void verExamen(HttpServletRequest request, HttpServletResponse response, Usuario usuario)
            throws ServletException, IOException, SQLException {
        String idCursoParam = request.getParameter("idCurso");
        if (idCursoParam == null || idCursoParam.trim().isEmpty()) {
            request.setAttribute("msje", "ID de curso no proporcionado.");
            listarTemas(request, response);
            return;
        }

        int idCurso = Integer.parseInt(idCursoParam);
        ProgresoTemaDAO progresoTemaDAO = new ProgresoTemaDAO();
        boolean examenHabilitado = progresoTemaDAO.todosTemasCompletados(idCurso, usuario.getIdUsuario());
        if (!examenHabilitado) {
            request.setAttribute("msje", "Debes completar todos los temas antes de acceder al examen.");
            listarTemas(request, response);
            return;
        }

        PreguntaDAO preguntaDAO = new PreguntaDAO();
        RespuestaDAO respuestaDAO = new RespuestaDAO();
        try {
            List<Pregunta> preguntas = preguntaDAO.listarPorCurso(idCurso);
            if (preguntas != null) {
                for (Pregunta pregunta : preguntas) {
                    List<Respuesta> respuestas = respuestaDAO.listarPorPregunta(pregunta.getIdPregunta());
                    request.setAttribute("respuestas_" + pregunta.getIdPregunta(), respuestas);
                }
            }
            request.setAttribute("preguntas", preguntas);
            request.setAttribute("idCurso", idCurso);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("msje", "Error al cargar examen: " + e.getMessage());
        }
        request.getRequestDispatcher("/vista/postulante/examen.jsp").forward(request, response);
    }

    private void finalizarTema(HttpServletRequest request, HttpServletResponse response, Usuario usuario)
            throws ServletException, IOException {
        int idTema = Integer.parseInt(request.getParameter("idTema"));
        int idCurso = Integer.parseInt(request.getParameter("idCurso"));
        TemaDAO temaDAO = new TemaDAO();
        ProgresoTemaDAO progresoDAO = new ProgresoTemaDAO();
        try {
            Tema tema = temaDAO.obtenerPorId(idTema);
            ProgresoTema progreso = new ProgresoTema();
            progreso.setTema(tema);
            progreso.setUsuario(usuario);
            progreso.setEstado("FINALIZADO");
            boolean exito = progresoDAO.registrarOActualizarProgreso(progreso);
            request.setAttribute("msje", exito ? "Tema marcado como finalizado." : "Error al marcar tema como finalizado.");
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("msje", "Error al marcar tema como finalizado: " + e.getMessage());
        }
        listarTemas(request, response);
    }
}