package test;

import modelo.Cliente;
import modelo.Juego;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ClienteTest {

    private Cliente cliente;
    private Juego juego1;
    private Juego juego2;

    @Before
    public void setUp() {
        cliente = new Cliente("santiago", "123");

        juego1 = new Juego(
                "Catan",
                1995,
                "Kosmos",
                "TABLERO",
                3,
                4,
                10,
                false
        );

        juego2 = new Juego(
                "Uno",
                1971,
                "Mattel",
                "CARTAS",
                2,
                10,
                0,
                false
        );
    }

    // =========================
    // PUNTOS FIDELIDAD
    // =========================

    @Test
    public void testPuntosInicialesEnCero() {
        assertEquals(0, cliente.getPuntosFidelidad());
    }

    @Test
    public void testAgregarPuntos() {
        cliente.agregarPuntos(50);
        assertEquals(50, cliente.getPuntosFidelidad());
    }

    @Test
    public void testAgregarPuntosAcumulados() {
        cliente.agregarPuntos(30);
        cliente.agregarPuntos(20);
        assertEquals(50, cliente.getPuntosFidelidad());
    }

    @Test
    public void testUsarPuntosCorrectamente() {
        cliente.agregarPuntos(100);

        boolean resultado = cliente.usarPuntos(40);

        assertTrue(resultado);
        assertEquals(60, cliente.getPuntosFidelidad());
    }

    @Test
    public void testUsarMasPuntosDeLosDisponibles() {
        cliente.agregarPuntos(20);

        boolean resultado = cliente.usarPuntos(50);

        assertFalse(resultado);
        assertEquals(20, cliente.getPuntosFidelidad());
    }

    // =========================
    // FAVORITOS
    // =========================

    @Test
    public void testFavoritosInicianVacios() {
        assertTrue(cliente.getJuegosFavoritos().isEmpty());
    }

    @Test
    public void testAgregarJuegoFavorito() {
        cliente.agregarJuegoFavorito(juego1);

        assertEquals(1, cliente.getJuegosFavoritos().size());
        assertTrue(cliente.getJuegosFavoritos().contains(juego1));
    }

    @Test
    public void testAgregarDosFavoritos() {
        cliente.agregarJuegoFavorito(juego1);
        cliente.agregarJuegoFavorito(juego2);

        assertEquals(2, cliente.getJuegosFavoritos().size());
    }

    @Test
    public void testNoDuplicarFavorito() {
        cliente.agregarJuegoFavorito(juego1);
        cliente.agregarJuegoFavorito(juego1);

        assertEquals(1, cliente.getJuegosFavoritos().size());
    }

    @Test
    public void testEsFanaticoDeJuegoVerdadero() {
        cliente.agregarJuegoFavorito(juego1);

        assertTrue(cliente.esFanaticoDeJuego(juego1));
    }

    @Test
    public void testEsFanaticoDeJuegoFalso() {
        assertFalse(cliente.esFanaticoDeJuego(juego2));
    }

    // =========================
    // BONOS DESCUENTO
    // =========================

    @Test
    public void testBonosInicianVacios() {
        assertFalse(cliente.tieneBonosDescuento());
        assertEquals(0, cliente.getBonosDescuento().size());
    }

    @Test
    public void testAgregarBono() {
        cliente.agregarBonoDescuento(0.15);

        assertTrue(cliente.tieneBonosDescuento());
        assertEquals(1, cliente.getBonosDescuento().size());
    }

    @Test
    public void testAgregarVariosBonos() {
        cliente.agregarBonoDescuento(0.10);
        cliente.agregarBonoDescuento(0.20);

        assertEquals(2, cliente.getBonosDescuento().size());
    }

    @Test
    public void testUsarPrimerBono() {
        cliente.agregarBonoDescuento(0.10);
        cliente.agregarBonoDescuento(0.25);

        double bono = cliente.usarPrimerBonoDescuento();

        assertEquals(0.10, bono, 0.001);
        assertEquals(1, cliente.getBonosDescuento().size());
    }

    @Test
    public void testUsarPrimerBonoSinBonos() {
        double bono = cliente.usarPrimerBonoDescuento();

        assertEquals(0.0, bono, 0.001);
    }

    @Test
    public void testUsarBonoPorIndice() {
        cliente.agregarBonoDescuento(0.10);
        cliente.agregarBonoDescuento(0.30);

        double bono = cliente.usarBonoDescuento(1);

        assertEquals(0.30, bono, 0.001);
        assertEquals(1, cliente.getBonosDescuento().size());
    }

    @Test
    public void testUsarBonoIndiceInvalido() {
        cliente.agregarBonoDescuento(0.10);

        double bono = cliente.usarBonoDescuento(5);

        assertEquals(0.0, bono, 0.001);
        assertEquals(1, cliente.getBonosDescuento().size());
    }

    // =========================
    // TOSTRING
    // =========================

    @Test
    public void testToStringNoEsNull() {
        assertNotNull(cliente.toString());
    }
}