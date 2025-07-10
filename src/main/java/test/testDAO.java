package test;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import modelo.Curso;
import modelo.dao.CursoDAO;

public class testDAO {
    public static void main(String[] args) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        CursoDAO dao = new CursoDAO();

        while (true) {
            System.out.println("\n=== MENÚ CURSO ===");
            System.out.println("1. Registrar curso");
            System.out.println("2. Listar cursos");
            System.out.println("3. Buscar curso por ID");
            System.out.println("4. Actualizar curso");
            System.out.println("5. Eliminar curso");
            System.out.println("0. Salir");
            System.out.print("Opción: ");
            int opcion = Integer.parseInt(scanner.nextLine());

            switch (opcion) {
                case 1: // Registrar
                    System.out.print("Nombre del curso: ");
                    String nombre = scanner.nextLine();
                    System.out.print("Número de preguntas: ");
                    int preguntas = Integer.parseInt(scanner.nextLine());
                    System.out.print("Número de temas: ");
                    int temas= Integer.parseInt(scanner.nextLine());
                    Curso nuevoCurso = new Curso(0,nombre, preguntas, temas);
                    boolean registrado = dao.registrar(nuevoCurso);
                    System.out.println(registrado ? "Curso registrado." : "Error al registrar.");
                    break;

                case 2: // Listar
                    List<Curso> cursos = dao.listar();
                    System.out.println("Cursos encontrados:");
                    for (Curso c : cursos) {
                        System.out.println(c.getIdCurso() + ": " + c.getNombreCurso() + " (" + c.getNumPreguntas() + " preguntas)");
                    }
                    break;

                case 3: // Buscar por ID
                    System.out.print("ID del curso: ");
                    int idBuscar = Integer.parseInt(scanner.nextLine());
                    Curso curso = dao.obtenerPorId(idBuscar);
                    if (curso != null) {
                        System.out.println("Curso: " + curso.getNombreCurso() + " - Preguntas: " + curso.getNumPreguntas());
                    } else {
                        System.out.println("Curso no encontrado.");
                    }
                    break;

                case 4: // Actualizar
                    System.out.print("ID del curso a actualizar: ");
                    int idActualizar = Integer.parseInt(scanner.nextLine());
                    Curso cursoExistente = dao.obtenerPorId(idActualizar);
                    if (cursoExistente != null) {
                        System.out.print("Nuevo nombre del curso: ");
                        String nuevoNombre = scanner.nextLine();
                        System.out.print("Nuevo número de preguntas: ");
                        int nuevasPreguntas = Integer.parseInt(scanner.nextLine());

                        cursoExistente.setNombreCurso(nuevoNombre);
                        cursoExistente.setNumPreguntas(nuevasPreguntas);

                        boolean actualizado = dao.actualizar(cursoExistente);
                        System.out.println(actualizado ? "Curso actualizado." : "Error al actualizar.");
                    } else {
                        System.out.println("Curso no encontrado.");
                    }
                    break;

                case 5: // Eliminar
                    System.out.print("ID del curso a eliminar: ");
                    int idEliminar = Integer.parseInt(scanner.nextLine());
                    boolean eliminado = dao.eliminar(idEliminar);
                    System.out.println(eliminado ? "Curso eliminado." : "Error al eliminar.");
                    break;

                case 0:
                    System.out.println("Saliendo...");
                    return;

                default:
                    System.out.println("Opción inválida.");
            }
        }
    }
}
