package controlador;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;

@WebFilter("/*") // Aplica el filtro a todas las URLs
public class AuthorizationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("AuthorizationFilter inicializado"); // Depuración
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String relativeURI = requestURI.substring(contextPath.length());
        System.out.println("Procesando solicitud para: " + requestURI); // Depuración

        // Excluir rutas públicas
        if (relativeURI.equals("/vista/login.jsp") || relativeURI.contains("/LoginServlet")) {
            chain.doFilter(request, response); // Permitir acceso sin verificar sesión o rol
            return;
        }

        // Verificar si el usuario está autenticado
        HttpSession session = httpRequest.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            System.out.println("Sin sesión activa, redirigiendo a login.jsp"); // Depuración
            httpResponse.sendRedirect(contextPath + "/vista/login.jsp");
            return;
        }

        // Obtener el rol del usuario
        String role = (String) session.getAttribute("role");
        System.out.println("Rol del usuario: " + role); // Depuración

        // Determinar la página por defecto según el rol
        String redirectPath;
        if ("ADMINISTRADOR".equals(role)) {
            redirectPath = "/vista/admin/indexAdmin.jsp"; // Redirige a la página de gestión de cursos
        } else if ("POSTULANTE".equals(role)) {
            redirectPath = "/vista/postulante/indexPostulante.jsp"; // Redirige al dashboard del postulante
        } else {
            redirectPath = "/vista/login.jsp"; // En caso de rol desconocido, redirige al login
        }

        // Proteger rutas según el rol
        if (relativeURI.startsWith("/vista/admin") && !"ADMINISTRADOR".equals(role)) {
            System.out.println("Acceso denegado: No es administrador");
            String mensaje = URLEncoder.encode("Acceso denegado: No tienes permisos para acceder a esta página.", "UTF-8");
            httpResponse.sendRedirect(contextPath + redirectPath + "?msje=" + mensaje + "&mensajeTipo=danger");
            return;
        } else if (relativeURI.startsWith("/vista/postulante") && !"POSTULANTE".equals(role)) {
            System.out.println("Acceso denegado: No es postulante");
            String mensaje = URLEncoder.encode("Acceso denegado: No tienes permisos para acceder a esta página.", "UTF-8");
            httpResponse.sendRedirect(contextPath + redirectPath + "?msje=" + mensaje + "&mensajeTipo=danger");
            return;
        }

        // Si el rol es correcto o la ruta no está protegida, permitir acceso
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        System.out.println("AuthorizationFilter destruido"); // Depuración
    }
}