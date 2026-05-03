package modelo;

import java.util.ArrayList;
import java.util.List;

public class Cliente extends Usuario {
    private static final long serialVersionUID = 1L;
    
    private List<Juego> juegosFavoritos;
    private int puntosFidelidad;

    public Cliente(String login, String password) {
        super(login, password);
        this.juegosFavoritos = new ArrayList<>();
        this.puntosFidelidad = 0;
    }

    public List<Juego> getJuegosFavoritos() { return juegosFavoritos; }

    public void agregarJuegoFavorito(Juego j) {
        if (!juegosFavoritos.contains(j)) {
            juegosFavoritos.add(j);
        }
    }

    public int getPuntosFidelidad() { return puntosFidelidad; }

    public void agregarPuntos(int puntos) {
        this.puntosFidelidad += puntos;
    }

    public boolean usarPuntos(int puntos) {
        if (puntos <= puntosFidelidad) {
            puntosFidelidad -= puntos;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Cliente[login=" + getLogin() + ", puntos=" + puntosFidelidad + "]";
    }
}