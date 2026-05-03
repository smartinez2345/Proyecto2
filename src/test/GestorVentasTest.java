package test;

import logica.GestorVentas;
import modelo.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GestorVentasTest {

    private Cafe cafe;
    private GestorVentas gestor;

    private Cliente cliente;
    private Bebida cerveza;
    private Bebida cafeCaliente;
    private Pasteleria torta;
    private Juego juego;

    @Before
    public void setUp() {
        cafe = new Cafe(20);
        gestor = new GestorVentas(cafe);

        cliente = new Cliente("cliente1", "123");

        cerveza = new Bebida("Cerveza", 10000, true, false);
        cafeCaliente = new Bebida("Cafe", 8000, false, true);

        torta = new Pasteleria("Torta", 12000);
        torta.agregarAlergeno("Gluten");

        juego = new Juego("Catan", 1995, "Kosmos", "TABLERO", 3, 4, 10, false);

        cafe.agregarInventarioCafeteria(new InventarioCafeteria(cerveza, 10));
        cafe.agregarInventarioCafeteria(new InventarioCafeteria(cafeCaliente, 10));
        cafe.agregarInventarioCafeteria(new InventarioCafeteria(torta, 10));
    }

    @Test
    public void testIniciarVentaCafeteria() {
        Venta v = gestor.iniciarVentaCafeteria(cliente);

        assertNotNull(v);
        assertEquals("CAFETERIA", v.getTipoVenta());
    }

    @Test
    public void testIniciarVentaJuegos() {
        Venta v = gestor.iniciarVentaJuegos(cliente);

        assertNotNull(v);
        assertEquals("JUEGOS", v.getTipoVenta());
    }

    @Test
    public void testAgregarProducto_exitoso() {
        Venta v = gestor.iniciarVentaCafeteria(cliente);

        assertTrue(gestor.agregarProductoAVenta(v, torta, 2, null));
        assertEquals(1, v.getDetalles().size());
    }

    @Test
    public void testAgregarProducto_stockInsuficiente() {
        Venta v = gestor.iniciarVentaCafeteria(cliente);

        assertFalse(gestor.agregarProductoAVenta(v, torta, 50, null));
    }

    @Test
    public void testNoVenderAlcoholConMenores() {
        Venta v = gestor.iniciarVentaCafeteria(cliente);
        Mesa mesa = new Mesa(4, false, true, cliente);

        assertFalse(gestor.agregarProductoAVenta(v, cerveza, 1, mesa));
    }

    @Test
    public void testBebidaCalienteMarcaMesa() {
        Venta v = gestor.iniciarVentaCafeteria(cliente);
        Mesa mesa = new Mesa(2, false, false, cliente);

        assertTrue(gestor.agregarProductoAVenta(v, cafeCaliente, 1, mesa));
        assertTrue(mesa.tieneBebidaCaliente());
    }

    @Test
    public void testAgregarJuegoAVenta() {
        Venta v = gestor.iniciarVentaJuegos(cliente);

        assertTrue(gestor.agregarJuegoAVenta(v, juego, 1, 90000));
        assertEquals(1, v.getDetalles().size());
    }

    @Test
    public void testAplicarDescuentoEmpleado() {
        Venta v = gestor.iniciarVentaCafeteria(cliente);
        gestor.agregarProductoAVenta(v, torta, 1, null);

        gestor.aplicarDescuentoCodigo(v, true);

        assertEquals(0.20, v.getDescuento(), 0.001);
    }

    @Test
    public void testAplicarDescuentoNormal() {
        Venta v = gestor.iniciarVentaCafeteria(cliente);
        gestor.agregarProductoAVenta(v, torta, 1, null);

        gestor.aplicarDescuentoCodigo(v, false);

        assertEquals(0.10, v.getDescuento(), 0.001);
    }

    @Test
    public void testUsarPuntosFidelidad() {
        cliente.agregarPuntos(100);

        Venta v = gestor.iniciarVentaCafeteria(cliente);
        gestor.usarPuntosFidelidad(v, cliente, 50);

        assertEquals(50, cliente.getPuntosFidelidad());
    }

    @Test
    public void testFinalizarVenta_registraVenta() {
        Venta v = gestor.iniciarVentaCafeteria(cliente);
        gestor.agregarProductoAVenta(v, torta, 1, null);

        gestor.finalizarVenta(v);

        assertEquals(1, cafe.getVentas().size());
    }

    @Test
    public void testVentaCafeteriaTienePropina() {
        Venta v = gestor.iniciarVentaCafeteria(cliente);
        gestor.agregarProductoAVenta(v, torta, 1, null);

        gestor.finalizarVenta(v);

        assertTrue(v.getPropina() > 0);
    }

    @Test
    public void testVentaJuegosNoTienePropina() {
        Venta v = gestor.iniciarVentaJuegos(cliente);
        gestor.agregarJuegoAVenta(v, juego, 1, 100000);

        gestor.finalizarVenta(v);

        assertEquals(0, v.getPropina(), 0.001);
    }

    @Test
    public void testClienteGanaPuntosAlFinalizarVenta() {
        Venta v = gestor.iniciarVentaCafeteria(cliente);
        gestor.agregarProductoAVenta(v, torta, 1, null);

        gestor.finalizarVenta(v);

        assertTrue(cliente.getPuntosFidelidad() > 0);
    }
}