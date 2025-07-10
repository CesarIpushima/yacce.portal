<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, modelo.Tema, modelo.Curso, java.util.Map" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

    int idCurso = request.getParameter("idCurso") != null ? Integer.parseInt(request.getParameter("idCurso")) : 0;
    request.setAttribute("idCurso", idCurso);

    Curso curso = (Curso) request.getAttribute("curso");
    String nombreCurso = (curso != null && curso.getNombreCurso() != null) ? curso.getNombreCurso() : "Curso ID " + idCurso;

    Boolean examenHabilitado = (Boolean) request.getAttribute("examenHabilitado");
    boolean puedeDarExamen = examenHabilitado != null && examenHabilitado;

    @SuppressWarnings("unchecked")
    Map<Integer, String> temaEstados = (Map<Integer, String>) request.getAttribute("temaEstados");
    List<Tema> temas = (List<Tema>) request.getAttribute("temas");
    int totalTemas = (temas != null) ? temas.size() : 0;
    int temasCompletados = 0;
    if (temas != null && temaEstados != null) {
        for (Tema t : temas) {
            String estado = temaEstados.get(t.getIdTema());
            if ("FINALIZADO".equals(estado)) {
                temasCompletados++;
            }
        }
    }
    int porcentaje = totalTemas > 0 ? (temasCompletados * 100) / totalTemas : 0;
%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Temas del Curso - <%= nombreCurso %></title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/estilos/estilo.css">
    <style>
        body { padding-top: 56px; }
        .toast-container { position: fixed; top: 20px; right: 20px; z-index: 1055; }
        .tema-item {
            display: flex;
            align-items: center;
            margin-bottom: 10px;
            min-height: 80px; /* Increased height of theme boxes */
        }
        .status-circle {
            width: 25px; /* Larger status circles */
            height: 25px; /* Larger status circles */
            border-radius: 50%;
            margin-right: 15px;
        }
        .progress-ring {
            width: 150px; /* Larger ring */
            height: 150px; /* Larger ring */
            border-radius: 50%;
            background: conic-gradient(#007bff <%= porcentaje %>%, #e9ecef 0);
            position: relative;
            display: inline-block;
            margin-left: 20px;
        }
        .progress-ring::before {
            content: '';
            position: absolute;
            top: 10px;
            left: 10px;
            right: 10px;
            bottom: 10px;
            background: white;
            border-radius: 50%;
            z-index: 1;
        }
        .progress-ring::after {
            content: '<%= porcentaje %>%';
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            font-weight: bold;
            z-index: 2;
            font-size: 1.5em; /* Larger text for readability */
        }
    </style>
</head>
<body>
<%@ include file="/vista/faces/topbar.jspf" %>
<%@ include file="/vista/faces/sidebar2.jspf" %>
<main class="col-md-10 ms-sm-auto px-md-4 mt-5">
    <h2 class="mb-4">Temas del Curso: <%= nombreCurso %></h2>

    <% if (request.getAttribute("msje") != null) { %>
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <%= request.getAttribute("msje") %>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    <% } %>

    <div class="d-flex justify-content-between align-items-center">
        <ul class="list-group w-75">
            <%
                if (temas != null && !temas.isEmpty()) {
                    for (Tema t : temas) {
                        String estado = temaEstados != null ? temaEstados.get(t.getIdTema()) : null;
                        String circleColor = "gray";
                        if ("INICIADO".equals(estado)) circleColor = "yellow";
                        else if ("FINALIZADO".equals(estado)) circleColor = "green";
            %>
            <li class="list-group-item tema-item">
                <div class="status-circle" style="background-color: <%= circleColor %>;"></div>
                <a href="${pageContext.request.contextPath}/TemaServlet?accion=verContenido&idTema=<%= t.getIdTema() %>&idCurso=<%= idCurso %>" class="text-decoration-none">
                    <%= t.getNombreTema() %>
                </a>
            </li>
            <%
                    }
                } else {
            %>
            <li class="list-group-item">No hay temas disponibles para este curso</li>
            <%
                }
            %>
        </ul>
        <div class="progress-ring"></div>
    </div>
    <div class="mt-3">
        <a href="${pageContext.request.contextPath}/CursoServlet?accion=listar" class="btn btn-secondary">Volver a Cursos</a>
        <a href="${pageContext.request.contextPath}/TemaServlet?accion=verExamen&idCurso=<%= idCurso %>" 
           class="btn btn-primary me-2 <%= puedeDarExamen ? "" : "disabled" %>"
           id="irExamenBtn"
           <%= puedeDarExamen ? "" : "title='Debes completar todos los temas para acceder al examen'" %>>
           Ir al Examen
        </a>
    </div>

    <div class="toast-container">
        <div id="examenToast" class="toast align-items-center text-bg-warning border-0" role="alert" aria-live="assertive" aria-atomic="true">
            <div class="d-flex">
                <div class="toast-body">
                    Finalice todos los temas para habilitar el examen.
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
        </div>
    </div>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    document.getElementById('irExamenBtn').addEventListener('click', function(e) {
        if (this.classList.contains('disabled')) {
            var toastEl = document.getElementById('examenToast');
            var toast = new bootstrap.Toast(toastEl);
            toast.show();
            e.preventDefault();
        }
    });
</script>
</body>
</html>