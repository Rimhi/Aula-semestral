package monterrosa.ricardo.aprendermaps;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListView;

public class InformacionTrampaActivity extends AppCompatActivity {
    ListView fechasdeInspeccion;
    EditText CodigTrampa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion_trampa);
        fechasdeInspeccion = findViewById(R.id.fechasInspeccion);
        CodigTrampa = findViewById(R.id.editaridtrampa);
        CodigTrampa.setText(getIntent().getStringExtra("codigotrampa"));
    }
}
