package monterrosa.ricardo.aprendermaps.Inspector;

import java.util.ArrayList;

/**
 * Created by Ricardo Monterrosa H on 21/08/2018.
 */

public class LlegadaMapa {
    public String Fecha, idTrampa,NombreColector,cedula,correo,idinspector;

    public LlegadaMapa(String fecha, String idTrampa, String nombreColector,String cedula,String correo,String idInpector) {
        Fecha = fecha;
        this.idTrampa = idTrampa;
        NombreColector = nombreColector;
        this.cedula = cedula;
        this.correo = correo;
        this.idinspector = idInpector;
    }

    public LlegadaMapa() {
    }
}