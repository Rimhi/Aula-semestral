package monterrosa.ricardo.aprendermaps;

/**
 * Created by Ricardo Monterrosa H on 17/08/2018.
 */

public class Trampa {
    public long id;
    public String fechaInicio;
    public String direccion;
    public double latitud, longitud;

    public Trampa(long id, String fechaInicio, String direccion, double latitud, double longitud) {
        this.id = id;
        this.fechaInicio = fechaInicio;
        this.direccion = direccion;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public Trampa() {
    }

    @Override
    public String toString() {
        return "id: "+id+", direccion: "+direccion;
    }
}
