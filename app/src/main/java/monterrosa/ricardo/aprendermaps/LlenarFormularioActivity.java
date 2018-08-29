package monterrosa.ricardo.aprendermaps;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LlenarFormularioActivity extends AppCompatActivity {
    EditText CodigoTrampa, fechacoleccion;
    Button Guardar;
    private DatabaseReference baseDatos;
    private DatabaseReference trampaRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_llenar_formulario);
        CodigoTrampa = findViewById(R.id.CodigoTrampa);
        fechacoleccion = findViewById(R.id.FechaColeccion);
        Guardar = findViewById(R.id.GuardarFormulario);

        fechacoleccion.setText(fechaactual());
        CodigoTrampa.setText(getIntent().getStringExtra("codigotrampa"));
        baseDatos = FirebaseDatabase.getInstance().getReference();

        trampaRef = baseDatos.child("trampas");
        baseDatos.child("Fecha_llenar_formulario");
        Guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LlegadaMapa llegadaMapa = new LlegadaMapa(fechaactual(),CodigoTrampa.getText().toString(),"Ricardo");
                baseDatos.child("Fecha_llenar_formulario").child(CodigoTrampa.getText().toString()).setValue(llegadaMapa);
            }
        });
    }
    public String fechaactual(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();

        String fecha = dateFormat.format(date);

        return  fecha;
    }
}
