<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, modelo.Tema, modelo.Curso" %>
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
    <title>Gestión de Temas</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/estilos/estilo.css">
</head>
<body>

        <%@ include file="/vista/faces/topbar.jspf" %>
        <%@ include file="/vista/faces/sidebar.jspf" %>
        <main class="col-md-10 ms-sm-auto px-md-4 mt-5">
            <%
                Curso curso = (Curso) request.getAttribute("curso");
                String nombreCurso = curso != null ? curso.getNombreCurso() : "Desconocido";
            %>
            <h2 class="mb-4">Gestión de Temas - Curso: <%= nombreCurso %></h2>
            <a href="${pageContext.request.contextPath}/CursoServlet" class="btn btn-secondary mb-4">Volver a Cursos</a>
            <%
                String mensaje = (String) request.getAttribute("msje");
                if (mensaje != null) {
            %>
            <div class="alert alert-info"><%= mensaje %></div>
            <%
                }
            %>

            <!-- Formulario para agregar/editar tema -->
            <%
                Tema tema = (Tema) request.getAttribute("tema");
                int idCurso = curso != null ? curso.getIdCurso() : 0;
            %>
            <form action="${pageContext.request.contextPath}/TemaServlet" method="post" class="mb-4">
                <input type="hidden" name="idTema" value="<%= tema != null ? tema.getIdTema() : "" %>">
                <input type="hidden" name="accion" value="<%= tema != null ? "actualizar" : "insertar" %>">
                <input type="hidden" name="idCurso" value="<%= idCurso %>">
                <div class="mb-3">
                    <label for="nombreTema" class="form-label">Nombre del Tema</label>
                    <input type="text" class="form-control" id="nombreTema" name="nombreTema" value="<%= tema != null ? tema.getNombreTema() : "" %>" required>
                    <label for="contenido" class="form-label">Contenido</label>
                    <input type="text" class="form-control" id="contenido" name="contenido" value="<%= tema != null ? tema.getContenido() : "" %>" required>
                </div>
                <button type="submit" class="btn btn-primary w-100"><%= tema != null ? "Actualizar" : "Agregar" %> Tema</button>
                <% if (tema != null) { %>
                <a href="${pageContext.request.contextPath}/TemaServlet?accion=listar&idCurso=<%= idCurso %>" class="btn btn-secondary w-100 mt-2">Cancelar</a>
                <% } %>
            </form>

            <!-- Tabla de temas -->
            <table class="table table-bordered">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Nombre del Tema</th>
                    <th>Contenido</th>
                    <th>Acciones</th>
                </tr>
                </thead>
                <tbody>
                <%
                    List<Tema> temas = (List<Tema>) request.getAttribute("temas");
                    if (temas != null && !temas.isEmpty()) {
                        for (Tema t : temas) {
                %>
                <tr>
                    <td><%= t.getIdTema() %></td>
                    <td><%= t.getNombreTema() %></td>
                    <td><%= t.getContenido() %></td>
                    <td>
                        <a href="${pageContext.request.contextPath}/TemaServlet?accion=editar&id=<%= t.getIdTema() %>&idCurso=<%= idCurso %>" class="btn btn-sm btn-warning">Editar</a>
                        <a href="${pageContext.request.contextPath}/TemaServlet?accion=eliminar&id=<%= t.getIdTema() %>&idCurso=<%= idCurso %>" class="btn btn-sm btn-danger" onclick="return confirm('¿Estás seguro de eliminar este tema?');">Eliminar</a>
                    </td>
                </tr>
                <%
                        }
                    } else {
                %>
                <tr>
                    <td colspan="3" class="text-center">No hay temas registrados para este curso</td>
                </tr>
                <%
                    }
                %>
                </tbody>
            </table>
        </main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>