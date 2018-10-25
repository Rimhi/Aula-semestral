package monterrosa.ricardo.aprendermaps.Inspector;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
    private AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_llenar_formulario);
        Inicializar();
        if (getIntent().getExtras()!=null){
            añadir = getIntent().getExtras().getInt("añadir");
            CentroAcopio.setText(getIntent().getStringExtra("CentroAcopio"));
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
                Toast.makeText(LlenarFormularioActivity.this, path, Toast.LENGTH_SHORT).show();
                Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Reportes/","forma.pdf" ));
                String[] mailto = {""};
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_EMAIL, mailto);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,"");
                emailIntent.setType("application/pdf");
                emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
              //  startActivity(Intent.createChooser(emailIntent, ""));
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
        addformulario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LlegadaMapa llegadaMapa = new LlegadaMapa(fechaactual(),CodigoTrampa.getText().toString(),Nombre,Cedula,correo,auth.getCurrentUser().getUid());
                baseDatos.child("trampas").child(CodigoTrampa.getText().toString()).child("Inspeccion").child(fechaactual()).setValue(llegadaMapa);
                trampas = baseDatos.child("Inspecciones");
                trampas.child(fechaactual()+" "+CodigoTrampa.getText()).setValue(llegadaMapa);
                if (añadir<8) {
                    if (path==null || path.isEmpty()){
                        AlertDialog.Builder builder = new AlertDialog.Builder(LlenarFormularioActivity.this);
                        builder.setCancelable(false);
                        builder.setMessage("No hay firma, ¿desea continuar?");
                        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Enviardatos();
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialog.dismiss();
                            }
                        });
                        dialog = builder.create();
                        dialog.show();
                    }else {
                        Enviardatos();
                    }

                }
                else
                    Toast.makeText(LlenarFormularioActivity.this, "Maximo 8 trampas", Toast.LENGTH_SHORT).show();
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
           // Log.e("Datos", acroFields.getFields()+"");



            if (getIntent().getExtras() !=null){
                añadir = getIntent().getExtras().getInt("añadir");
                Log.e("añadir llena",añadir+"");
                acroFields.setField("Centro_de _acopio", getIntent().getExtras().getString("CentroAcopio"));
                acroFields.setField("Fecha_ddmmaaaa",fechaactual());
                acroFields.setField("Semana", getIntent().getExtras().getString("semana"));
                acroFields.setField("Oficina", getIntent().getExtras().getString("oficina"));
                acroFields.setField("Responsable", getIntent().getExtras().getString("responsable"));
                acroFields.setField("colectorRow1", getIntent().getExtras().getString("colector"));
                acroFields.setField("Registro_ruta", getIntent().getExtras().getString("registroruta"));
                acroFields.setField("Nombrepredioempresa", getIntent().getExtras().getString("nombreruta"));
                acroFields.setField("Codigo_ruta", getIntent().getExtras().getString("codigoruta"));
                if (añadir ==1){
                    Log.e("llenar","entro añadir 1 pdf");
                    acroFields.setField("CODIGOTRAMPARow1.0", getIntent().getExtras().getString("codigotrampa1"));
                    acroFields.setField("MUNICIPIORow1", getIntent().getExtras().getString("municipio1"));
                    acroFields.setField("TIPO_ATRAYENTERow1", getIntent().getExtras().getString("tipoatrayente1"));
                    acroFields.setField("AnastrephaRow1", getIntent().getExtras().getString("anastrepha1"));
                    acroFields.setField("CeratitisRow1", getIntent().getExtras().getString("ceratis1"));
                    acroFields.setField("OtrosRow1", getIntent().getExtras().getString("otros1"));
                    acroFields.setField("FENOLOGIARow1", getIntent().getExtras().getString("fenologia1"));
                    acroFields.setField("ESTADOTRAMPARow1", getIntent().getExtras().getString("estado1"));
                    acroFields.setField("OBSERVACIONESRow1", getIntent().getExtras().getString("observaciones1"));
                    PushbuttonField ad  = acroFields.getNewPushbuttonFromField("Firma1");
                    ad.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
                    ad.setProportionalIcon(false);
                    if (getIntent().getExtras().getString("firma1")!=null) {
                        ad.setImage(Image.getInstance(getIntent().getExtras().getString("firma1")));
                        acroFields.replacePushbuttonField("Firma1", ad.getField());
                    }
                }
                if (añadir == 2){
                    Log.e("llenar","entro añadir 2 pdf");
                    Toast.makeText(this, "recibir añadir 2", Toast.LENGTH_SHORT).show();
                    acroFields.setField("CODIGOTRAMPARow1.0", getIntent().getExtras().getString("codigotrampa1"));
                    acroFields.setField("MUNICIPIORow1", getIntent().getExtras().getString("municipio1"));
                    acroFields.setField("TIPO_ATRAYENTERow1", getIntent().getExtras().getString("tipoatrayente1"));
                    acroFields.setField("AnastrephaRow1", getIntent().getExtras().getString("anastrepha1"));
                    acroFields.setField("CeratitisRow1", getIntent().getExtras().getString("ceratis1"));
                    acroFields.setField("OtrosRow1", getIntent().getExtras().getString("otros1"));
                    acroFields.setField("FENOLOGIARow1", getIntent().getExtras().getString("fenologia1"));
                    acroFields.setField("ESTADOTRAMPARow1", getIntent().getExtras().getString("estado1"));
                    acroFields.setField("OBSERVACIONESRow1", getIntent().getExtras().getString("observaciones1"));
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

                        PushbuttonField ad = acroFields.getNewPushbuttonFromField("Firma1");
                        ad.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
                        ad.setProportionalIcon(false);
                    if (getIntent().getExtras().getString("firma1")!=null) {
                        ad.setImage(Image.getInstance(getIntent().getExtras().getString("firma1")));
                        acroFields.replacePushbuttonField("Firma1", ad.getField());
                    }
                    PushbuttonField ad2  = acroFields.getNewPushbuttonFromField("Firma2");
                    ad2.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
                    ad2.setProportionalIcon(false);
                    if (getIntent().getExtras().getString("firma2")!=null){
                        ad2.setImage(Image.getInstance(getIntent().getExtras().getString("firma2")));
                        acroFields.replacePushbuttonField("Firma2", ad.getField());
                    }

                } if (añadir == 3){
                    Log.e("llenar","entro añadir 3 pdf");
                    acroFields.setField("CODIGOTRAMPARow1.0", getIntent().getExtras().getString("codigotrampa1"));
                    acroFields.setField("MUNICIPIORow1", getIntent().getExtras().getString("municipio1"));
                    acroFields.setField("TIPO_ATRAYENTERow1", getIntent().getExtras().getString("tipoatrayente1"));
                    acroFields.setField("AnastrephaRow1", getIntent().getExtras().getString("anastrepha1"));
                    acroFields.setField("CeratitisRow1", getIntent().getExtras().getString("ceratis1"));
                    acroFields.setField("OtrosRow1", getIntent().getExtras().getString("otros1"));
                    acroFields.setField("FENOLOGIARow1", getIntent().getExtras().getString("fenologia1"));
                    acroFields.setField("ESTADOTRAMPARow1", getIntent().getExtras().getString("estado1"));
                    acroFields.setField("OBSERVACIONESRow1", getIntent().getExtras().getString("observaciones1"));
                    acroFields.setField("CODIGOTRAMPARow2", getIntent().getExtras().getString("codigotrampa2"));
                    acroFields.setField("MUNICIPIORow2", getIntent().getExtras().getString("municipio2"));
                    acroFields.setField("TIPO_ATRAYENTERow2", getIntent().getExtras().getString("tipoatrayente2"));
                    acroFields.setField("AnastrephaRow2", getIntent().getExtras().getString("anastrepha2"));
                    acroFields.setField("CeratitisRow2", getIntent().getExtras().getString("ceratis2"));
                    acroFields.setField("OtrosRow2", getIntent().getExtras().getString("otros2"));
                    acroFields.setField("FENOLOGIARow2", getIntent().getExtras().getString("fenologia2"));
                    acroFields.setField("ESTADOTRAMPARow2", getIntent().getExtras().getString("estado2"));
                    acroFields.setField("OBSERVACIONESRow2", getIntent().getExtras().getString("observaciones2"));
                    acroFields.setField("FIRMAPROPIETARIORow2", "Aqui");
                    acroFields.setField("CODIGOTRAMPARow3", CodigoTrampa.getText() + "");
                    acroFields.setField("MUNICIPIORow3", Municipio.getText() + "");
                    acroFields.setField("TIPO_ATRAYENTERow3", Tipo_Atrayente.getText().toString());
                    acroFields.setField("AnastrephaRow3", numeroanas.getText() + "");
                    acroFields.setField("CeratitisRow3", numeroceratis.getText() + "");
                    acroFields.setField("OtrosRow3", numerootros.getText() + "");
                    acroFields.setField("FENOLOGIARow3", fenologia.getText() + "");
                    acroFields.setField("ESTADOTRAMPARow3", Estadotrampa.getText() + "");
                    acroFields.setField("OBSERVACIONESRow3", Observaciones.getText() + "");
                    PushbuttonField ad  = acroFields.getNewPushbuttonFromField("Firma1");
                    ad.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
                    ad.setProportionalIcon(false);
                    if (getIntent().getExtras().getString("firma1")!=null) {
                        ad.setImage(Image.getInstance(getIntent().getExtras().getString("firma1")));
                        acroFields.replacePushbuttonField("Firma1", ad.getField());
                    }

                    PushbuttonField ad2  = acroFields.getNewPushbuttonFromField("Firma2");
                    ad2.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
                    ad2.setProportionalIcon(false);
                    if (getIntent().getExtras().getString("firma2")!=null) {
                        ad2.setImage(Image.getInstance(getIntent().getExtras().getString("firma2")));
                        acroFields.replacePushbuttonField("Firma2", ad.getField());
                    }

                    PushbuttonField ad3  = acroFields.getNewPushbuttonFromField("Firma3");
                    ad3.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
                    ad3.setProportionalIcon(false);
                    if (getIntent().getExtras().getString("firma3")!=null) {
                        ad3.setImage(Image.getInstance(getIntent().getExtras().getString("firma3")));
                        acroFields.replacePushbuttonField("Firma3", ad.getField());
                    }

                }
                if (añadir==4){
                    acroFields.setField("CODIGOTRAMPARow1.0", getIntent().getExtras().getString("codigotrampa1"));
                    acroFields.setField("MUNICIPIORow1", getIntent().getExtras().getString("municipio1"));
                    acroFields.setField("TIPO_ATRAYENTERow1", getIntent().getExtras().getString("tipoatrayente1"));
                    acroFields.setField("AnastrephaRow1", getIntent().getExtras().getString("anastrepha1"));
                    acroFields.setField("CeratitisRow1", getIntent().getExtras().getString("ceratis1"));
                    acroFields.setField("OtrosRow1", getIntent().getExtras().getString("otros1"));
                    acroFields.setField("FENOLOGIARow1", getIntent().getExtras().getString("fenologia1"));
                    acroFields.setField("ESTADOTRAMPARow1", getIntent().getExtras().getString("estado1"));
                    acroFields.setField("OBSERVACIONESRow1", getIntent().getExtras().getString("observaciones1"));
                    acroFields.setField("CODIGOTRAMPARow2", getIntent().getExtras().getString("codigotrampa2"));
                    acroFields.setField("MUNICIPIORow2", getIntent().getExtras().getString("municipio2"));
                    acroFields.setField("TIPO_ATRAYENTERow2", getIntent().getExtras().getString("tipoatrayente2"));
                    acroFields.setField("AnastrephaRow2", getIntent().getExtras().getString("anastrepha2"));
                    acroFields.setField("CeratitisRow2", getIntent().getExtras().getString("ceratis2"));
                    acroFields.setField("OtrosRow2", getIntent().getExtras().getString("otros2"));
                    acroFields.setField("FENOLOGIARow2", getIntent().getExtras().getString("fenologia2"));
                    acroFields.setField("ESTADOTRAMPARow2", getIntent().getExtras().getString("estado2"));
                    acroFields.setField("OBSERVACIONESRow2", getIntent().getExtras().getString("observaciones2"));
                    acroFields.setField("FIRMAPROPIETARIORow2", "Aqui");
                    acroFields.setField("MUNICIPIORow3", getIntent().getExtras().getString("municipio3"));
                    acroFields.setField("TIPO_ATRAYENTERow3", getIntent().getExtras().getString("tipoatrayente3"));
                    acroFields.setField("AnastrephaRow3", getIntent().getExtras().getString("anastrepha3"));
                    acroFields.setField("CeratitisRow3", getIntent().getExtras().getString("ceratis3"));
                    acroFields.setField("OtrosRow3", getIntent().getExtras().getString("otros3"));
                    acroFields.setField("FENOLOGIARow3", getIntent().getExtras().getString("fenologia3"));
                    acroFields.setField("ESTADOTRAMPARow3", getIntent().getExtras().getString("estado3"));
                    acroFields.setField("OBSERVACIONESRow3", getIntent().getExtras().getString("observaciones3"));
                    acroFields.setField("FIRMAPROPIETARIORow3", "Aqui");
                    acroFields.setField("CODIGOTRAMPARow4", CodigoTrampa.getText() + "");
                    acroFields.setField("MUNICIPIORow4", Municipio.getText() + "");
                    acroFields.setField("TIPO_ATRAYENTERow4", Tipo_Atrayente.getText().toString());
                    acroFields.setField("AnastrephaRow4", numeroanas.getText() + "");
                    acroFields.setField("CeratitisRow4", numeroceratis.getText() + "");
                    acroFields.setField("OtrosRow4", numerootros.getText() + "");
                    acroFields.setField("FENOLOGIARow4", fenologia.getText() + "");
                    acroFields.setField("ESTADOTRAMPARow4", Estadotrampa.getText() + "");
                    acroFields.setField("OBSERVACIONESRow4", Observaciones.getText() + "");
                    PushbuttonField ad  = acroFields.getNewPushbuttonFromField("Firma1");
                    ad.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
                    ad.setProportionalIcon(false);
                    if (getIntent().getExtras().getString("firma1")!=null) {
                        ad.setImage(Image.getInstance(getIntent().getExtras().getString("firma1")));
                        acroFields.replacePushbuttonField("Firma1", ad.getField());
                    }

                    PushbuttonField ad2  = acroFields.getNewPushbuttonFromField("Firma2");
                    ad2.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
                    ad2.setProportionalIcon(false);
                    if (getIntent().getExtras().getString("firma2")!=null) {
                        ad2.setImage(Image.getInstance(getIntent().getExtras().getString("firma2")));
                        acroFields.replacePushbuttonField("Firma2", ad.getField());
                    }

                    PushbuttonField ad3  = acroFields.getNewPushbuttonFromField("Firma3");
                    ad3.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
                    ad3.setProportionalIcon(false);
                    if (getIntent().getExtras().getString("firma3")!=null) {
                        ad3.setImage(Image.getInstance(getIntent().getExtras().getString("firma3")));
                        acroFields.replacePushbuttonField("Firma3", ad.getField());
                    }

                    PushbuttonField ad4  = acroFields.getNewPushbuttonFromField("Firma4");
                    ad4.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
                    ad4.setProportionalIcon(false);
                    if (getIntent().getExtras().getString("firma4")!=null) {
                        ad4.setImage(Image.getInstance(getIntent().getExtras().getString("firma4")));
                        acroFields.replacePushbuttonField("Firma4", ad.getField());
                    }


                }
                if (añadir == 5){
                    acroFields.setField("CODIGOTRAMPARow1.0", getIntent().getExtras().getString("codigotrampa1"));
                    acroFields.setField("MUNICIPIORow1", getIntent().getExtras().getString("municipio1"));
                    acroFields.setField("TIPO_ATRAYENTERow1", getIntent().getExtras().getString("tipoatrayente1"));
                    acroFields.setField("AnastrephaRow1", getIntent().getExtras().getString("anastrepha1"));
                    acroFields.setField("CeratitisRow1", getIntent().getExtras().getString("ceratis1"));
                    acroFields.setField("OtrosRow1", getIntent().getExtras().getString("otros1"));
                    acroFields.setField("FENOLOGIARow1", getIntent().getExtras().getString("fenologia1"));
                    acroFields.setField("ESTADOTRAMPARow1", getIntent().getExtras().getString("estado1"));
                    acroFields.setField("OBSERVACIONESRow1", getIntent().getExtras().getString("observaciones1"));
                    acroFields.setField("CODIGOTRAMPARow2", getIntent().getExtras().getString("codigotrampa2"));
                    acroFields.setField("MUNICIPIORow2", getIntent().getExtras().getString("municipio2"));
                    acroFields.setField("TIPO_ATRAYENTERow2", getIntent().getExtras().getString("tipoatrayente2"));
                    acroFields.setField("AnastrephaRow2", getIntent().getExtras().getString("anastrepha2"));
                    acroFields.setField("CeratitisRow2", getIntent().getExtras().getString("ceratis2"));
                    acroFields.setField("OtrosRow2", getIntent().getExtras().getString("otros2"));
                    acroFields.setField("FENOLOGIARow2", getIntent().getExtras().getString("fenologia2"));
                    acroFields.setField("ESTADOTRAMPARow2", getIntent().getExtras().getString("estado2"));
                    acroFields.setField("OBSERVACIONESRow2", getIntent().getExtras().getString("observaciones2"));
                    acroFields.setField("FIRMAPROPIETARIORow2", "Aqui");
                    acroFields.setField("CODIGOTRAMPARow3", getIntent().getExtras().getString("codigotrampa3"));
                    acroFields.setField("MUNICIPIORow3", getIntent().getExtras().getString("municipio3"));
                    acroFields.setField("TIPO_ATRAYENTERow3", getIntent().getExtras().getString("tipoatrayente3"));
                    acroFields.setField("AnastrephaRow3", getIntent().getExtras().getString("anastrepha3"));
                    acroFields.setField("CeratitisRow3", getIntent().getExtras().getString("ceratis3"));
                    acroFields.setField("OtrosRow3", getIntent().getExtras().getString("otros3"));
                    acroFields.setField("FENOLOGIARow3", getIntent().getExtras().getString("fenologia3"));
                    acroFields.setField("ESTADOTRAMPARow3", getIntent().getExtras().getString("estado3"));
                    acroFields.setField("OBSERVACIONESRow3", getIntent().getExtras().getString("observaciones3"));
                    acroFields.setField("FIRMAPROPIETARIORow3", "Aqui");
                    acroFields.setField("CODIGOTRAMPARow4", getIntent().getExtras().getString("codigotrampa4"));
                    acroFields.setField("MUNICIPIORow4", getIntent().getExtras().getString("municipio4"));
                    acroFields.setField("TIPO_ATRAYENTERow4", getIntent().getExtras().getString("tipoatrayente4"));
                    acroFields.setField("AnastrephaRow4", getIntent().getExtras().getString("anastrepha4"));
                    acroFields.setField("CeratitisRow4", getIntent().getExtras().getString("ceratis4"));
                    acroFields.setField("OtrosRow4", getIntent().getExtras().getString("otros4"));
                    acroFields.setField("FENOLOGIARow4", getIntent().getExtras().getString("fenologia4"));
                    acroFields.setField("ESTADOTRAMPARow4", getIntent().getExtras().getString("estado4"));
                    acroFields.setField("OBSERVACIONESRow4", getIntent().getExtras().getString("observaciones4"));
                    acroFields.setField("FIRMAPROPIETARIORow4", "Aqui");
                    acroFields.setField("CODIGOTRAMPARow5", CodigoTrampa.getText() + "");
                    acroFields.setField("MUNICIPIORow5", Municipio.getText() + "");
                    acroFields.setField("TIPO_ATRAYENTERow5", Tipo_Atrayente.getText().toString());
                    acroFields.setField("AnastrephaRow5", numeroanas.getText() + "");
                    acroFields.setField("CeratitisRow5", numeroceratis.getText() + "");
                    acroFields.setField("OtrosRow5", numerootros.getText() + "");
                    acroFields.setField("FENOLOGIARow5", fenologia.getText() + "");
                    acroFields.setField("ESTADOTRAMPARow5", Estadotrampa.getText() + "");
                    acroFields.setField("OBSERVACIONESRow5", Observaciones.getText() + "");
                    PushbuttonField ad  = acroFields.getNewPushbuttonFromField("Firma1");
                    ad.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
                    ad.setProportionalIcon(false);
                    if (getIntent().getExtras().getString("firma1")!=null) {
                        ad.setImage(Image.getInstance(getIntent().getExtras().getString("firma1")));
                        acroFields.replacePushbuttonField("Firma1", ad.getField());
                    }

                    PushbuttonField ad2  = acroFields.getNewPushbuttonFromField("Firma2");
                    ad2.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
                    ad2.setProportionalIcon(false);
                    if (getIntent().getExtras().getString("firma2")!=null) {
                        ad2.setImage(Image.getInstance(getIntent().getExtras().getString("firma2")));
                        acroFields.replacePushbuttonField("Firma2", ad.getField());
                    }

                    PushbuttonField ad3  = acroFields.getNewPushbuttonFromField("Firma3");
                    ad3.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
                    ad3.setProportionalIcon(false);
                    if (getIntent().getExtras().getString("firma3")!=null) {
                        ad3.setImage(Image.getInstance(getIntent().getExtras().getString("firma3")));
                        acroFields.replacePushbuttonField("Firma3", ad.getField());
                    }

                    PushbuttonField ad4  = acroFields.getNewPushbuttonFromField("Firma4");
                    ad4.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
                    ad4.setProportionalIcon(false);
                    if (getIntent().getExtras().getString("firma4")!=null) {
                        ad4.setImage(Image.getInstance(getIntent().getExtras().getString("firma4")));
                        acroFields.replacePushbuttonField("Firma4", ad.getField());
                    }

                    PushbuttonField ad5  = acroFields.getNewPushbuttonFromField("Firma5");
                    ad5.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
                    ad5.setProportionalIcon(false);
                    if (getIntent().getExtras().getString("firma5")!=null) {
                        ad5.setImage(Image.getInstance(getIntent().getExtras().getString("firma5")));
                        acroFields.replacePushbuttonField("Firma5", ad.getField());
                    }
                }
                if (añadir==6){
                    acroFields.setField("CODIGOTRAMPARow1.0", getIntent().getExtras().getString("codigotrampa1"));
                    acroFields.setField("MUNICIPIORow1", getIntent().getExtras().getString("municipio1"));
                    acroFields.setField("TIPO_ATRAYENTERow1", getIntent().getExtras().getString("tipoatrayente1"));
                    acroFields.setField("AnastrephaRow1", getIntent().getExtras().getString("anastrepha1"));
                    acroFields.setField("CeratitisRow1", getIntent().getExtras().getString("ceratis1"));
                    acroFields.setField("OtrosRow1", getIntent().getExtras().getString("otros1"));
                    acroFields.setField("FENOLOGIARow1", getIntent().getExtras().getString("fenologia1"));
                    acroFields.setField("ESTADOTRAMPARow1", getIntent().getExtras().getString("estado1"));
                    acroFields.setField("OBSERVACIONESRow1", getIntent().getExtras().getString("observaciones1"));
                    acroFields.setField("CODIGOTRAMPARow2", getIntent().getExtras().getString("codigotrampa2"));
                    acroFields.setField("MUNICIPIORow2", getIntent().getExtras().getString("municipio2"));
                    acroFields.setField("TIPO_ATRAYENTERow2", getIntent().getExtras().getString("tipoatrayente2"));
                    acroFields.setField("AnastrephaRow2", getIntent().getExtras().getString("anastrepha2"));
                    acroFields.setField("CeratitisRow2", getIntent().getExtras().getString("ceratis2"));
                    acroFields.setField("OtrosRow2", getIntent().getExtras().getString("otros2"));
                    acroFields.setField("FENOLOGIARow2", getIntent().getExtras().getString("fenologia2"));
                    acroFields.setField("ESTADOTRAMPARow2", getIntent().getExtras().getString("estado2"));
                    acroFields.setField("OBSERVACIONESRow2", getIntent().getExtras().getString("observaciones2"));
                    acroFields.setField("FIRMAPROPIETARIORow2", "Aqui");
                    acroFields.setField("CODIGOTRAMPARow3", getIntent().getExtras().getString("codigotrampa3"));
                    acroFields.setField("MUNICIPIORow3", getIntent().getExtras().getString("municipio3"));
                    acroFields.setField("TIPO_ATRAYENTERow3", getIntent().getExtras().getString("tipoatrayente3"));
                    acroFields.setField("AnastrephaRow3", getIntent().getExtras().getString("anastrepha3"));
                    acroFields.setField("CeratitisRow3", getIntent().getExtras().getString("ceratis3"));
                    acroFields.setField("OtrosRow3", getIntent().getExtras().getString("otros3"));
                    acroFields.setField("FENOLOGIARow3", getIntent().getExtras().getString("fenologia3"));
                    acroFields.setField("ESTADOTRAMPARow3", getIntent().getExtras().getString("estado3"));
                    acroFields.setField("OBSERVACIONESRow3", getIntent().getExtras().getString("observaciones3"));
                    acroFields.setField("FIRMAPROPIETARIORow3", "Aqui");
                    acroFields.setField("CODIGOTRAMPARow4", getIntent().getExtras().getString("codigotrampa4"));
                    acroFields.setField("MUNICIPIORow4", getIntent().getExtras().getString("municipio4"));
                    acroFields.setField("TIPO_ATRAYENTERow4", getIntent().getExtras().getString("tipoatrayente4"));
                    acroFields.setField("AnastrephaRow4", getIntent().getExtras().getString("anastrepha4"));
                    acroFields.setField("CeratitisRow4", getIntent().getExtras().getString("ceratis4"));
                    acroFields.setField("OtrosRow4", getIntent().getExtras().getString("otros4"));
                    acroFields.setField("FENOLOGIARow4", getIntent().getExtras().getString("fenologia4"));
                    acroFields.setField("ESTADOTRAMPARow4", getIntent().getExtras().getString("estado4"));
                    acroFields.setField("OBSERVACIONESRow4", getIntent().getExtras().getString("observaciones4"));
                    acroFields.setField("FIRMAPROPIETARIORow4", "Aqui");
                    acroFields.setField("CODIGOTRAMPARow5", getIntent().getExtras().getString("codigotrampa5"));
                    acroFields.setField("MUNICIPIORow5", getIntent().getExtras().getString("municipio5"));
                    acroFields.setField("TIPO_ATRAYENTERow5", getIntent().getExtras().getString("tipoatrayente5"));
                    acroFields.setField("AnastrephaRow5", getIntent().getExtras().getString("anastrepha5"));
                    acroFields.setField("CeratitisRow5", getIntent().getExtras().getString("ceratis5"));
                    acroFields.setField("OtrosRow5", getIntent().getExtras().getString("otros5"));
                    acroFields.setField("FENOLOGIARow5", getIntent().getExtras().getString("fenologia5"));
                    acroFields.setField("ESTADOTRAMPARow5", getIntent().getExtras().getString("estado5"));
                    acroFields.setField("OBSERVACIONESRow5", getIntent().getExtras().getString("observaciones5"));
                    acroFields.setField("CODIGOTRAMPARow6", CodigoTrampa.getText() + "");
                    acroFields.setField("MUNICIPIORow6", Municipio.getText() + "");
                    acroFields.setField("TIPO_ATRAYENTERow6", Tipo_Atrayente.getText().toString());
                    acroFields.setField("AnastrephaRow6", numeroanas.getText() + "");
                    acroFields.setField("CeratitisRow6", numeroceratis.getText() + "");
                    acroFields.setField("OtrosRow6", numerootros.getText() + "");
                    acroFields.setField("FENOLOGIARow6", fenologia.getText() + "");
                    acroFields.setField("ESTADOTRAMPARow6", Estadotrampa.getText() + "");
                    acroFields.setField("OBSERVACIONESRow6", Observaciones.getText() + "");
                    PushbuttonField ad  = acroFields.getNewPushbuttonFromField("Firma1");
                    ad.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
                    ad.setProportionalIcon(false);
                    if (getIntent().getExtras().getString("firma1")!=null) {
                        ad.setImage(Image.getInstance(getIntent().getExtras().getString("firma1")));
                        acroFields.replacePushbuttonField("Firma1", ad.getField());
                    }

                    PushbuttonField ad2  = acroFields.getNewPushbuttonFromField("Firma2");
                    ad2.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
                    ad2.setProportionalIcon(false);
                    if (getIntent().getExtras().getString("firma2")!=null) {
                        ad2.setImage(Image.getInstance(getIntent().getExtras().getString("firma2")));
                        acroFields.replacePushbuttonField("Firma2", ad.getField());
                    }

                    PushbuttonField ad3  = acroFields.getNewPushbuttonFromField("Firma3");
                    ad3.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
                    ad3.setProportionalIcon(false);
                    if (getIntent().getExtras().getString("firma3")!=null) {
                        ad3.setImage(Image.getInstance(getIntent().getExtras().getString("firma3")));
                        acroFields.replacePushbuttonField("Firma3", ad.getField());
                    }

                    PushbuttonField ad4  = acroFields.getNewPushbuttonFromField("Firma4");
                    ad4.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
                    ad4.setProportionalIcon(false);
                    if (getIntent().getExtras().getString("firma4")!=null) {
                        ad4.setImage(Image.getInstance(getIntent().getExtras().getString("firma4")));
                        acroFields.replacePushbuttonField("Firma4", ad.getField());
                    }

                    PushbuttonField ad5  = acroFields.getNewPushbuttonFromField("Firma5");
                    ad5.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
                    ad5.setProportionalIcon(false);
                    if (getIntent().getExtras().getString("firma5")!=null) {
                        ad5.setImage(Image.getInstance(getIntent().getExtras().getString("firma5")));
                        acroFields.replacePushbuttonField("Firma5", ad.getField());
                    }
                    PushbuttonField ad6  = acroFields.getNewPushbuttonFromField("Firma6");
                    ad6.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
                    ad6.setProportionalIcon(false);
                    if (getIntent().getExtras().getString("firma6")!=null) {
                        ad6.setImage(Image.getInstance(getIntent().getExtras().getString("firma6")));
                        acroFields.replacePushbuttonField("Firma6", ad.getField());
                    }
                }
                if (añadir==7){
                    acroFields.setField("CODIGOTRAMPARow1.0", getIntent().getExtras().getString("codigotrampa1"));
                    acroFields.setField("MUNICIPIORow1", getIntent().getExtras().getString("municipio1"));
                    acroFields.setField("TIPO_ATRAYENTERow1", getIntent().getExtras().getString("tipoatrayente1"));
                    acroFields.setField("AnastrephaRow1", getIntent().getExtras().getString("anastrepha1"));
                    acroFields.setField("CeratitisRow1", getIntent().getExtras().getString("ceratis1"));
                    acroFields.setField("OtrosRow1", getIntent().getExtras().getString("otros1"));
                    acroFields.setField("FENOLOGIARow1", getIntent().getExtras().getString("fenologia1"));
                    acroFields.setField("ESTADOTRAMPARow1", getIntent().getExtras().getString("estado1"));
                    acroFields.setField("OBSERVACIONESRow1", getIntent().getExtras().getString("observaciones1"));
                    acroFields.setField("CODIGOTRAMPARow2", getIntent().getExtras().getString("codigotrampa2"));
                    acroFields.setField("MUNICIPIORow2", getIntent().getExtras().getString("municipio2"));
                    acroFields.setField("TIPO_ATRAYENTERow2", getIntent().getExtras().getString("tipoatrayente2"));
                    acroFields.setField("AnastrephaRow2", getIntent().getExtras().getString("anastrepha2"));
                    acroFields.setField("CeratitisRow2", getIntent().getExtras().getString("ceratis2"));
                    acroFields.setField("OtrosRow2", getIntent().getExtras().getString("otros2"));
                    acroFields.setField("FENOLOGIARow2", getIntent().getExtras().getString("fenologia2"));
                    acroFields.setField("ESTADOTRAMPARow2", getIntent().getExtras().getString("estado2"));
                    acroFields.setField("OBSERVACIONESRow2", getIntent().getExtras().getString("observaciones2"));
                    acroFields.setField("FIRMAPROPIETARIORow2", "Aqui");
                    acroFields.setField("CODIGOTRAMPARow3", getIntent().getExtras().getString("codigotrampa3"));
                    acroFields.setField("MUNICIPIORow3", getIntent().getExtras().getString("municipio3"));
                    acroFields.setField("TIPO_ATRAYENTERow3", getIntent().getExtras().getString("tipoatrayente3"));
                    acroFields.setField("AnastrephaRow3", getIntent().getExtras().getString("anastrepha3"));
                    acroFields.setField("CeratitisRow3", getIntent().getExtras().getString("ceratis3"));
                    acroFields.setField("OtrosRow3", getIntent().getExtras().getString("otros3"));
                    acroFields.setField("FENOLOGIARow3", getIntent().getExtras().getString("fenologia3"));
                    acroFields.setField("ESTADOTRAMPARow3", getIntent().getExtras().getString("estado3"));
                    acroFields.setField("OBSERVACIONESRow3", getIntent().getExtras().getString("observaciones3"));
                    acroFields.setField("FIRMAPROPIETARIORow3", "Aqui");
                    acroFields.setField("CODIGOTRAMPARow4", getIntent().getExtras().getString("codigotrampa4"));
                    acroFields.setField("MUNICIPIORow4", getIntent().getExtras().getString("municipio4"));
                    acroFields.setField("TIPO_ATRAYENTERow4", getIntent().getExtras().getString("tipoatrayente4"));
                    acroFields.setField("AnastrephaRow4", getIntent().getExtras().getString("anastrepha4"));
                    acroFields.setField("CeratitisRow4", getIntent().getExtras().getString("ceratis4"));
                    acroFields.setField("OtrosRow4", getIntent().getExtras().getString("otros4"));
                    acroFields.setField("FENOLOGIARow4", getIntent().getExtras().getString("fenologia4"));
                    acroFields.setField("ESTADOTRAMPARow4", getIntent().getExtras().getString("estado4"));
                    acroFields.setField("OBSERVACIONESRow4", getIntent().getExtras().getString("observaciones4"));
                    acroFields.setField("FIRMAPROPIETARIORow4", "Aqui");
                    acroFields.setField("CODIGOTRAMPARow5", getIntent().getExtras().getString("codigotrampa5"));
                    acroFields.setField("MUNICIPIORow5", getIntent().getExtras().getString("municipio5"));
                    acroFields.setField("TIPO_ATRAYENTERow5", getIntent().getExtras().getString("tipoatrayente5"));
                    acroFields.setField("AnastrephaRow5", getIntent().getExtras().getString("anastrepha5"));
                    acroFields.setField("CeratitisRow5", getIntent().getExtras().getString("ceratis5"));
                    acroFields.setField("OtrosRow5", getIntent().getExtras().getString("otros5"));
                    acroFields.setField("FENOLOGIARow5", getIntent().getExtras().getString("fenologia5"));
                    acroFields.setField("ESTADOTRAMPARow5", getIntent().getExtras().getString("estado5"));
                    acroFields.setField("OBSERVACIONESRow5", getIntent().getExtras().getString("observaciones5"));
                    acroFields.setField("CODIGOTRAMPARow6", getIntent().getExtras().getString("codigotrampa6"));
                    acroFields.setField("MUNICIPIORow6", getIntent().getExtras().getString("municipio6"));
                    acroFields.setField("TIPO_ATRAYENTERow6", getIntent().getExtras().getString("tipoatrayente6"));
                    acroFields.setField("AnastrephaRow6", getIntent().getExtras().getString("anastrepha6"));
                    acroFields.setField("CeratitisRow6", getIntent().getExtras().getString("ceratis6"));
                    acroFields.setField("OtrosRow6", getIntent().getExtras().getString("otros6"));
                    acroFields.setField("FENOLOGIARow6", getIntent().getExtras().getString("fenologia6"));
                    acroFields.setField("ESTADOTRAMPARow6", getIntent().getExtras().getString("estado6"));
                    acroFields.setField("OBSERVACIONESRow6", getIntent().getExtras().getString("observaciones6"));
                    acroFields.setField("CODIGOTRAMPARow7", CodigoTrampa.getText() + "");
                    acroFields.setField("MUNICIPIORow7", Municipio.getText() + "");
                    acroFields.setField("TIPO_ATRAYENTERow7", Tipo_Atrayente.getText().toString());
                    acroFields.setField("AnastrephaRow7", numeroanas.getText() + "");
                    acroFields.setField("CeratitisRow7", numeroceratis.getText() + "");
                    acroFields.setField("OtrosRow7", numerootros.getText() + "");
                    acroFields.setField("FENOLOGIARow7", fenologia.getText() + "");
                    acroFields.setField("ESTADOTRAMPARow7", Estadotrampa.getText() + "");
                    acroFields.setField("OBSERVACIONESRow7", Observaciones.getText() + "");

                    PushbuttonField ad  = acroFields.getNewPushbuttonFromField("Firma1");
                    ad.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
                    ad.setProportionalIcon(false);
                    if (getIntent().getExtras().getString("firma1")!=null) {
                        ad.setImage(Image.getInstance(getIntent().getExtras().getString("firma1")));
                        acroFields.replacePushbuttonField("Firma1", ad.getField());
                    }

                    PushbuttonField ad2  = acroFields.getNewPushbuttonFromField("Firma2");
                    ad2.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
                    ad2.setProportionalIcon(false);
                    if (getIntent().getExtras().getString("firma2")!=null) {
                        ad2.setImage(Image.getInstance(getIntent().getExtras().getString("firma2")));
                        acroFields.replacePushbuttonField("Firma2", ad.getField());
                    }

                    PushbuttonField ad3  = acroFields.getNewPushbuttonFromField("Firma3");
                    ad3.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
                    ad3.setProportionalIcon(false);
                    if (getIntent().getExtras().getString("firma3")!=null) {
                        ad3.setImage(Image.getInstance(getIntent().getExtras().getString("firma3")));
                        acroFields.replacePushbuttonField("Firma3", ad.getField());
                    }

                    PushbuttonField ad4  = acroFields.getNewPushbuttonFromField("Firma4");
                    ad4.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
                    ad4.setProportionalIcon(false);
                    if (getIntent().getExtras().getString("firma4")!=null) {
                        ad4.setImage(Image.getInstance(getIntent().getExtras().getString("firma4")));
                        acroFields.replacePushbuttonField("Firma4", ad.getField());
                    }

                    PushbuttonField ad5  = acroFields.getNewPushbuttonFromField("Firma5");
                    ad5.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
                    ad5.setProportionalIcon(false);
                    if (getIntent().getExtras().getString("firma5")!=null) {
                        ad5.setImage(Image.getInstance(getIntent().getExtras().getString("firma5")));
                        acroFields.replacePushbuttonField("Firma5", ad.getField());
                    }
                    PushbuttonField ad6  = acroFields.getNewPushbuttonFromField("Firma6");
                    ad6.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
                    ad6.setProportionalIcon(false);
                    if (getIntent().getExtras().getString("firma6")!=null) {
                        ad6.setImage(Image.getInstance(getIntent().getExtras().getString("firma6")));
                        acroFields.replacePushbuttonField("Firma6", ad.getField());
                    }
                    PushbuttonField ad7  = acroFields.getNewPushbuttonFromField("Firma7");
                    ad7.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
                    ad7.setProportionalIcon(false);
                    if (getIntent().getExtras().getString("firma7")!=null) {
                        ad7.setImage(Image.getInstance(getIntent().getExtras().getString("firma7")));
                        acroFields.replacePushbuttonField("Firma7", ad.getField());
                    }
                }
                if (añadir==8){
                    acroFields.setField("CODIGOTRAMPARow1.0", getIntent().getExtras().getString("codigotrampa1"));
                    acroFields.setField("MUNICIPIORow1", getIntent().getExtras().getString("municipio1"));
                    acroFields.setField("TIPO_ATRAYENTERow1", getIntent().getExtras().getString("tipoatrayente1"));
                    acroFields.setField("AnastrephaRow1", getIntent().getExtras().getString("anastrepha1"));
                    acroFields.setField("CeratitisRow1", getIntent().getExtras().getString("ceratis1"));
                    acroFields.setField("OtrosRow1", getIntent().getExtras().getString("otros1"));
                    acroFields.setField("FENOLOGIARow1", getIntent().getExtras().getString("fenologia1"));
                    acroFields.setField("ESTADOTRAMPARow1", getIntent().getExtras().getString("estado1"));
                    acroFields.setField("OBSERVACIONESRow1", getIntent().getExtras().getString("observaciones1"));
                    acroFields.setField("CODIGOTRAMPARow2", getIntent().getExtras().getString("codigotrampa2"));
                    acroFields.setField("MUNICIPIORow2", getIntent().getExtras().getString("municipio2"));
                    acroFields.setField("TIPO_ATRAYENTERow2", getIntent().getExtras().getString("tipoatrayente2"));
                    acroFields.setField("AnastrephaRow2", getIntent().getExtras().getString("anastrepha2"));
                    acroFields.setField("CeratitisRow2", getIntent().getExtras().getString("ceratis2"));
                    acroFields.setField("OtrosRow2", getIntent().getExtras().getString("otros2"));
                    acroFields.setField("FENOLOGIARow2", getIntent().getExtras().getString("fenologia2"));
                    acroFields.setField("ESTADOTRAMPARow2", getIntent().getExtras().getString("estado2"));
                    acroFields.setField("OBSERVACIONESRow2", getIntent().getExtras().getString("observaciones2"));
                    acroFields.setField("FIRMAPROPIETARIORow2", "Aqui");
                    acroFields.setField("CODIGOTRAMPARow3", getIntent().getExtras().getString("codigotrampa3"));
                    acroFields.setField("MUNICIPIORow3", getIntent().getExtras().getString("municipio3"));
                    acroFields.setField("TIPO_ATRAYENTERow3", getIntent().getExtras().getString("tipoatrayente3"));
                    acroFields.setField("AnastrephaRow3", getIntent().getExtras().getString("anastrepha3"));
                    acroFields.setField("CeratitisRow3", getIntent().getExtras().getString("ceratis3"));
                    acroFields.setField("OtrosRow3", getIntent().getExtras().getString("otros3"));
                    acroFields.setField("FENOLOGIARow3", getIntent().getExtras().getString("fenologia3"));
                    acroFields.setField("ESTADOTRAMPARow3", getIntent().getExtras().getString("estado3"));
                    acroFields.setField("OBSERVACIONESRow3", getIntent().getExtras().getString("observaciones3"));
                    acroFields.setField("FIRMAPROPIETARIORow3", "Aqui");
                    acroFields.setField("CODIGOTRAMPARow4", getIntent().getExtras().getString("codigotrampa4"));
                    acroFields.setField("MUNICIPIORow4", getIntent().getExtras().getString("municipio4"));
                    acroFields.setField("TIPO_ATRAYENTERow4", getIntent().getExtras().getString("tipoatrayente4"));
                    acroFields.setField("AnastrephaRow4", getIntent().getExtras().getString("anastrepha4"));
                    acroFields.setField("CeratitisRow4", getIntent().getExtras().getString("ceratis4"));
                    acroFields.setField("OtrosRow4", getIntent().getExtras().getString("otros4"));
                    acroFields.setField("FENOLOGIARow4", getIntent().getExtras().getString("fenologia4"));
                    acroFields.setField("ESTADOTRAMPARow4", getIntent().getExtras().getString("estado4"));
                    acroFields.setField("OBSERVACIONESRow4", getIntent().getExtras().getString("observaciones4"));
                    acroFields.setField("FIRMAPROPIETARIORow4", "Aqui");
                    acroFields.setField("CODIGOTRAMPARow5", getIntent().getExtras().getString("codigotrampa5"));
                    acroFields.setField("MUNICIPIORow5", getIntent().getExtras().getString("municipio5"));
                    acroFields.setField("TIPO_ATRAYENTERow5", getIntent().getExtras().getString("tipoatrayente5"));
                    acroFields.setField("AnastrephaRow5", getIntent().getExtras().getString("anastrepha5"));
                    acroFields.setField("CeratitisRow5", getIntent().getExtras().getString("ceratis5"));
                    acroFields.setField("OtrosRow5", getIntent().getExtras().getString("otros5"));
                    acroFields.setField("FENOLOGIARow5", getIntent().getExtras().getString("fenologia5"));
                    acroFields.setField("ESTADOTRAMPARow5", getIntent().getExtras().getString("estado5"));
                    acroFields.setField("OBSERVACIONESRow5", getIntent().getExtras().getString("observaciones5"));
                    acroFields.setField("CODIGOTRAMPARow6", getIntent().getExtras().getString("codigotrampa6"));
                    acroFields.setField("MUNICIPIORow6", getIntent().getExtras().getString("municipio6"));
                    acroFields.setField("TIPO_ATRAYENTERow6", getIntent().getExtras().getString("tipoatrayente6"));
                    acroFields.setField("AnastrephaRow6", getIntent().getExtras().getString("anastrepha6"));
                    acroFields.setField("CeratitisRow6", getIntent().getExtras().getString("ceratis6"));
                    acroFields.setField("OtrosRow6", getIntent().getExtras().getString("otros6"));
                    acroFields.setField("FENOLOGIARow6", getIntent().getExtras().getString("fenologia6"));
                    acroFields.setField("ESTADOTRAMPARow6", getIntent().getExtras().getString("estado6"));
                    acroFields.setField("OBSERVACIONESRow6", getIntent().getExtras().getString("observaciones6"));
                    acroFields.setField("CODIGOTRAMPARow7", getIntent().getExtras().getString("codigotrampa7"));
                    acroFields.setField("MUNICIPIORow7", getIntent().getExtras().getString("municipio7"));
                    acroFields.setField("TIPO_ATRAYENTERow7", getIntent().getExtras().getString("tipoatrayente7"));
                    acroFields.setField("AnastrephaRow7", getIntent().getExtras().getString("anastrepha7"));
                    acroFields.setField("CeratitisRow7", getIntent().getExtras().getString("ceratis7"));
                    acroFields.setField("OtrosRow7", getIntent().getExtras().getString("otros7"));
                    acroFields.setField("FENOLOGIARow7", getIntent().getExtras().getString("fenologia7"));
                    acroFields.setField("ESTADOTRAMPARow7", getIntent().getExtras().getString("estado7"));
                    acroFields.setField("OBSERVACIONESRow7", getIntent().getExtras().getString("observaciones7"));
                    acroFields.setField("CODIGOTRAMPARow8", CodigoTrampa.getText() + "");
                    acroFields.setField("MUNICIPIORow8", Municipio.getText() + "");
                    acroFields.setField("TIPO_ATRAYENTERow8", Tipo_Atrayente.getText().toString());
                    acroFields.setField("AnastrephaRow8", numeroanas.getText() + "");
                    acroFields.setField("CeratitisRow8", numeroceratis.getText() + "");
                    acroFields.setField("OtrosRow8", numerootros.getText() + "");
                    acroFields.setField("FENOLOGIARow8", fenologia.getText() + "");
                    acroFields.setField("ESTADOTRAMPARow8", Estadotrampa.getText() + "");
                    acroFields.setField("OBSERVACIONESRow8", Observaciones.getText() + "");

                    PushbuttonField ad  = acroFields.getNewPushbuttonFromField("Firma1");
                    ad.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
                    ad.setProportionalIcon(false);
                    if (getIntent().getExtras().getString("firma1")!=null) {
                        ad.setImage(Image.getInstance(getIntent().getExtras().getString("firma1")));
                        acroFields.replacePushbuttonField("Firma1", ad.getField());
                    }

                    PushbuttonField ad2  = acroFields.getNewPushbuttonFromField("Firma2");
                    ad2.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
                    ad2.setProportionalIcon(false);
                    if (getIntent().getExtras().getString("firma2")!=null) {
                        ad2.setImage(Image.getInstance(getIntent().getExtras().getString("firma2")));
                        acroFields.replacePushbuttonField("Firma2", ad.getField());
                    }

                    PushbuttonField ad3  = acroFields.getNewPushbuttonFromField("Firma3");
                    ad3.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
                    ad3.setProportionalIcon(false);
                    if (getIntent().getExtras().getString("firma3")!=null) {
                        ad3.setImage(Image.getInstance(getIntent().getExtras().getString("firma3")));
                        acroFields.replacePushbuttonField("Firma3", ad.getField());
                    }

                    PushbuttonField ad4  = acroFields.getNewPushbuttonFromField("Firma4");
                    ad4.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
                    ad4.setProportionalIcon(false);
                    if (getIntent().getExtras().getString("firma4")!=null) {
                        ad4.setImage(Image.getInstance(getIntent().getExtras().getString("firma4")));
                        acroFields.replacePushbuttonField("Firma4", ad.getField());
                    }

                    PushbuttonField ad5  = acroFields.getNewPushbuttonFromField("Firma5");
                    ad5.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
                    ad5.setProportionalIcon(false);
                    if (getIntent().getExtras().getString("firma5")!=null) {
                        ad5.setImage(Image.getInstance(getIntent().getExtras().getString("firma5")));
                        acroFields.replacePushbuttonField("Firma5", ad.getField());
                    }
                    PushbuttonField ad6  = acroFields.getNewPushbuttonFromField("Firma6");
                    ad6.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
                    ad6.setProportionalIcon(false);
                    if (getIntent().getExtras().getString("firma6")!=null) {
                        ad6.setImage(Image.getInstance(getIntent().getExtras().getString("firma6")));
                        acroFields.replacePushbuttonField("Firma6", ad.getField());
                    }
                    PushbuttonField ad7  = acroFields.getNewPushbuttonFromField("Firma7");
                    ad7.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
                    ad7.setProportionalIcon(false);
                    if (getIntent().getExtras().getString("firma7")!=null) {
                        ad7.setImage(Image.getInstance(getIntent().getExtras().getString("firma7")));
                        acroFields.replacePushbuttonField("Firma7", ad.getField());
                    }

                    PushbuttonField ad8  = acroFields.getNewPushbuttonFromField("Firma8");
                    ad8.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
                    ad8.setProportionalIcon(false);
                    if (getIntent().getExtras().getString("firma8")!=null) {
                        ad8.setImage(Image.getInstance(getIntent().getExtras().getString("firma8")));
                        acroFields.replacePushbuttonField("Firma8", ad.getField());
                    }
                }if(añadir==9){
                    acroFields.setField("CODIGOTRAMPARow1.0", getIntent().getExtras().getString("codigotrampa1"));
                    acroFields.setField("MUNICIPIORow1", getIntent().getExtras().getString("municipio1"));
                    acroFields.setField("TIPO_ATRAYENTERow1", getIntent().getExtras().getString("tipoatrayente1"));
                    acroFields.setField("AnastrephaRow1", getIntent().getExtras().getString("anastrepha1"));
                    acroFields.setField("CeratitisRow1", getIntent().getExtras().getString("ceratis1"));
                    acroFields.setField("OtrosRow1", getIntent().getExtras().getString("otros1"));
                    acroFields.setField("FENOLOGIARow1", getIntent().getExtras().getString("fenologia1"));
                    acroFields.setField("ESTADOTRAMPARow1", getIntent().getExtras().getString("estado1"));
                    acroFields.setField("OBSERVACIONESRow1", getIntent().getExtras().getString("observaciones1"));
                    acroFields.setField("CODIGOTRAMPARow2", getIntent().getExtras().getString("codigotrampa2"));
                    acroFields.setField("MUNICIPIORow2", getIntent().getExtras().getString("municipio2"));
                    acroFields.setField("TIPO_ATRAYENTERow2", getIntent().getExtras().getString("tipoatrayente2"));
                    acroFields.setField("AnastrephaRow2", getIntent().getExtras().getString("anastrepha2"));
                    acroFields.setField("CeratitisRow2", getIntent().getExtras().getString("ceratis2"));
                    acroFields.setField("OtrosRow2", getIntent().getExtras().getString("otros2"));
                    acroFields.setField("FENOLOGIARow2", getIntent().getExtras().getString("fenologia2"));
                    acroFields.setField("ESTADOTRAMPARow2", getIntent().getExtras().getString("estado2"));
                    acroFields.setField("OBSERVACIONESRow2", getIntent().getExtras().getString("observaciones2"));
                    acroFields.setField("FIRMAPROPIETARIORow2", "Aqui");
                    acroFields.setField("CODIGOTRAMPARow3", getIntent().getExtras().getString("codigotrampa3"));
                    acroFields.setField("MUNICIPIORow3", getIntent().getExtras().getString("municipio3"));
                    acroFields.setField("TIPO_ATRAYENTERow3", getIntent().getExtras().getString("tipoatrayente3"));
                    acroFields.setField("AnastrephaRow3", getIntent().getExtras().getString("anastrepha3"));
                    acroFields.setField("CeratitisRow3", getIntent().getExtras().getString("ceratis3"));
                    acroFields.setField("OtrosRow3", getIntent().getExtras().getString("otros3"));
                    acroFields.setField("FENOLOGIARow3", getIntent().getExtras().getString("fenologia3"));
                    acroFields.setField("ESTADOTRAMPARow3", getIntent().getExtras().getString("estado3"));
                    acroFields.setField("OBSERVACIONESRow3", getIntent().getExtras().getString("observaciones3"));
                    acroFields.setField("FIRMAPROPIETARIORow3", "Aqui");
                    acroFields.setField("CODIGOTRAMPARow4", getIntent().getExtras().getString("codigotrampa4"));
                    acroFields.setField("MUNICIPIORow4", getIntent().getExtras().getString("municipio4"));
                    acroFields.setField("TIPO_ATRAYENTERow4", getIntent().getExtras().getString("tipoatrayente4"));
                    acroFields.setField("AnastrephaRow4", getIntent().getExtras().getString("anastrepha4"));
                    acroFields.setField("CeratitisRow4", getIntent().getExtras().getString("ceratis4"));
                    acroFields.setField("OtrosRow4", getIntent().getExtras().getString("otros4"));
                    acroFields.setField("FENOLOGIARow4", getIntent().getExtras().getString("fenologia4"));
                    acroFields.setField("ESTADOTRAMPARow4", getIntent().getExtras().getString("estado4"));
                    acroFields.setField("OBSERVACIONESRow4", getIntent().getExtras().getString("observaciones4"));
                    acroFields.setField("FIRMAPROPIETARIORow4", "Aqui");
                    acroFields.setField("CODIGOTRAMPARow5", getIntent().getExtras().getString("codigotrampa5"));
                    acroFields.setField("MUNICIPIORow5", getIntent().getExtras().getString("municipio5"));
                    acroFields.setField("TIPO_ATRAYENTERow5", getIntent().getExtras().getString("tipoatrayente5"));
                    acroFields.setField("AnastrephaRow5", getIntent().getExtras().getString("anastrepha5"));
                    acroFields.setField("CeratitisRow5", getIntent().getExtras().getString("ceratis5"));
                    acroFields.setField("OtrosRow5", getIntent().getExtras().getString("otros5"));
                    acroFields.setField("FENOLOGIARow5", getIntent().getExtras().getString("fenologia5"));
                    acroFields.setField("ESTADOTRAMPARow5", getIntent().getExtras().getString("estado5"));
                    acroFields.setField("OBSERVACIONESRow5", getIntent().getExtras().getString("observaciones5"));
                    acroFields.setField("CODIGOTRAMPARow6", getIntent().getExtras().getString("codigotrampa6"));
                    acroFields.setField("MUNICIPIORow6", getIntent().getExtras().getString("municipio6"));
                    acroFields.setField("TIPO_ATRAYENTERow6", getIntent().getExtras().getString("tipoatrayente6"));
                    acroFields.setField("AnastrephaRow6", getIntent().getExtras().getString("anastrepha6"));
                    acroFields.setField("CeratitisRow6", getIntent().getExtras().getString("ceratis6"));
                    acroFields.setField("OtrosRow6", getIntent().getExtras().getString("otros6"));
                    acroFields.setField("FENOLOGIARow6", getIntent().getExtras().getString("fenologia6"));
                    acroFields.setField("ESTADOTRAMPARow6", getIntent().getExtras().getString("estado6"));
                    acroFields.setField("OBSERVACIONESRow6", getIntent().getExtras().getString("observaciones6"));
                    acroFields.setField("CODIGOTRAMPARow7", getIntent().getExtras().getString("codigotrampa7"));
                    acroFields.setField("MUNICIPIORow7", getIntent().getExtras().getString("municipio7"));
                    acroFields.setField("TIPO_ATRAYENTERow7", getIntent().getExtras().getString("tipoatrayente7"));
                    acroFields.setField("AnastrephaRow7", getIntent().getExtras().getString("anastrepha7"));
                    acroFields.setField("CeratitisRow7", getIntent().getExtras().getString("ceratis7"));
                    acroFields.setField("OtrosRow7", getIntent().getExtras().getString("otros7"));
                    acroFields.setField("FENOLOGIARow7", getIntent().getExtras().getString("fenologia7"));
                    acroFields.setField("ESTADOTRAMPARow7", getIntent().getExtras().getString("estado7"));
                    acroFields.setField("OBSERVACIONESRow7", getIntent().getExtras().getString("observaciones7"));             acroFields.setField("CODIGOTRAMPARow9", CodigoTrampa.getText() + "");
                    acroFields.setField("CODIGOTRAMPARow8", getIntent().getExtras().getString("codigotrampa8"));
                    acroFields.setField("MUNICIPIORow8", getIntent().getExtras().getString("municipio8"));
                    acroFields.setField("TIPO_ATRAYENTERow8", getIntent().getExtras().getString("tipoatrayente8"));
                    acroFields.setField("AnastrephaRow8", getIntent().getExtras().getString("anastrepha8"));
                    acroFields.setField("CeratitisRow8", getIntent().getExtras().getString("ceratis8"));
                    acroFields.setField("OtrosRow8", getIntent().getExtras().getString("otros8"));
                    acroFields.setField("FENOLOGIARow8", getIntent().getExtras().getString("fenologia8"));
                    acroFields.setField("ESTADOTRAMPARow8", getIntent().getExtras().getString("estado8"));
                    acroFields.setField("OBSERVACIONESRow8", getIntent().getExtras().getString("observaciones8"));                     acroFields.setField("MUNICIPIORow9", Municipio.getText() + "");
                    acroFields.setField("CODIGOTRAMPARow9", CodigoTrampa.getText() + "");
                    acroFields.setField("MUNICIPIORow9", Municipio.getText() + "");
                    acroFields.setField("TIPO_ATRAYENTERow9", Tipo_Atrayente.getText().toString());
                    acroFields.setField("AnastrephaRow9", numeroanas.getText() + "");
                    acroFields.setField("CeratitisRow9", numeroceratis.getText() + "");
                    acroFields.setField("OtrosRow9", numerootros.getText() + "");
                    acroFields.setField("FENOLOGIARow9", fenologia.getText() + "");
                    acroFields.setField("ESTADOTRAMPARow9", Estadotrampa.getText() + "");
                    acroFields.setField("OBSERVACIONESRow9", Observaciones.getText() + "");

                    PushbuttonField ad  = acroFields.getNewPushbuttonFromField("Firma1");
                    ad.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
                    ad.setProportionalIcon(false);
                    if (getIntent().getExtras().getString("firma1")!=null) {
                        ad.setImage(Image.getInstance(getIntent().getExtras().getString("firma1")));
                        acroFields.replacePushbuttonField("Firma1", ad.getField());
                    }

                    PushbuttonField ad2  = acroFields.getNewPushbuttonFromField("Firma2");
                    ad2.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
                    ad2.setProportionalIcon(false);
                    if (getIntent().getExtras().getString("firma2")!=null) {
                        ad2.setImage(Image.getInstance(getIntent().getExtras().getString("firma2")));
                        acroFields.replacePushbuttonField("Firma2", ad.getField());
                    }

                    PushbuttonField ad3  = acroFields.getNewPushbuttonFromField("Firma3");
                    ad3.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
                    ad3.setProportionalIcon(false);
                    if (getIntent().getExtras().getString("firma3")!=null) {
                        ad3.setImage(Image.getInstance(getIntent().getExtras().getString("firma3")));
                        acroFields.replacePushbuttonField("Firma3", ad.getField());
                    }

                    PushbuttonField ad4  = acroFields.getNewPushbuttonFromField("Firma4");
                    ad4.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
                    ad4.setProportionalIcon(false);
                    if (getIntent().getExtras().getString("firma4")!=null) {
                        ad4.setImage(Image.getInstance(getIntent().getExtras().getString("firma4")));
                        acroFields.replacePushbuttonField("Firma4", ad.getField());
                    }

                    PushbuttonField ad5  = acroFields.getNewPushbuttonFromField("Firma5");
                    ad5.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
                    ad5.setProportionalIcon(false);
                    if (getIntent().getExtras().getString("firma5")!=null) {
                        ad5.setImage(Image.getInstance(getIntent().getExtras().getString("firma5")));
                        acroFields.replacePushbuttonField("Firma5", ad.getField());
                    }
                    PushbuttonField ad6  = acroFields.getNewPushbuttonFromField("Firma6");
                    ad6.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
                    ad6.setProportionalIcon(false);
                    if (getIntent().getExtras().getString("firma6")!=null) {
                        ad6.setImage(Image.getInstance(getIntent().getExtras().getString("firma6")));
                        acroFields.replacePushbuttonField("Firma6", ad.getField());
                    }
                    PushbuttonField ad7  = acroFields.getNewPushbuttonFromField("Firma7");
                    ad7.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
                    ad7.setProportionalIcon(false);
                    if (getIntent().getExtras().getString("firma7")!=null) {
                        ad7.setImage(Image.getInstance(getIntent().getExtras().getString("firma7")));
                        acroFields.replacePushbuttonField("Firma7", ad.getField());
                    }

                    PushbuttonField ad8  = acroFields.getNewPushbuttonFromField("Firma8");
                    ad8.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
                    ad8.setProportionalIcon(false);
                    if (getIntent().getExtras().getString("firma8")!=null) {
                        ad8.setImage(Image.getInstance(getIntent().getExtras().getString("firma8")));
                        acroFields.replacePushbuttonField("Firma8", ad.getField());
                    }

                    PushbuttonField ad9  = acroFields.getNewPushbuttonFromField("Firma9");
                    ad9.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
                    ad9.setProportionalIcon(false);
                    if (ruta!=null || !ruta.isEmpty()) {
                        ad9.setImage(Image.getInstance(ruta));
                        acroFields.replacePushbuttonField("Firma9", ad.getField());
                    }
                }
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

    private void Inicializar(){
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
    }

    private void Enviardatos(){
        Log.e("añadir",añadir+"");
        Intent intent = new Intent(LlenarFormularioActivity.this,MapaInspectorActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        intent.putExtra("CentroAcopio",CentroAcopio.getText()+"");
        intent.putExtra("semana",semana.getText()+"");
        intent.putExtra("oficina",Oficina.getText()+"");
        intent.putExtra("responsable",responsable.getText()+"");
        intent.putExtra("colector1",nombreColector.getText()+"");
        intent.putExtra("registroruta",registroruta.getText()+"");
        intent.putExtra("nombreruta",Nombredelaruta.getText()+"");
        intent.putExtra("codigoruta",codigoruta.getText()+"");
        if (añadir == 1){
            Log.e("llenar","entro añadir 1 enviar");
            intent.putExtra("codigotrampa1",CodigoTrampa.getText()+"");
            intent.putExtra("municipio1",Municipio.getText()+"");
            intent.putExtra("atrayente1",Tipo_Atrayente.getText()+"");
            intent.putExtra("anastrepha1",numeroanas.getText()+"");
            intent.putExtra("ceratis1",numeroceratis.getText()+"");
            intent.putExtra("otros1",numerootros.getText()+"");
            intent.putExtra("fenologia1",fenologia.getText()+"");
            intent.putExtra("estado1",Estadotrampa.getText()+"");
            intent.putExtra("observaciones1",Observaciones.getText()+"");
            intent.putExtra("firma1",path);
        }
        if (añadir == 2) {
            if (getIntent().getExtras() != null) {
                Log.e("llenar","entro añadir 2 enviar");
                intent.putExtra("codigotrampa1", getIntent().getExtras().getString("codigotrampa1"));
                intent.putExtra("municipio1", getIntent().getExtras().getString("municipio1"));
                intent.putExtra("atrayente1", getIntent().getExtras().getString("tipoatrayente1"));
                intent.putExtra("anastrepha1", getIntent().getExtras().getString("anastrepha1"));
                intent.putExtra("ceratis1", getIntent().getExtras().getString("ceratis1"));
                intent.putExtra("otros1", getIntent().getExtras().getString("otros1"));
                intent.putExtra("fenologia1", getIntent().getExtras().getString("fenologia1"));
                intent.putExtra("estado1", getIntent().getExtras().getString("estado1"));
                Log.e("estado", getIntent().getExtras().getString("estado1"));
                intent.putExtra("observaciones1", getIntent().getExtras().getString("observaciones1"));
                intent.putExtra("firma1", getIntent().getExtras().getString("firma1"));
                intent.putExtra("codigotrampa2", CodigoTrampa.getText() + "");
                intent.putExtra("municipio2", Municipio.getText() + "");
                intent.putExtra("atrayente2", Tipo_Atrayente.getText() + "");
                intent.putExtra("anastrepha2", numeroanas.getText() + "");
                intent.putExtra("ceratis2", numeroceratis.getText() + "");
                intent.putExtra("otros2", numerootros.getText() + "");
                intent.putExtra("fenologia2", fenologia.getText() + "");
                intent.putExtra("estado2", Estadotrampa.getText() + "");
                intent.putExtra("observaciones2", Observaciones.getText() + "");
                intent.putExtra("firma2",path);
            }
            if (añadir == 3) {
                intent.putExtra("codigotrampa1", getIntent().getExtras().getString("codigotrampa1"));
                intent.putExtra("municipio1", getIntent().getExtras().getString("municipio1"));
                intent.putExtra("atrayente1", getIntent().getExtras().getString("tipoatrayente1"));
                intent.putExtra("anastrepha1", getIntent().getExtras().getString("anastrepha1"));
                intent.putExtra("ceratis1", getIntent().getExtras().getString("ceratis1"));
                intent.putExtra("otros1", getIntent().getExtras().getString("otros1"));
                intent.putExtra("fenologia1", getIntent().getExtras().getString("fenologia1"));
                intent.putExtra("estado1", getIntent().getExtras().getString("estado1"));
                intent.putExtra("observaciones1", getIntent().getExtras().getString("observaciones1"));
                intent.putExtra("codigotrampa2", getIntent().getExtras().getString("codigotrampa2"));
                intent.putExtra("municipio2", getIntent().getExtras().getString("municipio2"));
                intent.putExtra("atrayente2", getIntent().getExtras().getString("tipoatrayente2"));
                intent.putExtra("anastrepha2", getIntent().getExtras().getString("anastrepha2"));
                intent.putExtra("ceratis2", getIntent().getExtras().getString("ceratis2"));
                intent.putExtra("otros2", getIntent().getExtras().getString("otros2"));
                intent.putExtra("fenologia2", getIntent().getExtras().getString("fenologia2"));
                intent.putExtra("estado2", getIntent().getExtras().getString("estado2"));
                intent.putExtra("observaciones2", getIntent().getExtras().getString("observaciones2"));
                intent.putExtra("codigotrampa3", CodigoTrampa.getText() + "");
                intent.putExtra("municipio3", CodigoTrampa.getText() + "");
                intent.putExtra("atrayente3", CodigoTrampa.getText() + "");
                intent.putExtra("anastrepha3", CodigoTrampa.getText() + "");
                intent.putExtra("ceratis3", CodigoTrampa.getText() + "");
                intent.putExtra("otros3", CodigoTrampa.getText() + "");
                intent.putExtra("fenologia3", CodigoTrampa.getText() + "");
                intent.putExtra("estado3", CodigoTrampa.getText() + "");
                intent.putExtra("observaciones3", CodigoTrampa.getText() + "");
                intent.putExtra("firma1",getIntent().getExtras().getString("firma1"));
                intent.putExtra("firma2",getIntent().getExtras().getString("firma2"));
                intent.putExtra("firma3",path);

            }
            if (añadir == 4) {
                intent.putExtra("codigotrampa1", getIntent().getExtras().getString("codigotrampa1"));
                intent.putExtra("municipio1", getIntent().getExtras().getString("municipio1"));
                intent.putExtra("atrayente1", getIntent().getExtras().getString("tipoatrayente1"));
                intent.putExtra("anastrepha1", getIntent().getExtras().getString("anastrepha1"));
                intent.putExtra("ceratis1", getIntent().getExtras().getString("ceratis1"));
                intent.putExtra("otros1", getIntent().getExtras().getString("otros1"));
                intent.putExtra("fenologia1", getIntent().getExtras().getString("fenologia1"));
                intent.putExtra("estado1", getIntent().getExtras().getString("estado1"));
                intent.putExtra("observaciones1", getIntent().getExtras().getString("observaciones1"));
                intent.putExtra("codigotrampa2", getIntent().getExtras().getString("codigotrampa2"));
                intent.putExtra("municipio2", getIntent().getExtras().getString("municipio2"));
                intent.putExtra("atrayente2", getIntent().getExtras().getString("tipoatrayente2"));
                intent.putExtra("anastrepha2", getIntent().getExtras().getString("anastrepha2"));
                intent.putExtra("ceratis2", getIntent().getExtras().getString("ceratis2"));
                intent.putExtra("otros2", getIntent().getExtras().getString("otros2"));
                intent.putExtra("fenologia2", getIntent().getExtras().getString("fenologia2"));
                intent.putExtra("estado2", getIntent().getExtras().getString("estado2"));
                intent.putExtra("observaciones2", getIntent().getExtras().getString("observaciones2"));
                intent.putExtra("codigotrampa3", getIntent().getExtras().getString("codigotrampa3"));
                intent.putExtra("municipio3", getIntent().getExtras().getString("municipio3"));
                intent.putExtra("atrayente3", getIntent().getExtras().getString("tipoatrayente3"));
                intent.putExtra("anastrepha3", getIntent().getExtras().getString("anastrepha3"));
                intent.putExtra("ceratis3", getIntent().getExtras().getString("ceratis3"));
                intent.putExtra("otros3", getIntent().getExtras().getString("otros3"));
                intent.putExtra("fenologia3", getIntent().getExtras().getString("fenologia3"));
                intent.putExtra("estado3", getIntent().getExtras().getString("estado3"));
                intent.putExtra("observaciones3", getIntent().getExtras().getString("observaciones3"));
                intent.putExtra("codigotrampa4", CodigoTrampa.getText() + "");
                intent.putExtra("municipio4", CodigoTrampa.getText() + "");
                intent.putExtra("atrayente4", CodigoTrampa.getText() + "");
                intent.putExtra("anastrepha4", CodigoTrampa.getText() + "");
                intent.putExtra("ceratis4", CodigoTrampa.getText() + "");
                intent.putExtra("otros4", CodigoTrampa.getText() + "");
                intent.putExtra("fenologia4", CodigoTrampa.getText() + "");
                intent.putExtra("estado4", CodigoTrampa.getText() + "");
                intent.putExtra("observaciones4", CodigoTrampa.getText() + "");
                intent.putExtra("firma1",getIntent().getExtras().getString("firma1"));
                intent.putExtra("firma2",getIntent().getExtras().getString("firma2"));
                intent.putExtra("firma3",getIntent().getExtras().getString("firma3"));
                intent.putExtra("firma4",path);
            }
            if (añadir==5){
                intent.putExtra("codigotrampa1", getIntent().getExtras().getString("codigotrampa1"));
                intent.putExtra("municipio1", getIntent().getExtras().getString("municipio1"));
                intent.putExtra("atrayente1", getIntent().getExtras().getString("tipoatrayente1"));
                intent.putExtra("anastrepha1", getIntent().getExtras().getString("anastrepha1"));
                intent.putExtra("ceratis1", getIntent().getExtras().getString("ceratis1"));
                intent.putExtra("otros1", getIntent().getExtras().getString("otros1"));
                intent.putExtra("fenologia1", getIntent().getExtras().getString("fenologia1"));
                intent.putExtra("estado1", getIntent().getExtras().getString("estado1"));
                intent.putExtra("observaciones1", getIntent().getExtras().getString("observaciones1"));
                intent.putExtra("codigotrampa2", getIntent().getExtras().getString("codigotrampa2"));
                intent.putExtra("municipio2", getIntent().getExtras().getString("municipio2"));
                intent.putExtra("atrayente2", getIntent().getExtras().getString("tipoatrayente2"));
                intent.putExtra("anastrepha2", getIntent().getExtras().getString("anastrepha2"));
                intent.putExtra("ceratis2", getIntent().getExtras().getString("ceratis2"));
                intent.putExtra("otros2", getIntent().getExtras().getString("otros2"));
                intent.putExtra("fenologia2", getIntent().getExtras().getString("fenologia2"));
                intent.putExtra("estado2", getIntent().getExtras().getString("estado2"));
                intent.putExtra("observaciones2", getIntent().getExtras().getString("observaciones2"));
                intent.putExtra("codigotrampa3", getIntent().getExtras().getString("codigotrampa3"));
                intent.putExtra("municipio3", getIntent().getExtras().getString("municipio3"));
                intent.putExtra("atrayente3", getIntent().getExtras().getString("tipoatrayente3"));
                intent.putExtra("anastrepha3", getIntent().getExtras().getString("anastrepha3"));
                intent.putExtra("ceratis3", getIntent().getExtras().getString("ceratis3"));
                intent.putExtra("otros3", getIntent().getExtras().getString("otros3"));
                intent.putExtra("fenologia3", getIntent().getExtras().getString("fenologia3"));
                intent.putExtra("estado3", getIntent().getExtras().getString("estado3"));
                intent.putExtra("observaciones3", getIntent().getExtras().getString("observaciones3"));
                intent.putExtra("codigotrampa4", getIntent().getExtras().getString("codigotrampa3"));
                intent.putExtra("municipio4", getIntent().getExtras().getString("municipio4"));
                intent.putExtra("atrayente4", getIntent().getExtras().getString("tipoatrayente4"));
                intent.putExtra("anastrepha4", getIntent().getExtras().getString("anastrepha4"));
                intent.putExtra("ceratis4", getIntent().getExtras().getString("ceratis4"));
                intent.putExtra("otros4", getIntent().getExtras().getString("otros4"));
                intent.putExtra("fenologia4", getIntent().getExtras().getString("fenologia4"));
                intent.putExtra("estado4", getIntent().getExtras().getString("estado4"));
                intent.putExtra("observaciones4", getIntent().getExtras().getString("observaciones4"));
                intent.putExtra("codigotrampa5", CodigoTrampa.getText() + "");
                intent.putExtra("municipio5", CodigoTrampa.getText() + "");
                intent.putExtra("atrayente5", CodigoTrampa.getText() + "");
                intent.putExtra("anastrepha5", CodigoTrampa.getText() + "");
                intent.putExtra("ceratis5", CodigoTrampa.getText() + "");
                intent.putExtra("otros5", CodigoTrampa.getText() + "");
                intent.putExtra("fenologia5", CodigoTrampa.getText() + "");
                intent.putExtra("estado5", CodigoTrampa.getText() + "");
                intent.putExtra("observaciones5", CodigoTrampa.getText() + "");
                intent.putExtra("firma1",getIntent().getExtras().getString("firma1"));
                intent.putExtra("firma2",getIntent().getExtras().getString("firma2"));
                intent.putExtra("firma3",getIntent().getExtras().getString("firma3"));
                intent.putExtra("firma4",getIntent().getExtras().getString("firma4"));
                intent.putExtra("firma5",path);
            }  if (añadir==6){
                intent.putExtra("codigotrampa1", getIntent().getExtras().getString("codigotrampa1"));
                intent.putExtra("municipio1", getIntent().getExtras().getString("municipio1"));
                intent.putExtra("atrayente1", getIntent().getExtras().getString("tipoatrayente1"));
                intent.putExtra("anastrepha1", getIntent().getExtras().getString("anastrepha1"));
                intent.putExtra("ceratis1", getIntent().getExtras().getString("ceratis1"));
                intent.putExtra("otros1", getIntent().getExtras().getString("otros1"));
                intent.putExtra("fenologia1", getIntent().getExtras().getString("fenologia1"));
                intent.putExtra("estado1", getIntent().getExtras().getString("estado1"));
                intent.putExtra("observaciones1", getIntent().getExtras().getString("observaciones1"));
                intent.putExtra("codigotrampa2", getIntent().getExtras().getString("codigotrampa2"));
                intent.putExtra("municipio2", getIntent().getExtras().getString("municipio2"));
                intent.putExtra("atrayente2", getIntent().getExtras().getString("tipoatrayente2"));
                intent.putExtra("anastrepha2", getIntent().getExtras().getString("anastrepha2"));
                intent.putExtra("ceratis2", getIntent().getExtras().getString("ceratis2"));
                intent.putExtra("otros2", getIntent().getExtras().getString("otros2"));
                intent.putExtra("fenologia2", getIntent().getExtras().getString("fenologia2"));
                intent.putExtra("estado2", getIntent().getExtras().getString("estado2"));
                intent.putExtra("observaciones2", getIntent().getExtras().getString("observaciones2"));
                intent.putExtra("codigotrampa3", getIntent().getExtras().getString("codigotrampa3"));
                intent.putExtra("municipio3", getIntent().getExtras().getString("municipio3"));
                intent.putExtra("atrayente3", getIntent().getExtras().getString("tipoatrayente3"));
                intent.putExtra("anastrepha3", getIntent().getExtras().getString("anastrepha3"));
                intent.putExtra("ceratis3", getIntent().getExtras().getString("ceratis3"));
                intent.putExtra("otros3", getIntent().getExtras().getString("otros3"));
                intent.putExtra("fenologia3", getIntent().getExtras().getString("fenologia3"));
                intent.putExtra("estado3", getIntent().getExtras().getString("estado3"));
                intent.putExtra("observaciones3", getIntent().getExtras().getString("observaciones3"));
                intent.putExtra("codigotrampa4", getIntent().getExtras().getString("codigotrampa4"));
                intent.putExtra("municipio4", getIntent().getExtras().getString("municipio4"));
                intent.putExtra("atrayente4", getIntent().getExtras().getString("tipoatrayente4"));
                intent.putExtra("anastrepha4", getIntent().getExtras().getString("anastrepha4"));
                intent.putExtra("ceratis4", getIntent().getExtras().getString("ceratis4"));
                intent.putExtra("otros4", getIntent().getExtras().getString("otros4"));
                intent.putExtra("fenologia4", getIntent().getExtras().getString("fenologia4"));
                intent.putExtra("estado4", getIntent().getExtras().getString("estado4"));
                intent.putExtra("observaciones4", getIntent().getExtras().getString("observaciones4"));
                intent.putExtra("codigotrampa5", getIntent().getExtras().getString("codigotrampa5"));
                intent.putExtra("municipio5", getIntent().getExtras().getString("municipio5"));
                intent.putExtra("atrayente5", getIntent().getExtras().getString("tipoatrayente5"));
                intent.putExtra("anastrepha5", getIntent().getExtras().getString("anastrepha5"));
                intent.putExtra("ceratis5", getIntent().getExtras().getString("ceratis5"));
                intent.putExtra("otros5", getIntent().getExtras().getString("otros5"));
                intent.putExtra("fenologia5", getIntent().getExtras().getString("fenologia5"));
                intent.putExtra("estado5", getIntent().getExtras().getString("estado5"));
                intent.putExtra("observaciones5", getIntent().getExtras().getString("observaciones5"));
                intent.putExtra("codigotrampa6", CodigoTrampa.getText() + "");
                intent.putExtra("municipio6", CodigoTrampa.getText() + "");
                intent.putExtra("atrayente6", CodigoTrampa.getText() + "");
                intent.putExtra("anastrepha6", CodigoTrampa.getText() + "");
                intent.putExtra("ceratis6", CodigoTrampa.getText() + "");
                intent.putExtra("otros6", CodigoTrampa.getText() + "");
                intent.putExtra("fenologia6", CodigoTrampa.getText() + "");
                intent.putExtra("estado6", CodigoTrampa.getText() + "");
                intent.putExtra("observaciones6", CodigoTrampa.getText() + "");
                intent.putExtra("firma1",getIntent().getExtras().getString("firma1"));
                intent.putExtra("firma2",getIntent().getExtras().getString("firma2"));
                intent.putExtra("firma3",getIntent().getExtras().getString("firma3"));
                intent.putExtra("firma4",getIntent().getExtras().getString("firma4"));
                intent.putExtra("firma5",getIntent().getExtras().getString("firma5"));
                intent.putExtra("firma6",path);
            }
            if (añadir==7){
                intent.putExtra("codigotrampa1", getIntent().getExtras().getString("codigotrampa1"));
                intent.putExtra("municipio1", getIntent().getExtras().getString("municipio1"));
                intent.putExtra("atrayente1", getIntent().getExtras().getString("tipoatrayente1"));
                intent.putExtra("anastrepha1", getIntent().getExtras().getString("anastrepha1"));
                intent.putExtra("ceratis1", getIntent().getExtras().getString("ceratis1"));
                intent.putExtra("otros1", getIntent().getExtras().getString("otros1"));
                intent.putExtra("fenologia1", getIntent().getExtras().getString("fenologia1"));
                intent.putExtra("estado1", getIntent().getExtras().getString("estado1"));
                intent.putExtra("observaciones1", getIntent().getExtras().getString("observaciones1"));
                intent.putExtra("codigotrampa2", getIntent().getExtras().getString("codigotrampa2"));
                intent.putExtra("municipio2", getIntent().getExtras().getString("municipio2"));
                intent.putExtra("atrayente2", getIntent().getExtras().getString("tipoatrayente2"));
                intent.putExtra("anastrepha2", getIntent().getExtras().getString("anastrepha2"));
                intent.putExtra("ceratis2", getIntent().getExtras().getString("ceratis2"));
                intent.putExtra("otros2", getIntent().getExtras().getString("otros2"));
                intent.putExtra("fenologia2", getIntent().getExtras().getString("fenologia2"));
                intent.putExtra("estado2", getIntent().getExtras().getString("estado2"));
                intent.putExtra("observaciones2", getIntent().getExtras().getString("observaciones2"));
                intent.putExtra("codigotrampa3", getIntent().getExtras().getString("codigotrampa3"));
                intent.putExtra("municipio3", getIntent().getExtras().getString("municipio3"));
                intent.putExtra("atrayente3", getIntent().getExtras().getString("tipoatrayente3"));
                intent.putExtra("anastrepha3", getIntent().getExtras().getString("anastrepha3"));
                intent.putExtra("ceratis3", getIntent().getExtras().getString("ceratis3"));
                intent.putExtra("otros3", getIntent().getExtras().getString("otros3"));
                intent.putExtra("fenologia3", getIntent().getExtras().getString("fenologia3"));
                intent.putExtra("estado3", getIntent().getExtras().getString("estado3"));
                intent.putExtra("observaciones3", getIntent().getExtras().getString("observaciones3"));
                intent.putExtra("codigotrampa4", getIntent().getExtras().getString("codigotrampa4"));
                intent.putExtra("municipio4", getIntent().getExtras().getString("municipio4"));
                intent.putExtra("atrayente4", getIntent().getExtras().getString("tipoatrayente4"));
                intent.putExtra("anastrepha4", getIntent().getExtras().getString("anastrepha4"));
                intent.putExtra("ceratis4", getIntent().getExtras().getString("ceratis4"));
                intent.putExtra("otros4", getIntent().getExtras().getString("otros4"));
                intent.putExtra("fenologia4", getIntent().getExtras().getString("fenologia4"));
                intent.putExtra("estado4", getIntent().getExtras().getString("estado4"));
                intent.putExtra("observaciones4", getIntent().getExtras().getString("observaciones4"));
                intent.putExtra("codigotrampa5", getIntent().getExtras().getString("codigotrampa5"));
                intent.putExtra("municipio5", getIntent().getExtras().getString("municipio5"));
                intent.putExtra("atrayente5", getIntent().getExtras().getString("tipoatrayente5"));
                intent.putExtra("anastrepha5", getIntent().getExtras().getString("anastrepha5"));
                intent.putExtra("ceratis5", getIntent().getExtras().getString("ceratis5"));
                intent.putExtra("otros5", getIntent().getExtras().getString("otros5"));
                intent.putExtra("fenologia5", getIntent().getExtras().getString("fenologia5"));
                intent.putExtra("estado5", getIntent().getExtras().getString("estado5"));
                intent.putExtra("observaciones5", getIntent().getExtras().getString("observaciones5"));
                intent.putExtra("codigotrampa6", getIntent().getExtras().getString("codigotrampa6"));
                intent.putExtra("municipio6", getIntent().getExtras().getString("municipio6"));
                intent.putExtra("atrayente6", getIntent().getExtras().getString("tipoatrayente6"));
                intent.putExtra("anastrepha6", getIntent().getExtras().getString("anastrepha6"));
                intent.putExtra("ceratis6", getIntent().getExtras().getString("ceratis6"));
                intent.putExtra("otros6", getIntent().getExtras().getString("otros6"));
                intent.putExtra("fenologia6", getIntent().getExtras().getString("fenologia6"));
                intent.putExtra("estado6", getIntent().getExtras().getString("estado6"));
                intent.putExtra("observaciones6", getIntent().getExtras().getString("observaciones6"));
                intent.putExtra("codigotrampa7", CodigoTrampa.getText() + "");
                intent.putExtra("municipio7", CodigoTrampa.getText() + "");
                intent.putExtra("atrayente7", CodigoTrampa.getText() + "");
                intent.putExtra("anastrepha7", CodigoTrampa.getText() + "");
                intent.putExtra("ceratis7", CodigoTrampa.getText() + "");
                intent.putExtra("otros7", CodigoTrampa.getText() + "");
                intent.putExtra("fenologia7", CodigoTrampa.getText() + "");
                intent.putExtra("estado7", CodigoTrampa.getText() + "");
                intent.putExtra("observaciones7", CodigoTrampa.getText() + "");
                intent.putExtra("firma1",getIntent().getExtras().getString("firma1"));
                intent.putExtra("firma2",getIntent().getExtras().getString("firma2"));
                intent.putExtra("firma3",getIntent().getExtras().getString("firma3"));
                intent.putExtra("firma4",getIntent().getExtras().getString("firma4"));
                intent.putExtra("firma5",getIntent().getExtras().getString("firma5"));
                intent.putExtra("firma6",getIntent().getExtras().getString("firma6"));
                intent.putExtra("firma7",path);
            }if (añadir==8){
                intent.putExtra("codigotrampa1", getIntent().getExtras().getString("codigotrampa1"));
                intent.putExtra("municipio1", getIntent().getExtras().getString("municipio1"));
                intent.putExtra("atrayente1", getIntent().getExtras().getString("tipoatrayente1"));
                intent.putExtra("anastrepha1", getIntent().getExtras().getString("anastrepha1"));
                intent.putExtra("ceratis1", getIntent().getExtras().getString("ceratis1"));
                intent.putExtra("otros1", getIntent().getExtras().getString("otros1"));
                intent.putExtra("fenologia1", getIntent().getExtras().getString("fenologia1"));
                intent.putExtra("estado1", getIntent().getExtras().getString("estado1"));
                intent.putExtra("observaciones1", getIntent().getExtras().getString("observaciones1"));
                intent.putExtra("codigotrampa2", getIntent().getExtras().getString("codigotrampa2"));
                intent.putExtra("municipio2", getIntent().getExtras().getString("municipio2"));
                intent.putExtra("atrayente2", getIntent().getExtras().getString("tipoatrayente2"));
                intent.putExtra("anastrepha2", getIntent().getExtras().getString("anastrepha2"));
                intent.putExtra("ceratis2", getIntent().getExtras().getString("ceratis2"));
                intent.putExtra("otros2", getIntent().getExtras().getString("otros2"));
                intent.putExtra("fenologia2", getIntent().getExtras().getString("fenologia2"));
                intent.putExtra("estado2", getIntent().getExtras().getString("estado2"));
                intent.putExtra("observaciones2", getIntent().getExtras().getString("observaciones2"));
                intent.putExtra("codigotrampa3", getIntent().getExtras().getString("codigotrampa3"));
                intent.putExtra("municipio3", getIntent().getExtras().getString("municipio3"));
                intent.putExtra("atrayente3", getIntent().getExtras().getString("tipoatrayente3"));
                intent.putExtra("anastrepha3", getIntent().getExtras().getString("anastrepha3"));
                intent.putExtra("ceratis3", getIntent().getExtras().getString("ceratis3"));
                intent.putExtra("otros3", getIntent().getExtras().getString("otros3"));
                intent.putExtra("fenologia3", getIntent().getExtras().getString("fenologia3"));
                intent.putExtra("estado3", getIntent().getExtras().getString("estado3"));
                intent.putExtra("observaciones3", getIntent().getExtras().getString("observaciones3"));
                intent.putExtra("codigotrampa4", getIntent().getExtras().getString("codigotrampa4"));
                intent.putExtra("municipio4", getIntent().getExtras().getString("municipio4"));
                intent.putExtra("atrayente4", getIntent().getExtras().getString("tipoatrayente4"));
                intent.putExtra("anastrepha4", getIntent().getExtras().getString("anastrepha4"));
                intent.putExtra("ceratis4", getIntent().getExtras().getString("ceratis4"));
                intent.putExtra("otros4", getIntent().getExtras().getString("otros4"));
                intent.putExtra("fenologia4", getIntent().getExtras().getString("fenologia4"));
                intent.putExtra("estado4", getIntent().getExtras().getString("estado4"));
                intent.putExtra("observaciones4", getIntent().getExtras().getString("observaciones4"));
                intent.putExtra("codigotrampa5", getIntent().getExtras().getString("codigotrampa5"));
                intent.putExtra("municipio5", getIntent().getExtras().getString("municipio5"));
                intent.putExtra("atrayente5", getIntent().getExtras().getString("tipoatrayente5"));
                intent.putExtra("anastrepha5", getIntent().getExtras().getString("anastrepha5"));
                intent.putExtra("ceratis5", getIntent().getExtras().getString("ceratis5"));
                intent.putExtra("otros5", getIntent().getExtras().getString("otros5"));
                intent.putExtra("fenologia5", getIntent().getExtras().getString("fenologia5"));
                intent.putExtra("estado5", getIntent().getExtras().getString("estado5"));
                intent.putExtra("observaciones5", getIntent().getExtras().getString("observaciones5"));
                intent.putExtra("codigotrampa6", getIntent().getExtras().getString("codigotrampa6"));
                intent.putExtra("municipio6", getIntent().getExtras().getString("municipio6"));
                intent.putExtra("atrayente6", getIntent().getExtras().getString("tipoatrayente6"));
                intent.putExtra("anastrepha6", getIntent().getExtras().getString("anastrepha6"));
                intent.putExtra("ceratis6", getIntent().getExtras().getString("ceratis6"));
                intent.putExtra("otros6", getIntent().getExtras().getString("otros6"));
                intent.putExtra("fenologia6", getIntent().getExtras().getString("fenologia6"));
                intent.putExtra("estado6", getIntent().getExtras().getString("estado6"));
                intent.putExtra("observaciones6", getIntent().getExtras().getString("observaciones6"));
                intent.putExtra("codigotrampa8", CodigoTrampa.getText() + "");
                intent.putExtra("codigotrampa7", getIntent().getExtras().getString("codigotrampa7"));
                intent.putExtra("municipio7", getIntent().getExtras().getString("municipio7"));
                intent.putExtra("atrayente7", getIntent().getExtras().getString("tipoatrayente7"));
                intent.putExtra("anastrepha7", getIntent().getExtras().getString("anastrepha7"));
                intent.putExtra("ceratis7", getIntent().getExtras().getString("ceratis7"));
                intent.putExtra("otros7", getIntent().getExtras().getString("otros7"));
                intent.putExtra("fenologia7", getIntent().getExtras().getString("fenologia7"));
                intent.putExtra("estado7", getIntent().getExtras().getString("estado7"));
                intent.putExtra("observaciones7", getIntent().getExtras().getString("observaciones7"));
                intent.putExtra("codigotrampa8", CodigoTrampa.getText() + "");
                intent.putExtra("municipio8", CodigoTrampa.getText() + "");
                intent.putExtra("atrayente8", CodigoTrampa.getText() + "");
                intent.putExtra("anastrepha8", CodigoTrampa.getText() + "");
                intent.putExtra("ceratis8", CodigoTrampa.getText() + "");
                intent.putExtra("otros8", CodigoTrampa.getText() + "");
                intent.putExtra("fenologia8", CodigoTrampa.getText() + "");
                intent.putExtra("estado8", CodigoTrampa.getText() + "");
                intent.putExtra("observaciones8", CodigoTrampa.getText() + "");
                intent.putExtra("firma1",getIntent().getExtras().getString("firma1"));
                intent.putExtra("firma2",getIntent().getExtras().getString("firma2"));
                intent.putExtra("firma3",getIntent().getExtras().getString("firma3"));
                intent.putExtra("firma4",getIntent().getExtras().getString("firma4"));
                intent.putExtra("firma5",getIntent().getExtras().getString("firma5"));
                intent.putExtra("firma6",getIntent().getExtras().getString("firma6"));
                intent.putExtra("firma7",getIntent().getExtras().getString("firma7"));
                intent.putExtra("firma8",path);
            }

        }
        añadir ++;
        intent.putExtra("añadir",añadir);
        startActivity(intent);
    }

}








