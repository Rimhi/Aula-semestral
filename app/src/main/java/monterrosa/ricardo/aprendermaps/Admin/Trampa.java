package monterrosa.ricardo.aprendermaps.Admin;

/**
 * Created by Ricardo Monterrosa H on 17/08/2018.
 */

public class Trampa {
    public String id;
    public String fechaInicio;
    public String Tipo_trampa;
    public double latitud, longitud;

    public Trampa(String id, String fechaInicio, double latitud, double longitud, String tipo_trapma) {
        this.id = id;
        this.fechaInicio = fechaInicio;
        this.latitud = latitud;
        this.longitud = longitud;
        this.Tipo_trampa = tipo_trapma;
    }

    public Trampa() {
    }

    @Override
    public String toString() {
        return "id: "+id;
    }
}
