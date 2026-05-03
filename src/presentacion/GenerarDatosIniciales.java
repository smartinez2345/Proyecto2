package presentacion;

import logica.*;
import modelo.*;
import persistencia.ExportadorTexto;
import java.io.File;
import java.io.IOException;

public class GenerarDatosIniciales {
    public static void main(String[] args) {
        System.out.println("Generando datos iniciales...");

        Cafe cafe = new Cafe(30);
        GestorUsuarios gu = new GestorUsuarios(cafe);
        GestorInventario gi = new GestorInventario(cafe);
        GestorPrestamos gp = new GestorPrestamos(cafe);
        GestorVentas gv = new GestorVentas(cafe);
        GestorTurnos gt = new GestorTurnos(cafe);

        // 1. Administrador
        Administrador admin = new Administrador("admin", "admin123");
        cafe.setAdministrador(admin);

        // 2. Clientes
        Cliente c1 = gu.registrarCliente("cliente1", "pass");
        Cliente c2 = gu.registrarCliente("cliente2", "pass");
        Cliente c3 = gu.registrarCliente("cliente3", "pass");

        // 3. Empleados
        Empleado cocinero = gu.registrarEmpleado("cocinero1", "pass", "Lunes-Viernes", "COCINERO");
        Empleado mesero1 = gu.registrarEmpleado("mesero1", "pass", "Lunes-Viernes", "MESERO");
        Empleado mesero2 = gu.registrarEmpleado("mesero2", "pass", "Sabado-Domingo", "MESERO");

        // 4. Juegos
        Juego catan = new Juego("Catan", 1995, "Kosmos", "TABLERO", 3, 4, 10, true);
        Juego uno = new Juego("Uno", 1971, "Mattel", "CARTAS", 2, 10, 0, false);
        Juego twister = new Juego("Twister", 1966, "Hasbro", "ACCION", 2, 6, 5, false);
        Juego ajedrez = new Juego("Ajedrez", 600, "Genérico", "TABLERO", 2, 2, 5, false);

        ((Mesero)mesero1).agregarJuegoConocido(catan);

        gi.agregarJuegoAPrestamo(catan, 2);
        gi.agregarJuegoAPrestamo(uno, 3);
        gi.agregarJuegoAPrestamo(twister, 1);
        gi.agregarJuegoAPrestamo(ajedrez, 1);

        // 5. Productos de cafetería
        Bebida cafeBebida = new Bebida("Café Americano", 5000, false, true);
        Bebida cerveza = new Bebida("Cerveza Artesanal", 12000, true, false);
        Pasteleria brownie = new Pasteleria("Brownie", 6000);
        brownie.agregarAlergeno("gluten");
        gi.agregarProductoCafeteria(cafeBebida, 20);
        gi.agregarProductoCafeteria(cerveza, 10);
        gi.agregarProductoCafeteria(brownie, 5);

        // 6. Mesa y préstamo
        Mesa mesa = gp.crearMesa(3, false, false, c1);
        Prestamo p = gp.iniciarPrestamo(mesa);
        gp.agregarJuegoAPrestamo(p, uno);

        // 7. Ventas
        Venta vCafe = gv.iniciarVentaCafeteria(c2);
        gv.agregarProductoAVenta(vCafe, cafeBebida, 2, null);
        gv.finalizarVenta(vCafe);

        Venta vJuego = gv.iniciarVentaJuegos(c3);
        gv.agregarJuegoAVenta(vJuego, catan, 1, 150000);
        gv.finalizarVenta(vJuego);

        // 8. Solicitud de cambio de turno (opcional)
        SolicitudCambioTurno sol = gt.solicitarCambio(mesero1, mesero2);
        if (sol != null) {
            gt.aprobarCambio(sol);
        }

        // 9. Guardar estado binario
        String rutaBase = System.getProperty("user.dir") + "/data/";
        File dataDir = new File(rutaBase);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
            System.out.println("Carpeta 'data' creada en: " + dataDir.getAbsolutePath());
        }

        String rutaDat = rutaBase + "cafe_inicial.dat";
        cafe.guardarEstado(rutaDat);
        System.out.println("Estado binario guardado en: " + rutaDat);

        // 10. Exportar a archivo de texto legible
        try {
            String rutaTxt = rutaBase + "cafe_inicial.txt";
            ExportadorTexto.exportar(cafe, rutaTxt);
            System.out.println("Reporte de texto generado en: " + rutaTxt);
        } catch (IOException e) {
            System.err.println("Error al exportar a texto: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n¡Generación de datos iniciales completada!");
    }
}