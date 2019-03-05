package monterrosa.ricardo.aprendermaps;

/**
 * Created by Ricardo Monterrosa H on 19/02/2019.
 */

public class ModeloForma {
    public String centro_acopio,fecha_actual,semana,oficina,responsabe,nombre_colector,registro_ruta,nombre_predio,codigo_ruta,
    codigo_trampa,municipio,tipo_atrayente,anastrepha,ceratis,otro,fenologia,estado_trampa,observaciones;

    public ModeloForma(String centro_acopio, String fecha_actual, String semana, String oficina, String responsabe,
                       String nombre_colector, String registro_ruta, String nombre_predio, String codigo_ruta,
                       String codigo_trampa, String municipio, String tipo_atrayente, String anastrepha, String ceratis,
                       String otro, String fenologia, String estado_trampa, String observaciones) {
        this.centro_acopio = centro_acopio;
        this.fecha_actual = fecha_actual;
        this.semana = semana;
        this.oficina = oficina;
        this.responsabe = responsabe;
        this.nombre_colector = nombre_colector;
        this.registro_ruta = registro_ruta;
        this.nombre_predio = nombre_predio;
        this.codigo_ruta = codigo_ruta;
        this.codigo_trampa = codigo_trampa;
        this.municipio = municipio;
        this.tipo_atrayente = tipo_atrayente;
        this.anastrepha = anastrepha;
        this.ceratis = ceratis;
        this.otro = otro;
        this.fenologia = fenologia;
        this.estado_trampa = estado_trampa;
        this.observaciones = observaciones;
    }
    public ModeloForma(){

    }
}
