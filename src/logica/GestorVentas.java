package logica;

import modelo.*;
import java.util.Date;
import java.util.Calendar;

public class GestorVentas {

    private Cafe cafe;

    public GestorVentas(Cafe cafe) {
        this.cafe = cafe;
    }

    public Venta iniciarVentaCafeteria(Cliente cliente) {
        Venta v = new Venta("CAFETERIA", cliente);
        System.out.println("Venta de cafetería iniciada para: " + cliente.getLogin());
        return v;
    }

    public Venta iniciarVentaJuegos(Cliente cliente) {
        Venta v = new Venta("JUEGOS", cliente);
        System.out.println("Venta de juegos iniciada para: " + cliente.getLogin());
        return v;
    }

    public boolean agregarProductoAVenta(Venta venta, ProductoCafeteria producto,
                                          int cantidad, Mesa mesa) {
        InventarioCafeteria inv = cafe.buscarInventarioCafeteria(producto);
        if (inv == null || inv.getStock() < cantidad) {
            System.out.println("ERROR: Stock insuficiente de " + producto.getNombre());
            return false;
        }

        if (producto instanceof Bebida) {
            Bebida b = (Bebida) producto;
            if (mesa != null && b.isEsAlcoholica() &&
               (mesa.isTieneMenores5() || mesa.isTieneMenores18())) {
                System.out.println("ERROR: No se puede vender bebida alcohólica "
                    + "a mesa con menores de edad.");
                return false;
            }
            if (mesa != null && b.isEsCaliente()) {
                // Verificar si la mesa ya tiene juego de acción
                if (mesa.tieneJuegoDeAccion()) {
                    System.out.println("ERROR: No se puede vender bebida caliente "
                        + "a mesa con juego de Acción.");
                    return false;
                }
                // Marcar la mesa con bebida caliente
                mesa.setTieneBebidaCaliente(true);
            }
        }

        if (producto instanceof Pasteleria) {
            Pasteleria p = (Pasteleria) producto;
            if (!p.getAlergenos().isEmpty()) {
                System.out.println("ADVERTENCIA: " + producto.getNombre()
                    + " contiene alérgenos: " + p.getAlergenos());
            }
        }

        inv.actualizarStock(-cantidad);
        DetalleVenta detalle = new DetalleVenta(cantidad, producto.getPrecio(), producto);
        venta.agregarDetalle(detalle);
        System.out.println("Producto agregado: " + producto.getNombre() + " x" + cantidad);
        return true;
    }

    public boolean agregarJuegoAVenta(Venta venta, Juego juego, int cantidad,
                                       double precioUnitario) {
        // Nota: No hay inventario de juegos para venta en este diseño básico.
        // Se asume que siempre hay stock. Si quieres controlar stock, debes
        // agregar un inventario separado para venta de juegos.
        DetalleVenta detalle = new DetalleVenta(cantidad, precioUnitario, null);
        venta.agregarDetalle(detalle);
        System.out.println("Juego agregado a venta: " + juego.getNombre() + " x" + cantidad);
        return true;
    }

    public void aplicarDescuentoCodigo(Venta venta, boolean esEmpleado) {
        if (esEmpleado) {
            venta.setDescuento(0.20);
            System.out.println("Descuento de empleado aplicado: 20%");
        } else {
            venta.setDescuento(0.10);
            System.out.println("Descuento de código aplicado: 10%");
        }
    }

    public void usarPuntosFidelidad(Venta venta, Cliente cliente, int puntos) {
        if (cliente.usarPuntos(puntos)) {
            venta.setDescuentoPuntos(puntos);
            System.out.println("Puntos usados como descuento: $" + puntos);
        } else {
            System.out.println("ERROR: No tiene suficientes puntos.");
        }
    }

    public void finalizarVenta(Venta venta) {
        if (venta.getTipoVenta().equals("CAFETERIA")) {
            venta.setPropinaSugerida();
        }
        cafe.registrarVenta(venta);

        System.out.println("\n===== RESUMEN DE VENTA =====");
        System.out.println("Tipo: " + venta.getTipoVenta());
        System.out.println("Cliente: " + venta.getCliente().getLogin());
        System.out.println("Subtotal: $" + String.format("%.2f", venta.calcularSubtotal()));
        System.out.println("Impuesto: $" + String.format("%.2f", venta.calcularImpuesto()));
        if (venta.getTipoVenta().equals("CAFETERIA")) {
            System.out.println("Propina sugerida: $" + String.format("%.2f", venta.getPropina()));
        }
        System.out.println("TOTAL: $" + String.format("%.2f", venta.calcularTotal()));
        System.out.println("Puntos ganados: " + venta.calcularPuntosFidelidad());
        System.out.println("============================\n");
    }

    public void mostrarHistorialVentas() {
        System.out.println("\n===== HISTORIAL DE VENTAS =====");
        double totalJuegos = 0, totalCafeteria = 0;
        for (Venta v : cafe.getVentas()) {
            System.out.println(v.getFecha() + " | " + v.getTipoVenta()
                + " | Cliente: " + v.getCliente().getLogin()
                + " | Total: $" + String.format("%.2f", v.calcularTotal()));
            if (v.getTipoVenta().equals("JUEGOS")) totalJuegos += v.calcularTotal();
            else totalCafeteria += v.calcularTotal();
        }
        System.out.println("Total ventas juegos: $" + String.format("%.2f", totalJuegos));
        System.out.println("Total ventas cafetería: $" + String.format("%.2f", totalCafeteria));
        System.out.println("================================\n");
    }

    // --- Informes para administrador ---
    public void informeDiario(Date fecha) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        System.out.println("\n===== INFORME DIARIO: " + fecha + " =====");
        double totalJuegos = 0, totalCafeteria = 0;
        double impJuegos = 0, impCafeteria = 0;
        double propinas = 0;
        for (Venta v : cafe.getVentas()) {
            Calendar calV = Calendar.getInstance();
            calV.setTime(v.getFecha());
            if (calV.get(Calendar.YEAR) == cal.get(Calendar.YEAR) &&
                calV.get(Calendar.DAY_OF_YEAR) == cal.get(Calendar.DAY_OF_YEAR)) {
                
                double subt = v.calcularSubtotal();
                double imp = v.calcularImpuesto();
                if (v.getTipoVenta().equals("JUEGOS")) {
                    totalJuegos += subt;
                    impJuegos += imp;
                } else {
                    totalCafeteria += subt;
                    impCafeteria += imp;
                    propinas += v.getPropina();
                }
            }
        }
        System.out.println("Ventas JUEGOS: $" + String.format("%.2f", totalJuegos) + " + IVA: $" + String.format("%.2f", impJuegos));
        System.out.println("Ventas CAFETERÍA: $" + String.format("%.2f", totalCafeteria) + " + Imp: $" + String.format("%.2f", impCafeteria) + " + Propinas: $" + String.format("%.2f", propinas));
        System.out.println("=====================================\n");
    }

    public void informeSemanal(Date fechaInicio) {
        Calendar calIni = Calendar.getInstance();
        calIni.setTime(fechaInicio);
        calIni.set(Calendar.HOUR_OF_DAY, 0);
        calIni.set(Calendar.MINUTE, 0);
        calIni.set(Calendar.SECOND, 0);
        Calendar calFin = (Calendar) calIni.clone();
        calFin.add(Calendar.DAY_OF_YEAR, 7);
        
        System.out.println("\n===== INFORME SEMANAL desde " + fechaInicio + " =====");
        double totalJuegos = 0, totalCafeteria = 0;
        double impJuegos = 0, impCafeteria = 0;
        double propinas = 0;
        for (Venta v : cafe.getVentas()) {
            if (v.getFecha().after(calIni.getTime()) && v.getFecha().before(calFin.getTime())) {
                double subt = v.calcularSubtotal();
                double imp = v.calcularImpuesto();
                if (v.getTipoVenta().equals("JUEGOS")) {
                    totalJuegos += subt;
                    impJuegos += imp;
                } else {
                    totalCafeteria += subt;
                    impCafeteria += imp;
                    propinas += v.getPropina();
                }
            }
        }
        System.out.println("Ventas JUEGOS: $" + String.format("%.2f", totalJuegos) + " + IVA: $" + String.format("%.2f", impJuegos));
        System.out.println("Ventas CAFETERÍA: $" + String.format("%.2f", totalCafeteria) + " + Imp: $" + String.format("%.2f", impCafeteria) + " + Propinas: $" + String.format("%.2f", propinas));
        System.out.println("==============================================\n");
    }

    public void informeMensual(int mes, int anio) {
        System.out.println("\n===== INFORME MENSUAL: " + mes + "/" + anio + " =====");
        double totalJuegos = 0, totalCafeteria = 0;
        double impJuegos = 0, impCafeteria = 0;
        double propinas = 0;
        for (Venta v : cafe.getVentas()) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(v.getFecha());
            if (cal.get(Calendar.MONTH) + 1 == mes && cal.get(Calendar.YEAR) == anio) {
                double subt = v.calcularSubtotal();
                double imp = v.calcularImpuesto();
                if (v.getTipoVenta().equals("JUEGOS")) {
                    totalJuegos += subt;
                    impJuegos += imp;
                } else {
                    totalCafeteria += subt;
                    impCafeteria += imp;
                    propinas += v.getPropina();
                }
            }
        }
        System.out.println("Ventas JUEGOS: $" + String.format("%.2f", totalJuegos) + " + IVA: $" + String.format("%.2f", impJuegos));
        System.out.println("Ventas CAFETERÍA: $" + String.format("%.2f", totalCafeteria) + " + Imp: $" + String.format("%.2f", impCafeteria) + " + Propinas: $" + String.format("%.2f", propinas));
        System.out.println("========================================\n");
    }
}