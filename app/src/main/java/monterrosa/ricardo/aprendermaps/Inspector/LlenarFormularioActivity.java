package monterrosa.ricardo.aprendermaps.Inspector;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import monterrosa.ricardo.aprendermaps.Admin.AdminActivity;
import monterrosa.ricardo.aprendermaps.Inspector.LlegadaMapa;
import monterrosa.ricardo.aprendermaps.ModeloRegistro;
import monterrosa.ricardo.aprendermaps.R;
import monterrosa.ricardo.aprendermaps.Servicios.ServiceNotificacion;

public class LlenarFormularioActivity extends AppCompatActivity {
    EditText CodigoTrampa, fechacoleccion;
    Button Guardar;
    private DatabaseReference baseDatos;
    private DatabaseReference usuarios;
    private DatabaseReference trampas;
    private String Nombre,Cedula,correo;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_llenar_formulario);
        CodigoTrampa = findViewById(R.id.CodigoTrampa);
        fechacoleccion = findViewById(R.id.FechaColeccion);
        Guardar = findViewById(R.id.GuardarFormulario);
        auth = FirebaseAuth.getInstance();
        fechacoleccion.setText(fechaactual());
        CodigoTrampa.setText(getIntent().getStringExtra("codigotrampa"));
        baseDatos = FirebaseDatabase.getInstance().getReference();
        usuarios = baseDatos.child("Usuarios");
        usuarios.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ModeloRegistro llegadaMapa = dataSnapshot.getValue(ModeloRegistro.class);
                if (auth.getCurrentUser().getUid().equals(llegadaMapa.IDguidDatabase)) {
                    Nombre = llegadaMapa.Nombre;
                    Cedula = llegadaMapa.Cedula;
                    correo = llegadaMapa.correo;
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /***
                 * Guarda la fecha de inspeccionde la trampa a la cual se le dio en Informacion
                 */
                LlegadaMapa llegadaMapa = new LlegadaMapa(fechaactual(),CodigoTrampa.getText().toString(),Nombre,Cedula,correo,auth.getCurrentUser().getUid());
                baseDatos.child("trampas").child(CodigoTrampa.getText().toString()).child("Inspeccion").child(fechaactual()).setValue(llegadaMapa);
                trampas = baseDatos.child("Inspecciones");
                trampas.child(fechaactual()+" "+CodigoTrampa.getText()).setValue(llegadaMapa);

            }
        });
    }
    private String fechaactual(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();

        String fecha = dateFormat.format(date);

        return  fecha;
    }
}
