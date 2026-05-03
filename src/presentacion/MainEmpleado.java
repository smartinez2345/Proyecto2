package presentacion;

import logica.*;
import modelo.*;
import java.util.List;
import java.util.Scanner;

public class MainEmpleado {

    private static Cafe cafe;
    private static GestorUsuarios gestorUsuarios;
    private static GestorTorneos gestorTorneos;
    private static GestorPrestamos gestorPrestamos;
    private static GestorTurnos gestorTurnos;
    private static Scanner scanner = new Scanner(System.in);
    private static final String ARCHIVO = "data/cafe_inicial.dat";
    private static Empleado empleadoActual;

    public static void main(String[] args) {
        cargarDatos();
        autenticar();
    }

    // ─── Persistencia ─────────────────────────────────────────────────
    private static void cargarDatos() {
        Cafe cargado = Cafe.cargarEstado(ARCHIVO);
        if (cargado != null) {
            cafe = cargado;
        } else {
            cafe = new Cafe(30);
            System.out.println("Iniciando con datos por defecto.");
        }
        gestorUsuarios  = new GestorUsuarios(cafe);
        gestorTorneos   = new GestorTorneos(cafe);
        gestorPrestamos = new GestorPrestamos(cafe);
        gestorTurnos    = new GestorTurnos(cafe);
    }

    private static void guardarDatos() {
        new java.io.File("data").mkdirs();
        cafe.guardarEstado(ARCHIVO);
    }

    // ─── Autenticación ────────────────────────────────────────────────
    private static void autenticar() {
        System.out.println("========================================");
        System.out.println("   BOARD GAME CAFÉ — Portal Empleado");
        System.out.println("========================================");
        int intentos = 0;
        while (intentos < 3) {
            System.out.print("Login: ");
            String login = scanner.nextLine().trim();
            System.out.print("Password: ");
            String pass = scanner.nextLine().trim();

            Usuario u = gestorUsuarios.login(login, pass);
            if (u instanceof Empleado) {
                empleadoActual = (Empleado) u;
                System.out.println("¡Bienvenido, " + login + "!\n");
                menuPrincipal();
                return;
            } else {
                intentos++;
                System.out.println("Credenciales incorrectas. Intentos restantes: " + (3 - intentos));
            }
        }
        System.out.println("Demasiados intentos. Cerrando.");
    }

    // ─── Menú principal ───────────────────────────────────────────────
    private static void menuPrincipal() {
        boolean salir = false;
        while (!salir) {
            System.out.println("\n========== MENÚ EMPLEADO ==========");
            System.out.println("Turno: " + empleadoActual.getTurnoSemana());
            System.out.println("1. Ver torneos disponibles");
            System.out.println("2. Inscribirme en un torneo");
            System.out.println("3. Desinscribirme de un torneo");
            System.out.println("4. Solicitar cambio de turno");
            System.out.println("5. Préstamo de juego");
            System.out.println("6. Salir");
            int op = leerEntero(1, 6);
            switch (op) {
                case 1: gestorTorneos.mostrarTorneos(); break;
                case 2: inscribirseEnTorneo(); break;
                case 3: desinscribirseDelTorneo(); break;
                case 4: solicitarCambioTurno(); break;
                case 5: menuPrestamo(); break;
                case 6: salir = true; break;
            }
        }
        guardarDatos();
        System.out.println("Datos guardados. ¡Hasta luego!");
    }

    // ─── Torneos ──────────────────────────────────────────────────────
    private static void inscribirseEnTorneo() {
        Torneo t = elegirTorneo();
        if (t == null) return;

        System.out.print("¿Cuántos participantes quieres inscribir? (1-3): ");
        int cant = leerEntero(1, 3);
        gestorTorneos.inscribirEmpleado(t, empleadoActual, cant);
    }

    private static void desinscribirseDelTorneo() {
        List<Torneo> torneos = cafe.getTorneos();
        List<Torneo> misInscritos = new java.util.ArrayList<>();
        for (Torneo t : torneos)
            if (t.yaInscrito(empleadoActual)) misInscritos.add(t);

        if (misInscritos.isEmpty()) {
            System.out.println("No estás inscrito en ningún torneo.");
            return;
        }
        System.out.println("Torneos en los que estás inscrito:");
        for (int i = 0; i < misInscritos.size(); i++) {
            InscripcionTorneo ins = misInscritos.get(i).buscarInscripcion(empleadoActual);
            System.out.println((i+1) + ". " + misInscritos.get(i).getNombre()
                + " | Cupos: " + ins.totalCupos());
        }
        System.out.print("Seleccione torneo para desinscribirse: ");
        int idx = leerEntero(1, misInscritos.size()) - 1;
        gestorTorneos.desinscribirEmpleado(misInscritos.get(idx), empleadoActual);
    }

    // ─── Cambio de turno ──────────────────────────────────────────────
    private static void solicitarCambioTurno() {
        List<Empleado> empleados = cafe.getEmpleados();
        List<Empleado> otros = new java.util.ArrayList<>();
        for (Empleado e : empleados)
            if (!e.getLogin().equals(empleadoActual.getLogin())) otros.add(e);

        if (otros.isEmpty()) { System.out.println("No hay otros empleados disponibles."); return; }

        System.out.println("Empleados disponibles para cambio:");
        for (int i = 0; i < otros.size(); i++) {
            String tipo = otros.get(i) instanceof Cocinero ? "Cocinero" : "Mesero";
            System.out.println((i+1) + ". " + otros.get(i).getLogin()
                + " | " + tipo + " | Turno: " + otros.get(i).getTurnoSemana());
        }
        System.out.print("Seleccione empleado para solicitar cambio: ");
        int idx = leerEntero(1, otros.size()) - 1;
        gestorTurnos.solicitarCambio(empleadoActual, otros.get(idx));
    }

    // ─── Préstamo ─────────────────────────────────────────────────────
    private static void menuPrestamo() {
        if (empleadoActual.estaEnTurno()) {
            System.out.println("ERROR: No puedes pedir préstamos mientras estás en turno.");
            return;
        }
        System.out.println("\n--- PRÉSTAMO DE JUEGO ---");
        System.out.println("1. Iniciar préstamo");
        System.out.println("2. Volver");
        if (leerEntero(1, 2) == 2) return;

        Prestamo p = gestorPrestamos.iniciarPrestamoEmpleado(empleadoActual);
        if (p == null) return;

        List<InventarioJuegos> inventario = cafe.getInventariosJuegos();
        if (inventario.isEmpty()) { System.out.println("No hay juegos disponibles."); return; }

        System.out.println("Juegos disponibles:");
        for (int i = 0; i < inventario.size(); i++) {
            InventarioJuegos inv = inventario.get(i);
            if (inv.estaDisponible())
                System.out.println((i+1) + ". " + inv.getJuego().getNombre()
                    + " | Disponibles: " + inv.getCantidadDisponible());
        }
        System.out.print("Seleccione juego (0 para cancelar): ");
        int idx = leerEntero(0, inventario.size());
        if (idx == 0) return;

        gestorPrestamos.agregarJuegoAPrestamoEmpleado(p, inventario.get(idx-1).getJuego(), empleadoActual);
        gestorPrestamos.mostrarPrestamo(p);
    }

    // ─── Auxiliares ───────────────────────────────────────────────────
    private static Torneo elegirTorneo() {
        List<Torneo> torneos = cafe.getTorneos();
        if (torneos.isEmpty()) { System.out.println("No hay torneos disponibles."); return null; }
        System.out.println("Torneos disponibles:");
        for (int i = 0; i < torneos.size(); i++) {
            Torneo t = torneos.get(i);
            System.out.println((i+1) + ". " + t.getNombre()
                + " | " + t.getDiaSemana()
                + " | " + (t.esAmistoso() ? "Amistoso (gratis)" : "Competitivo (gratis para empleados, sin premio)")
                + " | Cupos: " + t.cuposTotalesDisponibles());
        }
        System.out.print("Seleccione torneo: ");
        int idx = leerEntero(1, torneos.size()) - 1;
        return torneos.get(idx);
    }

    private static int leerEntero(int min, int max) {
        while (true) {
            try {
                String linea = scanner.nextLine().trim();
                int val = Integer.parseInt(linea);
                if (val >= min && val <= max) return val;
                System.out.print("Ingrese un número entre " + min + " y " + max + ": ");
            } catch (NumberFormatException e) {
                System.out.print("Entrada inválida. Ingrese un número: ");
            }
        }
    }
}