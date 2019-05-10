package monterrosa.ricardo.aprendermaps;

import android.Manifest;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.content.res.AppCompatResources;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import monterrosa.ricardo.aprendermaps.Admin.AdminActivity;
import monterrosa.ricardo.aprendermaps.Inspector.InspectorActivity;

public class MainActivity extends AppCompatActivity{
    private static final String TAG ="hola" ;
    private EditText correo,contraseña;
    private FirebaseAuth mAuth;
    private  FirebaseAuth.AuthStateListener authStateListener;
    private ProgressDialog progreso;
    private static final int COD_PERMISOS = 3452;
    private DatabaseReference reference;
    private DatabaseReference acceso;
    private DatabaseReference inhabilitado;
    private DatabaseReference admin, usuario_final;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MultiDex.install(this);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        pedirPermisosFaltantes();
        mAuth = FirebaseAuth.getInstance();
        contraseña = findViewById(R.id.contraseñaInspector);
        correo = findViewById(R.id.correoInspector);
        progreso = new ProgressDialog(this);
        reference = FirebaseDatabase.getInstance().getReference();
        inhabilitado = reference.child("inhabilitarCorreos");
        acceso = reference.child("habilitarCorreos");

        progreso.setMessage("Iniciando...");
        progreso.setCancelable(false);
        progreso.show();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {

                final ChildEventListener listener = acceso.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        if (firebaseAuth.getCurrentUser() != null)
                            if (dataSnapshot.getValue(String.class).equals(firebaseAuth.getCurrentUser().getEmail() + "")) {
                                final ChildEventListener listener = inhabilitado.addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                        if (firebaseAuth.getCurrentUser() != null)
                                            if (dataSnapshot.getValue().equals(firebaseAuth.getCurrentUser().getEmail() + "")) {
                                                mAuth.signOut();
                                                progreso.dismiss();
                                                Toast.makeText(MainActivity.this, "has sido INHABILITADO, consulta con tu administrador", Toast.LENGTH_SHORT).show();
                                            } else {
                                                if (firebaseAuth.getCurrentUser() != null) {
                                                    Log.d(TAG, "signInWithEmail:success");
                                                    final FirebaseUser user = mAuth.getCurrentUser();
                                                    admin = reference.child("Admin");
                                                    final ChildEventListener listener = admin.addChildEventListener(new ChildEventListener() {
                                                        @Override
                                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                                            if (dataSnapshot.getValue().toString().equals(user.getEmail() + "")) {
                                                                Intent intent = new Intent(MainActivity.this, AdminActivity.class).addFlags(
                                                                        Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                Toast.makeText(MainActivity.this, dataSnapshot.getValue() + " Correo admin", Toast.LENGTH_LONG).show();
                                                                progreso.dismiss();
                                                                startActivity(intent);

                                                            }else{
                                                                usuario_final  = reference.child("usuario_final");
                                                                final ChildEventListener listener = usuario_final.addChildEventListener(new ChildEventListener() {
                                                                    @Override
                                                                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                                                        if (dataSnapshot.getValue().toString().equals(user.getEmail() + "")) {
                                                                            Intent intent = new Intent(MainActivity.this, InspectorActivity.class).addFlags(
                                                                                    Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                            Toast.makeText(MainActivity.this, dataSnapshot.getValue() + " Correo user", Toast.LENGTH_LONG).show();
                                                                            progreso.dismiss();
                                                                            startActivity(intent);
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
                                                            }
                                                        }

                                                        @Override
                                                        public void onChildChanged(DataSnapshot dataSnapshot, String s){

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


                                                } else {
                                                     progreso.dismiss();
                                                    //Toast.makeText(MainActivity.this, "Datos Incorrectos", Toast.LENGTH_SHORT).show();
                                                }
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
                if (mAuth.getCurrentUser() == null && progreso.isShowing()){
                    progreso.dismiss();
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
            progreso.setMessage("Enviando ...");
            progreso.setCancelable(false);
            progreso.show();
            auth.sendPasswordResetEmail(correo.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                progreso.dismiss();
                                Toast.makeText(MainActivity.this, "Correo electrónico enviado", Toast.LENGTH_SHORT).show();
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
                        public void onComplete(@NonNull final Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.e("DATOS","CORRECTOS");
                                acceso.addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                        Log.e("entro acceso", dataSnapshot.getValue(String.class));
                                        if (dataSnapshot.getValue(String.class).equals(correo.getText()+"")) {
                                            inhabilitado.addChildEventListener(new ChildEventListener() {
                                                @Override
                                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                                    if (dataSnapshot.getValue(String.class).equals(correo.getText()+"")){
                                                        mAuth.signOut();
                                                        Toast.makeText(MainActivity.this, "has sido INHABILITADO, consulta con tu administrador", Toast.LENGTH_SHORT).show();
                                                        progreso.dismiss();
                                                    }
                                                    else {
                                                        Log.d(TAG, "signInWithEmail:success");
                                                        final FirebaseUser user = mAuth.getCurrentUser();
                                                        if (user!=null)
                                                            admin = reference.child("Admin");
                                                        admin.addChildEventListener(new ChildEventListener() {
                                                            @Override
                                                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                                                if (user.getEmail().equals(dataSnapshot.getValue(String.class))) {
                                                                    progreso.dismiss();
                                                                    Intent intent = new Intent(MainActivity.this, AdminActivity.class)
                                                                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                    startActivity(intent);
                                                                } else {
                                                                    progreso.dismiss();
                                                                    usuario_final  = reference.child("usuario_final");
                                                                    final ChildEventListener listener = usuario_final.addChildEventListener(new ChildEventListener() {
                                                                        @Override
                                                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                                                            if (dataSnapshot.getValue().toString().equals(user.getEmail() + "")) {
                                                                                Intent intent = new Intent(MainActivity.this, InspectorActivity.class).addFlags(
                                                                                        Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                                Toast.makeText(MainActivity.this, dataSnapshot.getValue() + " Correo user", Toast.LENGTH_LONG).show();
                                                                                // progreso.dismiss();
                                                                                startActivity(intent);
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

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(MainActivity.this, "Usuario o Contraseña Erroneos",Toast.LENGTH_SHORT).show();
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
        startActivity(new Intent(MainActivity.this,RegistroActivity.class)
        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
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
    public Drawable getDrawable(Resources res, int id){
        Drawable img = null;
        final int version = Build.VERSION.SDK_INT;
        if (version < 21) {
            img = res.getDrawable(id);
        }
        return img;
    }

}
