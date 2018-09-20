package monterrosa.ricardo.aprendermaps;

/**
 * Created by Ricardo Monterrosa H on 10/09/2018.
 */

public class Modelochat {
    private String Id,Mensaje,Horamensaje,Nombre;

    public Modelochat(String id, String mensaje, String horamensaje, String nombre) {
        Id = id;
        Mensaje = mensaje;
        Horamensaje = horamensaje;
        Nombre = nombre;
    }

    public Modelochat() {
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getMensaje() {
        return Mensaje;
    }

    public void setMensaje(String mensaje) {
        Mensaje = mensaje;
    }

    public String getHoramensaje() {
        return Horamensaje;
    }

    public void setHoramensaje(String horamensaje) {
        Horamensaje = horamensaje;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }
}
