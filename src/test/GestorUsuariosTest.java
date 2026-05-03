package test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import logica.GestorUsuarios;
import modelo.Cafe;
import modelo.Cliente;
import modelo.Usuario;
import modelo.Empleado;

public class GestorUsuariosTest {

    private Cafe cafe;
    private GestorUsuarios gestor;

    @Before
    public void setUp() {
        cafe = new Cafe(30);
        gestor = new GestorUsuarios(cafe);
    }

    @Test
    public void testRegistrarClienteCorrecto() {
        Cliente c = gestor.registrarCliente("santi", "123");

        assertNotNull(c);
        assertEquals("santi", c.getLogin());
        assertEquals(1, cafe.getClientes().size());
    }

    @Test
    public void testRegistrarClienteDuplicado() {
        gestor.registrarCliente("santi", "123");
        Cliente repetido = gestor.registrarCliente("santi", "999");

        assertNull(repetido);
        assertEquals(1, cafe.getClientes().size());
    }

    @Test
    public void testLoginCorrectoCliente() {
        gestor.registrarCliente("juan", "abc");

        Usuario u = gestor.login("juan", "abc");

        assertNotNull(u);
        assertEquals("juan", u.getLogin());
    }

    @Test
    public void testLoginIncorrectoPassword() {
        gestor.registrarCliente("juan", "abc");

        Usuario u = gestor.login("juan", "mala");

        assertNull(u);
    }

    @Test
    public void testBuscarUsuarioExistente() {
        gestor.registrarCliente("maria", "123");

        Usuario u = gestor.buscarUsuario("maria");

        assertNotNull(u);
        assertEquals("maria", u.getLogin());
    }

    @Test
    public void testBuscarUsuarioNoExiste() {
        Usuario u = gestor.buscarUsuario("nadie");

        assertNull(u);
    }

    @Test
    public void testRegistrarEmpleadoMesero() {
        Empleado e = gestor.registrarEmpleado("pedro", "123", "Lunes", "MESERO");

        assertNotNull(e);
        assertEquals("pedro", e.getLogin());
        assertEquals(1, cafe.getEmpleados().size());
    }

    @Test
    public void testRegistrarEmpleadoDuplicado() {
        gestor.registrarEmpleado("pedro", "123", "Lunes", "MESERO");
        Empleado e2 = gestor.registrarEmpleado("pedro", "999", "Martes", "COCINERO");

        assertNull(e2);
        assertEquals(1, cafe.getEmpleados().size());
    }
}