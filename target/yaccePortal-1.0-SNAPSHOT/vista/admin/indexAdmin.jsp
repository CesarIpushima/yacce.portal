<%@ page contentType="text/html" pageEncoding="UTF-8" %>
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
<%@ include file="/vista/faces/topbar.jspf" %>
<body>
    <div class="container mt-5">
        <h2 class="mb-4">YaccePortal - Gestión</h2>
        <div class="d-flex flex-column gap-2">
            <a href="${pageContext.request.contextPath}/CursoServlet" class="btn btn-primary">Gestionar Cursos</a>
            <a href="${pageContext.request.contextPath}/UsuarioServlet" class="btn btn-primary">Gestionar Usuarios</a>
            <a href="${pageContext.request.contextPath}/UsuarioServlet?accion=reporteGeneral" class="btn btn-primary">Reporte General</a>
        </div>
    </div>
</body>
</html>
