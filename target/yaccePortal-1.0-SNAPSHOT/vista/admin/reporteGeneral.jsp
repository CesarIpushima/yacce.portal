<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, modelo.Usuario" %>
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
    <title>Reporte General</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/estilos/estilo.css">
    <style>
        body { padding-top: 56px; }
        .progress-bar-custom {
            height: 30px;
            font-size: 14px;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .legend {
            margin-top: 10px;
            display: flex;
            gap: 10px;
        }
        .legend span {
            display: flex;
            align-items: center;
            gap: 5px;
        }
        .legend span::before {
            content: '';
            display: inline-block;
            width: 15px;
            height: 15px;
            border-radius: 50%;
        }
        .legend .aprobado::before { background-color: #007BFF; }
        .legend .iniciado::before { background-color: #6C757D; }
        .legend .por-iniciar::before { background-color: #28A745; }
        .legend .bloqueado::before { background-color: #DC3545; }
    </style>
</head>
<body>
<%@ include file="/vista/faces/topbar.jspf" %>
<%@ include file="/vista/faces/sidebar.jspf" %>
<main class="col-md-10 ms-sm-auto px-md-4 mt-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Reporte General de Usuarios</h2>
        <button class="btn btn-success" onclick="descargarCSV()">Descargar CSV</button>
    </div>
    <%
        String mensaje = (String) request.getAttribute("msje");
        if (mensaje != null) {
    %>
    <div class="alert alert-info"><%= mensaje %></div>
    <%
        }
    %>
    <table class="table table-bordered" id="tablaUsuarios">
        <thead>
        <tr>
            <th>Nombre de Usuario</th>
            <th>Estado</th>
            <th>Fecha de Inicio</th>
            <th>Acción</th>
        </tr>
        </thead>
        <tbody>
        <%
        List<Usuario> usuarios = (List<Usuario>) request.getAttribute("usuarios");
        if (usuarios != null && !usuarios.isEmpty()) {
            for (Usuario u : usuarios) {
                String estadoTexto;
                switch (u.getEstado()) {
                    case 0: estadoTexto = "Bloqueado"; break;
                    case 1: estadoTexto = "Sin iniciar"; break;
                    case 2: estadoTexto = "Iniciado"; break;
                    case 3: estadoTexto = "Aprobado"; break;
                    default: estadoTexto = "Desconocido";
                }
        %>
        <tr>
            <td><%= u.getUser() != null ? u.getUser() : "Sin nombre" %></td>
            <td><%= estadoTexto %></td>
            <td><%= u.getFechaInicio() != null ? u.getFechaInicio() : "N/A" %></td>
            <td>
                <a href="${pageContext.request.contextPath}/UsuarioServlet?accion=verReporteDetalle&idUsuario=<%= u.getIdUsuario() %>" class="btn btn-primary btn-sm">Ver Detalle</a>
            </td>
        </tr>
        <%
                }
            } else {
        %>
        <tr>
            <td colspan="4" class="text-center">No hay usuarios registrados</td>
        </tr>
        <%
            }
        %>
        </tbody>
    </table>
  
    <!-- Barra de resumen de estatus -->
    <h3 class="mt-4">Progresión de Capacitación</h3>
    <div class="progress" style="height: 30px;">
        <%
            int totalUsuarios = (usuarios != null) ? usuarios.size() : 0;
            int aprobados = 0, iniciados = 0, porIniciar = 0, bloqueados = 0;
            if (usuarios != null) {
                for (Usuario u : usuarios) {
                    switch (u.getEstado()) {
                        case 3: aprobados++; break;
                        case 2: iniciados++; break;
                        case 1: porIniciar++; break;
                        case 0: bloqueados++; break;
                    }
                }
            }
            int total = aprobados + iniciados + porIniciar + bloqueados;
            double porcentajeAprobados = (total > 0) ? (aprobados * 100.0 / total) : 0;
            double porcentajeIniciados = (total > 0) ? (iniciados * 100.0 / total) : 0;
            double porcentajePorIniciar = (total > 0) ? (porIniciar * 100.0 / total) : 0;
            double porcentajeBloqueados = (total > 0) ? (bloqueados * 100.0 / total) : 0;
        %>
        <div class="progress-bar bg-primary progress-bar-custom" role="progressbar" style="width: <%= porcentajeAprobados %>%;" aria-valuenow="<%= aprobados %>" aria-valuemin="0" aria-valuemax="<%= total %>">Aprobado (<%= aprobados %>)</div>
        <div class="progress-bar bg-secondary progress-bar-custom" role="progressbar" style="width: <%= porcentajeIniciados %>%;" aria-valuenow="<%= iniciados %>" aria-valuemin="0" aria-valuemax="<%= total %>">Iniciado (<%= iniciados %>)</div>
        <div class="progress-bar bg-success progress-bar-custom" role="progressbar" style="width: <%= porcentajePorIniciar %>%;" aria-valuenow="<%= porIniciar %>" aria-valuemin="0" aria-valuemax="<%= total %>">Sin Iniciar (<%= porIniciar %>)</div>
        <div class="progress-bar bg-danger progress-bar-custom" role="progressbar" style="width: <%= porcentajeBloqueados %>%;" aria-valuenow="<%= bloqueados %>" aria-valuemin="0" aria-valuemax="<%= total %>">Bloqueado (<%= bloqueados %>)</div>
    </div>
    <div class="legend">
        <span class="aprobado">Aprobado</span>
        <span class="iniciado">Iniciado</span>
        <span class="por-iniciar">Por Iniciar</span>
        <span class="bloqueado">Bloqueado</span>
    </div>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
function descargarCSV() {
    const tabla = document.getElementById("tablaUsuarios");
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
        for (let i = 0; i < 3; i++) { // Solo las 3 primeras columnas
            fila.push('"' + celdas[i].innerText.replace(/"/g, '""') + '"');
        }
        csv += fila.join(",") + "\n";
    });

    // Crear blob y descargar
    const blob = new Blob([csv], { type: "text/csv;charset=utf-8;" });
    const link = document.createElement("a");
    link.href = URL.createObjectURL(blob);
    link.download = "reporte_usuarios.csv";
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
}
</script>

</body>
</html>