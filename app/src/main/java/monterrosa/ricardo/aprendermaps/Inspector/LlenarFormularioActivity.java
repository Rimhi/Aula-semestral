package monterrosa.ricardo.aprendermaps.Inspector;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.codec.Base64;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import monterrosa.ricardo.aprendermaps.ModeloRegistro;
import monterrosa.ricardo.aprendermaps.R;


public class LlenarFormularioActivity extends AppCompatActivity {
    private EditText CodigoTrampa, fechacoleccion,CentroAcopio,Oficina,nombreColector,Nombredelaruta,semana,responsable,registroruta,codigoruta,firma;
    Button Guardar;
    private DatabaseReference baseDatos;
    private DatabaseReference usuarios;
    private DatabaseReference trampas;
    private String Nombre,Cedula,correo;
    private FirebaseAuth auth;
    private String TAG = "DatoCopiar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_llenar_formulario);
        Oficina = findViewById(R.id.oficina);
        nombreColector = findViewById(R.id.NombreColector);
        Nombredelaruta = findViewById(R.id.NombreRuta);
        semana = findViewById(R.id.Numerosemanaepidemiologica);
        responsable = findViewById(R.id.Responsable);
        registroruta = findViewById(R.id.RegistroRuta);
        codigoruta = findViewById(R.id.CodigoRuta);
        CodigoTrampa = findViewById(R.id.CodigoTrampa);
        fechacoleccion = findViewById(R.id.FechaColeccion);
        firma = findViewById(R.id.firmadueñopredio);
        Guardar = findViewById(R.id.GuardarFormulario);
        auth = FirebaseAuth.getInstance();
        fechacoleccion.setText(fechaactual());
        CodigoTrampa.setText(getIntent().getStringExtra("codigotrampa"));
        baseDatos = FirebaseDatabase.getInstance().getReference();
        usuarios = baseDatos.child("Usuarios");
        CentroAcopio = findViewById(R.id.CentroAcopio);
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
                LlenarPedf(new File(Environment.getExternalStorageDirectory().toString(),"Reportes")+"");

            }
        });
    }
    private String fechaactual(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Date date = new Date();

        String fecha = dateFormat.format(date);

        return  fecha;
    }
    private void LlenarPedf(String destino){
        File folder = new File(Environment.getExternalStorageDirectory().toString(),"Reportes");
        if (!folder.exists()){
            folder.mkdirs();
            CopyRawToSDCard((R.raw.forma),Environment.getExternalStorageDirectory()+"/forma.pdf");
        }
        else {
            CopyRawToSDCard((R.raw.forma),Environment.getExternalStorageDirectory()+"/forma.pdf");
        }

        try {
            PdfReader pdfReader = new PdfReader(getResources().openRawResource(R.raw.forma));
            PdfStamper stamper = new PdfStamper(pdfReader,new FileOutputStream(destino+"/forma.pdf"));
            AcroFields acroFields = stamper.getAcroFields();
            acroFields.setField("Centro_de _acopio",CentroAcopio.getText()+"");
            acroFields.setField("Fecha_ddmmaaaa",fechaactual());
            acroFields.setField("Semana",semana.getText()+"");
            acroFields.setField("Oficina",Oficina.getText()+"");
            acroFields.setField("Responsable",responsable.getText()+"");
            acroFields.setField("colectorRow1",nombreColector.getText()+"");
            acroFields.setField("Registro_ruta",registroruta.getText()+"");
            acroFields.setField("Nombrepredioempresa",Nombredelaruta.getText()+"");
            acroFields.setField("Codigo_ruta",codigoruta.getText()+"");
            acroFields.setField("CODIGOTRAMPARow1.0",CodigoTrampa.getText()+"");
            acroFields.setField("MUNICIPIORow1","Aqui");
            acroFields.setField("TIPO_ATRAYENTERow1","Aqui");
            acroFields.setField("AnastrephaRow1","Aqui");
            acroFields.setField("CeratitisRow1","Aqui");
            acroFields.setField("OtrosRow1","Aqui");
            acroFields.setField("FENOLOGIARow1","Aqui");
            acroFields.setField("ESTADOTRAMPARow1","Aqui");
            acroFields.setField("OBSERVACIONESRow1","Aqui");
            acroFields.setField("FIRMAPROPIETARIORow1","Aqui");
            stamper.close();
            pdfReader.close();
            Log.e("Datos", acroFields.getFields()+"");

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void cratePdf(String nombre){
        Document document = new Document();
        try {
            PdfWriter.getInstance(document,new FileOutputStream(nombre));
            document.open();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
    private void CopyRawToSDCard(int id, String path) {
        InputStream in = getResources().openRawResource(id);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(path);
            byte[] buff = new byte[1024];
            int read = 0;
            while ((read = in.read(buff)) > 0) {
                out.write(buff, 0, read);
            }
            in.close();
            out.close();
            Log.i(TAG, "copyFile, success!");
        } catch (FileNotFoundException e) {
            Log.e(TAG, "copyFile FileNotFoundException " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "copyFile IOException " + e.getMessage());
        }
    }

}
