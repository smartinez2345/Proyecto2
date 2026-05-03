package logica;

import modelo.*;

public class GestorUsuarios {

    private Cafe cafe;

    public GestorUsuarios(Cafe cafe) {
        this.cafe = cafe;
    }

    public Cliente registrarCliente(String login, String password) {
        if (buscarUsuario(login) != null) {
            System.out.println("ERROR: Ya existe un usuario con ese login.");
            return null;
        }
        Cliente c = new Cliente(login, password);
        cafe.agregarCliente(c);
        System.out.println("Cliente registrado: " + login);
        return c;
    }

    public Empleado registrarEmpleado(String login, String password, String turno, String tipo) {
        if (buscarUsuario(login) != null) {
            System.out.println("ERROR: Ya existe un usuario con ese login.");
            return null;
        }
        Empleado e;
        if (tipo.equalsIgnoreCase("COCINERO")) {
            e = new Cocinero(login, password, turno);
        } else {
            e = new Mesero(login, password, turno);
        }
        cafe.agregarEmpleado(e);
        System.out.println("Empleado registrado: " + login + " (" + tipo + ")");
        return e;
    }

    public Usuario login(String login, String password) {
        Usuario u = buscarUsuario(login);
        if (u != null && u.autenticar(login, password)) {
            System.out.println("Login exitoso: " + login);
            return u;
        }
        System.out.println("ERROR: Login o password incorrectos.");
        return null;
    }

    public Usuario buscarUsuario(String login) {
        for (Cliente c : cafe.getClientes()) {
            if (c.getLogin().equals(login)) return c;
        }
        for (Empleado e : cafe.getEmpleados()) {
            if (e.getLogin().equals(login)) return e;
        }
        if (cafe.getAdministrador() != null &&
            cafe.getAdministrador().getLogin().equals(login)) {
            return cafe.getAdministrador();
        }
        return null;
    }
}