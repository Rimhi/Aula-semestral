package monterrosa.ricardo.aprendermaps;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListView;

public class InformacionTrampaActivity extends AppCompatActivity {
    ListView fechasdeInspeccion;
    EditText CodigTrampa,indicio,posicion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion_trampa);
        fechasdeInspeccion = findViewById(R.id.fechasInspeccion);
        CodigTrampa = findViewById(R.id.editaridtrampa);
        indicio = findViewById(R.id.editarLugardelatrampa);
        posicion = findViewById(R.id.editarlatlangtrampa);

        CodigTrampa.setText(getIntent().getStringExtra("codigotrampa"));
        indicio.setText(getIntent().getStringExtra("indicio"));
        posicion.setText(getIntent().getStringExtra("posicion"));

    }
}
