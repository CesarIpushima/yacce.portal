<%@ page contentType="text/html" pageEncoding="UTF-8" %>
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
    <title>Login - Yacce</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">

    <style>
        .login-container {
            max-width: 400px;
            margin: 100px auto;
            padding: 20px;
            border: 1px solid #ccc;
            border-radius: 5px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }
        body {
            padding-top: 56px; /* Altura de la navbar */
        }
    </style>
</head>
<body>
<div class="login-container">
    <h2 class="text-center mb-4">Iniciar Sesión</h2>
    <%
        String mensaje = (String) request.getAttribute("msje");
        if (mensaje != null) {
    %>
    <div class="alert alert-<%= request.getAttribute("mensajeTipo") != null ? request.getAttribute("mensajeTipo") : "info" %>">
        <%= mensaje %>
    </div>
    <%
        }
    %>
    <form action="${pageContext.request.contextPath}/LoginServlet" method="post">
        <input type="hidden" name="accion" value="login">
        <div class="mb-3">
            <label for="username" class="form-label">Usuario</label>
            <input type="text" class="form-control" id="username" name="username" required>
        </div>
        <div class="mb-3">
            <label for="password" class="form-label">Contraseña</label>
            <input type="password" class="form-control" id="password" name="password" required>
        </div>
        <button type="submit" class="btn btn-primary w-100">Iniciar Sesión</button>
    </form>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>