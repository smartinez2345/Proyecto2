package modelo;

public class Administrador extends Usuario {
    private static final long serialVersionUID = 1L;

    public Administrador(String login, String password) {
        super(login, password);
    }

    public void gestionarInventario(InventarioJuegos inv, Juego j, int cantidad) {
        inv.actualizarCantidad(cantidad);
    }

    @Override
    public String toString() {
        return "Administrador[login=" + getLogin() + "]";
    }
}