package controlador;

import modelo.Usuario;
import modelo.dao.UsuarioDAO;
import modelo.dao.ProgresoCursoDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

@WebServlet(name = "CertificadoServlet", urlPatterns = {"/CertificadoServlet"})
public class CertificadoServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String accion = request.getParameter("accion");
        String username = (String) session.getAttribute("username");
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        ProgresoCursoDAO progresoCursoDAO = new ProgresoCursoDAO();

        try {
            Usuario usuario = usuarioDAO.obtenerPorUser(username);
            if (usuario == null) {
                request.setAttribute("msje", "Usuario no encontrado.");
                request.setAttribute("mensajeTipo", "danger");
                request.getRequestDispatcher("/vista/postulante/temas.jsp").forward(request, response);
                return;
            }

            String resultado = progresoCursoDAO.verificarElegibilidadCertificado(usuario.getIdUsuario());
            if (!"ELIGIBLE".equals(resultado)) {
                request.setAttribute("msje", resultado);
                request.setAttribute("mensajeTipo", "warning");
                request.getRequestDispatcher("/vista/postulante/temas.jsp").forward(request, response);
                return;
            }

            if ("descargar".equals(accion)) {
                // Generar y descargar el PDF en orientación horizontal
                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", "attachment; filename=certificado_" + usuario.getUser() + ".pdf");

                // Crear el documento PDF con PDFBox
                PDDocument document = new PDDocument();
                PDPage page = new PDPage(PDRectangle.A4); // Página A4 estándar
                page.setRotation(90); // Rotar 90 grados para orientación horizontal
                document.addPage(page);

                // Crear el flujo de contenido y aplicar la transformación para orientación horizontal
                PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true);
                contentStream.transform(new org.apache.pdfbox.util.Matrix(0, 1, -1, 0, PDRectangle.A4.getHeight(), 0));

                // Calcular el centro horizontal de la página (ancho horizontal = 595.28 puntos)
                float centerX = PDRectangle.A4.getWidth() / 2;

                // Línea decorativa superior
                contentStream.setLineWidth(1);
                contentStream.moveTo(50, 550);
                contentStream.lineTo(545, 550); // Ajustado para no sobrepasar los márgenes
                contentStream.stroke();

                // Título
                contentStream.setFont(PDType1Font.TIMES_BOLD, 28);
                contentStream.beginText();
                contentStream.newLineAtOffset(centerX - 150, 500); // Centrado
                contentStream.showText("Certificado de Finalización");
                contentStream.endText();

                // Subtítulo
                contentStream.setFont(PDType1Font.TIMES_ITALIC, 18);
                contentStream.beginText();
                contentStream.newLineAtOffset(centerX - 130, 470); // Centrado
                contentStream.showText("Programa de Formación Académica");
                contentStream.endText();

                // Texto introductorio
                contentStream.setFont(PDType1Font.TIMES_ROMAN, 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(centerX - 200, 400); // Centrado
                contentStream.showText("Por la presente se certifica que");
                contentStream.endText();

                // Nombre del usuario (destacado)
                contentStream.setFont(PDType1Font.TIMES_BOLD, 24);
                contentStream.beginText();
                contentStream.newLineAtOffset(centerX - 100, 360); // Centrado
                contentStream.showText(usuario.getUser());
                contentStream.endText();

                // Texto descriptivo
                contentStream.setFont(PDType1Font.TIMES_ROMAN, 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(centerX - 250, 320); // Centrado
                contentStream.showText("ha completado con éxito todos los cursos requeridos del programa,");
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("demostrando dedicación, esfuerzo y compromiso con la excelencia académica.");
                contentStream.endText();

                // Fecha
                String fecha = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy").format(new Date());
                contentStream.beginText();
                contentStream.newLineAtOffset(centerX - 80, 280); // Centrado
                contentStream.showText("Otorgado el " + fecha);
                contentStream.endText();

                // Línea decorativa inferior
                contentStream.moveTo(50, 250);
                contentStream.lineTo(545, 250); // Ajustado para no sobrepasar los márgenes
                contentStream.stroke();

                // Firmas
                contentStream.moveTo(centerX - 150, 200); // Centrado y ajustado
                contentStream.lineTo(centerX - 50, 200);
                contentStream.stroke();
                contentStream.beginText();
                contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
                contentStream.newLineAtOffset(centerX - 150, 185);
                contentStream.showText("Director General");
                contentStream.endText();

                contentStream.moveTo(centerX + 50, 200); // Centrado y ajustado
                contentStream.lineTo(centerX + 150, 200);
                contentStream.stroke();
                contentStream.beginText();
                contentStream.newLineAtOffset(centerX + 50, 185);
                contentStream.showText("Coordinador Académico");
                contentStream.endText();

                contentStream.close();
                document.save(response.getOutputStream());
                document.close();
            } else {
                // Mostrar el certificado en JSP
                request.setAttribute("usuario", usuario);
                request.getRequestDispatcher("/vista/postulante/certificado.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("msje", "Error al verificar elegibilidad: " + e.getMessage());
            request.setAttribute("mensajeTipo", "danger");
            request.getRequestDispatcher("/vista/postulante/temas.jsp").forward(request, response);
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
}