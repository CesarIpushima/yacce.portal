<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, modelo.Respuesta, modelo.Pregunta, modelo.Curso" %>
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
    <title>Gestión de Respuestas</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/estilos/estilo.css">
</head>
<body>
    <%@ include file="/vista/faces/topbar.jspf" %>
<div class="container mt-5">
    <%
        Pregunta pregunta = (Pregunta) request.getAttribute("pregunta");
        String textoPregunta = pregunta != null ? pregunta.getPregunta() : "Desconocida";
        int idCurso = pregunta != null && pregunta.getCurso() != null ? pregunta.getCurso().getIdCurso() : 0;
    %>
    <h2 class="mb-4">Gestión de Respuestas - Pregunta: <%= textoPregunta %></h2>
    <a href="${pageContext.request.contextPath}/PreguntaServlet?accion=listar&idCurso=<%= idCurso %>" class="btn btn-secondary mb-4">Volver a Preguntas</a>
    <%
        String mensaje = (String) request.getAttribute("msje");
        if (mensaje != null) {
    %>
    <div class="alert alert-info"><%= mensaje %></div>
    <%
        }
    %>

    <!-- Formulario para agregar/editar respuesta -->
    <%
        Respuesta respuesta = (Respuesta) request.getAttribute("respuesta");
        int idPregunta = pregunta != null ? pregunta.getIdPregunta() : 0;
    %>
    <form action="${pageContext.request.contextPath}/RespuestaServlet" method="post" class="mb-4">
        <input type="hidden" name="idRespuesta" value="<%= respuesta != null ? respuesta.getIdRespuesta() : "" %>">
        <input type="hidden" name="accion" value="<%= respuesta != null ? "actualizar" : "insertar" %>">
        <input type="hidden" name="idPregunta" value="<%= idPregunta %>">
        <input type="hidden" name="idCurso" value="<%= idCurso %>">
        <div class="mb-3">
            <label for="respuesta" class="form-label">Respuesta</label>
            <textarea class="form-control" id="respuesta" name="respuesta" rows="3" required><%= respuesta != null ? respuesta.getRespuesta() : "" %></textarea>
        </div>
        <div class="mb-3">
            <div class="form-check">
                <input type="checkbox" class="form-check-input" id="correcta" name="correcta" value="true" <%= respuesta != null && respuesta.isCorrecta() ? "checked" : "" %>>
                <label class="form-check-label" for="correcta">¿Es correcta?</label>
            </div>
        </div>
        <button type="submit" class="btn btn-primary w-100"><%= respuesta != null ? "Actualizar" : "Agregar" %> Respuesta</button>
        <% if (respuesta != null) { %>
        <a href="${pageContext.request.contextPath}/RespuestaServlet?accion=listar&idPregunta=<%= idPregunta %>&idCurso=<%= idCurso %>" class="btn btn-secondary w-100 mt-2">Cancelar</a>
        <% } %>
    </form>

    <!-- Tabla de respuestas -->
    <table class="table table-bordered">
        <thead>
        <tr>
            <th>ID</th>
            <th>Respuesta</th>
            <th>Correcta</th>
            <th>Acciones</th>
        </tr>
        </thead>
        <tbody>
        <%
            List<Respuesta> respuestas = (List<Respuesta>) request.getAttribute("respuestas");
            if (respuestas != null && !respuestas.isEmpty()) {
                for (Respuesta r : respuestas) {
        %>
        <tr>
            <td><%= r.getIdRespuesta() %></td>
            <td><%= r.getRespuesta() %></td>
            <td><%= r.isCorrecta() ? "Sí" : "No" %></td>
            <td>
                <a href="${pageContext.request.contextPath}/RespuestaServlet?accion=editar&id=<%= r.getIdRespuesta() %>&idPregunta=<%= idPregunta %>&idCurso=<%= idCurso %>" class="btn btn-sm btn-warning">Editar</a>
                <a href="${pageContext.request.contextPath}/RespuestaServlet?accion=eliminar&id=<%= r.getIdRespuesta() %>&idPregunta=<%= idPregunta %>&idCurso=<%= idCurso %>" class="btn btn-sm btn-danger" onclick="return confirm('¿Estás seguro de eliminar esta respuesta?');">Eliminar</a>
            </td>
        </tr>
        <%
                }
            } else {
        %>
        <tr>
            <td colspan="4" class="text-center">No hay respuestas registradas para esta pregunta</td>
        </tr>
        <%
            }
        %>
        </tbody>
    </table>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>