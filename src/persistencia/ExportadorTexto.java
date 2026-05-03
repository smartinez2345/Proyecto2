package persistencia;

import modelo.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;

public class ExportadorTexto {

    public static void exportar(Cafe cafe, String rutaArchivo) throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter(rutaArchivo));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        out.println("===== ESTADO DEL BOARD GAME CAFE =====");
        out.println("Fecha de exportación: " + sdf.format(new java.util.Date()));
        out.println("Capacidad máxima: " + cafe.getCapacidadMaxima());
        out.println();

        // Administrador
        Administrador admin = cafe.getAdministrador();
        out.println("--- ADMINISTRADOR ---");
        if (admin != null) {
            out.println("Login: " + admin.getLogin());
        } else {
            out.println("No hay administrador registrado.");
        }
        out.println();

        // Clientes
        out.println("--- CLIENTES (" + cafe.getClientes().size() + ") ---");
        for (Cliente c : cafe.getClientes()) {
            out.println("Login: " + c.getLogin() + " | Puntos: " + c.getPuntosFidelidad() +
                       " | Juegos favoritos: " + c.getJuegosFavoritos().size());
        }
        out.println();

        // Empleados
        out.println("--- EMPLEADOS (" + cafe.getEmpleados().size() + ") ---");
        for (Empleado e : cafe.getEmpleados()) {
            String tipo = (e instanceof Cocinero) ? "Cocinero" : "Mesero";
            out.println("Login: " + e.getLogin() + " | Tipo: " + tipo +
                       " | Turno: " + e.getTurnoSemana() + " | En turno: " + e.estaEnTurno());
            if (e instanceof Mesero) {
                Mesero m = (Mesero) e;
                out.println("   Juegos que conoce: " + m.getJuegosConocidos().size());
            }
        }
        out.println();

        // Inventario de juegos
        out.println("--- INVENTARIO DE JUEGOS (PRÉSTAMO) ---");
        for (InventarioJuegos inv : cafe.getInventariosJuegos()) {
            Juego j = inv.getJuego();
            out.println("Juego: " + j.getNombre() + " | Categoría: " + j.getCategoria() +
                       " | Estado: " + j.getEstado() + " | Disponibles: " + inv.getCantidadDisponible() +
                       " | Dificil: " + j.isEsDificil() + " | Min/Max jugadores: " +
                       j.getMinJugadores() + "/" + j.getMaxJugadores());
        }
        out.println();

        // Inventario de cafetería
        out.println("--- INVENTARIO DE CAFETERÍA ---");
        for (InventarioCafeteria inv : cafe.getInventariosCafeteria()) {
            ProductoCafeteria p = inv.getProducto();
            String tipo = (p instanceof Bebida) ? "Bebida" : "Pastelería";
            out.println("Producto: " + p.getNombre() + " | Tipo: " + tipo +
                       " | Precio: $" + p.getPrecio() + " | Stock: " + inv.getStock());
            if (p instanceof Bebida) {
                Bebida b = (Bebida) p;
                out.println("   Alcoholica: " + b.isEsAlcoholica() + " | Caliente: " + b.isEsCaliente());
            } else if (p instanceof Pasteleria) {
                Pasteleria past = (Pasteleria) p;
                out.println("   Alérgenos: " + past.getAlergenos());
            }
        }
        out.println();

        // Mesas activas
        out.println("--- MESAS ACTIVAS (" + cafe.getMesas().size() + ") ---");
        for (Mesa m : cafe.getMesas()) {
            out.println("Mesa - Cliente: " + m.getCliente().getLogin() +
                       " | Personas: " + m.getCantidadPersonas() +
                       " | Menores5: " + m.isTieneMenores5() +
                       " | Menores18: " + m.isTieneMenores18() +
                       " | Bebida caliente: " + m.tieneBebidaCaliente() +
                       " | Juego acción: " + m.tieneJuegoDeAccion());
        }
        out.println();

        // Préstamos
        out.println("--- PRÉSTAMOS (" + cafe.getPrestamos().size() + ") ---");
        for (Prestamo p : cafe.getPrestamos()) {
            out.println("Préstamo - Fecha: " + sdf.format(p.getFechaPrestamo()) +
                       " | Estado: " + p.getEstado());
            if (p.getMesa() != null) {
                out.println("   Mesa del cliente: " + p.getMesa().getCliente().getLogin());
            } else {
                out.println("   Préstamo de empleado (sin mesa)");
            }
            out.println("   Juegos prestados:");
            for (Juego j : p.getJuegos()) {
                out.println("      - " + j.getNombre());
            }
        }
        out.println();

        // Ventas
        out.println("--- VENTAS (" + cafe.getVentas().size() + ") ---");
        for (Venta v : cafe.getVentas()) {
            out.println("Venta - Fecha: " + sdf.format(v.getFecha()) +
                       " | Tipo: " + v.getTipoVenta() +
                       " | Cliente: " + v.getCliente().getLogin() +
                       " | Total: $" + String.format("%.2f", v.calcularTotal()));
            out.println("   Subtotal: $" + String.format("%.2f", v.calcularSubtotal()) +
                       " | Impuestos: $" + String.format("%.2f", v.getImpuestos()) +
                       " | Propina: $" + String.format("%.2f", v.getPropina()));
            out.println("   Detalles:");
            for (DetalleVenta d : v.getDetalles()) {
                String nombre = (d.getProducto() != null) ? d.getProducto().getNombre() : "Juego";
                out.println("      - " + nombre + " x" + d.getCantidad() +
                           " ($" + d.getPrecioUnitario() + " c/u)");
            }
        }
        out.println();

        // Solicitudes de cambio de turno
        out.println("--- SOLICITUDES DE CAMBIO (" + cafe.getSolicitudes().size() + ") ---");
        for (SolicitudCambioTurno s : cafe.getSolicitudes()) {
            out.println("Solicitud - Solicitante: " + s.getSolicitante().getLogin() +
                       " | Reemplazo: " + s.getReemplazo().getLogin() +
                       " | Estado: " + s.getEstado());
        }
        out.println();

        out.println("===== FIN DEL REPORTE =====");
        out.close();
    }
}