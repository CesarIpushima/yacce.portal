<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="modelo.Usuario, modelo.ProgresoCurso" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%
    // Evitar que la página se almacene en caché
    response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

    // Obtener el usuario desde el request
    Usuario usuario = (Usuario) request.getAttribute("usuario");
    String nombreUsuario = (usuario != null && usuario.getUser() != null) ? usuario.getUser() : "Usuario Desconocido";
%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Detalle del Usuario</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/estilos/estilo.css">
    <style>
        body { padding-top: 56px; }
    </style>
</head>
<body>
<%@ include file="/vista/faces/topbar.jspf" %>
<%@ include file="/vista/faces/sidebar.jspf" %>
<main class="col-md-10 ms-sm-auto px-md-4 mt-5">
    <h1 class="mb-4 text-center">Progreso de <%= nombreUsuario %></h1>
    <%
        String mensaje = (String) request.getAttribute("msje");
        if (mensaje != null) {
    %>
    <div class="alert alert-info"><%= mensaje %></div>
    <%
        }
    %>
    <div class="d-flex justify-content-end mb-3">
    <button class="btn btn-success" onclick="descargarCSV()">Descargar CSV</button>
    </div>
    <table class="table table-bordered" id="tablaProgreso">
        <thead>
        <tr>
            <th>Curso</th>
            <th>Número de Intentos</th>
            <th>Nota</th>
            <th>Estado</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="progreso" items="${progresos}">
            <tr>
                <td>${progreso.curso.nombreCurso}</td>
                <td>
                    <c:choose>
                        <c:when test="${progreso.numIntento == 0}">-</c:when>
                        <c:otherwise>${progreso.numIntento}</c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <c:choose>
                        <c:when test="${progreso.numIntento == 0}">-</c:when>
                        <c:otherwise>
                            <fmt:formatNumber value="${progreso.nota}" pattern="#,##0.00"/>
                        </c:otherwise>
                    </c:choose>
                </td>
                <td>${progreso.estado}</td>
            </tr>
        </c:forEach>
        <c:if test="${empty progresos}">
            <tr>
                <td colspan="4" class="text-center">No hay datos de progreso disponibles</td>
            </tr>
        </c:if>
        </tbody>
    </table>
    <a href="${pageContext.request.contextPath}/UsuarioServlet?accion=reporteGeneral" class="btn btn-secondary mt-3">Volver a Reporte General</a>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
function descargarCSV() {
    const tabla = document.getElementById("tablaProgreso");
    let csv = "";

    // Encabezados
    const encabezados = tabla.querySelectorAll("thead th");
    let filaEncabezados = [];
    encabezados.forEach(th => {
        filaEncabezados.push('"' + th.innerText.replace(/"/g, '""') + '"');
    });
    csv += filaEncabezados.join(",") + "\n";

    // Filas de datos
    const filas = tabla.querySelectorAll("tbody tr");
    filas.forEach(tr => {
        const celdas = tr.querySelectorAll("td");
        let fila = [];
        for (let i = 0; i < 4; i++) { // Las 4 columnas: Curso, Intentos, Nota, Estado
            fila.push('"' + celdas[i].innerText.replace(/"/g, '""') + '"');
        }
        csv += fila.join(",") + "\n";
    });

    // Crear blob y descargar
    const blob = new Blob([csv], { type: "text/csv;charset=utf-8;" });
    const link = document.createElement("a");
    link.href = URL.createObjectURL(blob);
    link.download = "progreso_usuario.csv";
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
}
</script>
</body>
</html>