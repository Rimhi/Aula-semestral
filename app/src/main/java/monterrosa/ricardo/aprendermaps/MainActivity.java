package monterrosa.ricardo.aprendermaps;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG ="hola" ;
    private EditText correo,contraseña;
    private FirebaseAuth mAuth;
    private  FirebaseAuth.AuthStateListener authStateListener;
    private ProgressDialog progreso;
    private static final int COD_PERMISOS = 3452;
    private DatabaseReference correosAdmin;
    private DatabaseReference reference;
    private  String Verificacion = "rimhi7@gmail.com";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pedirPermisosFaltantes();
        mAuth = FirebaseAuth.getInstance();
        contraseña = findViewById(R.id.contraseñaInspector);
        correo = findViewById(R.id.correoInspector);
        progreso = new ProgressDialog(this);
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser()!=null){
                    Log.d(TAG, "signInWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user.getEmail().equals(Verificacion)) {
                        Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(MainActivity.this, InspectorActivity.class);
                        startActivity(intent);

                    }

                }else {
                    //Toast.makeText(MainActivity.this, "Datos Incorrectos", Toast.LENGTH_SHORT).show();
                }

            }
        };




    }
    public void recuperarcontraseña(View view){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (correo.getText().toString().isEmpty()){
            Toast.makeText(this, "por favor ingresa el correo", Toast.LENGTH_SHORT).show();
        }
        else {
            auth.sendPasswordResetEmail(correo.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(MainActivity.this, "Correo electronico enviado", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(MainActivity.this, "ha ocurrido un error, intenta de nuevo o verifica tu email", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    public void irMapInspector(View view){
        if (correo.getText().toString().equals("") || contraseña.getText().toString().equals("")) {
                        Toast.makeText(MainActivity.this,"Campos Vacios",Toast.LENGTH_LONG).show();
        }else {
            progreso.setMessage("Iniciando...");
            progreso.show();
            mAuth.signInWithEmailAndPassword(correo.getText().toString(), contraseña.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user.getEmail().equals(Verificacion)) {
                                    progreso.dismiss();
                                    Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                                    startActivity(intent);
                                } else {
                                    progreso.dismiss();
                                    Intent intent = new Intent(MainActivity.this, InspectorActivity.class);
                                    startActivity(intent);

                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(MainActivity.this, "Usuario o Contraseña Erroneos",
                                        Toast.LENGTH_SHORT).show();
                                progreso.dismiss();
                            }
                        }
                    });
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }
    public void onclick(View view){
        startActivity(new Intent(MainActivity.this,RegistroActivity.class));
    }
    private boolean pedirPermisosFaltantes(){
        boolean todosConsedidos = true;
        ArrayList<String> permisosFaltantes = new ArrayList<>();

        boolean permisoCoarse = ( ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED);

        boolean permisoFine = ( ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED);
        boolean permisoStorage = (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED);
        boolean permisoStorage2 = (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED);


        if(!permisoCoarse){
            todosConsedidos = false;
            permisosFaltantes.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if(!permisoFine){
            todosConsedidos = false;
            permisosFaltantes.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (!permisoStorage){
            todosConsedidos = false;
            permisosFaltantes.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permisoStorage2){
            todosConsedidos = false;
            permisosFaltantes.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }


        if(!todosConsedidos) {
            String[] permisos = new String[permisosFaltantes.size()];
            permisos = permisosFaltantes.toArray(permisos);

            ActivityCompat.requestPermissions(this, permisos, COD_PERMISOS);
        }

        return todosConsedidos;
    }

}
