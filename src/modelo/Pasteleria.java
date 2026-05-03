package modelo;

import java.util.ArrayList;
import java.util.List;

public class Pasteleria extends ProductoCafeteria {
    private static final long serialVersionUID = 1L;
    
    private List<String> alergenos;

    public Pasteleria(String nombre, double precio) {
        super(nombre, precio);
        this.alergenos = new ArrayList<>();
    }

    public List<String> getAlergenos() { return alergenos; }

    public void agregarAlergeno(String alergeno) {
        alergenos.add(alergeno);
    }	

    public boolean tieneAlergeno(String alergeno) {
        return alergenos.contains(alergeno);
    }

    @Override
    public String toString() {
        return "Pasteleria[nombre=" + getNombre() + ", alergenos=" + alergenos + "]";
    }
}