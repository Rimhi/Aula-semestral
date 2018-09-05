package monterrosa.ricardo.aprendermaps;

/**
 * Created by Ricardo Monterrosa H on 31/08/2018.
 */

public class ModeloRegistro {
    public String Nombre;
    public String Cedula;
    public String Telefono;
    public String correo;
    public String Direccion;
    public String IDguidDatabase;
    public String imagen;
    public String fechaRegistro;

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

}
