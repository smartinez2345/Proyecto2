package modelo;

import java.util.ArrayList;
import java.util.List;

public class Mesero extends Empleado {
    private static final long serialVersionUID = 1L;
    
    private List<Juego> juegosConocidos;

    public Mesero(String login, String password, String turnoSemana) {
        super(login, password, turnoSemana);
        this.juegosConocidos = new ArrayList<>();
    }

    public List<Juego> getJuegosConocidos() { return juegosConocidos; }

    public void agregarJuegoConocido(Juego j) {
        if (!juegosConocidos.contains(j)) {
            juegosConocidos.add(j);
        }
    }

    public boolean conoceJuego(Juego j) {
        return juegosConocidos.contains(j);
    }

    @Override
    public String toString() {
        return "Mesero[login=" + getLogin() + ", juegosConocidos=" + juegosConocidos.size() + "]";
    }
}