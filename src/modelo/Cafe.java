package modelo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Cafe implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    
    private List<Usuario> usuarios;
    private List<Cliente> clientes;
    private List<Empleado> empleados;
    private List<Juego> juegos;
    private List<InventarioJuegos> inventariosJuegos;
    private List<InventarioCafeteria> inventariosCafeteria;
    private List<Prestamo> prestamos;
    private List<Venta> ventas;
    private List<SolicitudCambioTurno> solicitudes;
    private List<Mesa> mesas;
    private Administrador administrador;
    private int capacidadMaxima;

    public Cafe(int capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
        this.usuarios = new ArrayList<>();
        this.clientes = new ArrayList<>();
        this.empleados = new ArrayList<>();
        this.juegos = new ArrayList<>();
        this.inventariosJuegos = new ArrayList<>();
        this.inventariosCafeteria = new ArrayList<>();
        this.prestamos = new ArrayList<>();
        this.ventas = new ArrayList<>();
        this.solicitudes = new ArrayList<>();
        this.mesas = new ArrayList<>();
    }

    public void agregarCliente(Cliente c) { clientes.add(c); usuarios.add(c); }
    public void agregarEmpleado(Empleado e) { empleados.add(e); usuarios.add(e); }
    public void setAdministrador(Administrador a) { this.administrador = a; usuarios.add(a); }
    public Administrador getAdministrador() { return administrador; }

    public void agregarMesa(Mesa m) { mesas.add(m); }
    
    public void liberarMesa(Mesa m) {
        m.liberar();
        mesas.remove(m);
    }
    
    public List<Mesa> getMesas() { return mesas; }

    public void agregarInventarioJuego(InventarioJuegos inv) {
        inventariosJuegos.add(inv);
        if (!juegos.contains(inv.getJuego())) juegos.add(inv.getJuego());
    }

    public void agregarInventarioCafeteria(InventarioCafeteria inv) {
        inventariosCafeteria.add(inv);
    }

    public InventarioJuegos buscarInventarioJuego(Juego j) {
        for (InventarioJuegos inv : inventariosJuegos) {
            if (inv.getJuego().equals(j)) return inv;
        }
        return null;
    }

    public InventarioCafeteria buscarInventarioCafeteria(ProductoCafeteria p) {
        for (InventarioCafeteria inv : inventariosCafeteria) {
            if (inv.getProducto().equals(p)) return inv;
        }
        return null;
    }

    public void registrarPrestamo(Prestamo p) { prestamos.add(p); }

    public void registrarVenta(Venta v) {
        ventas.add(v);
        if (v.getCliente() != null) {
            v.getCliente().agregarPuntos(v.calcularPuntosFidelidad());
        }
    }

    public void agregarSolicitud(SolicitudCambioTurno s) { solicitudes.add(s); }

    public boolean hayCapacidad(int personasNuevas) {
        int totalActual = 0;
        for (Mesa m : mesas) totalActual += m.getCantidadPersonas();
        return (totalActual + personasNuevas) <= capacidadMaxima;
    }

    public List<Cliente> getClientes() { return clientes; }
    public List<Empleado> getEmpleados() { return empleados; }
    public List<Juego> getJuegos() { return juegos; }
    public List<InventarioJuegos> getInventariosJuegos() { return inventariosJuegos; }
    public List<InventarioCafeteria> getInventariosCafeteria() { return inventariosCafeteria; }
    public List<Prestamo> getPrestamos() { return prestamos; }
    public List<Venta> getVentas() { return ventas; }
    public List<SolicitudCambioTurno> getSolicitudes() { return solicitudes; }
    public int getCapacidadMaxima() { return capacidadMaxima; }

    // Métodos de persistencia
    public void guardarEstado(String rutaArchivo) {
        try {
            persistencia.Persistencia.guardar(this, rutaArchivo);
            System.out.println("Estado guardado exitosamente en " + rutaArchivo);
        } catch (IOException e) {
            System.err.println("Error al guardar: " + e.getMessage());
        }
    }

    public static Cafe cargarEstado(String rutaArchivo) {
        try {
            Cafe cafe = persistencia.Persistencia.cargar(rutaArchivo);
            System.out.println("Estado cargado desde " + rutaArchivo);
            return cafe;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al cargar: " + e.getMessage());
            return null;
        }
    }

    @Override
    public String toString() {
        return "Cafe[clientes=" + clientes.size() + ", empleados=" + empleados.size()
            + ", capacidad=" + capacidadMaxima + "]";
    }
}