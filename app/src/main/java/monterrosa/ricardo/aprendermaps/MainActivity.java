package monterrosa.ricardo.aprendermaps;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
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

public class MainActivity extends AppCompatActivity {
    private static final String TAG ="hola" ;
    private EditText correo,contraseña;
    private FirebaseAuth mAuth;
    private  FirebaseAuth.AuthStateListener authStateListener;
    private ProgressDialog progreso;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                    String correo = user.getEmail();
                    if (user.getEmail().equals("rimhi7@gmail.com")) {
                        Intent intent = new Intent(MainActivity.this, InspectorActivity.class);
                        intent.putExtra("correo", correo);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                        intent.putExtra("correo", correo);
                        startActivity(intent);
                    }

                }else {
                    //Toast.makeText(MainActivity.this, "Datos Incorrectos", Toast.LENGTH_SHORT).show();
                }

            }
        };


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
                                String correo = user.getEmail();
                                Toast.makeText(MainActivity.this, user.getDisplayName(), Toast.LENGTH_LONG).show();
                                if (user.getEmail().equals("rimhi7@gmail.com")) {
                                    progreso.dismiss();
                                    Intent intent = new Intent(MainActivity.this, InspectorActivity.class);
                                    intent.putExtra("correo", correo);
                                    startActivity(intent);
                                } else {
                                    progreso.dismiss();
                                    Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                                    intent.putExtra("correo", correo);
                                    startActivity(intent);
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(MainActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
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

}
