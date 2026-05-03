package test;

import logica.GestorUsuarios;
import modelo.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CafeTest {

    private Cafe cafe;
    private Cliente cliente1;
    private Cliente cliente2;
    private Mesero mesero;
    private Administrador admin;

    private Juego juego1;
    private Juego juego2;

    private Bebida bebida;
    private Pasteleria pastel;

    @Before
    public void setUp() {
        cafe = new Cafe(10);

        cliente1 = new Cliente("cliente1", "123");
        cliente2 = new Cliente("cliente2", "123");

        mesero = new Mesero("mesero1", "123", "LUNES");
        admin = new Administrador("admin", "123");

        juego1 = new Juego("Catan", 1995, "Kosmos",
                "TABLERO", 3, 4, 10, false);

        juego2 = new Juego("Uno", 1971, "Mattel",
                "CARTAS", 2, 10, 0, false);

        bebida = new Bebida("Cafe", 5000, false, true);

        pastel = new Pasteleria("Brownie", 7000);
        pastel.agregarAlergeno("Gluten");
    }

    // =========================
    // USUARIOS
    // =========================

    @Test
    public void testAgregarCliente() {
        cafe.agregarCliente(cliente1);

        assertEquals(1, cafe.getClientes().size());
        assertTrue(cafe.getClientes().contains(cliente1));
    }

    @Test
    public void testAgregarEmpleado() {
        cafe.agregarEmpleado(mesero);

        assertEquals(1, cafe.getEmpleados().size());
        assertTrue(cafe.getEmpleados().contains(mesero));
    }

    @Test
    public void testSetAdministrador() {
        cafe.setAdministrador(admin);

        assertNotNull(cafe.getAdministrador());
        assertEquals("admin", cafe.getAdministrador().getLogin());
    }

    // =========================
    // INVENTARIO JUEGOS
    // =========================

    @Test
    public void testAgregarInventarioJuego() {
        InventarioJuegos inv = new InventarioJuegos(juego1, 5);

        cafe.agregarInventarioJuego(inv);

        assertEquals(1, cafe.getInventariosJuegos().size());
        assertEquals(1, cafe.getJuegos().size());
    }

    @Test
    public void testBuscarInventarioJuego() {
        InventarioJuegos inv = new InventarioJuegos(juego1, 3);
        cafe.agregarInventarioJuego(inv);

        assertNotNull(cafe.buscarInventarioJuego(juego1));
    }

    @Test
    public void testBuscarInventarioJuegoNoExiste() {
        assertNull(cafe.buscarInventarioJuego(juego1));
    }

    // =========================
    // INVENTARIO CAFETERIA
    // =========================

    @Test
    public void testAgregarInventarioCafeteria() {
        InventarioCafeteria inv = new InventarioCafeteria(bebida, 8);

        cafe.agregarInventarioCafeteria(inv);

        assertEquals(1, cafe.getInventariosCafeteria().size());
    }

    @Test
    public void testBuscarInventarioCafeteria() {
        InventarioCafeteria inv = new InventarioCafeteria(pastel, 4);
        cafe.agregarInventarioCafeteria(inv);

        assertNotNull(cafe.buscarInventarioCafeteria(pastel));
    }

    // =========================
    // MESAS Y CAPACIDAD
    // =========================

    @Test
    public void testAgregarMesa() {
        Mesa mesa = new Mesa(4, false, false, cliente1);

        cafe.agregarMesa(mesa);

        assertEquals(1, cafe.getMesas().size());
    }

    @Test
    public void testHayCapacidadTrue() {
        Mesa mesa = new Mesa(4, false, false, cliente1);
        cafe.agregarMesa(mesa);

        assertTrue(cafe.hayCapacidad(5));
    }

    @Test
    public void testHayCapacidadFalse() {
        Mesa mesa = new Mesa(8, false, false, cliente1);
        cafe.agregarMesa(mesa);

        assertFalse(cafe.hayCapacidad(3));
    }

    @Test
    public void testLiberarMesa() {
        Mesa mesa = new Mesa(4, false, false, cliente1);
        cafe.agregarMesa(mesa);

        cafe.liberarMesa(mesa);

        assertEquals(0, cafe.getMesas().size());
        assertFalse(mesa.isOcupada());
    }

    // =========================
    // PRESTAMOS
    // =========================

    @Test
    public void testRegistrarPrestamo() {
        Mesa mesa = new Mesa(4, false, false, cliente1);
        Prestamo p = new Prestamo(mesa);

        cafe.registrarPrestamo(p);

        assertEquals(1, cafe.getPrestamos().size());
    }

    // =========================
    // VENTAS
    // =========================

    @Test
    public void testRegistrarVenta() {
        Venta v = new Venta("CAFETERIA", cliente1);

        cafe.registrarVenta(v);

        assertEquals(1, cafe.getVentas().size());
    }

    @Test
    public void testRegistrarVentaDaPuntos() {
        Venta v = new Venta("CAFETERIA", cliente1);
        v.agregarDetalle(new DetalleVenta(2, 10000, bebida));

        cafe.registrarVenta(v);

        assertTrue(cliente1.getPuntosFidelidad() > 0);
    }

    // =========================
    // TORNEOS
    // =========================

    @Test
    public void testAgregarTorneo() {
        TorneoAmistoso t = new TorneoAmistoso(
                "Copa",
                "LUNES",
                juego1,
                8,
                0.10
        );

        cafe.agregarTorneo(t);

        assertEquals(1, cafe.getTorneos().size());
    }

    @Test
    public void testBuscarTorneoPorNombre() {
        TorneoAmistoso t = new TorneoAmistoso(
                "Copa",
                "LUNES",
                juego1,
                8,
                0.10
        );

        cafe.agregarTorneo(t);

        assertNotNull(cafe.buscarTorneoPorNombre("Copa"));
    }

    @Test
    public void testBuscarTorneoPorNombreNoExiste() {
        assertNull(cafe.buscarTorneoPorNombre("Nada"));
    }

    // =========================
    // PERSISTENCIA
    // =========================

    @Test
    public void testGuardarYCargarEstado() {
        cafe.agregarCliente(cliente1);
        cafe.agregarEmpleado(mesero);

        cafe.guardarEstado("testCafe.dat");

        Cafe cargado = Cafe.cargarEstado("testCafe.dat");

        assertNotNull(cargado);
        assertEquals(1, cargado.getClientes().size());
        assertEquals(1, cargado.getEmpleados().size());
    }

    @Test
    public void testCargarArchivoNoExiste() {
        Cafe cargado = Cafe.cargarEstado("archivo_fake.dat");

        assertNull(cargado);
    }

    // =========================
    // toString
    // =========================

    @Test
    public void testToString() {
        String texto = cafe.toString();

        assertTrue(texto.contains("Cafe"));
    }
}