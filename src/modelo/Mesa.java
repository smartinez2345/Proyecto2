package modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Mesa implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int cantidadPersonas;
    private boolean tieneMenores5;
    private boolean tieneMenores18;
    private boolean ocupada;
    private Cliente cliente;
    private List<Juego> juegosPrestados;
    private boolean tieneBebidaCaliente;

    public Mesa(int cantidadPersonas, boolean tieneMenores5, boolean tieneMenores18, Cliente cliente) {
        this.cantidadPersonas = cantidadPersonas;
        this.tieneMenores5 = tieneMenores5;
        this.tieneMenores18 = tieneMenores18;
        this.cliente = cliente;
        this.ocupada = true;
        this.juegosPrestados = new ArrayList<>();
        this.tieneBebidaCaliente = false;
    }

    public int getCantidadPersonas() { return cantidadPersonas; }
    public boolean isTieneMenores5() { return tieneMenores5; }
    public boolean isTieneMenores18() { return tieneMenores18; }
    public boolean isOcupada() { return ocupada; }
    public Cliente getCliente() { return cliente; }

    public void ocupar() { this.ocupada = true; }
    public void liberar() { 
        this.ocupada = false; 
        this.juegosPrestados.clear();
        this.tieneBebidaCaliente = false;
    }

    public void agregarJuego(Juego j) {
        juegosPrestados.add(j);
    }

    public void removerJuego(Juego j) {
        juegosPrestados.remove(j);
    }

    public boolean tieneJuegoDeAccion() {
        for (Juego j : juegosPrestados) {
            if (j.getCategoria().equalsIgnoreCase("ACCION")) {
                return true;
            }
        }
        return false;
    }

    public void setTieneBebidaCaliente(boolean value) {
        this.tieneBebidaCaliente = value;
    }

    public boolean tieneBebidaCaliente() {
        return tieneBebidaCaliente;
    }

    @Override
    public String toString() {
        return "Mesa[cant=" + cantidadPersonas + ", menores5=" + tieneMenores5 + 
               ", menores18=" + tieneMenores18 + ", cliente=" + cliente.getLogin() + "]";
    }
}