package test;

import logica.GestorPrestamos;
import modelo.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GestorPrestamosTest {

    private Cafe cafe;
    private GestorPrestamos gestor;

    private Cliente cliente;
    private Juego juegoNormal;
    private Juego juegoAccion;
    private Juego juegoAdultos;
    private Juego juegoDificil;

    @Before
    public void setUp() {
        cafe = new Cafe(10);
        gestor = new GestorPrestamos(cafe);

        cliente = new Cliente("cliente1", "123");

        juegoNormal = new Juego("Uno", 2000, "Mattel", "CARTAS", 2, 10, 0, false);
        juegoAccion = new Juego("Nerf Game", 2020, "Hasbro", "ACCION", 2, 6, 0, false);
        juegoAdultos = new Juego("Poker Pro", 2019, "Casino", "CARTAS", 2, 6, 18, false);
        juegoDificil = new Juego("Ajedrez Master", 1990, "Classic", "ESTRATEGIA", 2, 2, 0, true);

        cafe.agregarInventarioJuego(new InventarioJuegos(juegoNormal, 5));
        cafe.agregarInventarioJuego(new InventarioJuegos(juegoAccion, 5));
        cafe.agregarInventarioJuego(new InventarioJuegos(juegoAdultos, 5));
        cafe.agregarInventarioJuego(new InventarioJuegos(juegoDificil, 5));
    }

    @Test
    public void testCrearMesa_exitoso() {
        Mesa mesa = gestor.crearMesa(4, false, false, cliente);

        assertNotNull(mesa);
        assertEquals(1, cafe.getMesas().size());
    }

    @Test
    public void testCrearMesa_sinCapacidad() {
        gestor.crearMesa(8, false, false, cliente);
        Mesa mesa2 = gestor.crearMesa(5, false, false, cliente);

        assertNull(mesa2);
    }

    @Test
    public void testIniciarPrestamo() {
        Mesa mesa = gestor.crearMesa(4, false, false, cliente);
        Prestamo p = gestor.iniciarPrestamo(mesa);

        assertNotNull(p);
        assertEquals("ACTIVO", p.getEstado());
    }

    @Test
    public void testAgregarJuegoAPrestamo_exitoso() {
        Mesa mesa = gestor.crearMesa(4, false, false, cliente);
        Prestamo p = gestor.iniciarPrestamo(mesa);

        assertTrue(gestor.agregarJuegoAPrestamo(p, juegoNormal));
        assertEquals(1, p.getJuegos().size());
    }

    @Test
    public void testAgregarMasDeDosJuegos_falla() {
        Mesa mesa = gestor.crearMesa(4, false, false, cliente);
        Prestamo p = gestor.iniciarPrestamo(mesa);

        gestor.agregarJuegoAPrestamo(p, juegoNormal);
        gestor.agregarJuegoAPrestamo(p, juegoAccion);

        assertFalse(gestor.agregarJuegoAPrestamo(p, juegoDificil));
    }

    @Test
    public void testJuegoNoAptoPorEdad() {
        Mesa mesa = gestor.crearMesa(4, false, true, cliente);
        Prestamo p = gestor.iniciarPrestamo(mesa);

        assertFalse(gestor.agregarJuegoAPrestamo(p, juegoAdultos));
    }

    @Test
    public void testJuegoAccionConBebidaCaliente_falla() {
        Mesa mesa = gestor.crearMesa(4, false, false, cliente);
        mesa.setTieneBebidaCaliente(true);

        Prestamo p = gestor.iniciarPrestamo(mesa);

        assertFalse(gestor.agregarJuegoAPrestamo(p, juegoAccion));
    }

    @Test
    public void testCerrarPrestamo() {
        Mesa mesa = gestor.crearMesa(4, false, false, cliente);
        Prestamo p = gestor.iniciarPrestamo(mesa);

        gestor.agregarJuegoAPrestamo(p, juegoNormal);
        gestor.cerrarPrestamo(p);

        assertEquals("CERRADO", p.getEstado());
        assertEquals(0, cafe.getMesas().size());
    }

    @Test
    public void testPrestamoEmpleadoLibre() {
        Mesero mesero = new Mesero("mesero1", "123", "LUNES");
        mesero.setEnTurno(false);

        Prestamo p = gestor.iniciarPrestamoEmpleado(mesero);

        assertNotNull(p);
    }

    @Test
    public void testPrestamoEmpleadoEnTurno_falla() {
        Mesero mesero = new Mesero("mesero1", "123", "LUNES");
        mesero.setEnTurno(true);

        Prestamo p = gestor.iniciarPrestamoEmpleado(mesero);

        assertNull(p);
    }
}