package persistencia;

import modelo.Cafe;
import java.io.*;

public class Persistencia {

    public static void guardar(Cafe cafe, String archivo) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivo))) {
            oos.writeObject(cafe);
        }
    }

    public static Cafe cargar(String archivo) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            return (Cafe) ois.readObject();
        }
    }
}