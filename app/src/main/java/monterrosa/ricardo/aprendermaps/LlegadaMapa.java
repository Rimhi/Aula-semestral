package monterrosa.ricardo.aprendermaps;

import java.util.ArrayList;

/**
 * Created by Ricardo Monterrosa H on 21/08/2018.
 */

public class LlegadaMapa {
    public String Fecha, idTrampa,NombreColector;

    public LlegadaMapa(String fecha, String idTrampa, String nombreColector) {
        Fecha = fecha;
        this.idTrampa = idTrampa;
        NombreColector = nombreColector;
    }

    public LlegadaMapa() {
    }
}
