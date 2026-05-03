package test;

import logica.GestorTorneos;
import logica.GestorInventario;
import logica.GestorUsuarios;
import modelo.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class GestorTorneosTest {

    private Cafe cafe;
    private GestorTorneos gestorTorneos;
    private GestorInventario gestorInventario;
    private GestorUsuarios gestorUsuarios;

    private Juego juegoBase;
    private Juego juegoGrande;
    private Cliente cliente1;
    private Cliente cliente2;
    private Cliente clienteFanatico;
    private Empleado empleadoLibre;
    private Empleado empleadoEnTurno;

    @Before
    public void setUp() {
        cafe = new Cafe(50);
        gestorTorneos    = new GestorTorneos(cafe);
        gestorInventario = new GestorInventario(cafe);
        gestorUsuarios   = new GestorUsuarios(cafe);

        juegoBase   = new Juego("Catan", 1995, "Kosmos", "TABLERO", 3, 4, 10, false);
        juegoGrande = new Juego("Uno",   1971, "Mattel", "CARTAS",  2, 10, 0,  false);

        gestorInventario.agregarJuegoAPrestamo(juegoBase,   5);
        gestorInventario.agregarJuegoAPrestamo(juegoGrande, 5);

        cliente1        = gestorUsuarios.registrarCliente("cliente1", "pass");
        cliente2        = gestorUsuarios.registrarCliente("cliente2", "pass");
        clienteFanatico = gestorUsuarios.registrarCliente("fanatico", "pass");
        clienteFanatico.agregarJuegoFavorito(juegoBase);

        empleadoLibre   = gestorUsuarios.registrarEmpleado("empLibre", "pass", "SABADO", "MESERO");
        empleadoEnTurno = gestorUsuarios.registrarEmpleado("empTurno", "pass", "LUNES",  "MESERO");
    }

    // RF-1: Crear torneos
    @Test
    public void testCrearTorneoAmistoso_exitoso() {
        TorneoAmistoso t = gestorTorneos.crearTorneoAmistoso("Copa Amigos", "LUNES", juegoBase, 8, 0.15);
        assertNotNull(t);
        assertEquals("Copa Amigos", t.getNombre());
        assertEquals("LUNES", t.getDiaSemana());
        assertTrue(t.esAmistoso());
        assertEquals(8, t.getNumParticipantes());
        assertEquals(0.15, t.getPorcentajeDescuento(), 0.001);
    }

    @Test
    public void testCrearTorneoCompetitivo_exitoso() {
        TorneoCompetitivo t = gestorTorneos.crearTorneoCompetitivo("Liga Pro", "MARTES", juegoGrande, 10, 20000);
        assertNotNull(t);
        assertFalse(t.esAmistoso());
        assertEquals(20000, t.getTarifaEntrada(), 0.001);
    }

    @Test
    public void testCrearTorneo_nombreDuplicado_falla() {
        gestorTorneos.crearTorneoAmistoso("Copa Amigos", "LUNES", juegoBase, 4, 0.10);
        assertNull(gestorTorneos.crearTorneoAmistoso("Copa Amigos", "MIERCOLES", juegoGrande, 5, 0.10));
    }

    @Test
    public void testCrearTorneo_sinInventario_falla() {
        Juego sinStock = new Juego("Raro", 2020, "X", "TABLERO", 2, 4, 0, false);
        assertNull(gestorTorneos.crearTorneoAmistoso("Sin Stock", "LUNES", sinStock, 4, 0.10));
    }

    @Test
    public void testCrearTorneo_participantesMayorQueMaxJugadores_conInventarioSuficiente() {
        assertNotNull(gestorTorneos.crearTorneoAmistoso("Torneo Grande", "VIERNES", juegoBase, 12, 0.10));
    }

    @Test
    public void testCrearTorneo_tarifaEntradaCero_falla() {
        assertNull(gestorTorneos.crearTorneoCompetitivo("Mal Torneo", "LUNES", juegoGrande, 8, 0));
    }

    // RF-2: Cupos de fanáticos
    @Test
    public void testCuposFanaticos_calculoCorrecto() {
        TorneoAmistoso t = gestorTorneos.crearTorneoAmistoso("T1", "LUNES", juegoBase, 10, 0.10);
        assertNotNull(t);
        assertEquals(2, t.getCuposFanaticos());
    }

    @Test
    public void testCuposFanaticos_redondeoHaciaArriba() {
        TorneoAmistoso t = gestorTorneos.crearTorneoAmistoso("T2", "LUNES", juegoBase, 8, 0.10);
        assertNotNull(t);
        assertEquals(2, t.getCuposFanaticos());
    }

    @Test
    public void testCuposFanaticos_fanaticoPrimeroUsaCupoReservado() {
        TorneoAmistoso t = gestorTorneos.crearTorneoAmistoso("T3", "LUNES", juegoBase, 10, 0.10);
        assertNotNull(t);
        assertTrue(gestorTorneos.inscribirCliente(t, clienteFanatico, 1));
        assertEquals(1, t.getCuposFanaticosOcupados());
        assertEquals(0, t.getCuposOcupados());
    }

    @Test
    public void testCuposFanaticos_cuandoSeAgotan_fanaticTomaRegular() {
        TorneoAmistoso t = gestorTorneos.crearTorneoAmistoso("T4", "LUNES", juegoBase, 5, 0.10);
        assertNotNull(t);
        assertEquals(1, t.getCuposFanaticos());
        assertTrue(gestorTorneos.inscribirCliente(t, clienteFanatico, 2));
        assertEquals(1, t.getCuposFanaticosOcupados());
        assertEquals(1, t.getCuposOcupados());
    }

    @Test
    public void testCuposNoFanatico_soloTomaCuposRegulares() {
        TorneoAmistoso t = gestorTorneos.crearTorneoAmistoso("T5", "LUNES", juegoBase, 10, 0.10);
        assertNotNull(t);
        gestorTorneos.inscribirCliente(t, cliente1, 2);
        assertEquals(0, t.getCuposFanaticosOcupados());
        assertEquals(2, t.getCuposOcupados());
    }

    // RF-3: Inscripción de clientes
    @Test
    public void testInscribirCliente_exitoso() {
        TorneoAmistoso t = gestorTorneos.crearTorneoAmistoso("T6", "LUNES", juegoBase, 10, 0.10);
        assertNotNull(t);
        assertTrue(gestorTorneos.inscribirCliente(t, cliente1, 2));
        assertTrue(t.yaInscrito(cliente1));
    }

    @Test
    public void testInscribirCliente_masDe3Cupos_falla() {
        TorneoAmistoso t = gestorTorneos.crearTorneoAmistoso("T7", "LUNES", juegoBase, 10, 0.10);
        assertNotNull(t);
        assertFalse(gestorTorneos.inscribirCliente(t, cliente1, 4));
    }

    @Test
    public void testInscribirCliente_yaInscrito_falla() {
        TorneoAmistoso t = gestorTorneos.crearTorneoAmistoso("T8", "LUNES", juegoBase, 10, 0.10);
        assertNotNull(t);
        gestorTorneos.inscribirCliente(t, cliente1, 1);
        assertFalse(gestorTorneos.inscribirCliente(t, cliente1, 1));
    }

    @Test
    public void testInscribirCliente_sinCuposDisponibles_falla() {
        TorneoAmistoso t = gestorTorneos.crearTorneoAmistoso("T9", "LUNES", juegoBase, 2, 0.10);
        assertNotNull(t);
        gestorTorneos.inscribirCliente(t, cliente1, 2);
        assertFalse(gestorTorneos.inscribirCliente(t, cliente2, 1));
    }

    // RF-4: Desinscripción
    @Test
    public void testDesinscribirCliente_liberaTodosLosCupos() {
        TorneoAmistoso t = gestorTorneos.crearTorneoAmistoso("T10", "LUNES", juegoBase, 10, 0.10);
        assertNotNull(t);
        gestorTorneos.inscribirCliente(t, cliente1, 3);
        assertTrue(gestorTorneos.desinscribirCliente(t, cliente1));
        assertFalse(t.yaInscrito(cliente1));
        assertEquals(0, t.getCuposOcupados());
    }

    @Test
    public void testDesinscribirCliente_noInscrito_falla() {
        TorneoAmistoso t = gestorTorneos.crearTorneoAmistoso("T11", "LUNES", juegoBase, 10, 0.10);
        assertNotNull(t);
        assertFalse(gestorTorneos.desinscribirCliente(t, cliente1));
    }

    @Test
    public void testDesinscripcion_fanatico_liberaCuposFanaticos() {
        TorneoAmistoso t = gestorTorneos.crearTorneoAmistoso("T12", "LUNES", juegoBase, 10, 0.10);
        assertNotNull(t);
        gestorTorneos.inscribirCliente(t, clienteFanatico, 1);
        gestorTorneos.desinscribirCliente(t, clienteFanatico);
        assertEquals(0, t.getCuposFanaticosOcupados());
    }

    // RF-5: Inscripción de empleados
    @Test
    public void testInscribirEmpleado_libre_exitoso() {
        TorneoAmistoso t = gestorTorneos.crearTorneoAmistoso("T13", "LUNES", juegoBase, 10, 0.10);
        assertNotNull(t);
        assertTrue(gestorTorneos.inscribirEmpleado(t, empleadoLibre, 1));
        assertTrue(t.yaInscrito(empleadoLibre));
    }

    @Test
    public void testInscribirEmpleado_conTurno_falla() {
        TorneoAmistoso t = gestorTorneos.crearTorneoAmistoso("T14", "LUNES", juegoBase, 10, 0.10);
        assertNotNull(t);
        assertFalse(gestorTorneos.inscribirEmpleado(t, empleadoEnTurno, 1));
    }

    @Test
    public void testInscribirEmpleado_diaDiferente_exitoso() {
        TorneoAmistoso t = gestorTorneos.crearTorneoAmistoso("T15", "MARTES", juegoBase, 10, 0.10);
        assertNotNull(t);
        assertTrue(gestorTorneos.inscribirEmpleado(t, empleadoEnTurno, 1));
    }

    // RF-6: Premio amistoso
    @Test
    public void testEntregarBonoAmistoso_asignaCorrectamente() {
        TorneoAmistoso t = gestorTorneos.crearTorneoAmistoso("T17", "LUNES", juegoBase, 10, 0.20);
        assertNotNull(t);
        gestorTorneos.inscribirCliente(t, cliente1, 1);
        assertTrue(gestorTorneos.entregarBonoAmistoso(t, cliente1));
        assertTrue(cliente1.tieneBonosDescuento());
        assertEquals(0.20, cliente1.getBonosDescuento().get(0), 0.001);
    }

    @Test
    public void testEntregarBonoAmistoso_acumulaBonosDistintosTorneos() {
        TorneoAmistoso t1 = gestorTorneos.crearTorneoAmistoso("T18a", "LUNES",  juegoBase, 10, 0.15);
        TorneoAmistoso t2 = gestorTorneos.crearTorneoAmistoso("T18b", "MARTES", juegoBase, 10, 0.30);
        assertNotNull(t1);
        assertNotNull(t2);
        gestorTorneos.inscribirCliente(t1, cliente1, 1);
        gestorTorneos.inscribirCliente(t2, cliente1, 1);
        gestorTorneos.entregarBonoAmistoso(t1, cliente1);
        gestorTorneos.entregarBonoAmistoso(t2, cliente1);
        assertEquals(2, cliente1.getBonosDescuento().size());
        assertEquals(0.15, cliente1.getBonosDescuento().get(0), 0.001);
        assertEquals(0.30, cliente1.getBonosDescuento().get(1), 0.001);
    }

    @Test
    public void testUsarBonoDescuento_loConsume() {
        TorneoAmistoso t = gestorTorneos.crearTorneoAmistoso("T19", "LUNES", juegoBase, 10, 0.10);
        assertNotNull(t);
        gestorTorneos.inscribirCliente(t, cliente1, 1);
        gestorTorneos.entregarBonoAmistoso(t, cliente1);
        double bono = cliente1.usarPrimerBonoDescuento();
        assertEquals(0.10, bono, 0.001);
        assertFalse(cliente1.tieneBonosDescuento());
    }

    @Test
    public void testEntregarBono_clienteNoInscrito_falla() {
        TorneoAmistoso t = gestorTorneos.crearTorneoAmistoso("T20", "LUNES", juegoBase, 10, 0.10);
        assertNotNull(t);
        assertFalse(gestorTorneos.entregarBonoAmistoso(t, cliente1));
    }

    // RF-7: Premio competitivo
    @Test
    public void testPremioCompetitivo_calculoCorrectoConClientes() {
        TorneoCompetitivo t = gestorTorneos.crearTorneoCompetitivo("T21", "VIERNES", juegoGrande, 10, 15000);
        assertNotNull(t);
        gestorTorneos.inscribirCliente(t, cliente1, 2);
        gestorTorneos.inscribirCliente(t, cliente2, 1);
        t.calcularPremio();
        assertEquals(45000.0, t.getPremioMetalico(), 0.001);
    }

    @Test
    public void testPremioCompetitivo_empleadoNoRecibePremio() {
        TorneoCompetitivo t = gestorTorneos.crearTorneoCompetitivo("T22", "VIERNES", juegoGrande, 10, 15000);
        assertNotNull(t);
        gestorTorneos.inscribirEmpleado(t, empleadoLibre, 1);
        assertEquals(0.0, gestorTorneos.calcularPremioCompetitivo(t, empleadoLibre), 0.001);
    }

    @Test
    public void testPremioCompetitivo_empleadoNoContaEnRecaudado() {
        TorneoCompetitivo t = gestorTorneos.crearTorneoCompetitivo("T23", "VIERNES", juegoGrande, 10, 10000);
        assertNotNull(t);
        gestorTorneos.inscribirCliente(t, cliente1, 1);
        gestorTorneos.inscribirEmpleado(t, empleadoLibre, 1);
        t.calcularPremio();
        assertEquals(10000.0, t.getPremioMetalico(), 0.001);
    }

    // RF-8: Múltiples torneos
    @Test
    public void testMultiplesTorneos_simultaneos() {
        assertNotNull(gestorTorneos.crearTorneoAmistoso("Copa Lunes",    "LUNES",   juegoBase,   10, 0.10));
        assertNotNull(gestorTorneos.crearTorneoCompetitivo("Liga Martes","MARTES",  juegoGrande,  8, 5000));
        assertNotNull(gestorTorneos.crearTorneoAmistoso("Amigos Viernes","VIERNES", juegoBase,    4, 0.15));
        assertEquals(3, cafe.getTorneos().size());
    }

    @Test
    public void testCliente_puedeInscribirseEnVariosTorneos() {
        TorneoAmistoso t1 = gestorTorneos.crearTorneoAmistoso("Copa1", "LUNES",  juegoBase,   10, 0.10);
        TorneoAmistoso t2 = gestorTorneos.crearTorneoAmistoso("Copa2", "MARTES", juegoGrande, 10, 0.10);
        assertNotNull(t1);
        assertNotNull(t2);
        assertTrue(gestorTorneos.inscribirCliente(t1, cliente1, 1));
        assertTrue(gestorTorneos.inscribirCliente(t2, cliente1, 2));
        assertTrue(t1.yaInscrito(cliente1));
        assertTrue(t2.yaInscrito(cliente1));
    }

    // RF-9: Verificar turno empleado
    @Test
    public void testEmpleadoTieneTurnoEnDia_mismo_dia() {
        assertTrue(gestorTorneos.empleadoTieneTurnoEnDia(empleadoEnTurno, "LUNES"));
    }

    @Test
    public void testEmpleadoTieneTurnoEnDia_dia_diferente() {
        assertFalse(gestorTorneos.empleadoTieneTurnoEnDia(empleadoEnTurno, "MARTES"));
    }

    @Test
    public void testEmpleadoLibre_no_tiene_turno_en_dia_torneo() {
        assertFalse(gestorTorneos.empleadoTieneTurnoEnDia(empleadoLibre, "LUNES"));
    }

    // RF-10: Cupos totales
    @Test
    public void testCuposTotalesDisponibles_seReducenAlInscribir() {
        TorneoAmistoso t = gestorTorneos.crearTorneoAmistoso("T30", "LUNES", juegoBase, 8, 0.10);
        assertNotNull(t);
        assertEquals(8, t.cuposTotalesDisponibles());
        gestorTorneos.inscribirCliente(t, cliente1, 3);
        assertEquals(5, t.cuposTotalesDisponibles());
        gestorTorneos.inscribirCliente(t, cliente2, 2);
        assertEquals(3, t.cuposTotalesDisponibles());
    }

    @Test
    public void testDesinscripcion_restauraCuposDisponibles() {
        TorneoAmistoso t = gestorTorneos.crearTorneoAmistoso("T31", "LUNES", juegoBase, 8, 0.10);
        assertNotNull(t);
        gestorTorneos.inscribirCliente(t, cliente1, 3);
        gestorTorneos.desinscribirCliente(t, cliente1);
        assertEquals(8, t.cuposTotalesDisponibles());
    }
}