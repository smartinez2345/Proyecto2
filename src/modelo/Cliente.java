package modelo;

import java.util.ArrayList;
import java.util.List;

public class Cliente extends Usuario {
    private static final long serialVersionUID = 1L;

    private List<Juego> juegosFavoritos;
    private int puntosFidelidad;
    private List<Double> bonosDescuento;

    public Cliente(String login, String password) {
        super(login, password);
        this.juegosFavoritos = new ArrayList<>();
        this.puntosFidelidad = 0;
        this.bonosDescuento = new ArrayList<>();
    }

    public List<Juego> getJuegosFavoritos() { return juegosFavoritos; }

    public void agregarJuegoFavorito(Juego j) {
        if (!juegosFavoritos.contains(j)) juegosFavoritos.add(j);
    }

    public boolean esFanaticoDeJuego(Juego j) {
        return juegosFavoritos.contains(j);
    }

    public int getPuntosFidelidad() { return puntosFidelidad; }

    public void agregarPuntos(int puntos) { this.puntosFidelidad += puntos; }

    public boolean usarPuntos(int puntos) {
        if (puntos <= puntosFidelidad) {
            puntosFidelidad -= puntos;
            return true;
        }
        return false;
    }

    public List<Double> getBonosDescuento() { return bonosDescuento; }

    public boolean tieneBonosDescuento() { return !bonosDescuento.isEmpty(); }

    public void agregarBonoDescuento(double porcentaje) {
        bonosDescuento.add(porcentaje);
    }

    public double usarPrimerBonoDescuento() {
        if (bonosDescuento.isEmpty()) return 0.0;
        return bonosDescuento.remove(0);
    }

    public double usarBonoDescuento(int indice) {
        if (indice < 0 || indice >= bonosDescuento.size()) return 0.0;
        return bonosDescuento.remove(indice);
    }

    @Override
    public String toString() {
        return "Cliente[login=" + getLogin()
            + ", puntos=" + puntosFidelidad
            + ", bonos=" + bonosDescuento.size() + "]";
    }
}