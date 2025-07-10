package controlador;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Curso;
import modelo.IntentoExamen;
import modelo.Pregunta;
import modelo.Respuesta;
import modelo.Usuario;
import modelo.ProgresoCurso;
import modelo.dao.CursoDAO;
import modelo.dao.IntentoExamenDAO;
import modelo.dao.PreguntaDAO;
import modelo.dao.RespuestaDAO;
import modelo.dao.UsuarioDAO;
import modelo.dao.ProgresoCursoDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@WebServlet(name = "ExamenServlet", urlPatterns = {"/ExamenServlet"})
public class ExamenServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        if ("finalizar".equals(accion)) {
            procesarExamen(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/vista/error.jsp");
        }
    }

private void procesarExamen(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    try {
        // Obtener parámetros
        int idCurso = Integer.parseInt(request.getParameter("idCurso"));
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/vista/login.jsp");
            return;
        }
        String username = (String) session.getAttribute("username");

        // Obtener usuario
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario usuario = usuarioDAO.obtenerPorUser(username);
        if (usuario == null) {
            request.setAttribute("msje", "Usuario no encontrado.");
            request.setAttribute("mensajeTipo", "danger");
            request.getRequestDispatcher("/vista/postulante/examen.jsp").forward(request, response);
            return;
        }

        // Obtener curso
        CursoDAO cursoDAO = new CursoDAO();
        Curso curso = cursoDAO.obtenerPorId(idCurso);
        if (curso == null) {
            request.setAttribute("msje", "Curso no encontrado.");
            request.setAttribute("mensajeTipo", "danger");
            request.getRequestDispatcher("/vista/postulante/examen.jsp").forward(request, response);
            return;
        }

        // Verificar número de intentos
        ProgresoCursoDAO progresoDAO = new ProgresoCursoDAO();
        ProgresoCurso progreso = progresoDAO.obtenerProgreso(idCurso, usuario.getIdUsuario());
        int numIntento = (progreso != null) ? progreso.getNumIntento() + 1 : 1;
        if (numIntento > 3) {
            request.setAttribute("msje", "Has alcanzado el límite de 3 intentos para este curso.");
            request.setAttribute("mensajeTipo", "danger");
            request.getRequestDispatcher("/vista/postulante/examen.jsp").forward(request, response);
            return;
        }

        // Obtener preguntas
        PreguntaDAO preguntaDAO = new PreguntaDAO();
        List<Pregunta> preguntas = preguntaDAO.listarPorCurso(idCurso);
        if (preguntas == null || preguntas.isEmpty()) {
            request.setAttribute("msje", "No hay preguntas disponibles para este curso.");
            request.setAttribute("mensajeTipo", "danger");
            request.getRequestDispatcher("/vista/postulante/examen.jsp").forward(request, response);
            return;
        }

        // Calcular puntuación
        int aciertos = 0;
        RespuestaDAO respuestaDAO = new RespuestaDAO();
        for (Pregunta p : preguntas) {
            String respuestaIdStr = request.getParameter("respuesta_" + p.getIdPregunta());
            if (respuestaIdStr != null) {
                int respuestaId = Integer.parseInt(respuestaIdStr);
                Respuesta respuesta = respuestaDAO.obtenerPorId(respuestaId);
                if (respuesta != null && respuesta.isCorrecta()) {
                    aciertos++;
                }
            }
        }
        double nota = preguntas.isEmpty() ? 0.0 : ((double) aciertos / preguntas.size()) * 20; // (aciertos / totalPreguntas) * 20

        // Guardar intento en intento_examen
        IntentoExamen intento = new IntentoExamen();
        intento.setCurso(curso);
        intento.setUsuario(usuario);
        intento.setNota(nota);
        intento.setFechaHora(new Timestamp(System.currentTimeMillis()));

        IntentoExamenDAO intentoDAO = new IntentoExamenDAO();
        boolean exitoIntento = intentoDAO.registrarIntento(intento);

        // Actualizar progreso_curso
        double notaMaxima = (progreso != null && progreso.getNota() > nota) ? progreso.getNota() : nota;
        String estado = notaMaxima >= 12 ? "APROBADO" : "DESAPROBADO";
        ProgresoCurso nuevoProgreso = new ProgresoCurso();
        nuevoProgreso.setCurso(curso);
        nuevoProgreso.setUsuario(usuario);
        nuevoProgreso.setNumIntento(numIntento);
        nuevoProgreso.setNota(notaMaxima);
        nuevoProgreso.setEstado(estado);

        boolean exitoProgreso = progresoDAO.registrarOActualizarProgreso(nuevoProgreso);

        // Check if all courses are approved and update user estado
        if (exitoProgreso && "APROBADO".equals(estado)) {
            String elegibilidad = progresoDAO.verificarElegibilidadCertificado(usuario.getIdUsuario());
            if ("ELIGIBLE".equals(elegibilidad)) {
                request.setAttribute("msje", "¡Felicidades! Has aprobado todos los cursos y eres elegible para el certificado.");
                request.setAttribute("mensajeTipo", "success");
            }
        }

if (exitoIntento && exitoProgreso) {
    // Guardar mensaje en sesión para mostrarlo después en cursos.jsp (opcional)
    session.setAttribute("msje", "Examen finalizado con éxito. Puntuación: " + String.format("%.2f", nota) + ". Estado: " + estado);
    session.setAttribute("mensajeTipo", "success");

    // Redirigir a la página de cursos
    response.sendRedirect(request.getContextPath() + "/CursoServlet?accion=listar");
    return;
} else {
            request.setAttribute("msje", "Error al registrar el intento o progreso.");
            request.setAttribute("mensajeTipo", "danger");
        }

        // Reenviar atributos para recargar la página
        request.setAttribute("idCurso", idCurso);
        request.setAttribute("curso", curso);
        request.setAttribute("preguntas", preguntas);
        for (Pregunta p : preguntas) {
            List<Respuesta> respuestas = respuestaDAO.listarPorPregunta(p.getIdPregunta());
            request.setAttribute("respuestas_" + p.getIdPregunta(), respuestas);
        }

        request.getRequestDispatcher("/vista/postulante/examen.jsp").forward(request, response);

    } catch (SQLException e) {
        e.printStackTrace();
        request.setAttribute("msje", "Error al procesar el examen: " + e.getMessage());
        request.setAttribute("mensajeTipo", "danger");
        request.getRequestDispatcher("/vista/postulante/examen.jsp").forward(request, response);
    } catch (NumberFormatException e) {
        e.printStackTrace();
        request.setAttribute("msje", "Datos inválidos.");
        request.setAttribute("mensajeTipo", "danger");
        request.getRequestDispatcher("/vista/postulante/examen.jsp").forward(request, response);
    }
}
}