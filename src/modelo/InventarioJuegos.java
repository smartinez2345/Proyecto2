package modelo;

import java.io.Serializable;

public class InventarioJuegos implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Juego juego;
    private int cantidadDisponible;

    public InventarioJuegos(Juego juego, int cantidadDisponible) {
        this.juego = juego;
        this.cantidadDisponible = cantidadDisponible;
    }

    public Juego getJuego() { return juego; }
    public int getCantidadDisponible() { return cantidadDisponible; }

    public void actualizarCantidad(int cantidad) {
        this.cantidadDisponible += cantidad;
    }

    public boolean estaDisponible() {
        return cantidadDisponible > 0;
    }

    @Override
    public String toString() {
        return "InventarioJuegos[juego=" + juego.getNombre() + ", disponible=" + cantidadDisponible + "]";
    }
}