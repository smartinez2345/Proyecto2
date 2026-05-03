package logica;

import modelo.*;

public class GestorPrestamos {

    private Cafe cafe;

    public GestorPrestamos(Cafe cafe) {
        this.cafe = cafe;
    }

    public Mesa crearMesa(int cantidadPersonas, boolean tieneMenores5,
                          boolean tieneMenores18, Cliente cliente) {
        if (!cafe.hayCapacidad(cantidadPersonas)) {
            System.out.println("ERROR: El café no tiene capacidad para " + cantidadPersonas + " personas.");
            return null;
        }
        Mesa mesa = new Mesa(cantidadPersonas, tieneMenores5, tieneMenores18, cliente);
        cafe.agregarMesa(mesa);
        System.out.println("Mesa creada para cliente: " + cliente.getLogin()
            + " | Personas: " + cantidadPersonas);
        return mesa;
    }

    public Prestamo iniciarPrestamo(Mesa mesa) {
        Prestamo p = new Prestamo(mesa);
        cafe.registrarPrestamo(p);
        System.out.println("Préstamo iniciado para mesa del cliente: "
            + mesa.getCliente().getLogin());
        return p;
    }

    public boolean agregarJuegoAPrestamo(Prestamo prestamo, Juego juego) {
        if (prestamo.getJuegos().size() >= 2) {
            System.out.println("ERROR: Ya tiene 2 juegos en el préstamo.");
            return false;
        }

        InventarioJuegos inv = cafe.buscarInventarioJuego(juego);
        if (inv == null || !inv.estaDisponible()) {
            System.out.println("ERROR: El juego '" + juego.getNombre() + "' no está disponible.");
            return false;
        }

        Mesa mesa = prestamo.getMesa();
        if (mesa == null) {
            System.out.println("ERROR: Préstamo sin mesa no puede usar este método. Use agregarJuegoAPrestamoEmpleado.");
            return false;
        }

        if (!esAptoParaMesa(juego, mesa)) {
            System.out.println("ERROR: El juego '" + juego.getNombre()
                + "' no es apto para esta mesa (edad o número de jugadores).");
            return false;
        }

        if (juego.getCategoria().equalsIgnoreCase("ACCION")) {
            if (mesa.tieneBebidaCaliente()) {
                System.out.println("ERROR: No se puede prestar un juego de Acción "
                    + "a una mesa con bebida caliente.");
                return false;
            }
        }

        if (juego.isEsDificil()) {
            Mesero mesero = buscarMeseroParaJuego(juego);
            if (mesero == null) {
                System.out.println("ADVERTENCIA: No hay mesero capacitado para '"
                    + juego.getNombre() + "'. Se presta bajo advertencia.");
            } else {
                System.out.println("Mesero '" + mesero.getLogin()
                    + "' enseñará las reglas de '" + juego.getNombre() + "'.");
            }
        }

        prestamo.agregarJuego(juego);
        inv.actualizarCantidad(-1);
        mesa.agregarJuego(juego);
        System.out.println("Juego '" + juego.getNombre() + "' agregado al préstamo.");
        return true;
    }

    public void cerrarPrestamo(Prestamo prestamo) {
        for (Juego j : prestamo.getJuegos()) {
            InventarioJuegos inv = cafe.buscarInventarioJuego(j);
            if (inv != null) {
                inv.actualizarCantidad(1);
            }
        }
        prestamo.cerrarPrestamo();
        if (prestamo.getMesa() != null) {
            cafe.liberarMesa(prestamo.getMesa());
        }
        System.out.println("Préstamo cerrado. Juegos devueltos al inventario.");
    }

    public void mostrarPrestamo(Prestamo p) {
        System.out.println("\n===== PRÉSTAMO =====");
        if (p.getMesa() != null) {
            System.out.println("Cliente: " + p.getMesa().getCliente().getLogin());
        } else {
            System.out.println("Préstamo de empleado (sin mesa)");
        }
        System.out.println("Fecha: " + p.getFechaPrestamo());
        System.out.println("Estado: " + p.getEstado());
        System.out.println("Juegos prestados:");
        for (Juego j : p.getJuegos()) {
            System.out.println("  - " + j.getNombre() + " (" + j.getCategoria() + ")");
        }
        System.out.println("====================\n");
    }

    // --- Métodos para préstamo de empleados ---
    public Prestamo iniciarPrestamoEmpleado(Empleado empleado) {
        if (empleado.estaEnTurno()) {
            System.out.println("ERROR: Empleado en turno no puede pedir prestado.");
            return null;
        }
        Prestamo p = new Prestamo(null); // sin mesa
        cafe.registrarPrestamo(p);
        System.out.println("Préstamo iniciado para empleado: " + empleado.getLogin());
        return p;
    }

    public boolean agregarJuegoAPrestamoEmpleado(Prestamo prestamo, Juego juego, Empleado empleado) {
        if (prestamo.getMesa() != null) {
            System.out.println("ERROR: Este préstamo está asociado a una mesa.");
            return false;
        }
        if (prestamo.getJuegos().size() >= 2) {
            System.out.println("ERROR: Ya tiene 2 juegos en el préstamo.");
            return false;
        }
        InventarioJuegos inv = cafe.buscarInventarioJuego(juego);
        if (inv == null || !inv.estaDisponible()) {
            System.out.println("ERROR: El juego '" + juego.getNombre() + "' no está disponible.");
            return false;
        }

        // Para empleados no hay restricciones de edad ni número de jugadores
        if (juego.isEsDificil()) {
            Mesero mesero = buscarMeseroParaJuego(juego);
            if (mesero == null) {
                System.out.println("ADVERTENCIA: No hay mesero capacitado para '" + juego.getNombre() + "'.");
            } else {
                System.out.println("Mesero '" + mesero.getLogin() + "' enseñará las reglas.");
            }
        }

        prestamo.agregarJuego(juego);
        inv.actualizarCantidad(-1);
        System.out.println("Juego '" + juego.getNombre() + "' agregado al préstamo del empleado.");
        return true;
    }

    public void cerrarPrestamoEmpleado(Prestamo prestamo) {
        cerrarPrestamo(prestamo); // mismo proceso
    }

    // --- Métodos auxiliares ---
    private boolean esAptoParaMesa(Juego juego, Mesa mesa) {
        int personas = mesa.getCantidadPersonas();
        if (personas < juego.getMinJugadores() || personas > juego.getMaxJugadores()) return false;
        if (juego.getEdadMinima() >= 18 && mesa.isTieneMenores18()) return false;
        if (juego.getEdadMinima() >= 5 && mesa.isTieneMenores5()) return false;
        return true;
    }

    private Mesero buscarMeseroParaJuego(Juego juego) {
        for (Empleado e : cafe.getEmpleados()) {
            if (e instanceof Mesero) {
                Mesero m = (Mesero) e;
                if (m.conoceJuego(juego)) return m;
            }
        }
        return null;
    }
}