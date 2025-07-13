<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, modelo.Curso" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    // Evitar que la página se almacene en caché
    response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);
%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Gestión de Cursos</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/estilos/estilo.css">
</head>
<body>

        <%@ include file="/vista/faces/topbar.jspf" %>
        <%@ include file="/vista/faces/sidebar2.jspf" %>
        <%
    String msje = (String) session.getAttribute("msje");
    String mensajeTipo = (String) session.getAttribute("mensajeTipo");
    if (msje != null) {
%>
<div class="alert alert-<%= mensajeTipo != null ? mensajeTipo : "info" %> alert-dismissible fade show" role="alert">
    <%= msje %>
    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
</div>
<%
        session.removeAttribute("msje");
        session.removeAttribute("mensajeTipo");
    }
%>
        <main class="col-md-10 ms-sm-auto px-md-4 mt-5">
            <h2 class="mb-4">Gestión de Cursos</h2>
            <%
                String mensaje = (String) request.getAttribute("msje");
                if (mensaje != null) {
            %>
            <div class="alert alert-info"><%= mensaje %></div>
            <%
                }
            %>

            <!-- Grid de cards -->
            <div class="row row-cols-1 row-cols-md-3 g-4">
                <!-- Cards de cursos existentes -->
                <%
                    List<Curso> cursos = (List<Curso>) request.getAttribute("cursos");
                    if (cursos != null && !cursos.isEmpty()) {
                        for (Curso c : cursos) {
                %>
                <div class="col">
                    <div class="card flip-card">
                        <div class="flip-card-inner">
                            <a href="${pageContext.request.contextPath}/TemaServlet?accion=listar&idCurso=<%= c.getIdCurso() %>" class="flip-card-front card-body d-flex flex-column text-decoration-none">
                                <h5 class="card-title"><%= c.getNombreCurso() %></h5>
                                <p class="card-text">Temas: <%= c.getNumTemas() %></p>
                            </a>
                        </div>
                    </div>
                </div>
                <%
                        }
                    } else {
                %>
                <div class="col-12">
                    <p class="text-center">No hay cursos registrados</p>
                </div>
                <%
                    }
                %>
            </div>
        </main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Variable para rastrear el card actualmente volteado
    let currentlyFlippedCard = null;

    // Manejar clics en .flip-card-front para el flip
    document.querySelectorAll('.flip-card-front').forEach(front => {
        front.addEventListener('click', function(e) {
            // Prevenir el flip si se hace clic en un enlace o en un elemento dentro de un enlace
            if (e.target.tagName === 'A' || e.target.closest('A')) {
                e.stopPropagation();
                return;
            }

            // Permitir el flip si se hace clic en el botón "Editar" o en el signo "+"
            if (e.target.classList.contains('edit-button') || e.target.classList.contains('plus-sign')) {
                const flipCard = this.closest('.flip-card');

                // Si hay un card ya volteado y no es el mismo que estamos intentando voltear
                if (currentlyFlippedCard && currentlyFlippedCard !== flipCard) {
                    currentlyFlippedCard.classList.remove('flipped'); // Revertir el card anterior
                }

                // Voltear el card actual
                flipCard.classList.toggle('flipped');

                // Actualizar el card actualmente volteado
                if (flipCard.classList.contains('flipped')) {
                    currentlyFlippedCard = flipCard;
                } else {
                    currentlyFlippedCard = null; // Si se desvoltea, limpiamos la referencia
                }

                console.log('Flip toggled for:', e.target, 'Currently flipped:', currentlyFlippedCard);
            }
        });
    });

    // Manejar el botón "Cancelar" para asegurarse de que actualice currentlyFlippedCard
    document.querySelectorAll('.flip-card-back .btn-secondary').forEach(cancelButton => {
        cancelButton.addEventListener('click', function() {
            const flipCard = this.closest('.flip-card');
            if (flipCard === currentlyFlippedCard) {
                currentlyFlippedCard = null; // Limpiar la referencia al desvoltear
            }
        });
    });
</script>
</body>
</html>