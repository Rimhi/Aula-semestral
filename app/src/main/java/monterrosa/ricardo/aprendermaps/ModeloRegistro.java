package monterrosa.ricardo.aprendermaps;

/**
 * Created by Ricardo Monterrosa H on 31/08/2018.
 */

public class ModeloRegistro {
    String Nombre;
    String Cedula;
    String Telefono;
    String correo;
    String Direccion;
    String IDguidDatabase;
    String imagen;
    String fechaRegistro;

    public ModeloRegistro(String id,String nombre, String cedula, String telefono, String correo, String direccion, String imagen, String Fecharegistro) {
        IDguidDatabase = id;
        Nombre = nombre;
        Cedula = cedula;
        Telefono = telefono;
        this.correo = correo;
        Direccion = direccion;
        this.imagen = imagen;
        fechaRegistro = Fecharegistro;
    }

    public ModeloRegistro() {
    }

    @Override
    public String toString() {
        return "nombre: "+Nombre+", Cedula: "+Cedula;
    }
}
