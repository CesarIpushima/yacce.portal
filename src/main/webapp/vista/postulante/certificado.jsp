<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="modelo.Usuario" %>
<%
    Usuario usuario = (Usuario) request.getAttribute("usuario");
    String username = (String) session.getAttribute("username");
    if (username == null || usuario == null || usuario.getEstado() != 3) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Certificado de Finalización</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            background-color: #f8f9fa;
            font-family: 'Times New Roman', Times, serif;
        }
        .certificate-container {
            max-width: 1050px;
            margin: 20px auto;
            background: white;
            border: 5px solid #FFD700;
            border-radius: 10px;
            padding: 40px;
            box-shadow: 0 0 20px rgba(0, 0, 0, 0.2);
            position: relative;
        }
        .certificate-header {
            text-align: center;
            margin-bottom: 30px;
        }
        .certificate-header h1 {
            font-size: 3rem;
            color: #1a3c34;
            margin-bottom: 20px;
        }
        .certificate-header h2 {
            font-size: 2rem;
            color: #555;
            font-style: italic;
        }
        .certificate-body {
            text-align: center;
            font-size: 1.5rem;
            line-height: 1.6;
            margin-bottom: 100px;
        }
        .certificate-body .recipient-name {
            font-size: 2.5rem;
            font-weight: bold;
            color: #1a3c34;
            margin: 40px 0;
        }
        .certificate-footer {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-top: 80px;
        }
        .signature {
            text-align: center;
            width: 40%;
        }
        .signature-line {
            border-top: 2px solid #000;
            width: 200px;
            margin: 10px auto;
        }
        .signature-title {
            font-size: 1.5rem;
            color: #555;
        }
        .download-btn {
            text-align: center;
            margin-top: 20px;
        }
    </style>
</head>
<body>
    <div class="certificate-container">
        <div class="certificate-header">
            <h1>Certificado de Finalización</h1>
            <h2>Programa de Formación Académica</h2>
        </div>
        <div class="certificate-body">
            <p>Por la presente se certifica que</p>
            <div class="recipient-name"><%= usuario.getUser() %></div>
            <p>ha completado con éxito todos los cursos requeridos del programa, demostrando dedicación, esfuerzo y compromiso con la excelencia académica.</p>
            <p>Otorgado el <%= new java.text.SimpleDateFormat("dd 'de' MMMM 'de' yyyy").format(new java.util.Date()) %></p>
        </div>
        <div class="certificate-footer">
            <div class="signature">
                <div class="signature-line"></div>
                <div class="signature-title">Director General</div>
            </div>
            <div class="signature">
                <div class="signature-line"></div>
                <div class="signature-title">Coordinador Académico</div>
            </div>
        </div>
    </div> 
    <div class="download-btn text-center">
        <button id="descargarPdf" class="btn btn-success btn-lg">Descargar como PDF</button>
        <a href="${pageContext.request.contextPath}/CursoServlet?accion=listar" class="btn btn-secondary">Volver a Cursos</a>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/html2pdf.js/0.10.1/html2pdf.bundle.min.js"></script>y
    <script>
      document.getElementById("descargarPdf").addEventListener("click", function () {
        const element = document.querySelector(".certificate-container");

        const opt = {
          margin: 0,
          filename: 'certificado_<%= usuario.getUser() %>.pdf',
          image: { type: 'jpeg', quality: 1 },
          html2canvas: { scale: 3, useCORS: true, scrollY: 0 },
          jsPDF: { unit: 'mm', format: 'a4', orientation: 'landscape' },
          pagebreak: { avoid: 'div' }
        };

        html2pdf().set(opt).from(element).save();
      });
    </script>



</body>
</html>