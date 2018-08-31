package monterrosa.ricardo.aprendermaps;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contraseña = findViewById(R.id.contraseñaInspector);
        correo = findViewById(R.id.correoInspector);
        mAuth = FirebaseAuth.getInstance();
    }

    public void irMapInspector(View view){
        if (correo.getText().toString().equals("") || contraseña.getText().toString().equals("")) {
                        Toast.makeText(MainActivity.this,"Campos Vacios",Toast.LENGTH_LONG).show();
        }else {
            mAuth.signInWithEmailAndPassword(correo.getText().toString(), contraseña.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                                String correo = user.getEmail();
                                Toast.makeText(MainActivity.this, user.getDisplayName(), Toast.LENGTH_LONG).show();
                                if (user.getEmail().equals("rimhi7@gmail.com")) {
                                    Intent intent = new Intent(MainActivity.this, InspectorActivity.class);
                                    intent.putExtra("correo", correo);
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                                    intent.putExtra("correo", correo);
                                    startActivity(intent);
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(MainActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }

                            // ...
                        }
                    });
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    private void updateUI(FirebaseUser user) {
        if (user != null) {

        }
    }


}
