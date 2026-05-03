package modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Venta implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private static final double IVA = 0.19;
    private static final double IMP_CONSUMO = 0.08;

    private Date fecha;
    private String tipoVenta;
    private double impuestos;
    private double propina;
    private double descuento;
    private double descuentoPuntos;
    private List<DetalleVenta> detalles;
    private Cliente cliente;

    public Venta(String tipoVenta, Cliente cliente) {
        this.fecha = new Date();
        this.tipoVenta = tipoVenta;
        this.cliente = cliente;
        this.detalles = new ArrayList<>();
        this.propina = 0;
        this.descuento = 0;
        this.descuentoPuntos = 0;
    }

    public void agregarDetalle(DetalleVenta detalle) { detalles.add(detalle); }

    public double calcularSubtotal() {
        double subtotal = 0;
        for (DetalleVenta d : detalles) subtotal += d.calcularSubtotal();
        subtotal -= subtotal * descuento;
        subtotal -= descuentoPuntos;
        return Math.max(subtotal, 0);
    }

    public double calcularImpuesto() {
        double subtotal = calcularSubtotal();
        impuestos = tipoVenta.equals("JUEGOS") ? subtotal * IVA : subtotal * IMP_CONSUMO;
        return impuestos;
    }

    public double calcularTotal() {
        return calcularSubtotal() + calcularImpuesto() + propina;
    }

    public int calcularPuntosFidelidad() {
        return (int) (calcularTotal() * 0.01);
    }

    public void setPropina(double propina) { this.propina = propina; }
    public void setPropinaSugerida() { this.propina = calcularSubtotal() * 0.10; }
    public void setDescuento(double descuento) { this.descuento = descuento; }
    public void setDescuentoPuntos(double descuentoPuntos) { this.descuentoPuntos = descuentoPuntos; }

    public Date getFecha() { return fecha; }
    public String getTipoVenta() { return tipoVenta; }
    public double getImpuestos() { return impuestos; }
    public double getPropina() { return propina; }
    public double getDescuento() { return descuento; }
    public List<DetalleVenta> getDetalles() { return detalles; }
    public Cliente getCliente() { return cliente; }

    @Override
    public String toString() {
        return "Venta[tipo=" + tipoVenta + ", total=" + String.format("%.2f", calcularTotal()) + "]";
    }
}