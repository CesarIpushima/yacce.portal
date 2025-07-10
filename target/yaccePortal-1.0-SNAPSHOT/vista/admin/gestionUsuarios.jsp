<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, modelo.Usuario, modelo.Rol, modelo.Curso" %>
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
    <title>Gestión de Usuarios</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/estilos/estilo.css">
</head>
<body>

        <%@ include file="/vista/faces/topbar.jspf" %>
        <%@ include file="/vista/faces/sidebar.jspf" %>
        <main class="col-md-10 ms-sm-auto px-md-4 mt-5">
            <h2 class="mb-4">Gestión de Usuarios</h2>
            <%
                String mensaje = (String) request.getAttribute("msje");
                if (mensaje != null) {
            %>
            <div class="alert alert-info"><%= mensaje %></div>
            <%
                }
            %>

            <!-- Formulario para agregar/editar usuario -->
            <%
                Usuario usuario = (Usuario) request.getAttribute("usuario");
            %>
            <form action="${pageContext.request.contextPath}/UsuarioServlet" method="post" class="mb-4" onsubmit="return validateForm()">
                <input type="hidden" name="idUsuario" value="<%= usuario != null ? usuario.getIdUsuario() : "" %>">
                <input type="hidden" name="accion" value="<%= usuario != null ? "actualizar" : "insertar" %>">
                <input type="hidden" id="rolValidation" name="rolValidation" required>
                <div class="mb-3">
                    <label for="user" class="form-label">Nombre de Usuario</label>
                    <input type="text" class="form-control" id="user" name="user" value="<%= usuario != null ? usuario.getUser() : "" %>" required>
                </div>
                <div class="mb-3">
                    <label for="pass" class="form-label">Contraseña</label>
                    <input type="text" class="form-control" id="pass" name="pass" value="<%= usuario != null ? usuario.getPass() : "" %>" required>
                </div>
                <div class="mb-3">
                    <label for="email" class="form-label">Correo Electrónico</label>
                    <input type="email" class="form-control" id="email" name="email" value="<%= usuario != null ? usuario.getEmail() : "" %>" required>
                </div>
                <div class="mb-3">
                    <label class="form-label">Rol</label>
                    <div class="form-check">
                        <input type="checkbox" class="form-check-input" id="rolAdmin" name="idRol" value="1" 
                               <%= usuario != null && usuario.getRol() != null && usuario.getRol().getIdRol() == 1 ? "checked" : "" %> 
                               onchange="updateRol('rolAdmin', 'rolPostulante')">
                        <label class="form-check-label" for="rolAdmin">Administrador</label>
                    </div>
                    <div class="form-check">
                        <input type="checkbox" class="form-check-input" id="rolPostulante" name="idRol" value="2" 
                               <%= usuario != null && usuario.getRol() != null && usuario.getRol().getIdRol() == 2 ? "checked" : "" %> 
                               onchange="updateRol('rolPostulante', 'rolAdmin')">
                        <label class="form-check-label" for="rolPostulante">Postulante</label>
                    </div>
                    <div id="rolError" class="invalid-feedback" style="display: none;">
                        Por favor, selecciona un rol (Administrador o Postulante).
                    </div>
                </div>
                <button type="submit" class="btn btn-primary w-100"><%= usuario != null ? "Actualizar" : "Agregar" %> Usuario</button>
                <% if (usuario != null) { %>
                <a href="${pageContext.request.contextPath}/UsuarioServlet" class="btn btn-secondary w-100 mt-2">Cancelar</a>
                <% } %>
            </form>

            <!-- Tabla de usuarios -->
            <table class="table table-bordered">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Usuario</th>
                    <th>Email</th>
                    <th>Estado</th>
                    <th>Rol</th>
                    <th>Fecha Inicio</th>
                    <th>Acciones</th>
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
                    <td><%= u.getIdUsuario() %></td>
                    <td><%= u.getUser() %></td>
                    <td><%= u.getEmail() %></td>
                    <td><%= estadoTexto %></td>
                    <td><%= u.getRol() != null ? u.getRol().getNombreRol() : "Sin rol" %></td>
                    <td><%= u.getFechaInicio() != null ? u.getFechaInicio() : "N/A" %></td>
                    <td>
                        <a href="${pageContext.request.contextPath}/UsuarioServlet?accion=editar&id=<%= u.getIdUsuario() %>" class="btn btn-sm btn-warning">Editar</a>
                        <% if (u.getEstado() == 0) { %>
                        <a href="${pageContext.request.contextPath}/UsuarioServlet?accion=desbloquear&id=<%= u.getIdUsuario() %>" class="btn btn-sm btn-success" onclick="return confirm('¿Estás seguro de desbloquear este usuario?');">Desbloquear</a>
                        <% } else { %>
                        <a href="${pageContext.request.contextPath}/UsuarioServlet?accion=eliminar&id=<%= u.getIdUsuario() %>" class="btn btn-sm btn-danger" onclick="return confirm('¿Estás seguro de bloquear este usuario?');">Bloquear</a>
                        <% } %>
                    </td>
                </tr>
                <%
                        }
                    } else {
                %>
                <tr>
                    <td colspan="7" class="text-center">No hay usuarios registrados</td>
                </tr>
                <%
                    }
                %>
                </tbody>
            </table>
        </main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function updateRol(selectedId, otherId) {
        const selected = document.getElementById(selectedId);
        const other = document.getElementById(otherId);
        const rolValidation = document.getElementById('rolValidation');
        const rolError = document.getElementById('rolError');

        if (selected.checked) {
            other.checked = false;
            rolValidation.value = selected.value; // Establece el valor del campo oculto
            rolError.style.display = 'none'; // Oculta el mensaje de error
        } else {
            rolValidation.value = ''; // Limpia el campo oculto si no hay selección
        }
    }

    function validateForm() {
        const rolAdmin = document.getElementById('rolAdmin');
        const rolPostulante = document.getElementById('rolPostulante');
        const rolError = document.getElementById('rolError');

        if (!rolAdmin.checked && !rolPostulante.checked) {
            rolError.style.display = 'block'; // Muestra el mensaje de error
            return false; // Impide el envío del formulario
        }
        return true; // Permite el envío del formulario
    }
</script>
</body>
</html>