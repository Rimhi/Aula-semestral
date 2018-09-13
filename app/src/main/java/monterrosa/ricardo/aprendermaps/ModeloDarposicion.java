package monterrosa.ricardo.aprendermaps;

/**
 * Created by Ricardo Monterrosa H on 8/09/2018.
 */

public class ModeloDarposicion {
    public double latitud,longitud;
    public String iddarposicioninspector,nombredarposicioninspector,telefonodarposicioninspector,ceduladarposiciontelefonoinspector;

    public ModeloDarposicion(double latitud, double longitud, String iddarposicioninspector, String nombredarposicioninspector, String telefonodarposicioninspector, String ceduladarposiciontelefonoinspector) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.iddarposicioninspector = iddarposicioninspector;
        this.nombredarposicioninspector = nombredarposicioninspector;
        this.telefonodarposicioninspector = telefonodarposicioninspector;
        this.ceduladarposiciontelefonoinspector = ceduladarposiciontelefonoinspector;

    }

    public ModeloDarposicion() {
    }
}
