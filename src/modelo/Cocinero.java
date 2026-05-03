package modelo;

public class Cocinero extends Empleado {
    private static final long serialVersionUID = 1L;

    public Cocinero(String login, String password, String turnoSemana) {
        super(login, password, turnoSemana);
    }

    @Override
    public String toString() {
        return "Cocinero[login=" + getLogin() + ", turno=" + getTurnoSemana() + "]";
    }
}