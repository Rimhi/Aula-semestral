package monterrosa.ricardo.aprendermaps.Inspector;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignature;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PushbuttonField;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import monterrosa.ricardo.aprendermaps.ModeloRegistro;
import monterrosa.ricardo.aprendermaps.R;


public class LlenarFormularioActivity extends AppCompatActivity {
    private String path;
    private EditText CodigoTrampa, fechacoleccion,CentroAcopio,Oficina,nombreColector,Nombredelaruta,semana,
            responsable,registroruta,codigoruta,Municipio,Tipo_Atrayente,numeroanas,numeroceratis,numerootros,fenologia
            ,Estadotrampa,Observaciones;
    private Button Guardar,Guardarfirma,limpiarfirma,addformulario;
    private SignaturePad firma;
    private DatabaseReference baseDatos;
    private DatabaseReference usuarios;
    private DatabaseReference trampas;
    private String Nombre,Cedula,correo;
    private FirebaseAuth auth;
    private String TAG = "DatoCopiar";
    private int añadir;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_llenar_formulario);
        Oficina = findViewById(R.id.oficina);
        Municipio = findViewById(R.id.FormularioMunicipio);
        Tipo_Atrayente = findViewById(R.id.formularioTipoAtrayente);
        numeroanas = findViewById(R.id.numeroAnastrepha);
        numeroceratis = findViewById(R.id.NumeroCeratitis);
        numerootros = findViewById(R.id.numeroOtros);
        fenologia = findViewById(R.id.FormularioFenologia);
        Estadotrampa = findViewById(R.id.FormularioEstadotrampa);
        Observaciones = findViewById(R.id.FomuarioObservaciones);
        nombreColector = findViewById(R.id.NombreColector);
        Nombredelaruta = findViewById(R.id.NombreRuta);
        semana = findViewById(R.id.Numerosemanaepidemiologica);
        responsable = findViewById(R.id.Responsable);
        registroruta = findViewById(R.id.RegistroRuta);
        codigoruta = findViewById(R.id.CodigoRuta);
        CodigoTrampa = findViewById(R.id.CodigoTrampa);
        fechacoleccion = findViewById(R.id.FechaColeccion);
        firma = (SignaturePad) findViewById(R.id.firmadueñopredio);
        Guardar = findViewById(R.id.GuardarFormulario);
        auth = FirebaseAuth.getInstance();
        fechacoleccion.setText(fechaactual());
        CodigoTrampa.setText(getIntent().getStringExtra("codigotrampa"));
        baseDatos = FirebaseDatabase.getInstance().getReference();
        usuarios = baseDatos.child("Usuarios");
        CentroAcopio = findViewById(R.id.CentroAcopio);
        Guardarfirma = findViewById(R.id.savesignature);
        limpiarfirma = findViewById(R.id.clearsignature);
        if (getIntent().getExtras() !=null){
            añadir = getIntent().getExtras().getInt("añadir");
            Log.e("añadir llena",añadir+"");
        }
        firma.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {

            }

            @Override
            public void onSigned() {
                Guardarfirma.setEnabled(true);
                limpiarfirma.setEnabled(true);
            }

            @Override
            public void onClear() {
                Guardarfirma.setEnabled(false);
                limpiarfirma.setEnabled(false);
            }
        });
        limpiarfirma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firma.clear();
            }
        });
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
        addformulario = findViewById(R.id.addformulariotrampa);
        addformulario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LlenarPedf(new File(Environment.getExternalStorageDirectory().toString(),"Reportes")+"",path);
                añadir ++;
                Log.e("añadir",añadir+"");
                Intent intent = new Intent(LlenarFormularioActivity.this,MapaInspectorActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("añadir",añadir);
                intent.putExtra("CentroAcopio",CentroAcopio.getText()+"");
                startActivity(intent);
            }
        });

        Guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /***
                 * Guarda la fecha de inspeccionde la trampa a la cual se le dio en Informacion
                 */
                Copiarpdf();
                LlegadaMapa llegadaMapa = new LlegadaMapa(fechaactual(),CodigoTrampa.getText().toString(),Nombre,Cedula,correo,auth.getCurrentUser().getUid());
                baseDatos.child("trampas").child(CodigoTrampa.getText().toString()).child("Inspeccion").child(fechaactual()).setValue(llegadaMapa);
                trampas = baseDatos.child("Inspecciones");
                trampas.child(fechaactual()+" "+CodigoTrampa.getText()).setValue(llegadaMapa);
                LlenarPedf(new File(Environment.getExternalStorageDirectory().toString(),"Reportes")+"",path);

            }
        });
        Guardarfirma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap signatureBitmap = firma.getSignatureBitmap();
                if (addJpgSignatureToGallery(signatureBitmap)) {
                    Toast.makeText(LlenarFormularioActivity.this, "Signature saved into the Gallery", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LlenarFormularioActivity.this, "Unable to store the signature", Toast.LENGTH_SHORT).show();
                }
                if (addSvgSignatureToGallery(firma.getSignatureSvg())) {
                    Toast.makeText(LlenarFormularioActivity.this, "SVG Signature saved into the Gallery", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LlenarFormularioActivity.this, "Unable to store the SVG signature", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private String fechaactual(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Date date = new Date();

        String fecha = dateFormat.format(date);

        return  fecha;
    }
    private void Copiarpdf(){
        File folder = new File(Environment.getExternalStorageDirectory().toString(),"Reportes");
        if (!folder.exists()){
            folder.mkdirs();
            CopyRawToSDCard((R.raw.forma),Environment.getExternalStorageDirectory()+"/forma.pdf");
        }
        else {
            CopyRawToSDCard((R.raw.forma),Environment.getExternalStorageDirectory()+"/forma.pdf");
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

    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("SignaturePad", "Directory not created");
        }
        return file;
    }

    public void saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(photo);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        stream.close();
    }

    public boolean addJpgSignatureToGallery(Bitmap signature) {
        boolean result = false;
        try {
            File photo = new File(getAlbumStorageDir("SignaturePad"), String.format("Signature_%d.jpg", System.currentTimeMillis()));
            saveBitmapToJPG(signature, photo);
            scanMediaFile(photo);
            path= photo+"";
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void scanMediaFile(File photo) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(photo);
        mediaScanIntent.setData(contentUri);
        LlenarFormularioActivity.this.sendBroadcast(mediaScanIntent);
    }

    public boolean addSvgSignatureToGallery(String signatureSvg) {
        boolean result = false;
        try {
            File svgFile = new File(getAlbumStorageDir("SignaturePad"), String.format("Signature_%d.svg", System.currentTimeMillis()));
            OutputStream stream = new FileOutputStream(svgFile);
            OutputStreamWriter writer = new OutputStreamWriter(stream);
            writer.write(signatureSvg);
            writer.close();
            stream.flush();
            stream.close();
            scanMediaFile(svgFile);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    private void LlenarPedf(String destino,String ruta){
        try {
            PdfReader pdfReader = new PdfReader(getResources().openRawResource(R.raw.forma));
            PdfStamper stamper = new PdfStamper(pdfReader,new FileOutputStream(destino+"/forma.pdf"));
            InputStream ims = getResources().openRawResource(R.raw.forma);
            AcroFields acroFields = stamper.getAcroFields();
            Log.e("Datos", acroFields.getFields()+"");
            acroFields.setField("Centro_de _acopio",CentroAcopio.getText()+"");
            acroFields.setField("Fecha_ddmmaaaa",fechaactual());
            acroFields.setField("Semana",semana.getText()+"");
            acroFields.setField("Oficina",Oficina.getText()+"");
            acroFields.setField("Responsable",responsable.getText()+"");
            acroFields.setField("colectorRow1",nombreColector.getText()+"");
            acroFields.setField("Registro_ruta",registroruta.getText()+"");
            acroFields.setField("Nombrepredioempresa",Nombredelaruta.getText()+"");
            acroFields.setField("Codigo_ruta",codigoruta.getText()+"");
            if (añadir == 1) {
                acroFields.setField("CODIGOTRAMPARow1.0", CodigoTrampa.getText() + "");
                acroFields.setField("MUNICIPIORow1", Municipio.getText() + "");
                acroFields.setField("TIPO_ATRAYENTERow1", Tipo_Atrayente.getText().toString());
                acroFields.setField("AnastrephaRow1", numeroanas.getText() + "");
                acroFields.setField("CeratitisRow1", numeroceratis.getText() + "");
                acroFields.setField("OtrosRow1", numerootros.getText() + "");
                acroFields.setField("FENOLOGIARow1", fenologia.getText() + "");
                acroFields.setField("ESTADOTRAMPARow1", Estadotrampa.getText() + "");
                acroFields.setField("OBSERVACIONESRow1", Observaciones.getText() + "");
            }if (añadir == 2){
                acroFields.setField("CODIGOTRAMPARow2", CodigoTrampa.getText() + "");
                acroFields.setField("MUNICIPIORow2", Municipio.getText() + "");
                acroFields.setField("TIPO_ATRAYENTERow2", Tipo_Atrayente.getText().toString());
                acroFields.setField("AnastrephaRow2", numeroanas.getText() + "");
                acroFields.setField("CeratitisRow2", numeroceratis.getText() + "");
                acroFields.setField("OtrosRow2", numerootros.getText() + "");
                acroFields.setField("FENOLOGIARow2", fenologia.getText() + "");
                acroFields.setField("ESTADOTRAMPARow2", Estadotrampa.getText() + "");
                acroFields.setField("OBSERVACIONESRow2", Observaciones.getText() + "");
                acroFields.setField("FIRMAPROPIETARIORow2", "Aqui");
            } if (añadir == 3){
                acroFields.setField("CODIGOTRAMPARow3", CodigoTrampa.getText() + "");
                acroFields.setField("MUNICIPIORow3", Municipio.getText() + "");
                acroFields.setField("TIPO_ATRAYENTERow3", Tipo_Atrayente.getText().toString());
                acroFields.setField("AnastrephaRow3", numeroanas.getText() + "");
                acroFields.setField("CeratitisRow3", numeroceratis.getText() + "");
                acroFields.setField("OtrosRow3", numerootros.getText() + "");
                acroFields.setField("FENOLOGIARow3", fenologia.getText() + "");
                acroFields.setField("ESTADOTRAMPARow3", Estadotrampa.getText() + "");
                acroFields.setField("OBSERVACIONESRow3", Observaciones.getText() + "");
                acroFields.setField("FIRMAPROPIETARIORow3", "Aqui");
            }

            stamper.close();
            pdfReader.close();
            Log.e("Datos", acroFields.getFields()+"");

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
