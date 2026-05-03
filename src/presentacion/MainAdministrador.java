package presentacion;

import logica.*;
import modelo.*;
import java.util.Scanner;
import java.util.List;

public class MainAdministrador {

    private static Cafe cafe;
    private static GestorUsuarios gestorUsuarios;
    private static GestorInventario gestorInventario;
    private static GestorTorneos gestorTorneos;
    private static GestorTurnos gestorTurnos;
    private static Scanner scanner = new Scanner(System.in);
    private static final String ARCHIVO = "data/cafe.dat";

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
            Administrador admin = new Administrador("admin", "admin123");
            cafe.setAdministrador(admin);
            System.out.println("Archivo no encontrado. Café iniciado con datos por defecto.");
        }
        gestorUsuarios  = new GestorUsuarios(cafe);
        gestorInventario = new GestorInventario(cafe);
        gestorTorneos   = new GestorTorneos(cafe);
        gestorTurnos    = new GestorTurnos(cafe);
    }

    private static void guardarDatos() {
        new java.io.File("data").mkdirs();
        cafe.guardarEstado(ARCHIVO);
    }

    // ─── Autenticación ────────────────────────────────────────────────
    private static void autenticar() {
        System.out.println("========================================");
        System.out.println("   BOARD GAME CAFÉ — Administrador");
        System.out.println("========================================");
        int intentos = 0;
        while (intentos < 3) {
            System.out.print("Login: ");
            String login = scanner.nextLine().trim();
            System.out.print("Password: ");
            String pass = scanner.nextLine().trim();

            Usuario u = gestorUsuarios.login(login, pass);
            if (u instanceof Administrador) {
                System.out.println("Bienvenido, " + login + "!\n");
                menuPrincipal();
                return;
            } else {
                intentos++;
                System.out.println("Credenciales incorrectas. Intentos restantes: " + (3 - intentos));
            }
        }
        System.out.println("Demasiados intentos fallidos. Cerrando.");
    }

    // ─── Menú principal ───────────────────────────────────────────────
    private static void menuPrincipal() {
        boolean salir = false;
        while (!salir) {
            System.out.println("========== MENÚ ADMINISTRADOR ==========");
            System.out.println("1. Gestionar empleados");
            System.out.println("2. Gestionar inventario");
            System.out.println("3. Gestionar torneos");
            System.out.println("4. Gestionar turnos");
            System.out.println("5. Salir");
            System.out.print("Opción: ");
            int op = leerEntero(1, 5);
            switch (op) {
                case 1: menuEmpleados(); break;
                case 2: menuInventario(); break;
                case 3: menuTorneos(); break;
                case 4: menuTurnos(); break;
                case 5: salir = true; break;
            }
        }
        guardarDatos();
        System.out.println("Datos guardados. ¡Hasta luego!");
    }

    // ─── Empleados ────────────────────────────────────────────────────
    private static void menuEmpleados() {
        System.out.println("\n--- GESTIÓN DE EMPLEADOS ---");
        System.out.println("1. Registrar empleado");
        System.out.println("2. Ver empleados");
        System.out.println("3. Volver");
        int op = leerEntero(1, 3);
        switch (op) {
            case 1: registrarEmpleado(); break;
            case 2: verEmpleados(); break;
        }
    }

    private static void registrarEmpleado() {
        System.out.print("Login del empleado: ");
        String login = scanner.nextLine().trim();
        if (login.isEmpty()) { System.out.println("Login no puede estar vacío."); return; }

        System.out.print("Password: ");
        String pass = scanner.nextLine().trim();
        if (pass.isEmpty()) { System.out.println("Password no puede estar vacío."); return; }

        System.out.println("Tipo: 1. Cocinero  2. Mesero");
        int tipo = leerEntero(1, 2);

        System.out.println("Día de turno:");
        System.out.println("1.LUNES 2.MARTES 3.MIERCOLES 4.JUEVES 5.VIERNES 6.SABADO 7.DOMINGO");
        int diaOp = leerEntero(1, 7);
        String[] dias = {"LUNES","MARTES","MIERCOLES","JUEVES","VIERNES","SABADO","DOMINGO"};
        String turno = dias[diaOp - 1];

        gestorUsuarios.registrarEmpleado(login, pass, turno, tipo == 1 ? "COCINERO" : "MESERO");
    }

    private static void verEmpleados() {
        List<Empleado> empleados = cafe.getEmpleados();
        if (empleados.isEmpty()) { System.out.println("No hay empleados registrados."); return; }
        System.out.println("\n--- EMPLEADOS ---");
        for (Empleado e : empleados) {
            String tipo = e instanceof Cocinero ? "Cocinero" : "Mesero";
            System.out.println("  " + e.getLogin() + " | " + tipo + " | Turno: " + e.getTurnoSemana());
        }
    }

    // ─── Inventario ───────────────────────────────────────────────────
    private static void menuInventario() {
        System.out.println("\n--- GESTIÓN DE INVENTARIO ---");
        System.out.println("1. Agregar juego a préstamo");
        System.out.println("2. Agregar producto cafetería");
        System.out.println("3. Ver inventario juegos");
        System.out.println("4. Ver inventario cafetería");
        System.out.println("5. Volver");
        int op = leerEntero(1, 5);
        switch (op) {
            case 1: agregarJuegoPrestamo(); break;
            case 2: agregarProductoCafeteria(); break;
            case 3: gestorInventario.mostrarInventarioJuegos(); break;
            case 4: gestorInventario.mostrarInventarioCafeteria(); break;
        }
    }

    private static void agregarJuegoPrestamo() {
        System.out.print("Nombre del juego: ");
        String nombre = scanner.nextLine().trim();
        if (nombre.isEmpty()) { System.out.println("Nombre no puede estar vacío."); return; }

        System.out.print("Año publicación: ");
        int anio = leerEntero(1000, 2100);
        System.out.print("Empresa: ");
        String empresa = scanner.nextLine().trim();
        System.out.print("Categoría (TABLERO/CARTAS/ACCION): ");
        String cat = scanner.nextLine().trim().toUpperCase();
        System.out.print("Min jugadores: ");
        int min = leerEntero(1, 20);
        System.out.print("Max jugadores: ");
        int max = leerEntero(min, 50);
        System.out.print("Edad mínima: ");
        int edad = leerEntero(0, 99);
        System.out.println("¿Es difícil? 1.Sí  2.No");
        boolean dificil = leerEntero(1, 2) == 1;
        System.out.print("Cantidad a agregar: ");
        int cant = leerEntero(1, 100);

        Juego j = new Juego(nombre, anio, empresa, cat, min, max, edad, dificil);
        gestorInventario.agregarJuegoAPrestamo(j, cant);
    }

    private static void agregarProductoCafeteria() {
        System.out.print("Nombre del producto: ");
        String nombre = scanner.nextLine().trim();
        if (nombre.isEmpty()) { System.out.println("Nombre no puede estar vacío."); return; }
        System.out.print("Precio: $");
        double precio = leerDouble(0.01, 1000000);
        System.out.println("Tipo: 1.Bebida  2.Pastelería");
        int tipo = leerEntero(1, 2);
        System.out.print("Stock inicial: ");
        int stock = leerEntero(1, 1000);

        if (tipo == 1) {
            System.out.println("¿Es alcohólica? 1.Sí  2.No");
            boolean alc = leerEntero(1, 2) == 1;
            System.out.println("¿Es caliente? 1.Sí  2.No");
            boolean cal = leerEntero(1, 2) == 1;
            gestorInventario.agregarProductoCafeteria(new Bebida(nombre, precio, alc, cal), stock);
        } else {
            Pasteleria p = new Pasteleria(nombre, precio);
            System.out.print("¿Tiene alérgenos? 1.Sí  2.No: ");
            if (leerEntero(1, 2) == 1) {
                System.out.print("Ingrese alérgenos separados por coma: ");
                String[] alergenos = scanner.nextLine().split(",");
                for (String a : alergenos) p.agregarAlergeno(a.trim());
            }
            gestorInventario.agregarProductoCafeteria(p, stock);
        }
    }

    // ─── Torneos ──────────────────────────────────────────────────────
    private static void menuTorneos() {
        System.out.println("\n--- GESTIÓN DE TORNEOS ---");
        System.out.println("1. Crear torneo amistoso");
        System.out.println("2. Crear torneo competitivo");
        System.out.println("3. Ver torneos");
        System.out.println("4. Ver inscripciones de un torneo");
        System.out.println("5. Entregar premio amistoso");
        System.out.println("6. Calcular premio competitivo");
        System.out.println("7. Volver");
        int op = leerEntero(1, 7);
        switch (op) {
            case 1: crearTorneoAmistoso(); break;
            case 2: crearTorneoCompetitivo(); break;
            case 3: gestorTorneos.mostrarTorneos(); break;
            case 4: verInscripcionesTorneo(); break;
            case 5: entregarPremioAmistoso(); break;
            case 6: calcularPremioCompetitivo(); break;
        }
    }

    private static void crearTorneoAmistoso() {
        System.out.print("Nombre del torneo: ");
        String nombre = scanner.nextLine().trim();
        if (nombre.isEmpty()) { System.out.println("Nombre no puede estar vacío."); return; }

        String dia = elegirDiaSemana();
        Juego juego = elegirJuego();
        if (juego == null) return;

        System.out.print("Número de participantes: ");
        int num = leerEntero(1, 1000);
        System.out.print("Porcentaje de descuento del premio (ej: 15 para 15%): ");
        int pct = leerEntero(1, 100);

        gestorTorneos.crearTorneoAmistoso(nombre, dia, juego, num, pct / 100.0);
    }

    private static void crearTorneoCompetitivo() {
        System.out.print("Nombre del torneo: ");
        String nombre = scanner.nextLine().trim();
        if (nombre.isEmpty()) { System.out.println("Nombre no puede estar vacío."); return; }

        String dia = elegirDiaSemana();
        Juego juego = elegirJuego();
        if (juego == null) return;

        System.out.print("Número de participantes: ");
        int num = leerEntero(1, 1000);
        System.out.print("Tarifa de entrada ($): ");
        double tarifa = leerDouble(0.01, 1000000);

        gestorTorneos.crearTorneoCompetitivo(nombre, dia, juego, num, tarifa);
    }

    private static void verInscripcionesTorneo() {
        Torneo t = elegirTorneo();
        if (t != null) gestorTorneos.mostrarInscripciones(t);
    }

    private static void entregarPremioAmistoso() {
        Torneo t = elegirTorneo();
        if (!(t instanceof TorneoAmistoso)) {
            System.out.println("El torneo seleccionado no es amistoso.");
            return;
        }
        System.out.print("Login del cliente ganador: ");
        String login = scanner.nextLine().trim();
        Cliente ganador = buscarCliente(login);
        if (ganador == null) return;
        gestorTorneos.entregarBonoAmistoso((TorneoAmistoso) t, ganador);
    }

    private static void calcularPremioCompetitivo() {
        Torneo t = elegirTorneo();
        if (!(t instanceof TorneoCompetitivo)) {
            System.out.println("El torneo seleccionado no es competitivo.");
            return;
        }
        System.out.print("Login del ganador: ");
        String login = scanner.nextLine().trim();
        Usuario ganador = gestorUsuarios.buscarUsuario(login);
        if (ganador == null) { System.out.println("Usuario no encontrado."); return; }
        gestorTorneos.calcularPremioCompetitivo((TorneoCompetitivo) t, ganador);
    }

    // ─── Turnos ───────────────────────────────────────────────────────
    private static void menuTurnos() {
        System.out.println("\n--- GESTIÓN DE TURNOS ---");
        System.out.println("1. Ver turnos");
        System.out.println("2. Ver solicitudes pendientes");
        System.out.println("3. Aprobar solicitud");
        System.out.println("4. Rechazar solicitud");
        System.out.println("5. Volver");
        int op = leerEntero(1, 5);
        switch (op) {
            case 1: gestorTurnos.mostrarTurnos(); break;
            case 2: gestorTurnos.mostrarSolicitudesPendientes(); break;
            case 3: gestionarSolicitud(true); break;
            case 4: gestionarSolicitud(false); break;
        }
    }

    private static void gestionarSolicitud(boolean aprobar) {
        List<SolicitudCambioTurno> solicitudes = cafe.getSolicitudes();
        List<SolicitudCambioTurno> pendientes = new java.util.ArrayList<>();
        for (SolicitudCambioTurno s : solicitudes)
            if (s.getEstado().equals("PENDIENTE")) pendientes.add(s);

        if (pendientes.isEmpty()) { System.out.println("No hay solicitudes pendientes."); return; }

        for (int i = 0; i < pendientes.size(); i++) {
            SolicitudCambioTurno s = pendientes.get(i);
            System.out.println((i+1) + ". " + s.getSolicitante().getLogin()
                + " ↔ " + s.getReemplazo().getLogin());
        }
        System.out.print("Seleccione solicitud: ");
        int idx = leerEntero(1, pendientes.size()) - 1;
        if (aprobar) gestorTurnos.aprobarCambio(pendientes.get(idx));
        else         gestorTurnos.rechazarCambio(pendientes.get(idx));
    }

    // ─── Auxiliares ───────────────────────────────────────────────────
    private static String elegirDiaSemana() {
        System.out.println("Día del torneo:");
        System.out.println("1.LUNES 2.MARTES 3.MIERCOLES 4.JUEVES 5.VIERNES 6.SABADO 7.DOMINGO");
        int op = leerEntero(1, 7);
        String[] dias = {"LUNES","MARTES","MIERCOLES","JUEVES","VIERNES","SABADO","DOMINGO"};
        return dias[op - 1];
    }

    private static Juego elegirJuego() {
        List<InventarioJuegos> inventario = cafe.getInventariosJuegos();
        if (inventario.isEmpty()) {
            System.out.println("No hay juegos en inventario de préstamos.");
            return null;
        }
        System.out.println("Juegos disponibles:");
        for (int i = 0; i < inventario.size(); i++) {
            Juego j = inventario.get(i).getJuego();
            System.out.println((i+1) + ". " + j.getNombre()
                + " | Max jugadores: " + j.getMaxJugadores()
                + " | Copias: " + inventario.get(i).getCantidadDisponible());
        }
        System.out.print("Seleccione juego: ");
        int idx = leerEntero(1, inventario.size()) - 1;
        return inventario.get(idx).getJuego();
    }

    private static Torneo elegirTorneo() {
        List<Torneo> torneos = cafe.getTorneos();
        if (torneos.isEmpty()) { System.out.println("No hay torneos registrados."); return null; }
        System.out.println("Torneos disponibles:");
        for (int i = 0; i < torneos.size(); i++) {
            System.out.println((i+1) + ". " + torneos.get(i).getNombre()
                + " | " + torneos.get(i).getDiaSemana()
                + " | " + (torneos.get(i).esAmistoso() ? "Amistoso" : "Competitivo"));
        }
        System.out.print("Seleccione torneo: ");
        int idx = leerEntero(1, torneos.size()) - 1;
        return torneos.get(idx);
    }

    private static Cliente buscarCliente(String login) {
        for (Cliente c : cafe.getClientes())
            if (c.getLogin().equals(login)) return c;
        System.out.println("Cliente no encontrado.");
        return null;
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

    private static double leerDouble(double min, double max) {
        while (true) {
            try {
                String linea = scanner.nextLine().trim();
                double val = Double.parseDouble(linea);
                if (val >= min && val <= max) return val;
                System.out.print("Ingrese un valor entre " + min + " y " + max + ": ");
            } catch (NumberFormatException e) {
                System.out.print("Entrada inválida. Ingrese un número: ");
            }
        }
    }
}