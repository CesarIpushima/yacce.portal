<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="modelo.Tema" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    // Evitar que la página se almacene en caché
    response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

    int idCurso = (int) request.getAttribute("idCurso");
    Tema tema = (Tema) request.getAttribute("tema");
    int idTema = (tema != null) ? tema.getIdTema() : 0;
%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Contenido del Tema</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/estilos/estilo.css">
    <style>
        body { padding-top: 56px; }
        .content { white-space: pre-wrap; }
    </style>
</head>
<body>
<%@ include file="/vista/faces/topbar.jspf" %>
<%@ include file="/vista/faces/sidebar2.jspf" %>
<main class="col-md-10 ms-sm-auto px-md-4 mt-5">
    <%
        if (tema != null) {
    %>
    <h2 class="mb-4"><%= tema.getNombreTema() %></h2>
    <div class="content p-3 bg-light rounded">
        <iframe src="<%= tema.getContenido() != null ? tema.getContenido() : "No hay contenido disponible" %>" 
                width="100%" height="1000px"></iframe>
    </div>
    
    <a href="${pageContext.request.contextPath}/TemaServlet?accion=finalizarTema&idTema=<%= idTema %>&idCurso=<%= idCurso %>" class="btn btn-success mt-3">Finalizado</a>
    <a href="${pageContext.request.contextPath}/TemaServlet?accion=listar&idCurso=<%= idCurso %>" class="btn btn-secondary mt-3">Volver a Temas</a>
    <%
        } else {
    %>
    <h2 class="mb-4">Tema no encontrado</h2>
    <p class="text-danger">No se pudo cargar el contenido del tema.</p>
    <a href="${pageContext.request.contextPath}/TemaServlet?accion=listar&idCurso=<%= idCurso %>" class="btn btn-secondary mt-3">Volver a Temas</a>
    <%
        }
    %>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>