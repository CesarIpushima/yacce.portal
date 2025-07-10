<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, modelo.Pregunta, modelo.Respuesta, modelo.Curso" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    // Evitar que la página se almacene en caché
    response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

    // Obtener idCurso del request con validación
    Integer idCursoObj = (Integer) request.getAttribute("idCurso");
    int idCurso = (idCursoObj != null) ? idCursoObj : 0;

    // Obtener el curso desde el request
    Curso curso = (Curso) request.getAttribute("curso");
    String nombreCurso = (curso != null && curso.getNombreCurso() != null) ? curso.getNombreCurso() : "Curso ID " + idCurso;

    // Obtener mensaje y tipo de mensaje (si existen)
    String mensaje = (String) request.getAttribute("msje");
    String mensajeTipo = (String) request.getAttribute("mensajeTipo");
%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Demuestra lo Aprendido - <%= nombreCurso %></title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/estilos/estilo.css">
    <style>
        body { padding-top: 56px; }
        .question-container {
            max-width: 800px;
            margin: 0 auto;
            padding: 20px;
        }
        .question-section {
            background-color: #e9ecef;
            padding: 30px;
            margin-bottom: 30px;
            border-radius: 5px;
            text-align: center;
        }
        .question-section h5 {
            margin-bottom: 20px;
        }
        .form-check {
            margin-bottom: 15px;
            text-align: left;
            display: flex;
            align-items: center;
        }
        .form-check-input {
            margin-right: 10px;
        }
        .form-check-label {
            flex-grow: 1;
            background-color: #dee2e6;
            padding: 10px;
            border-radius: 5px;
        }
        .btn-finalizar {
            background-color: #dc3545;
            color: white;
            border: none;
            padding: 10px 30px;
            border-radius: 20px;
            display: block;
            margin: 0 auto;
            font-size: 16px;
        }
        .btn-finalizar:hover {
            background-color: #c82333;
        }
        #score-display {
            position: fixed;
            top: 70px;
            right: 20px;
            background-color: #28a745;
            color: white;
            padding: 10px;
            border-radius: 5px;
            display: none;
            z-index: 1000;
        }
    </style>
</head>
<body>
<%@ include file="/vista/faces/topbar.jspf" %>
<%@ include file="/vista/faces/sidebar2.jspf" %>
<main class="col-md-10 ms-sm-auto px-md-4">
    <div class="question-container">
        <h2 class="mb-4 text-center">Demuestra lo Aprendido</h2>
        <p class="mb-4 text-center">Selecciona una respuesta por pregunta.</p>

        <!-- Mostrar mensaje si existe -->
        <% if (mensaje != null) { %>
            <div class="alert alert-<%= mensajeTipo %> alert-dismissible fade show" role="alert">
                <%= mensaje %>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        <% } %>

        <!-- Contenedor para mostrar la puntuación -->
        <div id="score-display"></div>

        <%
            List<Pregunta> preguntas = (List<Pregunta>) request.getAttribute("preguntas");
            if (preguntas != null && !preguntas.isEmpty()) {
        %>
        <form id="examenForm" action="${pageContext.request.contextPath}/ExamenServlet?accion=finalizar" method="POST">
            <input type="hidden" name="idCurso" value="<%= idCurso %>">
            <%
                int preguntaIndex = 1;
                for (Pregunta p : preguntas) {
                    List<Respuesta> respuestas = (List<Respuesta>) request.getAttribute("respuestas_" + p.getIdPregunta());
            %>
            <div class="question-section">
                <h5>Pregunta <%= preguntaIndex %>.</h5>
                <p class="mb-4"><%= p.getPregunta() %></p>
                <%
                    if (respuestas != null && !respuestas.isEmpty()) {
                %>
                <div>
                    <label class="form-label mb-2">Marca la opción correcta a la pregunta</label>
                    <%
                        for (Respuesta r : respuestas) {
                    %>
                    <div class="form-check">
                        <input class="form-check-input" type="radio" name="respuesta_<%= p.getIdPregunta() %>" id="respuesta_<%= r.getIdRespuesta() %>" value="<%= r.getIdRespuesta() %>">
                        <label class="form-check-label" for="respuesta_<%= r.getIdRespuesta() %>">
                            <%= r.getRespuesta() %>
                        </label>
                    </div>
                    <%
                        }
                    %>
                </div>
                <%
                    } else {
                %>
                <p class="text-muted">No hay respuestas disponibles para esta pregunta.</p>
                <%
                    }
                %>
            </div>
            <%
                    preguntaIndex++;
                }
            %>
           
            <button type="submit" class="btn-finalizar mt-3">Finalizar Examen</button>
        </form>
        <%
            } else {
        %>
        <p class="text-muted text-center">No hay preguntas disponibles para este curso.</p>
        <%
            }
        %>
    </div>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Mostrar la puntuación si se recibe desde el servlet
    <% if (request.getAttribute("nota") != null) { %>
        document.getElementById('score-display').style.display = 'block';
        document.getElementById('score-display').innerText = 'Puntuación: <%= request.getAttribute("nota") %>';
    <% } %>
</script>
<script>
    const TIEMPO_EXAMEN_MINUTOS = 1;
    const DURACION_MS = TIEMPO_EXAMEN_MINUTOS * 60 * 1000;
    // Obtener el idCurso desde el campo oculto
    const idCurso = document.querySelector('input[name="idCurso"]')?.value || "generico";
    const CLAVE_STORAGE = "finExamen_" + idCurso;

    // Si no existe aún, definimos la hora de fin
    if (!localStorage.getItem(CLAVE_STORAGE)) {
        const ahora = new Date().getTime();
        const horaFin = ahora + DURACION_MS;
        localStorage.setItem(CLAVE_STORAGE, horaFin);
    }

    const horaFinExamen = parseInt(localStorage.getItem(CLAVE_STORAGE));

    // Crear temporizador en la UI
    const temporizadorDiv = document.createElement("div");
    temporizadorDiv.id = "temporizador";
    temporizadorDiv.style.position = "fixed";
    temporizadorDiv.style.right = "10px";
    temporizadorDiv.style.top = "29%";
    temporizadorDiv.style.transform = "translateY(-50%)";
    temporizadorDiv.style.backgroundColor = "#343a40";
    temporizadorDiv.style.color = "#ffffff";
    temporizadorDiv.style.padding = "15px 25px";
    temporizadorDiv.style.borderRadius = "10px";
    temporizadorDiv.style.fontSize = "20px";
    temporizadorDiv.style.fontFamily = "monospace";
    temporizadorDiv.style.fontWeight = "bold";
    temporizadorDiv.style.boxShadow = "0 0 10px rgba(0,0,0,0.3)";
    temporizadorDiv.style.zIndex = "9999";
    document.body.appendChild(temporizadorDiv);

    function formatTime(msRestantes) {
        const totalSegundos = Math.floor(msRestantes / 1000);
        const minutos = Math.floor(totalSegundos / 60);
        const segundos = totalSegundos % 60;
        return (minutos < 10 ? "0" + minutos : minutos) + ":" + (segundos < 10 ? "0" + segundos : segundos);
    }

    function actualizarTemporizador() {
        const ahora = new Date().getTime();
        const tiempoRestante = horaFinExamen - ahora;

        if (tiempoRestante <= 0) {
            temporizadorDiv.textContent = "🕒 Tiempo: 00:00";
            clearInterval(intervalo);
            localStorage.removeItem(CLAVE_STORAGE);
            document.querySelector('.btn-finalizar').click();
        } else {
            temporizadorDiv.textContent = "🕒 Tiempo: " + formatTime(tiempoRestante);
        }
    }

    const intervalo = setInterval(actualizarTemporizador, 1000);
    actualizarTemporizador();
    // Borrar el temporizador guardado si el usuario finaliza manualmente
document.querySelector('.btn-finalizar').addEventListener('click', () => {
    localStorage.removeItem(CLAVE_STORAGE);
});
</script>
</body>
</html>