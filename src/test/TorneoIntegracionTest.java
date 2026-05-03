package test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import logica.*;
import modelo.*;

public class TorneoIntegracionTest {

    private Cafe cafe;
    private GestorTorneos gestorTorneos;
    private GestorUsuarios gestorUsuarios;
    private GestorInventario gestorInventario;
    private Juego juego;
    private Cliente clienteFanatico;
    private Cliente clienteNormal;
    private Empleado empleadoLibre;

    @Before
    public void setUp() {
        cafe = new Cafe(50);
        gestorTorneos = new GestorTorneos(cafe);
        gestorUsuarios = new GestorUsuarios(cafe);
        gestorInventario = new GestorInventario(cafe);

        juego = new Juego("Catan", 1995, "Kosmos", "TABLERO", 3, 4, 10, false);
        gestorInventario.agregarJuegoAPrestamo(juego, 5);

        clienteFanatico = gestorUsuarios.registrarCliente("fan", "pass");
        clienteFanatico.agregarJuegoFavorito(juego);
        clienteNormal = gestorUsuarios.registrarCliente("normal", "pass");
        empleadoLibre = gestorUsuarios.registrarEmpleado("emp", "pass", "SABADO", "MESERO");
    }

    // Historia: Admin crea torneo amistoso, fanático se inscribe con cupo reservado, 
    // se entrega bono al ganador.
    @Test
    public void testHistoriaCompletaTorneoAmistoso() {
        TorneoAmistoso t = gestorTorneos.crearTorneoAmistoso("Torneo Test", "LUNES", juego, 10, 0.15);
        assertNotNull(t);

        // Fanático inscrito (debe tomar cupo fanático)
        assertTrue(gestorTorneos.inscribirCliente(t, clienteFanatico, 1));
        assertEquals(1, t.getCuposFanaticosOcupados());
        assertEquals(0, t.getCuposOcupados());

        // Cliente normal inscrito (cupo regular)
        assertTrue(gestorTorneos.inscribirCliente(t, clienteNormal, 2));
        assertEquals(2, t.getCuposOcupados());

        // Entrega premio al fanático
        assertTrue(gestorTorneos.entregarBonoAmistoso(t, clienteFanatico));
        assertTrue(clienteFanatico.tieneBonosDescuento());
        assertEquals(0.15, clienteFanatico.getBonosDescuento().get(0), 0.001);
    }

    // Historia: Torneo competitivo con clientes y empleado (empleado no paga ni recibe premio)
    @Test
    public void testHistoriaTorneoCompetitivoConEmpleado() {
        TorneoCompetitivo t = gestorTorneos.crearTorneoCompetitivo("Liga", "VIERNES", juego, 8, 10000);
        assertNotNull(t);

        // Empleado sin turno se inscribe gratis
        assertTrue(gestorTorneos.inscribirEmpleado(t, empleadoLibre, 1));
        // Cliente normal paga
        assertTrue(gestorTorneos.inscribirCliente(t, clienteNormal, 2));

        t.calcularPremio();
        // Solo los cupos del cliente cuentan para el premio: 2 * 10000 = 20000
        assertEquals(20000.0, t.getPremioMetalico(), 0.001);

        // El empleado no recibe premio en metálico
        assertEquals(0.0, gestorTorneos.calcularPremioCompetitivo(t, empleadoLibre), 0.001);
        // El cliente sí recibe el premio calculado
        assertEquals(20000.0, gestorTorneos.calcularPremioCompetitivo(t, clienteNormal), 0.001);
    }

    // Historia: Inscripción y desinscripción libera cupos correctamente
    @Test
    public void testInscripcionYDesinscripcionLiberaCupos() {
        TorneoAmistoso t = gestorTorneos.crearTorneoAmistoso("Desinscripcion", "MARTES", juego, 6, 0.10);
        assertNotNull(t);
        assertEquals(6, t.cuposTotalesDisponibles());

        gestorTorneos.inscribirCliente(t, clienteNormal, 3);
        assertEquals(3, t.cuposTotalesDisponibles());

        gestorTorneos.desinscribirCliente(t, clienteNormal);
        assertEquals(6, t.cuposTotalesDisponibles());
        assertFalse(t.yaInscrito(clienteNormal));
    }
}