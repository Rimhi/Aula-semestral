package monterrosa.ricardo.aprendermaps.Admin;

/**
 * Created by Ricardo Monterrosa H on 17/08/2018.
 */

public class Trampa {
    public long id;
    public String fechaInicio;
    public String direccion;
    public String Tipo_trampa;
    public double latitud, longitud;

    public Trampa(long id, String fechaInicio, String direccion, double latitud, double longitud, String tipo_trapma) {
        this.id = id;
        this.fechaInicio = fechaInicio;
        this.direccion = direccion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.Tipo_trampa = tipo_trapma;
    }

    public Trampa() {
    }

    @Override
    public String toString() {
        return "id: "+id+", direccion: "+direccion;
    }
}
