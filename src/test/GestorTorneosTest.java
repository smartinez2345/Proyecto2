package test;

import logica.GestorTorneos;
import logica.GestorInventario;
import logica.GestorUsuarios;
import modelo.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Pruebas unitarias e integración para los torneos del Proyecto #2.
 * Cubre todos los requerimientos funcionales.
 */
public class GestorTorneosTest {

    private Cafe cafe;
    private GestorTorneos gestorTorneos;
    private GestorInventario gestorInventario;
    private GestorUsuarios gestorUsuarios;

    // Datos de prueba reutilizables
    private Juego juegoBase;       // max 4 jugadores
    private Juego juegoGrande;     // max 10 jugadores
    private Cliente cliente1;
    private Cliente cliente2;
    private Cliente clienteFanatico;
    private Empleado empleadoLibre;
    private Empleado empleadoEnTurno;

    @Before
    public void setUp() {
        cafe = new Cafe(50);
        gestorTorneos = new GestorTorneos(cafe);
        gestorInventario = new GestorInventario(cafe);
        gestorUsuarios = new GestorUsuarios(cafe);

        // Juegos
        juegoBase  = new Juego("Catan", 1995, "Kosmos", "TABLERO", 3, 4, 10, false);
        juegoGrande = new Juego("Uno",  1971, "Mattel", "CARTAS",  2, 10, 0, false);

        // Inventario: 5 copias de cada uno
        gestorInventario.agregarJuegoAPrestamo(juegoBase,   5);
        gestorInventario.agregarJuegoAPrestamo(juegoGrande, 5);

        // Clientes
        cliente1        = gestorUsuarios.registrarCliente("cliente1", "pass");
        cliente2        = gestorUsuarios.registrarCliente("cliente2", "pass");
        clienteFanatico = gestorUsuarios.registrarCliente("fanatico", "pass");
        clienteFanatico.agregarJuegoFavorito(juegoBase);

        // Empleado sin turno el día del torneo (turno: SABADO)
        empleadoLibre   = gestorUsuarios.registrarEmpleado("empLibre", "pass", "SABADO", "MESERO");
        // Empleado con turno el mismo día del torneo (LUNES)
        empleadoEnTurno = gestorUsuarios.registrarEmpleado("empTurno", "pass", "LUNES",  "MESERO");
    }

    // ══════════════════════════════════════════════════════════════════
    //  RF-1: Crear torneo amistoso
    // ══════════════════════════════════════════════════════════════════

    @Test
    public void testCrearTorneoAmistoso_exitoso() {
        TorneoAmistoso t = gestorTorneos.crearTorneoAmistoso(
            "Copa Amigos", "LUNES", juegoBase, 8, 0.15);
        assertNotNull("El torneo amistoso debe crearse correctamente", t);
        assertEquals("Copa Amigos", t.getNombre());
        assertEquals("LUNES", t.getDiaSemana());
        assertTrue(t.esAmistoso());
        assertEquals(8, t.getNumParticipantes());
        assertEquals(0.15, t.getPorcentajeDescuento(), 0.001);
    }

    @Test
    public void testCrearTorneoCompetitivo_exitoso() {
        TorneoCompetitivo t = gestorTorneos.crearTorneoCompetitivo(
            "Liga Pro", "MARTES", juegoGrande, 10, 20000);
        assertNotNull(t);
        assertFalse(t.esAmistoso());
        assertEquals(20000, t.getTarifaEntrada(), 0.001);
    }

    @Test
    public void testCrearTorneo_nombreDuplicado_falla() {
        gestorTorneos.crearTorneoAmistoso("Copa Amigos", "LUNES", juegoBase, 4, 0.10);
        TorneoAmistoso duplicado = gestorTorneos.crearTorneoAmistoso(
            "Copa Amigos", "MIERCOLES", juegoGrande, 5, 0.10);
        assertNull("No debe permitir nombre duplicado", duplicado);
    }

    @Test
    public void testCrearTorneo_sinInventario_falla() {
        Juego sinStock = new Juego("Raro", 2020, "X", "TABLERO", 2, 4, 0, false);
        // Sin agregar al inventario
        TorneoAmistoso t = gestorTorneos.crearTorneoAmistoso(
            "Sin Stock", "LUNES", sinStock, 4, 0.10);
        assertNull("No debe crearse torneo sin inventario", t);
    }

    @Test
    public void testCrearTorneo_participantesMayorQueMaxJugadores_conInventarioSuficiente() {
        // juegoBase max=4, con 5 copias podemos hacer torneo de hasta 20 participantes
        TorneoAmistoso t = gestorTorneos.crearTorneoAmistoso(
            "Torneo Grande", "VIERNES", juegoBase, 12, 0.10);
        assertNotNull("Debe crearse si hay copias suficientes (ceil(12/4)=3 <= 5 copias)", t);
    }

    @Test
    public void testCrearTorneo_tarifaEntradaCero_falla() {
        TorneoCompetitivo t = gestorTorneos.crearTorneoCompetitivo(
            "Mal Torneo", "LUNES", juegoGrande, 8, 0);
        assertNull("Tarifa de entrada debe ser mayor a 0", t);
    }

    // ══════════════════════════════════════════════════════════════════
    //  RF-2: Cupos de fanáticos (20% redondeado arriba)
    // ══════════════════════════════════════════════════════════════════

    @Test
    public void testCuposFanaticos_calculoCorrecto() {
        // 10 participantes → ceil(10*0.20) = 2 cupos fanáticos
        TorneoAmistoso t = gestorTorneos.crearTorneoAmistoso(
            "T1", "LUNES", juegoBase, 10, 0.10);
        assertNotNull(t);
        assertEquals("10 participantes → 2 cupos fanáticos", 2, t.getCuposFanaticos());
    }

    @Test
    public void testCuposFanaticos_redondeoHaciaArriba() {
        // 8 participantes → ceil(8*0.20) = ceil(1.6) = 2
        TorneoAmistoso t = gestorTorneos.crearTorneoAmistoso(
            "T2", "LUNES", juegoBase, 8, 0.10);
        assertNotNull(t);
        assertEquals("ceil(1.6) = 2 cupos fanáticos", 2, t.getCuposFanaticos());
    }

    @Test
    public void testCuposFanaticos_fanaticoPrimeroUsaCupoReservado() {
        TorneoAmistoso t = gestorTorneos.crearTorneoAmistoso(
            "T3", "LUNES", juegoBase, 10, 0.10); // 2 cupos fanáticos
        assertNotNull(t);

        boolean inscrito = gestorTorneos.inscribirCliente(t, clienteFanatico, 1);
        assertTrue(inscrito);
        assertEquals("El fanático debe ocupar un cupo fanático", 1, t.getCuposFanaticosOcupados());
        assertEquals("Cupos regulares no deben reducirse", 0, t.getCuposOcupados());
    }

    @Test
    public void testCuposFanaticos_cuandoSeAgotan_fanaticTomaRegular() {
        // 5 participantes → ceil(1.0) = 1 cupo fanático
        TorneoAmistoso t = gestorTorneos.crearTorneoAmistoso(
            "T4", "LUNES", juegoBase, 5, 0.10);
        assertNotNull(t);
        assertEquals(1, t.getCuposFanaticos());

        // Fanático pide 2 cupos: 1 va a zona fanáticos, 1 a regular
        boolean inscrito = gestorTorneos.inscribirCliente(t, clienteFanatico, 2);
        assertTrue(inscrito);
        assertEquals(1, t.getCuposFanaticosOcupados());
        assertEquals(1, t.getCuposOcupados());
    }

    @Test
    public void testCuposNoFanatico_soloTomaCuposRegulares() {
        TorneoAmistoso t = gestorTorneos.crearTorneoAmistoso(
            "T5", "LUNES", juegoBase, 10, 0.10); // 2 cupos fanáticos reservados
        assertNotNull(t);

        gestorTorneos.inscribirCliente(t, cliente1, 2);
        assertEquals("Cliente no fanático no debe tocar cupos fanáticos",
            0, t.getCuposFanaticosOcupados());
        assertEquals(2, t.getCuposOcupados());
    }

    // ══════════════════════════════════════════════════════════════════
    //  RF-3: Inscripción de clientes (máx 3 cupos)
    // ══════════════════════════════════════════════════════════════════

    @Test
    public void testInscribirCliente_exitoso() {
        TorneoAmistoso t = gestorTorneos.crearTorneoAmistoso(
            "T6", "LUNES", juegoBase, 10, 0.10);
        assertNotNull(t);
        assertTrue(gestorTorneos.inscribirCliente(t, cliente1, 2));
        assertTrue(t.yaInscrito(cliente1));
    }

    @Test
    public void testInscribirCliente_masDe3Cupos_falla() {
        TorneoAmistoso t = gestorTorneos.crearTorneoAmistoso(
            "T7", "LUNES", juegoBase, 10, 0.10);
        assertNotNull(t);
        assertFalse("No se pueden inscribir más de 3 participantes",
            gestorTorneos.inscribirCliente(t, cliente1, 4));
    }

    @Test
    public void testInscribirCliente_yaInscrito_falla() {
        TorneoAmistoso t = gestorTorneos.crearTorneoAmistoso(
            "T8", "LUNES", juegoBase, 10, 0.10);
        assertNotNull(t);
        gestorTorneos.inscribirCliente(t, cliente1, 1);
        assertFalse("No puede inscribirse dos veces",
            gestorTorneos.inscribirCliente(t, cliente1, 1));
    }

    @Test
    public void testInscribirCliente_sinCuposDisponibles_falla() {
        // Torneo con solo 2 cupos
        TorneoAmistoso t = gestorTorneos.crearTorneoAmistoso(
            "T9", "LUNES", juegoBase, 2, 0.10);
        assertNotNull(t);
        gestorTorneos.inscribirCliente(t, cliente1, 2);  // llena el torneo
        assertFalse("No debe inscribir si no hay cupos",
            gestorTorneos.inscribirCliente(t, cliente2, 1));
    }

    // ══════════════════════════════════════════════════════════════════
    //  RF-4: Desinscripción (elimina todos los cupos)
    // ══════════════════════════════════════════════════════════════════

    @Test
    public void testDesinscribirCliente_liberaTodosLosCupos() {
        TorneoAmistoso t = gestorTorneos.crearTorneoAmistoso(
            "T10", "LUNES", juegoBase, 10, 0.10);
        assertNotNull(t);
        gestorTorneos.inscribirCliente(t, cliente1, 3);
        assertEquals(3, t.getCuposOcupados());

        assertTrue(gestorTorneos.desinscribirCliente(t, cliente1));
        assertFalse(t.yaInscrito(cliente1));
        assertEquals("Todos los cupos deben liberarse", 0, t.getCuposOcupados());
    }

    @Test
    public void testDesinscribirCliente_noInscrito_falla() {
        TorneoAmistoso t = gestorTorneos.crearTorneoAmistoso(
            "T11", "LUNES", juegoBase, 10, 0.10);
        assertNotNull(t);
        assertFalse(gestorTorneos.desinscribirCliente(t, cliente1));
    }

    @Test
    public void testDesinscripcion_fanatico_liberaCuposFanaticos() {
        TorneoAmistoso t = gestorTorneos.crearTorneoAmistoso(
            "T12", "LUNES", juegoBase, 10, 0.10);
        assertNotNull(t);
        gestorTorneos.inscribirCliente(t, clienteFanatico, 1);
        assertEquals(1, t.getCuposFanaticosOcupados());

        gestorTorneos.desinscribirCliente(t, clienteFanatico);
        assertEquals("El cupo fanático debe liberarse", 0, t.getCuposFanaticosOcupados());
    }

    // ══════════════════════════════════════════════════════════════════
    //  RF-5: Inscripción de empleados
    // ══════════════════════════════════════════════════════════════════

    @Test
    public void testInscribirEmpleado_libre_exitoso() {
        // Torneo el LUNES, empleadoLibre tiene turno SABADO
        TorneoAmistoso t = gestorTorneos.crearTorneoAmistoso(
            "T13", "LUNES", juegoBase, 10, 0.10);
        assertNotNull(t);
        assertTrue(gestorTorneos.inscribirEmpleado(t, empleadoLibre, 1));
        assertTrue(t.yaInscrito(empleadoLibre));
    }

    @Test
    public void testInscribirEmpleado_conTurno_falla() {
        // Torneo el LUNES, empleadoEnTurno tiene turno LUNES
        TorneoAmistoso t = gestorTorneos.crearTorneoAmistoso(
            "T14", "LUNES", juegoBase, 10, 0.10);
        assertNotNull(t);
        assertFalse("Empleado en turno no puede inscribirse",
            gestorTorneos.inscribirEmpleado(t, empleadoEnTurno, 1));
    }

    @Test
    public void testInscribirEmpleado_turnoRango_incluye_dia() {
        // Empleado con turno "LUNES-VIERNES", torneo el MIERCOLES → debe fallar
        Empleado empRango = gestorUsuarios.registrarEmpleado(
            "empRango", "pass", "LUNES-VIERNES", "COCINERO");
        TorneoAmistoso t = gestorTorneos.crearTorneoAmistoso(
            "T15", "MIERCOLES", juegoBase, 10, 0.10);
        assertNotNull(t);
        assertFalse("Empleado con turno rango que incluye el día no puede inscribirse",
            gestorTorneos.inscribirEmpleado(t, empRango, 1));
    }

    @Test
    public void testInscribirEmpleado_turnoRango_excluye_dia() {
        // Empleado con turno "LUNES-VIERNES", torneo el SABADO → debe poder
        Empleado empRango = gestorUsuarios.registrarEmpleado(
            "empRango2", "pass", "LUNES-VIERNES", "COCINERO");
        TorneoAmistoso t = gestorTorneos.crearTorneoAmistoso(
            "T16", "SABADO", juegoBase, 10, 0.10);
        assertNotNull(t);
        assertTrue("Empleado con turno que NO incluye el día puede inscribirse",
            gestorTorneos.inscribirEmpleado(t, empRango, 1));
    }

    // ══════════════════════════════════════════════════════════════════
    //  RF-6: Premio amistoso (bono descuento)
    // ══════════════════════════════════════════════════════════════════

    @Test
    public void testEntregarBonoAmistoso_asignaCorrectamente() {
        TorneoAmistoso t = gestorTorneos.crearTorneoAmistoso(
            "T17", "LUNES", juegoBase, 10, 0.20);
        assertNotNull(t);
        gestorTorneos.inscribirCliente(t, cliente1, 1);

        assertTrue(gestorTorneos.entregarBonoAmistoso(t, cliente1));
        assertTrue(cliente1.tieneBonoDescuento());
        assertEquals(0.20, cliente1.getBonoDescuento(), 0.001);
    }

    @Test
    public void testEntregarBonoAmistoso_reemplazaBonoAnterior() {
        TorneoAmistoso t1 = gestorTorneos.crearTorneoAmistoso(
            "T18a", "LUNES",  juegoBase, 10, 0.15);
        TorneoAmistoso t2 = gestorTorneos.crearTorneoAmistoso(
            "T18b", "MARTES", juegoBase, 10, 0.30);
        assertNotNull(t1); assertNotNull(t2);
        gestorTorneos.inscribirCliente(t1, cliente1, 1);
        gestorTorneos.inscribirCliente(t2, cliente1, 1);

        gestorTorneos.entregarBonoAmistoso(t1, cliente1);
        assertEquals(0.15, cliente1.getBonoDescuento(), 0.001);

        gestorTorneos.entregarBonoAmistoso(t2, cliente1);
        assertEquals("El bono nuevo reemplaza al anterior (no acumulable)",
            0.30, cliente1.getBonoDescuento(), 0.001);
    }

    @Test
    public void testUsarBonoDescuento_loConsume() {
        TorneoAmistoso t = gestorTorneos.crearTorneoAmistoso(
            "T19", "LUNES", juegoBase, 10, 0.10);
        assertNotNull(t);
        gestorTorneos.inscribirCliente(t, cliente1, 1);
        gestorTorneos.entregarBonoAmistoso(t, cliente1);

        double bono = cliente1.usarBonoDescuento();
        assertEquals(0.10, bono, 0.001);
        assertFalse("El bono debe consumirse al usarlo", cliente1.tieneBonoDescuento());
    }

    @Test
    public void testEntregarBono_clienteNoInscrito_falla() {
        TorneoAmistoso t = gestorTorneos.crearTorneoAmistoso(
            "T20", "LUNES", juegoBase, 10, 0.10);
        assertNotNull(t);
        assertFalse("No se puede dar bono a quien no estaba inscrito",
            gestorTorneos.entregarBonoAmistoso(t, cliente1));
    }

    // ══════════════════════════════════════════════════════════════════
    //  RF-7: Premio competitivo (metálico)
    // ══════════════════════════════════════════════════════════════════

    @Test
    public void testPremioCompetitivo_calculoCorrectoConClientes() {
        TorneoCompetitivo t = gestorTorneos.crearTorneoCompetitivo(
            "T21", "VIERNES", juegoGrande, 10, 15000);
        assertNotNull(t);
        gestorTorneos.inscribirCliente(t, cliente1, 2); // 2 x 15000 = 30000
        gestorTorneos.inscribirCliente(t, cliente2, 1); // 1 x 15000 = 15000

        t.calcularPremio();
        assertEquals("Premio debe ser 45000", 45000.0, t.getPremioMetalico(), 0.001);
    }

    @Test
    public void testPremioCompetitivo_empleadoNoRecibePremio() {
        TorneoCompetitivo t = gestorTorneos.crearTorneoCompetitivo(
            "T22", "VIERNES", juegoGrande, 10, 15000);
        assertNotNull(t);
        gestorTorneos.inscribirEmpleado(t, empleadoLibre, 1);

        double premio = gestorTorneos.calcularPremioCompetitivo(t, empleadoLibre);
        assertEquals("Empleado no recibe premio", 0.0, premio, 0.001);
    }

    @Test
    public void testPremioCompetitivo_empleadoNoContaEnRecaudado() {
        TorneoCompetitivo t = gestorTorneos.crearTorneoCompetitivo(
            "T23", "VIERNES", juegoGrande, 10, 10000);
        assertNotNull(t);
        gestorTorneos.inscribirCliente(t, cliente1, 1);   // paga: 10000
        gestorTorneos.inscribirEmpleado(t, empleadoLibre, 1); // gratis, no cuenta

        t.calcularPremio();
        assertEquals("Solo clientes aportan al recaudo", 10000.0, t.getPremioMetalico(), 0.001);
    }

    // ══════════════════════════════════════════════════════════════════
    //  RF-8: Múltiples torneos simultáneos
    // ══════════════════════════════════════════════════════════════════

    @Test
    public void testMultiplesTorneos_simultaneos() {
        TorneoAmistoso t1 = gestorTorneos.crearTorneoAmistoso(
            "Copa Lunes",   "LUNES",  juegoBase,   10, 0.10);
        TorneoCompetitivo t2 = gestorTorneos.crearTorneoCompetitivo(
            "Liga Martes",  "MARTES", juegoGrande, 8, 5000);
        TorneoAmistoso t3 = gestorTorneos.crearTorneoAmistoso(
            "Amigos Viernes","VIERNES",juegoBase,  4, 0.15);

        assertNotNull(t1); assertNotNull(t2); assertNotNull(t3);
        assertEquals("Deben existir 3 torneos", 3, cafe.getTorneos().size());
    }

    @Test
    public void testCliente_puedeInscribirseEnVariosTorneos() {
        TorneoAmistoso t1 = gestorTorneos.crearTorneoAmistoso(
            "Copa1", "LUNES",  juegoBase,   10, 0.10);
        TorneoAmistoso t2 = gestorTorneos.crearTorneoAmistoso(
            "Copa2", "MARTES", juegoGrande, 10, 0.10);
        assertNotNull(t1); assertNotNull(t2);

        assertTrue(gestorTorneos.inscribirCliente(t1, cliente1, 1));
        assertTrue(gestorTorneos.inscribirCliente(t2, cliente1, 2));
        assertTrue(t1.yaInscrito(cliente1));
        assertTrue(t2.yaInscrito(cliente1));
    }

    // ══════════════════════════════════════════════════════════════════
    //  RF-9: Verificar turno empleado (auxiliar)
    // ══════════════════════════════════════════════════════════════════

    @Test
    public void testEmpleadoTieneTurnoEnDia_exact() {
        assertTrue(gestorTorneos.empleadoTieneTurnoEnDia(empleadoEnTurno, "LUNES"));
        assertFalse(gestorTorneos.empleadoTieneTurnoEnDia(empleadoEnTurno, "MARTES"));
    }

    @Test
    public void testEmpleadoTieneTurnoEnDia_rango() {
        Empleado e = gestorUsuarios.registrarEmpleado("eR", "p", "LUNES-VIERNES", "COCINERO");
        assertTrue(gestorTorneos.empleadoTieneTurnoEnDia(e, "LUNES"));
        assertTrue(gestorTorneos.empleadoTieneTurnoEnDia(e, "MIERCOLES"));
        assertTrue(gestorTorneos.empleadoTieneTurnoEnDia(e, "VIERNES"));
        assertFalse(gestorTorneos.empleadoTieneTurnoEnDia(e, "SABADO"));
        assertFalse(gestorTorneos.empleadoTieneTurnoEnDia(e, "DOMINGO"));
    }

    // ══════════════════════════════════════════════════════════════════
    //  RF-10: Cupos totales y disponibles
    // ══════════════════════════════════════════════════════════════════

    @Test
    public void testCuposTotalesDisponibles_seReducenAlInscribir() {
        TorneoAmistoso t = gestorTorneos.crearTorneoAmistoso(
            "T30", "LUNES", juegoBase, 8, 0.10);
        assertNotNull(t);
        assertEquals(8, t.cuposTotalesDisponibles());

        gestorTorneos.inscribirCliente(t, cliente1, 3);
        assertEquals(5, t.cuposTotalesDisponibles());

        gestorTorneos.inscribirCliente(t, cliente2, 2);
        assertEquals(3, t.cuposTotalesDisponibles());
    }

    @Test
    public void testDesinscripcion_restauraCuposDisponibles() {
        TorneoAmistoso t = gestorTorneos.crearTorneoAmistoso(
            "T31", "LUNES", juegoBase, 8, 0.10);
        assertNotNull(t);
        gestorTorneos.inscribirCliente(t, cliente1, 3);
        assertEquals(5, t.cuposTotalesDisponibles());

        gestorTorneos.desinscribirCliente(t, cliente1);
        assertEquals(8, t.cuposTotalesDisponibles());
    }
}