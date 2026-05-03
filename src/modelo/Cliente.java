package modelo;

import java.util.ArrayList;
import java.util.List;

public class Cliente extends Usuario {
    private static final long serialVersionUID = 1L;

    private List<Juego> juegosFavoritos;
    private int puntosFidelidad;
    // Bono de descuento ganado en torneo amistoso (no acumulable con otros descuentos)
    // 0.0 = sin bono activo; > 0.0 = porcentaje de descuento activo (ej. 0.15 = 15%)
    private double bonoDescuento;

    public Cliente(String login, String password) {
        super(login, password);
        this.juegosFavoritos = new ArrayList<>();
        this.puntosFidelidad = 0;
        this.bonoDescuento = 0.0;
    }

    // ─── Juegos favoritos ─────────────────────────────────────────────
    public List<Juego> getJuegosFavoritos() { return juegosFavoritos; }

    public void agregarJuegoFavorito(Juego j) {
        if (!juegosFavoritos.contains(j)) juegosFavoritos.add(j);
    }

    public boolean esFanaticoDeJuego(Juego j) {
        return juegosFavoritos.contains(j);
    }

    // ─── Puntos fidelidad ─────────────────────────────────────────────
    public int getPuntosFidelidad() { return puntosFidelidad; }

    public void agregarPuntos(int puntos) { this.puntosFidelidad += puntos; }

    public boolean usarPuntos(int puntos) {
        if (puntos <= puntosFidelidad) {
            puntosFidelidad -= puntos;
            return true;
        }
        return false;
    }

    // ─── Bono de descuento torneo amistoso ────────────────────────────

    /** Retorna el porcentaje de bono activo (0.0 si no hay bono). */
    public double getBonoDescuento() { return bonoDescuento; }

    /** ¿Tiene bono de descuento activo? */
    public boolean tieneBonoDescuento() { return bonoDescuento > 0.0; }

    /**
     * Asigna un bono de descuento ganado en torneo amistoso.
     * Reemplaza el anterior (no acumulable).
     */
    public void asignarBonoDescuento(double porcentaje) {
        this.bonoDescuento = porcentaje;
    }

    /**
     * Usa el bono de descuento y lo elimina (se consume al usarse).
     * @return el porcentaje consumido, o 0.0 si no había bono
     */
    public double usarBonoDescuento() {
        double bono = this.bonoDescuento;
        this.bonoDescuento = 0.0;
        return bono;
    }

    @Override
    public String toString() {
        return "Cliente[login=" + getLogin()
            + ", puntos=" + puntosFidelidad
            + ", bono=" + (bonoDescuento > 0 ? (int)(bonoDescuento*100)+"%" : "ninguno") + "]";
    }

}