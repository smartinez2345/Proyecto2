package modelo;

public abstract class Empleado extends Usuario {
    private static final long serialVersionUID = 1L;
    
    private String turnoSemana;
    private double descuento;
    private boolean enTurno;

    public Empleado(String login, String password, String turnoSemana) {
        super(login, password);
        this.turnoSemana = turnoSemana;
        this.descuento = 0.20;
        this.enTurno = false;
    }

    public String getTurnoSemana() { return turnoSemana; }
    public void setTurnoSemana(String turnoSemana) { this.turnoSemana = turnoSemana; }
    public double getDescuento() { return descuento; }

    public boolean estaEnTurno() {
        return enTurno;
    }

    public void setEnTurno(boolean enTurno) {
        this.enTurno = enTurno;
    }

    @Override
    public String toString() {
        return "Empleado[login=" + getLogin() + ", turno=" + turnoSemana + "]";
    }
}