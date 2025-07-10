<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, modelo.Pregunta, modelo.Curso" %>
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
    <title>Gestión de Preguntas</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}//estilos/estilo.css">
</head>
<body>
    <%@ include file="/vista/faces/topbar.jspf" %>
<div class="container mt-5">
    <%
        Curso curso = (Curso) request.getAttribute("curso");
        String nombreCurso = curso != null ? curso.getNombreCurso() : "Desconocido";
    %>
    <h2 class="mb-4">Gestión de Preguntas - Curso: <%= nombreCurso %></h2>
    <a href="${pageContext.request.contextPath}/CursoServlet" class="btn btn-secondary mb-4">Volver a Cursos</a>
    <%
        String mensaje = (String) request.getAttribute("msje");
        if (mensaje != null) {
    %>
    <div class="alert alert-info"><%= mensaje %></div>
    <%
        }
    %>

    <!-- Formulario para agregar/editar pregunta -->
    <%
        Pregunta pregunta = (Pregunta) request.getAttribute("pregunta");
        int idCurso = curso != null ? curso.getIdCurso() : 0;
    %>
    <form action="${pageContext.request.contextPath}/PreguntaServlet" method="post" class="mb-4">
        <input type="hidden" name="idPregunta" value="<%= pregunta != null ? pregunta.getIdPregunta() : "" %>">
        <input type="hidden" name="accion" value="<%= pregunta != null ? "actualizar" : "insertar" %>">
        <input type="hidden" name="idCurso" value="<%= idCurso %>">
        <div class="mb-3">
            <label for="pregunta" class="form-label">Pregunta</label>
            <textarea class="form-control" id="pregunta" name="pregunta" rows="4" required><%= pregunta != null ? pregunta.getPregunta() : "" %></textarea>
        </div>
        <button type="submit" class="btn btn-primary w-100"><%= pregunta != null ? "Actualizar" : "Agregar" %> Pregunta</button>
        <% if (pregunta != null) { %>
        <a href="${pageContext.request.contextPath}/PreguntaServlet?accion=listar&idCurso=<%= idCurso %>" class="btn btn-secondary w-100 mt-2">Cancelar</a>
        <% } %>
    </form>

    <!-- Tabla de preguntas -->
    <table class="table table-bordered">
        <thead>
        <tr>
            <th>ID</th>
            <th>Pregunta</th>
            <th>Acciones</th>
        </tr>
        </thead>
        <tbody>
        <%
            List<Pregunta> preguntas = (List<Pregunta>) request.getAttribute("preguntas");
            if (preguntas != null && !preguntas.isEmpty()) {
                for (Pregunta p : preguntas) {
        %>
        <tr>
            <td><%= p.getIdPregunta() %></td>
            <td><%= p.getPregunta() %></td>
            <td>
                <a href="${pageContext.request.contextPath}/PreguntaServlet?accion=editar&id=<%= p.getIdPregunta() %>&idCurso=<%= idCurso %>" class="btn btn-sm btn-warning">Editar</a>
                <a href="${pageContext.request.contextPath}/PreguntaServlet?accion=eliminar&id=<%= p.getIdPregunta() %>&idCurso=<%= idCurso %>" class="btn btn-sm btn-danger" onclick="return confirm('¿Estás seguro de eliminar esta pregunta?');">Eliminar</a>
                <a href="${pageContext.request.contextPath}/RespuestaServlet?accion=listar&idPregunta=<%= p.getIdPregunta() %>&idCurso=<%= idCurso %>" class="btn btn-sm btn-info">Agregar Respuestas</a>
            </td>
        </tr>
        <%
                }
            } else {
        %>
        <tr>
            <td colspan="3" class="text-center">No hay preguntas registradas para este curso</td>
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