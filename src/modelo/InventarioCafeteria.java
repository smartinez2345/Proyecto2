package modelo;

import java.io.Serializable;

public class InventarioCafeteria implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private ProductoCafeteria producto;
    private int stock;

    public InventarioCafeteria(ProductoCafeteria producto, int stock) {
        this.producto = producto;
        this.stock = stock;
    }

    public ProductoCafeteria getProducto() { return producto; }
    public int getStock() { return stock; }

    public void actualizarStock(int cantidad) {
        this.stock += cantidad;
    }

    public boolean estaDisponible() {
        return stock > 0;
    }

    @Override
    public String toString() {
        return "InventarioCafeteria[producto=" + producto.getNombre() + ", stock=" + stock + "]";
    }
}