package presentacion;

import logica.*;
import modelo.*;
import java.util.Date;
import java.util.Calendar;

public class PruebaInformes {
    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println("  PRUEBA 7: INFORMES DE VENTAS");
        System.out.println("====================================\n");

        Cafe cafe = new Cafe(30);
        GestorUsuarios gu = new GestorUsuarios(cafe);
        GestorVentas gv = new GestorVentas(cafe);
        GestorInventario gi = new GestorInventario(cafe);
        GestorPrestamos gp = new GestorPrestamos(cafe);

        Cliente c = gu.registrarCliente("ana", "pass");
        Bebida cafeBebida = new Bebida("Café", 4000, false, true);
        gi.agregarProductoCafeteria(cafeBebida, 10);
        
        Mesa mesa = gp.crearMesa(2, false, false, c);

        Venta v1 = gv.iniciarVentaCafeteria(c);
        gv.agregarProductoAVenta(v1, cafeBebida, 2, mesa);
        gv.finalizarVenta(v1);

        Venta v2 = gv.iniciarVentaJuegos(c);
        Juego catan = new Juego("Catan", 1995, "Kosmos", "TABLERO", 3, 4, 10, false);
        gv.agregarJuegoAVenta(v2, catan, 1, 120000);
        gv.finalizarVenta(v2);

        gv.informeDiario(new Date());

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -7);
        gv.informeSemanal(cal.getTime());

        gv.informeMensual(Calendar.getInstance().get(Calendar.MONTH)+1, 
                          Calendar.getInstance().get(Calendar.YEAR));

        System.out.println("====================================");
        System.out.println("  PRUEBA 7 COMPLETADA");
        System.out.println("====================================");
    }
}