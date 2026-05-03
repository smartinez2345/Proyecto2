package presentacion;

import logica.*;
import modelo.*;
import java.util.List;
import java.util.Scanner;

public class MainCliente {

    private static Cafe cafe;
    private static GestorUsuarios gestorUsuarios;
    private static GestorTorneos gestorTorneos;
    private static Scanner scanner = new Scanner(System.in);
    private static final String ARCHIVO = "data/cafe_inicial.dat";
    private static Cliente clienteActual;

    public static void main(String[] args) {
        cargarDatos();
        menuInicio();
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
    }

    private static void guardarDatos() {
        new java.io.File("data").mkdirs();
        cafe.guardarEstado(ARCHIVO);
    }

    // ─── Inicio ───────────────────────────────────────────────────────
    private static void menuInicio() {
        System.out.println("========================================");
        System.out.println("   BOARD GAME CAFÉ — Portal Cliente");
        System.out.println("========================================");
        System.out.println("1. Iniciar sesión");
        System.out.println("2. Registrarme");
        System.out.println("3. Salir");
        int op = leerEntero(1, 3);
        switch (op) {
            case 1: autenticar(); break;
            case 2: registrarse(); break;
            case 3: System.out.println("¡Hasta luego!"); break;
        }
    }

    private static void registrarse() {
        System.out.println("\n--- REGISTRO DE CLIENTE ---");
        System.out.print("Elige un login: ");
        String login = scanner.nextLine().trim();
        if (login.isEmpty()) { System.out.println("Login no puede estar vacío."); menuInicio(); return; }

        System.out.print("Elige una contraseña: ");
        String pass = scanner.nextLine().trim();
        if (pass.isEmpty()) { System.out.println("Contraseña no puede estar vacía."); menuInicio(); return; }

        Cliente c = gestorUsuarios.registrarCliente(login, pass);
        if (c != null) {
            System.out.println("¡Registro exitoso! Ya puedes iniciar sesión.");
            guardarDatos();
        }
        menuInicio();
    }

    private static void autenticar() {
        System.out.println("\n--- INICIAR SESIÓN ---");
        int intentos = 0;
        while (intentos < 3) {
            System.out.print("Login: ");
            String login = scanner.nextLine().trim();
            System.out.print("Password: ");
            String pass = scanner.nextLine().trim();

            Usuario u = gestorUsuarios.login(login, pass);
            if (u instanceof Cliente) {
                clienteActual = (Cliente) u;
                System.out.println("¡Bienvenido, " + login + "!\n");
                menuPrincipal();
                return;
            } else {
                intentos++;
                System.out.println("Credenciales incorrectas. Intentos restantes: " + (3 - intentos));
            }
        }
        System.out.println("Demasiados intentos. Volviendo al inicio.");
        menuInicio();
    }

    // ─── Menú principal ───────────────────────────────────────────────
    private static void menuPrincipal() {
        boolean salir = false;
        while (!salir) {
            System.out.println("\n========== MENÚ CLIENTE ==========");
            System.out.println("1. Ver torneos disponibles");
            System.out.println("2. Inscribirme en un torneo");
            System.out.println("3. Desinscribirme de un torneo");
            System.out.println("4. Mis bonos de descuento");
            System.out.println("5. Mis juegos favoritos");
            System.out.println("6. Salir");
            int op = leerEntero(1, 6);
            switch (op) {
                case 1: gestorTorneos.mostrarTorneos(); break;
                case 2: inscribirseEnTorneo(); break;
                case 3: desinscribirseDelTorneo(); break;
                case 4: verMisBonos(); break;
                case 5: menuFavoritos(); break;
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
        gestorTorneos.inscribirCliente(t, clienteActual, cant);
    }

    private static void desinscribirseDelTorneo() {
        List<Torneo> torneos = cafe.getTorneos();
        List<Torneo> misInscritos = new java.util.ArrayList<>();
        for (Torneo t : torneos)
            if (t.yaInscrito(clienteActual)) misInscritos.add(t);

        if (misInscritos.isEmpty()) {
            System.out.println("No estás inscrito en ningún torneo.");
            return;
        }
        System.out.println("Torneos en los que estás inscrito:");
        for (int i = 0; i < misInscritos.size(); i++) {
            InscripcionTorneo ins = misInscritos.get(i).buscarInscripcion(clienteActual);
            System.out.println((i+1) + ". " + misInscritos.get(i).getNombre()
                + " | Cupos tomados: " + ins.totalCupos());
        }
        System.out.print("Seleccione torneo para desinscribirse: ");
        int idx = leerEntero(1, misInscritos.size()) - 1;
        gestorTorneos.desinscribirCliente(misInscritos.get(idx), clienteActual);
    }

    // ─── Bonos ────────────────────────────────────────────────────────
    private static void verMisBonos() {
    	List<Double> bonos = clienteActual.getBonosDescuento();
        if (bonos.isEmpty()) {
            System.out.println("No tienes bonos de descuento acumulados.");
            return;
        }
        System.out.println("\n--- MIS BONOS DE DESCUENTO ---");
        for (int i = 0; i < bonos.size(); i++) {
            System.out.println((i+1) + ". Descuento del " + (int)(bonos.get(i)*100) + "%");
        }
        System.out.println("(Los bonos se usan al realizar una compra)");
    }

    // ─── Favoritos ────────────────────────────────────────────────────
    private static void menuFavoritos() {
        System.out.println("\n--- MIS JUEGOS FAVORITOS ---");
        List<Juego> favoritos = clienteActual.getJuegosFavoritos();
        if (favoritos.isEmpty()) System.out.println("No tienes juegos favoritos aún.");
        else for (Juego j : favoritos) System.out.println("  - " + j.getNombre());

        System.out.println("\n1. Agregar juego favorito");
        System.out.println("2. Volver");
        int op = leerEntero(1, 2);
        if (op == 1) agregarFavorito();
    }

    private static void agregarFavorito() {
        List<InventarioJuegos> inventario = cafe.getInventariosJuegos();
        if (inventario.isEmpty()) { System.out.println("No hay juegos disponibles."); return; }
        System.out.println("Juegos disponibles:");
        for (int i = 0; i < inventario.size(); i++)
            System.out.println((i+1) + ". " + inventario.get(i).getJuego().getNombre());
        System.out.print("Seleccione juego favorito: ");
        int idx = leerEntero(1, inventario.size()) - 1;
        clienteActual.agregarJuegoFavorito(inventario.get(idx).getJuego());
        System.out.println("Juego agregado a favoritos.");
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
                + " | " + (t.esAmistoso() ? "Amistoso" : "Competitivo")
                + " | Cupos disponibles: " + t.cuposTotalesDisponibles()
                + " | Premio: " + t.descripcionPremio());
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