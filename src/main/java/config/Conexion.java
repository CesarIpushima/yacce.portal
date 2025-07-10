package config;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    private static final String URL = "jdbc:mysql://database-yacce.mysql.database.azure.com:3306/yacce_portal?useSSL:true";
    private static final String USER = "Administrador";
    private static final String PASS = "Administrador123**";
    public static Connection getConexion() {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("Conexión exitosa a la base de datos.");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error al conectar: " + e.getMessage());
        }
        return con;
    }
    
    
    public final void main(String[] args) {
        Connection conn = Conexion.getConexion();
        // Aquí puedes usar la conexión...
   
    }
}
