package monterrosa.ricardo.aprendermaps;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegistroActivity extends AppCompatActivity {
    private DatabaseReference miBasedatos;
    private DatabaseReference databaseReference;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth auth;
    private EditText correo,contraseña,nombre,contraseña2,cedula,telefono,direccion;
    private ProgressDialog progreso;
    private StorageReference mStorageRef;
    private  static final int GALERY_INTENT = 1;
    private Uri file;
    private Uri downloadUrl;
    private ImageView imagen;
    private TextView subirimagen;
    private String Imagen;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        auth = FirebaseAuth.getInstance();
        correo = findViewById(R.id.RegistroCorreoInspector);
        contraseña = findViewById(R.id.RegistroContraseñaInspector);
        nombre = findViewById(R.id.RegistroNombreInspector);
        contraseña2 = findViewById(R.id.RegistroRepetirContraseñaInspector);
        cedula = findViewById(R.id.RegistroCedula);
        telefono = findViewById(R.id.RegistroTelefono);
        direccion = findViewById(R.id.RegistroDireccion);
        imagen = findViewById(R.id.RegistroImagen);
        subirimagen = findViewById(R.id.RegistroSubirimagen);
        progreso = new ProgressDialog(this);
        mStorageRef = FirebaseStorage.getInstance().getReference();

    }
    public void irInicio(View view){
        startActivity(new Intent(RegistroActivity.this,MainActivity.class));
    }
    public void registrar(View view){
        if (correo.getText().toString().isEmpty() || contraseña.getText().toString().isEmpty() || contraseña2.getText().toString().isEmpty()
                || nombre.getText().toString().isEmpty() || direccion.getText().toString().isEmpty() || telefono.getText().toString().isEmpty()) {
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
                                    ModeloRegistro registro = new ModeloRegistro(auth.getCurrentUser().getUid()+"",nombre.getText()+"",
                                            cedula.getText()+"", telefono.getText()+"",correo.getText()+"",
                                            direccion.getText()+"",Imagen+"",fechaactual()+"");
                                    miBasedatos.setValue(registro);
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALERY_INTENT && resultCode == RESULT_OK){
            file = data.getData();
        }
    }
    public void ObtenerUri(View view){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,GALERY_INTENT);
        imagen.setVisibility(View.VISIBLE);
        subirimagen.setVisibility(View.VISIBLE);
    }
    public void SubirArchivosAStorage(View view){


        StorageReference riversRef = mStorageRef.child(cedula.getText().toString()).child(file.getLastPathSegment());

        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        downloadUrl = taskSnapshot.getDownloadUrl();
                        Imagen = downloadUrl+"";
                        Glide.with(RegistroActivity.this)
                                .load(downloadUrl)
                                .fitCenter()
                                .centerCrop()
                                .into(imagen);



                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

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
