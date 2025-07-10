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
    <link rel="stylesheet" href="${pageContext.request.contextPath}//estilos/estilo.css">
</head>
<body>

        <%@ include file="/vista/faces/topbar.jspf" %>
        <%@ include file="/vista/faces/sidebar.jspf" %>
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
                            <div class="flip-card-front card-body d-flex flex-column">
                                <h5 class="card-title"><%= c.getNombreCurso() %></h5>
                                <p class="card-text">Preguntas: <%= c.getNumPreguntas() %></p>
                                <p class="card-text">Temas: <%= c.getNumTemas() %></p>
                                <div class="mt-auto">
                                    <button class="btn btn-sm btn-warning me-1 edit-button" data-id="<%= c.getIdCurso() %>">Editar</button>
                                    <a href="${pageContext.request.contextPath}/CursoServlet?accion=eliminar&id=<%= c.getIdCurso() %>" class="btn btn-sm btn-danger me-1" onclick="return confirm('¿Estás seguro de eliminar este curso?');">Eliminar</a>
                                    <a href="${pageContext.request.contextPath}/TemaServlet?accion=listar&idCurso=<%= c.getIdCurso() %>" class="btn btn-sm btn-info me-1">Temas</a>
                                    <a href="${pageContext.request.contextPath}/PreguntaServlet?accion=listar&idCurso=<%= c.getIdCurso() %>" class="btn btn-sm btn-info">Examen</a>
                                </div>
                            </div>
                            <div class="flip-card-back card-body d-flex flex-column">
                                <h5 class="card-title">Editar Curso</h5>
                                <form action="${pageContext.request.contextPath}/CursoServlet" method="post" class="d-flex flex-column h-100">
                                    <input type="hidden" name="idCurso" value="<%= c.getIdCurso() %>">
                                    <input type="hidden" name="accion" value="actualizar">
                                    <div class="mb-3">
                                        <label for="nombreCurso<%= c.getIdCurso() %>" class="form-label">Nombre del Curso</label>
                                        <input type="text" class="form-control" id="nombreCurso<%= c.getIdCurso() %>" name="nombreCurso" value="<%= c.getNombreCurso() %>" required>
                                    </div>
                                    <button type="submit" class="btn btn-primary mt-auto me-1">Actualizar</button>
                                    <button type="button" class="btn btn-secondary mt-2" onclick="this.closest('.flip-card').classList.toggle('flipped')">Cancelar</button>
                                </form>
                            </div>
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
                <!-- Card para agregar curso -->
                <div class="col">
                    <div class="card flip-card">
                        <div class="flip-card-inner">
                            <div class="flip-card-front d-flex align-items-center justify-content-center">
                                <span class="plus-sign">+</span>
                            </div>
                            <div class="flip-card-back card-body d-flex flex-column">
                                <h5 class="card-title">Agregar Curso</h5>
                                <form action="${pageContext.request.contextPath}/CursoServlet" method="post" class="d-flex flex-column h-100">
                                    <input type="hidden" name="accion" value="insertar">
                                    <div class="mb-3">
                                        <label for="nombreCursoAdd" class="form-label">Nombre del Curso</label>
                                        <input type="text" class="form-control" id="nombreCursoAdd" name="nombreCurso" required>
                                    </div>
                                    <div class="mt-auto">
                                        <button type="submit" class="btn btn-primary me-1">Agregar</button>
                                        <button type="button" class="btn btn-secondary" onclick="this.closest('.flip-card').classList.toggle('flipped')">Cancelar</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
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