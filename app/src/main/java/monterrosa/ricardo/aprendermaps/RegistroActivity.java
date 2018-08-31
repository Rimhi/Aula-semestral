package monterrosa.ricardo.aprendermaps;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistroActivity extends AppCompatActivity {
    private DatabaseReference miBasedatos;
    private DatabaseReference databaseReference;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth auth;
    private EditText correo,contraseña,nombre,contraseña2;
    private ProgressDialog progreso;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        auth = FirebaseAuth.getInstance();
        correo = findViewById(R.id.RegistroCorreoInspector);
        contraseña = findViewById(R.id.RegistroContraseñaInspector);
        nombre = findViewById(R.id.RegistroNombreInspector);
        contraseña2 = findViewById(R.id.RegistroRepetirContraseñaInspector);
        progreso = new ProgressDialog(this);
    }
    public void irInicio(View view){
        startActivity(new Intent(RegistroActivity.this,MainActivity.class));
    }
    public void registrar(View view){
        if (correo.getText().toString().equals("") || contraseña.getText().toString().equals("") || contraseña2.getText().toString().equals("") || nombre.getText().toString().equals(""))
        {
            Toast.makeText(RegistroActivity.this,"Campos Vacios",Toast.LENGTH_SHORT).show();
        }else {
            if (contraseña.getText().toString().equals(contraseña2.getText().toString())) {

                auth.createUserWithEmailAndPassword(correo.getText().toString(),contraseña.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progreso.setMessage("Registrando ...");
                                progreso.show();
                                if (task.isSuccessful()){
                                    auth.signInWithEmailAndPassword(correo.getText().toString(),contraseña.getText().toString());
                                    databaseReference = FirebaseDatabase.getInstance().getReference().child("Usuarios");
                                    miBasedatos = databaseReference.child(auth.getCurrentUser().getUid());
                                    miBasedatos.child("Nombre").setValue(nombre.getText().toString());
                                    miBasedatos.child("imagen").setValue("default");
                                    progreso.dismiss();
                                }else {
                                    Toast.makeText(RegistroActivity.this,"Error al Registrar Usuario",Toast.LENGTH_SHORT).show();
                                    progreso.dismiss();
                                }
                            }
                        });
            }else {
                Toast.makeText(RegistroActivity.this,"Contraseñas Distintas",Toast.LENGTH_SHORT).show();
            }
        }


    }
}
