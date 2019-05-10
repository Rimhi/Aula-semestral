package monterrosa.ricardo.aprendermaps.Inspector;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
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
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PushbuttonField;

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

import monterrosa.ricardo.aprendermaps.ModeloForma3007;
import monterrosa.ricardo.aprendermaps.R;

public class LlenarFormularioPicudoActivity extends AppCompatActivity {
    private String path,path2,TAG="Datos";
    private EditText FuncionarioLectura,CambioFeromona,NumeroFeromona,Year,Mes,Dia,CodigoTrampa,Municipio,Vereda,Predio,
            Negros,Rojos,EstadoCultivo,Observaciones,ObservacionesGenerales,CedulaSupervisor;
    private SignaturePad FirmaSupervisor,FirmaFuncionario;
    private Button LimpiarFuncionario,GuardarFirmaFuncionario,LimpiarSupervisor,GuardarFirmaSupervisor,GuardarFormulario,addFormulario;
    private int añadir = 1;
    private AlertDialog dialog;
    private ProgressDialog progressDialog;
    private File photo,photo2;
    private DatabaseReference baseDatos;
    private DatabaseReference usuarios;
    private DatabaseReference trampas,informe;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_llenar_formulario_picudo);
        inicializar();
        verInforme();
        progressDialog = new ProgressDialog(this);
        firmas();
        GuardarFormulario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LlegadaMapa llegadaMapa = new LlegadaMapa(fechaactual(),CodigoTrampa.getText()+"",auth.getCurrentUser().getDisplayName(),auth.getCurrentUser().getProviderId(),auth.getCurrentUser().getEmail(),auth.getCurrentUser().getUid(),Observaciones.getText()+"","Picudo de Algodon");
                trampas.child(fechaactual()+" "+CodigoTrampa.getText()+"").setValue(llegadaMapa);
                ModeloForma3007 modeloForma3007 = new ModeloForma3007(FuncionarioLectura.getText()+"",
                        CambioFeromona.getText()+"",NumeroFeromona.getText()+"",
                        Year.getText()+"",Mes.getText()+"",Dia.getText()+"",
                        CodigoTrampa.getText()+"",Municipio.getText()+"",
                        Vereda.getText()+"",Predio.getText()+"",Negros.getText()+"",
                        Rojos.getText()+"",EstadoCultivo.getText()+"",Observaciones.getText()+"");
                informe.push().setValue(modeloForma3007);
                llenarpdf(new File(Environment.getExternalStorageDirectory().toString(),"Reportes")+"",path,path2);
                Copiarpdf();
                Context context = LlenarFormularioPicudoActivity.this;
                String[] mailto = {""};
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_EMAIL, mailto);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Reporte "+fechaactual());
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,"");
                emailIntent.setType("application/pdf");
                emailIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"/Reportes/forma3007.pdf" )));
                emailIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivity(Intent.createChooser(emailIntent, ""));
            }
        });
        addFormulario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LlegadaMapa llegadaMapa = new LlegadaMapa(fechaactual(),CodigoTrampa.getText()+"",auth.getCurrentUser().getDisplayName(),auth.getCurrentUser().getProviderId(),auth.getCurrentUser().getEmail(),auth.getCurrentUser().getUid(),Observaciones.getText()+"","Picudo de Algodon");
                trampas.child(fechaactual()+" "+CodigoTrampa.getText()+"").setValue(llegadaMapa);
                ModeloForma3007 modeloForma3007 = new ModeloForma3007(FuncionarioLectura.getText()+"",
                        CambioFeromona.getText()+"",NumeroFeromona.getText()+"",
                        Year.getText()+"",Mes.getText()+"",Dia.getText()+"",
                        CodigoTrampa.getText()+"",Municipio.getText()+"",
                        Vereda.getText()+"",Predio.getText()+"",Negros.getText()+"",
                        Rojos.getText()+"",EstadoCultivo.getText()+"",Observaciones.getText()+"");
                informe.push().setValue(modeloForma3007);
                if (añadir<10){
                    enviardatos();
                }else{
                   Toast.makeText(getApplicationContext(),"Maximo 10 Registros, por favor en via este y crea otro",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
    private void inicializar(){
        FuncionarioLectura = findViewById(R.id.FuncionarioLectura);
        CambioFeromona = findViewById(R.id.CambioFeromona);
        NumeroFeromona = findViewById(R.id.LoteFeromona);
        Year = findViewById(R.id.AñoPicudo);
        Mes = findViewById(R.id.MesPicudo);
        Dia = findViewById(R.id.DiaPicudo);
        CodigoTrampa = findViewById(R.id.CodigoTrampaPicudo);
        Municipio = findViewById(R.id.MunicipioPicudo);
        Vereda = findViewById(R.id.VeredaPicudo);
        Predio = findViewById(R.id.PredioPicudo);
        Negros = findViewById(R.id.NegrosPicudo);
        Rojos = findViewById(R.id.RojosPicudo);
        EstadoCultivo = findViewById(R.id.EstadoCultivoPicudo);
        Observaciones = findViewById(R.id.ObservacionesPicudo);
        ObservacionesGenerales = findViewById(R.id.ObservacionesGeneralesPicudo);
        CedulaSupervisor = findViewById(R.id.CedulaSupervisorPicudo);
        FirmaFuncionario = findViewById(R.id.FirmaFuncionarioPicudo);
        FirmaSupervisor = findViewById(R.id.FirmaSupervisorPicudo);
        LimpiarFuncionario = findViewById(R.id.clearsignaturefuncioariopicudo);
        GuardarFirmaFuncionario = findViewById(R.id.savesignaturefucionariopicudo);
        LimpiarSupervisor = findViewById(R.id.clearsignaturepicudo);
        GuardarFirmaSupervisor = findViewById(R.id.savesignaturepicudo);
        GuardarFormulario = findViewById(R.id.GuardarFormularioPicudo);
        addFormulario = findViewById(R.id.addformulariotrampapicudo);
        if (getIntent().getExtras() != null){
            añadir = getIntent().getExtras().getInt("añadir");
            CodigoTrampa.setText(getIntent().getExtras().getString("codigotrampa"));
            if (getIntent().getExtras().getString("Funcionario") != null){
                FuncionarioLectura.setText(getIntent().getExtras().getString("Funcionario"));
                CambioFeromona.setText(getIntent().getExtras().getString("CambioFeromona"));
                NumeroFeromona.setText(getIntent().getExtras().getString("NumeroFeromona"));
            }
        }
        Mes.setText(mes());
        Year.setText(year());
        Dia.setText(dia());
        baseDatos = FirebaseDatabase.getInstance().getReference();
        usuarios = baseDatos.child("Usuarios");
        trampas = baseDatos.child("Inspecciones");
        informe = baseDatos.child("Formulario");
        auth = FirebaseAuth.getInstance();
    }

    public void firmas(){
        FirmaSupervisor.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {

            }

            @Override
            public void onSigned() {
                GuardarFirmaSupervisor.setEnabled(true);
                LimpiarSupervisor.setEnabled(true);
            }

            @Override
            public void onClear() {
                GuardarFirmaSupervisor.setEnabled(false);
                LimpiarSupervisor.setEnabled(false);
            }
        });
        LimpiarSupervisor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirmaSupervisor.clear();
            }
        });
        GuardarFirmaSupervisor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("consiguiendo firma");
                progressDialog.setCancelable(false);
                progressDialog.show();
                Bitmap signatureBitmap = FirmaSupervisor.getSignatureBitmap();
                if (addJpgSignatureToGallery(signatureBitmap)) {
                    Toast.makeText(LlenarFormularioPicudoActivity.this, "Signature saved into the Gallery", Toast.LENGTH_SHORT).show();
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                } else {
                    Toast.makeText(LlenarFormularioPicudoActivity.this, "Unable to store the signature", Toast.LENGTH_SHORT).show();
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                }
                if (addSvgSignatureToGallery(FirmaSupervisor.getSignatureSvg())) {
                    Toast.makeText(LlenarFormularioPicudoActivity.this, "SVG Signature saved into the Gallery", Toast.LENGTH_SHORT).show();
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                } else {
                    Toast.makeText(LlenarFormularioPicudoActivity.this, "Unable to store the SVG signature", Toast.LENGTH_SHORT).show();
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                }
            }
        });
        FirmaFuncionario.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {

            }

            @Override
            public void onSigned() {
                LimpiarFuncionario.setEnabled(true);
                GuardarFirmaFuncionario.setEnabled(true);
            }

            @Override
            public void onClear() {
                LimpiarFuncionario.setEnabled(false);
                GuardarFirmaFuncionario.setEnabled(false);
            }
        });
        GuardarFirmaFuncionario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("consiguiendo firma");
                progressDialog.setCancelable(false);
                progressDialog.show();
                Bitmap signatureBitmap = FirmaFuncionario.getSignatureBitmap();
                if (addJpgSignatureToGallery2(signatureBitmap)) {
                    Toast.makeText(LlenarFormularioPicudoActivity.this, "Signature saved into the Gallery", Toast.LENGTH_SHORT).show();
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                } else {
                    Toast.makeText(LlenarFormularioPicudoActivity.this, "Unable to store the signature", Toast.LENGTH_SHORT).show();
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                }
                if (addSvgSignatureToGallery2(FirmaFuncionario.getSignatureSvg())) {
                    Toast.makeText(LlenarFormularioPicudoActivity.this, "SVG Signature saved into the Gallery", Toast.LENGTH_SHORT).show();
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                } else {
                    Toast.makeText(LlenarFormularioPicudoActivity.this, "Unable to store the SVG signature", Toast.LENGTH_SHORT).show();
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                }

            }
        });
    }

    private String dia(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd", Locale.getDefault());
        Date date = new Date();

        String fecha = dateFormat.format(date);

        return  fecha;
    }

    private String mes(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM", Locale.getDefault());
        Date date = new Date();

        String fecha = dateFormat.format(date);

        return  fecha;
    }

    private String year(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
        Date date = new Date();

        String fecha = dateFormat.format(date);

        return  fecha;
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
            CopyRawToSDCard((R.raw.forma3007),Environment.getExternalStorageDirectory()+"/forma3007.pdf");
        }
        else {
            CopyRawToSDCard((R.raw.forma3007),Environment.getExternalStorageDirectory()+"/forma3007.pdf");
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
            photo = new File(getAlbumStorageDir("SignaturePad"), String.format("Signature_%d.jpg", System.currentTimeMillis()));
            saveBitmapToJPG(signature, photo);
            scanMediaFile(photo);
            path= photo+"";
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean addJpgSignatureToGallery2(Bitmap signature) {
        boolean result = false;
        try {
            photo2 = new File(getAlbumStorageDir("SignaturePad"), String.format("Signature_%d.jpg", System.currentTimeMillis()));
            saveBitmapToJPG(signature, photo2);
            scanMediaFile(photo2);
            path2= photo2+"";
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
        LlenarFormularioPicudoActivity.this.sendBroadcast(mediaScanIntent);
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

    public boolean addSvgSignatureToGallery2(String signatureSvg) {
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

    public void enviardatos(){
        Intent intent = new Intent(LlenarFormularioPicudoActivity.this,MapaInspectorActivity.class);

            intent.putExtra("llave", "Picudo");
            intent.putExtra("Funcionario", FuncionarioLectura.getText() + "");
            intent.putExtra("Year",Year.getText()+"");
            intent.putExtra("CambioFeromona",CambioFeromona.getText()+"");
            intent.putExtra("NumeroFeromona",NumeroFeromona.getText()+"");
            intent.putExtra("Mes",Mes.getText()+"");

            if (añadir==1){
                intent.putExtra("DiaRow1",Dia.getText()+"");
                intent.putExtra("CodigoTrampaRow1",CodigoTrampa.getText()+"");
                intent.putExtra("MunicipioRow1",Municipio.getText()+"");
                intent.putExtra("VeredaRow1",Vereda.getText()+"");
                intent.putExtra("PredioRow1",Predio.getText()+"");
                intent.putExtra("NegrosRow1",Negros.getText()+"");
                intent.putExtra("RojosRow1",Rojos.getText()+"");
                intent.putExtra("EstadoCultivoRow1",EstadoCultivo.getText()+"");
                intent.putExtra("ObservacionesRow1",Observaciones.getText()+"");
            }
            if (getIntent().getExtras() != null) {
                añadir = getIntent().getExtras().getInt("añadir");
                if (añadir == 2) {
                    intent.putExtra("DiaRow1",getIntent().getExtras().getString("DiaRow1"));
                    intent.putExtra("CodigoTrampaRow1",getIntent().getExtras().getString("CodigoTrampaRow1"));
                    intent.putExtra("MunicipioRow1",getIntent().getExtras().getString("MunicipioRow1"));
                    intent.putExtra("VeredaRow1",getIntent().getExtras().getString("VeredaRow1"));
                    intent.putExtra("PredioRow1",getIntent().getExtras().getString("PredioRow1"));
                    intent.putExtra("NegrosRow1",getIntent().getExtras().getString("NegrosRow1"));
                    intent.putExtra("RojosRow1",getIntent().getExtras().getString("RojosRow1"));
                    intent.putExtra("EstadoCultivoRow1",getIntent().getExtras().getString("EstadoCultivoRow1"));
                    intent.putExtra("ObservacionesRow1",getIntent().getExtras().getString("ObservacionesRow1"));

                    intent.putExtra("DiaRow2", Dia.getText() + "");
                    intent.putExtra("CodigoTrampaRow2", CodigoTrampa.getText() + "");
                    intent.putExtra("MunicipioRow2", Municipio.getText() + "");
                    intent.putExtra("VeredaRow2", Vereda.getText() + "");
                    intent.putExtra("PredioRow2", Predio.getText() + "");
                    intent.putExtra("NegrosRow2", Negros.getText() + "");
                    intent.putExtra("RojosRow2", Rojos.getText() + "");
                    intent.putExtra("EstadoCultivoRow2", EstadoCultivo.getText() + "");
                    intent.putExtra("ObservacionesRow2", Observaciones.getText() + "");
                }if (añadir==3){
                    intent.putExtra("DiaRow1",getIntent().getExtras().getString("DiaRow1"));
                    intent.putExtra("CodigoTrampaRow1",getIntent().getExtras().getString("CodigoTrampaRow1"));
                    intent.putExtra("MunicipioRow1",getIntent().getExtras().getString("MunicipioRow1"));
                    intent.putExtra("VeredaRow1",getIntent().getExtras().getString("VeredaRow1"));
                    intent.putExtra("PredioRow1",getIntent().getExtras().getString("PredioRow1"));
                    intent.putExtra("NegrosRow1",getIntent().getExtras().getString("NegrosRow1"));
                    intent.putExtra("RojosRow1",getIntent().getExtras().getString("RojosRow1"));
                    intent.putExtra("EstadoCultivoRow1",getIntent().getExtras().getString("EstadoCultivoRow1"));
                    intent.putExtra("ObservacionesRow1",getIntent().getExtras().getString("ObservacionesRow1"));

                    intent.putExtra("DiaRow2",getIntent().getExtras().getString("DiaRow2"));
                    intent.putExtra("CodigoTrampaRow2",getIntent().getExtras().getString("CodigoTrampaRow2"));
                    intent.putExtra("MunicipioRow2",getIntent().getExtras().getString("MunicipioRow2"));
                    intent.putExtra("VeredaRow2",getIntent().getExtras().getString("VeredaRow2"));
                    intent.putExtra("PredioRow2",getIntent().getExtras().getString("PredioRow2"));
                    intent.putExtra("NegrosRow2",getIntent().getExtras().getString("NegrosRow2"));
                    intent.putExtra("RojosRow2",getIntent().getExtras().getString("RojosRow2"));
                    intent.putExtra("EstadoCultivoRow2",getIntent().getExtras().getString("EstadoCultivoRow2"));
                    intent.putExtra("ObservacionesRow2",getIntent().getExtras().getString("ObservacionesRow2"));

                    intent.putExtra("DiaRow3", Dia.getText() + "");
                    intent.putExtra("CodigoTrampaRow3", CodigoTrampa.getText() + "");
                    intent.putExtra("MunicipioRow3", Municipio.getText() + "");
                    intent.putExtra("VeredaRow3", Vereda.getText() + "");
                    intent.putExtra("PredioRow3", Predio.getText() + "");
                    intent.putExtra("NegrosRow3", Negros.getText() + "");
                    intent.putExtra("RojosRow3", Rojos.getText() + "");
                    intent.putExtra("EstadoCultivoRow3", EstadoCultivo.getText() + "");
                    intent.putExtra("ObservacionesRow3", Observaciones.getText() + "");
                }
                if (añadir ==4){
                    intent.putExtra("DiaRow1",getIntent().getExtras().getString("DiaRow1"));
                    intent.putExtra("CodigoTrampaRow1",getIntent().getExtras().getString("CodigoTrampaRow1"));
                    intent.putExtra("MunicipioRow1",getIntent().getExtras().getString("MunicipioRow1"));
                    intent.putExtra("VeredaRow1",getIntent().getExtras().getString("VeredaRow1"));
                    intent.putExtra("PredioRow1",getIntent().getExtras().getString("PredioRow1"));
                    intent.putExtra("NegrosRow1",getIntent().getExtras().getString("NegrosRow1"));
                    intent.putExtra("RojosRow1",getIntent().getExtras().getString("RojosRow1"));
                    intent.putExtra("EstadoCultivoRow1",getIntent().getExtras().getString("EstadoCultivoRow1"));
                    intent.putExtra("ObservacionesRow1",getIntent().getExtras().getString("ObservacionesRow1"));

                    intent.putExtra("DiaRow2",getIntent().getExtras().getString("DiaRow2"));
                    intent.putExtra("CodigoTrampaRow2",getIntent().getExtras().getString("CodigoTrampaRow2"));
                    intent.putExtra("MunicipioRow2",getIntent().getExtras().getString("MunicipioRow2"));
                    intent.putExtra("VeredaRow2",getIntent().getExtras().getString("VeredaRow2"));
                    intent.putExtra("PredioRow2",getIntent().getExtras().getString("PredioRow2"));
                    intent.putExtra("NegrosRow2",getIntent().getExtras().getString("NegrosRow2"));
                    intent.putExtra("RojosRow2",getIntent().getExtras().getString("RojosRow2"));
                    intent.putExtra("EstadoCultivoRow2",getIntent().getExtras().getString("EstadoCultivoRow2"));
                    intent.putExtra("ObservacionesRow2",getIntent().getExtras().getString("ObservacionesRow2"));

                    intent.putExtra("DiaRow3",getIntent().getExtras().getString("DiaRow3"));
                    intent.putExtra("CodigoTrampaRow3",getIntent().getExtras().getString("CodigoTrampaRow3"));
                    intent.putExtra("MunicipioRow3",getIntent().getExtras().getString("MunicipioRow3"));
                    intent.putExtra("VeredaRow3",getIntent().getExtras().getString("VeredaRow3"));
                    intent.putExtra("PredioRow3",getIntent().getExtras().getString("PredioRow3"));
                    intent.putExtra("NegrosRow3",getIntent().getExtras().getString("NegrosRow3"));
                    intent.putExtra("RojosRow3",getIntent().getExtras().getString("RojosRow3"));
                    intent.putExtra("EstadoCultivoRow3",getIntent().getExtras().getString("EstadoCultivoRow3"));
                    intent.putExtra("ObservacionesRow3",getIntent().getExtras().getString("ObservacionesRow3"));

                    intent.putExtra("DiaRow4", Dia.getText() + "");
                    intent.putExtra("CodigoTrampaRow4", CodigoTrampa.getText() + "");
                    intent.putExtra("MunicipioRow4", Municipio.getText() + "");
                    intent.putExtra("VeredaRow4", Vereda.getText() + "");
                    intent.putExtra("PredioRow4", Predio.getText() + "");
                    intent.putExtra("NegrosRow4", Negros.getText() + "");
                    intent.putExtra("RojosRow4", Rojos.getText() + "");
                    intent.putExtra("EstadoCultivoRow4", EstadoCultivo.getText() + "");
                    intent.putExtra("ObservacionesRow4", Observaciones.getText() + "");
                }
                if (añadir==5){
                    intent.putExtra("DiaRow1",getIntent().getExtras().getString("DiaRow1"));
                    intent.putExtra("CodigoTrampaRow1",getIntent().getExtras().getString("CodigoTrampaRow1"));
                    intent.putExtra("MunicipioRow1",getIntent().getExtras().getString("MunicipioRow1"));
                    intent.putExtra("VeredaRow1",getIntent().getExtras().getString("VeredaRow1"));
                    intent.putExtra("PredioRow1",getIntent().getExtras().getString("PredioRow1"));
                    intent.putExtra("NegrosRow1",getIntent().getExtras().getString("NegrosRow1"));
                    intent.putExtra("RojosRow1",getIntent().getExtras().getString("RojosRow1"));
                    intent.putExtra("EstadoCultivoRow1",getIntent().getExtras().getString("EstadoCultivoRow1"));
                    intent.putExtra("ObservacionesRow1",getIntent().getExtras().getString("ObservacionesRow1"));

                    intent.putExtra("DiaRow2",getIntent().getExtras().getString("DiaRow2"));
                    intent.putExtra("CodigoTrampaRow2",getIntent().getExtras().getString("CodigoTrampaRow2"));
                    intent.putExtra("MunicipioRow2",getIntent().getExtras().getString("MunicipioRow2"));
                    intent.putExtra("VeredaRow2",getIntent().getExtras().getString("VeredaRow2"));
                    intent.putExtra("PredioRow2",getIntent().getExtras().getString("PredioRow2"));
                    intent.putExtra("NegrosRow2",getIntent().getExtras().getString("NegrosRow2"));
                    intent.putExtra("RojosRow2",getIntent().getExtras().getString("RojosRow2"));
                    intent.putExtra("EstadoCultivoRow2",getIntent().getExtras().getString("EstadoCultivoRow2"));
                    intent.putExtra("ObservacionesRow2",getIntent().getExtras().getString("ObservacionesRow2"));

                    intent.putExtra("DiaRow3",getIntent().getExtras().getString("DiaRow3"));
                    intent.putExtra("CodigoTrampaRow3",getIntent().getExtras().getString("CodigoTrampaRow3"));
                    intent.putExtra("MunicipioRow3",getIntent().getExtras().getString("MunicipioRow3"));
                    intent.putExtra("VeredaRow3",getIntent().getExtras().getString("VeredaRow3"));
                    intent.putExtra("PredioRow3",getIntent().getExtras().getString("PredioRow3"));
                    intent.putExtra("NegrosRow3",getIntent().getExtras().getString("NegrosRow3"));
                    intent.putExtra("RojosRow3",getIntent().getExtras().getString("RojosRow3"));
                    intent.putExtra("EstadoCultivoRow3",getIntent().getExtras().getString("EstadoCultivoRow3"));
                    intent.putExtra("ObservacionesRow3",getIntent().getExtras().getString("ObservacionesRow3"));

                    intent.putExtra("DiaRow4",getIntent().getExtras().getString("DiaRow4"));
                    intent.putExtra("CodigoTrampaRow4",getIntent().getExtras().getString("CodigoTrampaRow4"));
                    intent.putExtra("MunicipioRow4",getIntent().getExtras().getString("MunicipioRow4"));
                    intent.putExtra("VeredaRow4",getIntent().getExtras().getString("VeredaRow4"));
                    intent.putExtra("PredioRow4",getIntent().getExtras().getString("PredioRow4"));
                    intent.putExtra("NegrosRow4",getIntent().getExtras().getString("NegrosRow4"));
                    intent.putExtra("RojosRow4",getIntent().getExtras().getString("RojosRow4"));
                    intent.putExtra("EstadoCultivoRow4",getIntent().getExtras().getString("EstadoCultivoRow4"));
                    intent.putExtra("ObservacionesRow4",getIntent().getExtras().getString("ObservacionesRow4"));

                    intent.putExtra("DiaRow5", Dia.getText() + "");
                    intent.putExtra("CodigoTrampaRow5", CodigoTrampa.getText() + "");
                    intent.putExtra("MunicipioRow5", Municipio.getText() + "");
                    intent.putExtra("VeredaRow5", Vereda.getText() + "");
                    intent.putExtra("PredioRow5", Predio.getText() + "");
                    intent.putExtra("NegrosRow5", Negros.getText() + "");
                    intent.putExtra("RojosRow5", Rojos.getText() + "");
                    intent.putExtra("ObservacionesRow5", Observaciones.getText() + "");
                }
                if (añadir==6){
                    intent.putExtra("DiaRow1",getIntent().getExtras().getString("DiaRow1"));
                    intent.putExtra("CodigoTrampaRow1",getIntent().getExtras().getString("CodigoTrampaRow1"));
                    intent.putExtra("MunicipioRow1",getIntent().getExtras().getString("MunicipioRow1"));
                    intent.putExtra("VeredaRow1",getIntent().getExtras().getString("VeredaRow1"));
                    intent.putExtra("PredioRow1",getIntent().getExtras().getString("PredioRow1"));
                    intent.putExtra("NegrosRow1",getIntent().getExtras().getString("NegrosRow1"));
                    intent.putExtra("RojosRow1",getIntent().getExtras().getString("RojosRow1"));
                    intent.putExtra("EstadoCultivoRow1",getIntent().getExtras().getString("EstadoCultivoRow1"));
                    intent.putExtra("ObservacionesRow1",getIntent().getExtras().getString("ObservacionesRow1"));

                    intent.putExtra("DiaRow2",getIntent().getExtras().getString("DiaRow2"));
                    intent.putExtra("CodigoTrampaRow2",getIntent().getExtras().getString("CodigoTrampaRow2"));
                    intent.putExtra("MunicipioRow2",getIntent().getExtras().getString("MunicipioRow2"));
                    intent.putExtra("VeredaRow2",getIntent().getExtras().getString("VeredaRow2"));
                    intent.putExtra("PredioRow2",getIntent().getExtras().getString("PredioRow2"));
                    intent.putExtra("NegrosRow2",getIntent().getExtras().getString("NegrosRow2"));
                    intent.putExtra("RojosRow2",getIntent().getExtras().getString("RojosRow2"));
                    intent.putExtra("EstadoCultivoRow2",getIntent().getExtras().getString("EstadoCultivoRow2"));
                    intent.putExtra("ObservacionesRow2",getIntent().getExtras().getString("ObservacionesRow2"));

                    intent.putExtra("DiaRow3",getIntent().getExtras().getString("DiaRow3"));
                    intent.putExtra("CodigoTrampaRow3",getIntent().getExtras().getString("CodigoTrampaRow3"));
                    intent.putExtra("MunicipioRow3",getIntent().getExtras().getString("MunicipioRow3"));
                    intent.putExtra("VeredaRow3",getIntent().getExtras().getString("VeredaRow3"));
                    intent.putExtra("PredioRow3",getIntent().getExtras().getString("PredioRow3"));
                    intent.putExtra("NegrosRow3",getIntent().getExtras().getString("NegrosRow3"));
                    intent.putExtra("RojosRow3",getIntent().getExtras().getString("RojosRow3"));
                    intent.putExtra("EstadoCultivoRow3",getIntent().getExtras().getString("EstadoCultivoRow3"));
                    intent.putExtra("ObservacionesRow3",getIntent().getExtras().getString("ObservacionesRow3"));

                    intent.putExtra("DiaRow4",getIntent().getExtras().getString("DiaRow4"));
                    intent.putExtra("CodigoTrampaRow4",getIntent().getExtras().getString("CodigoTrampaRow4"));
                    intent.putExtra("MunicipioRow4",getIntent().getExtras().getString("MunicipioRow4"));
                    intent.putExtra("VeredaRow4",getIntent().getExtras().getString("VeredaRow4"));
                    intent.putExtra("PredioRow4",getIntent().getExtras().getString("PredioRow4"));
                    intent.putExtra("NegrosRow4",getIntent().getExtras().getString("NegrosRow4"));
                    intent.putExtra("RojosRow4",getIntent().getExtras().getString("RojosRow4"));
                    intent.putExtra("EstadoCultivoRow4",getIntent().getExtras().getString("EstadoCultivoRow4"));
                    intent.putExtra("ObservacionesRow4",getIntent().getExtras().getString("ObservacionesRow4"));

                    intent.putExtra("DiaRow5",getIntent().getExtras().getString("DiaRow5"));
                    intent.putExtra("CodigoTrampaRow5",getIntent().getExtras().getString("CodigoTrampaRow5"));
                    intent.putExtra("MunicipioRow5",getIntent().getExtras().getString("MunicipioRow5"));
                    intent.putExtra("VeredaRow5",getIntent().getExtras().getString("VeredaRow5"));
                    intent.putExtra("PredioRow5",getIntent().getExtras().getString("PredioRow5"));
                    intent.putExtra("NegrosRow5",getIntent().getExtras().getString("NegrosRow5"));
                    intent.putExtra("RojosRow5",getIntent().getExtras().getString("RojosRow5"));
                    intent.putExtra("EstadoCultivoRow5",getIntent().getExtras().getString("EstadoCultivoRow5"));
                    intent.putExtra("ObservacionesRow5",getIntent().getExtras().getString("ObservacionesRow5"));

                    intent.putExtra("DiaRow6", Dia.getText() + "");
                    intent.putExtra("CodigoTrampaRow6", CodigoTrampa.getText() + "");
                    intent.putExtra("MunicipioRow6", Municipio.getText() + "");
                    intent.putExtra("VeredaRow6", Vereda.getText() + "");
                    intent.putExtra("PredioRow6", Predio.getText() + "");
                    intent.putExtra("NegrosRow6", Negros.getText() + "");
                    intent.putExtra("RojosRow6", Rojos.getText() + "");
                    intent.putExtra("EstadoCultivoRow6", EstadoCultivo.getText() + "");
                    intent.putExtra("ObservacionesRow6", Observaciones.getText() + "");
                }
                if (añadir==7){
                    intent.putExtra("DiaRow1",getIntent().getExtras().getString("DiaRow1"));
                    intent.putExtra("CodigoTrampaRow1",getIntent().getExtras().getString("CodigoTrampaRow1"));
                    intent.putExtra("MunicipioRow1",getIntent().getExtras().getString("MunicipioRow1"));
                    intent.putExtra("VeredaRow1",getIntent().getExtras().getString("VeredaRow1"));
                    intent.putExtra("PredioRow1",getIntent().getExtras().getString("PredioRow1"));
                    intent.putExtra("NegrosRow1",getIntent().getExtras().getString("NegrosRow1"));
                    intent.putExtra("RojosRow1",getIntent().getExtras().getString("RojosRow1"));
                    intent.putExtra("EstadoCultivoRow1",getIntent().getExtras().getString("EstadoCultivoRow1"));
                    intent.putExtra("ObservacionesRow1",getIntent().getExtras().getString("ObservacionesRow1"));

                    intent.putExtra("DiaRow2",getIntent().getExtras().getString("DiaRow2"));
                    intent.putExtra("CodigoTrampaRow2",getIntent().getExtras().getString("CodigoTrampaRow2"));
                    intent.putExtra("MunicipioRow2",getIntent().getExtras().getString("MunicipioRow2"));
                    intent.putExtra("VeredaRow2",getIntent().getExtras().getString("VeredaRow2"));
                    intent.putExtra("PredioRow2",getIntent().getExtras().getString("PredioRow2"));
                    intent.putExtra("NegrosRow2",getIntent().getExtras().getString("NegrosRow2"));
                    intent.putExtra("RojosRow2",getIntent().getExtras().getString("RojosRow2"));
                    intent.putExtra("EstadoCultivoRow2",getIntent().getExtras().getString("EstadoCultivoRow2"));
                    intent.putExtra("ObservacionesRow2",getIntent().getExtras().getString("ObservacionesRow2"));

                    intent.putExtra("DiaRow3",getIntent().getExtras().getString("DiaRow3"));
                    intent.putExtra("CodigoTrampaRow3",getIntent().getExtras().getString("CodigoTrampaRow3"));
                    intent.putExtra("MunicipioRow3",getIntent().getExtras().getString("MunicipioRow3"));
                    intent.putExtra("VeredaRow3",getIntent().getExtras().getString("VeredaRow3"));
                    intent.putExtra("PredioRow3",getIntent().getExtras().getString("PredioRow3"));
                    intent.putExtra("NegrosRow3",getIntent().getExtras().getString("NegrosRow3"));
                    intent.putExtra("RojosRow3",getIntent().getExtras().getString("RojosRow3"));
                    intent.putExtra("EstadoCultivoRow3",getIntent().getExtras().getString("EstadoCultivoRow3"));
                    intent.putExtra("ObservacionesRow3",getIntent().getExtras().getString("ObservacionesRow3"));

                    intent.putExtra("DiaRow4",getIntent().getExtras().getString("DiaRow4"));
                    intent.putExtra("CodigoTrampaRow4",getIntent().getExtras().getString("CodigoTrampaRow4"));
                    intent.putExtra("MunicipioRow4",getIntent().getExtras().getString("MunicipioRow4"));
                    intent.putExtra("VeredaRow4",getIntent().getExtras().getString("VeredaRow4"));
                    intent.putExtra("PredioRow4",getIntent().getExtras().getString("PredioRow4"));
                    intent.putExtra("NegrosRow4",getIntent().getExtras().getString("NegrosRow4"));
                    intent.putExtra("RojosRow4",getIntent().getExtras().getString("RojosRow4"));
                    intent.putExtra("EstadoCultivoRow4",getIntent().getExtras().getString("EstadoCultivoRow4"));
                    intent.putExtra("ObservacionesRow4",getIntent().getExtras().getString("ObservacionesRow4"));

                    intent.putExtra("DiaRow5",getIntent().getExtras().getString("DiaRow5"));
                    intent.putExtra("CodigoTrampaRow5",getIntent().getExtras().getString("CodigoTrampaRow5"));
                    intent.putExtra("MunicipioRow5",getIntent().getExtras().getString("MunicipioRow5"));
                    intent.putExtra("VeredaRow5",getIntent().getExtras().getString("VeredaRow5"));
                    intent.putExtra("PredioRow5",getIntent().getExtras().getString("PredioRow5"));
                    intent.putExtra("NegrosRow5",getIntent().getExtras().getString("NegrosRow5"));
                    intent.putExtra("RojosRow5",getIntent().getExtras().getString("RojosRow5"));
                    intent.putExtra("EstadoCultivoRow5",getIntent().getExtras().getString("EstadoCultivoRow5"));
                    intent.putExtra("ObservacionesRow5",getIntent().getExtras().getString("ObservacionesRow5"));

                    intent.putExtra("DiaRow6",getIntent().getExtras().getString("DiaRow6"));
                    intent.putExtra("CodigoTrampaRow6",getIntent().getExtras().getString("CodigoTrampaRow6"));
                    intent.putExtra("MunicipioRow6",getIntent().getExtras().getString("MunicipioRow6"));
                    intent.putExtra("VeredaRow6",getIntent().getExtras().getString("VeredaRow6"));
                    intent.putExtra("PredioRow6",getIntent().getExtras().getString("PredioRow6"));
                    intent.putExtra("NegrosRow6",getIntent().getExtras().getString("NegrosRow6"));
                    intent.putExtra("RojosRow6",getIntent().getExtras().getString("RojosRow6"));
                    intent.putExtra("EstadoCultivoRow6",getIntent().getExtras().getString("EstadoCultivoRow6"));
                    intent.putExtra("ObservacionesRow6",getIntent().getExtras().getString("ObservacionesRow6"));

                    intent.putExtra("DiaRow7", Dia.getText() + "");
                    intent.putExtra("CodigoTrampaRow7", CodigoTrampa.getText() + "");
                    intent.putExtra("MunicipioRow7", Municipio.getText() + "");
                    intent.putExtra("VeredaRow7", Vereda.getText() + "");
                    intent.putExtra("PredioRow7", Predio.getText() + "");
                    intent.putExtra("NegrosRow7", Negros.getText() + "");
                    intent.putExtra("RojosRow7", Rojos.getText() + "");
                    intent.putExtra("EstadoCultivoRow7", EstadoCultivo.getText() + "");
                    intent.putExtra("ObservacionesRow7", Observaciones.getText() + "");
                }if (añadir==8){
                    intent.putExtra("DiaRow1",getIntent().getExtras().getString("DiaRow1"));
                    intent.putExtra("CodigoTrampaRow1",getIntent().getExtras().getString("CodigoTrampaRow1"));
                    intent.putExtra("MunicipioRow1",getIntent().getExtras().getString("MunicipioRow1"));
                    intent.putExtra("VeredaRow1",getIntent().getExtras().getString("VeredaRow1"));
                    intent.putExtra("PredioRow1",getIntent().getExtras().getString("PredioRow1"));
                    intent.putExtra("NegrosRow1",getIntent().getExtras().getString("NegrosRow1"));
                    intent.putExtra("RojosRow1",getIntent().getExtras().getString("RojosRow1"));
                    intent.putExtra("EstadoCultivoRow1",getIntent().getExtras().getString("EstadoCultivoRow1"));
                    intent.putExtra("ObservacionesRow1",getIntent().getExtras().getString("ObservacionesRow1"));

                    intent.putExtra("DiaRow2",getIntent().getExtras().getString("DiaRow2"));
                    intent.putExtra("CodigoTrampaRow2",getIntent().getExtras().getString("CodigoTrampaRow2"));
                    intent.putExtra("MunicipioRow2",getIntent().getExtras().getString("MunicipioRow2"));
                    intent.putExtra("VeredaRow2",getIntent().getExtras().getString("VeredaRow2"));
                    intent.putExtra("PredioRow2",getIntent().getExtras().getString("PredioRow2"));
                    intent.putExtra("NegrosRow2",getIntent().getExtras().getString("NegrosRow2"));
                    intent.putExtra("RojosRow2",getIntent().getExtras().getString("RojosRow2"));
                    intent.putExtra("EstadoCultivoRow2",getIntent().getExtras().getString("EstadoCultivoRow2"));
                    intent.putExtra("ObservacionesRow2",getIntent().getExtras().getString("ObservacionesRow2"));

                    intent.putExtra("DiaRow3",getIntent().getExtras().getString("DiaRow3"));
                    intent.putExtra("CodigoTrampaRow3",getIntent().getExtras().getString("CodigoTrampaRow3"));
                    intent.putExtra("MunicipioRow3",getIntent().getExtras().getString("MunicipioRow3"));
                    intent.putExtra("VeredaRow3",getIntent().getExtras().getString("VeredaRow3"));
                    intent.putExtra("PredioRow3",getIntent().getExtras().getString("PredioRow3"));
                    intent.putExtra("NegrosRow3",getIntent().getExtras().getString("NegrosRow3"));
                    intent.putExtra("RojosRow3",getIntent().getExtras().getString("RojosRow3"));
                    intent.putExtra("EstadoCultivoRow3",getIntent().getExtras().getString("EstadoCultivoRow3"));
                    intent.putExtra("ObservacionesRow3",getIntent().getExtras().getString("ObservacionesRow3"));

                    intent.putExtra("DiaRow4",getIntent().getExtras().getString("DiaRow4"));
                    intent.putExtra("CodigoTrampaRow4",getIntent().getExtras().getString("CodigoTrampaRow4"));
                    intent.putExtra("MunicipioRow4",getIntent().getExtras().getString("MunicipioRow4"));
                    intent.putExtra("VeredaRow4",getIntent().getExtras().getString("VeredaRow4"));
                    intent.putExtra("PredioRow4",getIntent().getExtras().getString("PredioRow4"));
                    intent.putExtra("NegrosRow4",getIntent().getExtras().getString("NegrosRow4"));
                    intent.putExtra("RojosRow4",getIntent().getExtras().getString("RojosRow4"));
                    intent.putExtra("EstadoCultivoRow4",getIntent().getExtras().getString("EstadoCultivoRow4"));
                    intent.putExtra("ObservacionesRow4",getIntent().getExtras().getString("ObservacionesRow4"));

                    intent.putExtra("DiaRow5",getIntent().getExtras().getString("DiaRow5"));
                    intent.putExtra("CodigoTrampaRow5",getIntent().getExtras().getString("CodigoTrampaRow5"));
                    intent.putExtra("MunicipioRow5",getIntent().getExtras().getString("MunicipioRow5"));
                    intent.putExtra("VeredaRow5",getIntent().getExtras().getString("VeredaRow5"));
                    intent.putExtra("PredioRow5",getIntent().getExtras().getString("PredioRow5"));
                    intent.putExtra("NegrosRow5",getIntent().getExtras().getString("NegrosRow5"));
                    intent.putExtra("RojosRow5",getIntent().getExtras().getString("RojosRow5"));
                    intent.putExtra("EstadoCultivoRow5",getIntent().getExtras().getString("EstadoCultivoRow5"));
                    intent.putExtra("ObservacionesRow5",getIntent().getExtras().getString("ObservacionesRow5"));

                    intent.putExtra("DiaRow6",getIntent().getExtras().getString("DiaRow6"));
                    intent.putExtra("CodigoTrampaRow6",getIntent().getExtras().getString("CodigoTrampaRow6"));
                    intent.putExtra("MunicipioRow6",getIntent().getExtras().getString("MunicipioRow6"));
                    intent.putExtra("VeredaRow6",getIntent().getExtras().getString("VeredaRow6"));
                    intent.putExtra("PredioRow6",getIntent().getExtras().getString("PredioRow6"));
                    intent.putExtra("NegrosRow6",getIntent().getExtras().getString("NegrosRow6"));
                    intent.putExtra("RojosRow6",getIntent().getExtras().getString("RojosRow6"));
                    intent.putExtra("EstadoCultivoRow6",getIntent().getExtras().getString("EstadoCultivoRow6"));
                    intent.putExtra("ObservacionesRow6",getIntent().getExtras().getString("ObservacionesRow6"));

                    intent.putExtra("DiaRow7",getIntent().getExtras().getString("DiaRow7"));
                    intent.putExtra("CodigoTrampaRow7",getIntent().getExtras().getString("CodigoTrampaRow7"));
                    intent.putExtra("MunicipioRow7",getIntent().getExtras().getString("MunicipioRow7"));
                    intent.putExtra("VeredaRow7",getIntent().getExtras().getString("VeredaRow7"));
                    intent.putExtra("PredioRow7",getIntent().getExtras().getString("PredioRow7"));
                    intent.putExtra("NegrosRow7",getIntent().getExtras().getString("NegrosRow7"));
                    intent.putExtra("RojosRow7",getIntent().getExtras().getString("RojosRow7"));
                    intent.putExtra("EstadoCultivoRow7",getIntent().getExtras().getString("EstadoCultivoRow7"));
                    intent.putExtra("ObservacionesRow7",getIntent().getExtras().getString("ObservacionesRow7"));

                    intent.putExtra("DiaRow8", Dia.getText() + "");
                    intent.putExtra("CodigoTrampaRow8", CodigoTrampa.getText() + "");
                    intent.putExtra("MunicipioRow8", Municipio.getText() + "");
                    intent.putExtra("VeredaRow8", Vereda.getText() + "");
                    intent.putExtra("PredioRow8", Predio.getText() + "");
                    intent.putExtra("NegrosRow8", Negros.getText() + "");
                    intent.putExtra("RojosRow8", Rojos.getText() + "");
                    intent.putExtra("EstadoCultivoRow8", EstadoCultivo.getText() + "");
                    intent.putExtra("ObservacionesRow10", Observaciones.getText() + "");
                }
                if (añadir==9){
                    intent.putExtra("DiaRow1",getIntent().getExtras().getString("DiaRow1"));
                    intent.putExtra("CodigoTrampaRow1",getIntent().getExtras().getString("CodigoTrampaRow1"));
                    intent.putExtra("MunicipioRow1",getIntent().getExtras().getString("MunicipioRow1"));
                    intent.putExtra("VeredaRow1",getIntent().getExtras().getString("VeredaRow1"));
                    intent.putExtra("PredioRow1",getIntent().getExtras().getString("PredioRow1"));
                    intent.putExtra("NegrosRow1",getIntent().getExtras().getString("NegrosRow1"));
                    intent.putExtra("RojosRow1",getIntent().getExtras().getString("RojosRow1"));
                    intent.putExtra("EstadoCultivoRow1",getIntent().getExtras().getString("EstadoCultivoRow1"));
                    intent.putExtra("ObservacionesRow1",getIntent().getExtras().getString("ObservacionesRow1"));

                    intent.putExtra("DiaRow2",getIntent().getExtras().getString("DiaRow2"));
                    intent.putExtra("CodigoTrampaRow2",getIntent().getExtras().getString("CodigoTrampaRow2"));
                    intent.putExtra("MunicipioRow2",getIntent().getExtras().getString("MunicipioRow2"));
                    intent.putExtra("VeredaRow2",getIntent().getExtras().getString("VeredaRow2"));
                    intent.putExtra("PredioRow2",getIntent().getExtras().getString("PredioRow2"));
                    intent.putExtra("NegrosRow2",getIntent().getExtras().getString("NegrosRow2"));
                    intent.putExtra("RojosRow2",getIntent().getExtras().getString("RojosRow2"));
                    intent.putExtra("EstadoCultivoRow2",getIntent().getExtras().getString("EstadoCultivoRow2"));
                    intent.putExtra("ObservacionesRow2",getIntent().getExtras().getString("ObservacionesRow2"));

                    intent.putExtra("DiaRow3",getIntent().getExtras().getString("DiaRow3"));
                    intent.putExtra("CodigoTrampaRow3",getIntent().getExtras().getString("CodigoTrampaRow3"));
                    intent.putExtra("MunicipioRow3",getIntent().getExtras().getString("MunicipioRow3"));
                    intent.putExtra("VeredaRow3",getIntent().getExtras().getString("VeredaRow3"));
                    intent.putExtra("PredioRow3",getIntent().getExtras().getString("PredioRow3"));
                    intent.putExtra("NegrosRow3",getIntent().getExtras().getString("NegrosRow3"));
                    intent.putExtra("RojosRow3",getIntent().getExtras().getString("RojosRow3"));
                    intent.putExtra("EstadoCultivoRow3",getIntent().getExtras().getString("EstadoCultivoRow3"));
                    intent.putExtra("ObservacionesRow3",getIntent().getExtras().getString("ObservacionesRow3"));

                    intent.putExtra("DiaRow4",getIntent().getExtras().getString("DiaRow4"));
                    intent.putExtra("CodigoTrampaRow4",getIntent().getExtras().getString("CodigoTrampaRow4"));
                    intent.putExtra("MunicipioRow4",getIntent().getExtras().getString("MunicipioRow4"));
                    intent.putExtra("VeredaRow4",getIntent().getExtras().getString("VeredaRow4"));
                    intent.putExtra("PredioRow4",getIntent().getExtras().getString("PredioRow4"));
                    intent.putExtra("NegrosRow4",getIntent().getExtras().getString("NegrosRow4"));
                    intent.putExtra("RojosRow4",getIntent().getExtras().getString("RojosRow4"));
                    intent.putExtra("EstadoCultivoRow4",getIntent().getExtras().getString("EstadoCultivoRow4"));
                    intent.putExtra("ObservacionesRow4",getIntent().getExtras().getString("ObservacionesRow4"));

                    intent.putExtra("DiaRow5",getIntent().getExtras().getString("DiaRow5"));
                    intent.putExtra("CodigoTrampaRow5",getIntent().getExtras().getString("CodigoTrampaRow5"));
                    intent.putExtra("MunicipioRow5",getIntent().getExtras().getString("MunicipioRow5"));
                    intent.putExtra("VeredaRow5",getIntent().getExtras().getString("VeredaRow5"));
                    intent.putExtra("PredioRow5",getIntent().getExtras().getString("PredioRow5"));
                    intent.putExtra("NegrosRow5",getIntent().getExtras().getString("NegrosRow5"));
                    intent.putExtra("RojosRow5",getIntent().getExtras().getString("RojosRow5"));
                    intent.putExtra("EstadoCultivoRow5",getIntent().getExtras().getString("EstadoCultivoRow5"));
                    intent.putExtra("ObservacionesRow5",getIntent().getExtras().getString("ObservacionesRow5"));

                    intent.putExtra("DiaRow6",getIntent().getExtras().getString("DiaRow6"));
                    intent.putExtra("CodigoTrampaRow6",getIntent().getExtras().getString("CodigoTrampaRow6"));
                    intent.putExtra("MunicipioRow6",getIntent().getExtras().getString("MunicipioRow6"));
                    intent.putExtra("VeredaRow6",getIntent().getExtras().getString("VeredaRow6"));
                    intent.putExtra("PredioRow6",getIntent().getExtras().getString("PredioRow6"));
                    intent.putExtra("NegrosRow6",getIntent().getExtras().getString("NegrosRow6"));
                    intent.putExtra("RojosRow6",getIntent().getExtras().getString("RojosRow6"));
                    intent.putExtra("EstadoCultivoRow6",getIntent().getExtras().getString("EstadoCultivoRow6"));
                    intent.putExtra("ObservacionesRow6",getIntent().getExtras().getString("ObservacionesRow6"));

                    intent.putExtra("DiaRow7",getIntent().getExtras().getString("DiaRow7"));
                    intent.putExtra("CodigoTrampaRow7",getIntent().getExtras().getString("CodigoTrampaRow7"));
                    intent.putExtra("MunicipioRow7",getIntent().getExtras().getString("MunicipioRow7"));
                    intent.putExtra("VeredaRow7",getIntent().getExtras().getString("VeredaRow7"));
                    intent.putExtra("PredioRow7",getIntent().getExtras().getString("PredioRow7"));
                    intent.putExtra("NegrosRow7",getIntent().getExtras().getString("NegrosRow7"));
                    intent.putExtra("RojosRow7",getIntent().getExtras().getString("RojosRow7"));
                    intent.putExtra("EstadoCultivoRow7",getIntent().getExtras().getString("EstadoCultivoRow7"));
                    intent.putExtra("ObservacionesRow7",getIntent().getExtras().getString("ObservacionesRow7"));

                    intent.putExtra("DiaRow8",getIntent().getExtras().getString("DiaRow8"));
                    intent.putExtra("CodigoTrampaRow8",getIntent().getExtras().getString("CodigoTrampaRow8"));
                    intent.putExtra("MunicipioRow8",getIntent().getExtras().getString("MunicipioRow8"));
                    intent.putExtra("VeredaRow8",getIntent().getExtras().getString("VeredaRow8"));
                    intent.putExtra("PredioRow8",getIntent().getExtras().getString("PredioRow8"));
                    intent.putExtra("NegrosRow8",getIntent().getExtras().getString("NegrosRow8"));
                    intent.putExtra("RojosRow8",getIntent().getExtras().getString("RojosRow8"));
                    intent.putExtra("EstadoCultivoRow8",getIntent().getExtras().getString("EstadoCultivoRow8"));
                    intent.putExtra("ObservacionesRow8",getIntent().getExtras().getString("ObservacionesRow8"));

                    intent.putExtra("DiaRow9", Dia.getText() + "");
                    intent.putExtra("CodigoTrampaRow9", CodigoTrampa.getText() + "");
                    intent.putExtra("MunicipioRow9", Municipio.getText() + "");
                    intent.putExtra("VeredaRow9", Vereda.getText() + "");
                    intent.putExtra("PredioRow9", Predio.getText() + "");
                    intent.putExtra("NegrosRow9", Negros.getText() + "");
                    intent.putExtra("RojosRow9", Rojos.getText() + "");
                    intent.putExtra("EstadoCultivoRow9", EstadoCultivo.getText() + "");
                    intent.putExtra("ObservacionesRow9", Observaciones.getText() + "");
                }
                if (añadir==10){
                    intent.putExtra("DiaRow1",getIntent().getExtras().getString("DiaRow1"));
                    intent.putExtra("CodigoTrampaRow1",getIntent().getExtras().getString("CodigoTrampaRow1"));
                    intent.putExtra("MunicipioRow1",getIntent().getExtras().getString("MunicipioRow1"));
                    intent.putExtra("VeredaRow1",getIntent().getExtras().getString("VeredaRow1"));
                    intent.putExtra("PredioRow1",getIntent().getExtras().getString("PredioRow1"));
                    intent.putExtra("NegrosRow1",getIntent().getExtras().getString("NegrosRow1"));
                    intent.putExtra("RojosRow1",getIntent().getExtras().getString("RojosRow1"));
                    intent.putExtra("EstadoCultivoRow1",getIntent().getExtras().getString("EstadoCultivoRow1"));
                    intent.putExtra("ObservacionesRow1",getIntent().getExtras().getString("ObservacionesRow1"));

                    intent.putExtra("DiaRow2",getIntent().getExtras().getString("DiaRow2"));
                    intent.putExtra("CodigoTrampaRow2",getIntent().getExtras().getString("CodigoTrampaRow2"));
                    intent.putExtra("MunicipioRow2",getIntent().getExtras().getString("MunicipioRow2"));
                    intent.putExtra("VeredaRow2",getIntent().getExtras().getString("VeredaRow2"));
                    intent.putExtra("PredioRow2",getIntent().getExtras().getString("PredioRow2"));
                    intent.putExtra("NegrosRow2",getIntent().getExtras().getString("NegrosRow2"));
                    intent.putExtra("RojosRow2",getIntent().getExtras().getString("RojosRow2"));
                    intent.putExtra("EstadoCultivoRow2",getIntent().getExtras().getString("EstadoCultivoRow2"));
                    intent.putExtra("ObservacionesRow2",getIntent().getExtras().getString("ObservacionesRow2"));

                    intent.putExtra("DiaRow3",getIntent().getExtras().getString("DiaRow3"));
                    intent.putExtra("CodigoTrampaRow3",getIntent().getExtras().getString("CodigoTrampaRow3"));
                    intent.putExtra("MunicipioRow3",getIntent().getExtras().getString("MunicipioRow3"));
                    intent.putExtra("VeredaRow3",getIntent().getExtras().getString("VeredaRow3"));
                    intent.putExtra("PredioRow3",getIntent().getExtras().getString("PredioRow3"));
                    intent.putExtra("NegrosRow3",getIntent().getExtras().getString("NegrosRow3"));
                    intent.putExtra("RojosRow3",getIntent().getExtras().getString("RojosRow3"));
                    intent.putExtra("EstadoCultivoRow3",getIntent().getExtras().getString("EstadoCultivoRow3"));
                    intent.putExtra("ObservacionesRow3",getIntent().getExtras().getString("ObservacionesRow3"));

                    intent.putExtra("DiaRow4",getIntent().getExtras().getString("DiaRow4"));
                    intent.putExtra("CodigoTrampaRow4",getIntent().getExtras().getString("CodigoTrampaRow4"));
                    intent.putExtra("MunicipioRow4",getIntent().getExtras().getString("MunicipioRow4"));
                    intent.putExtra("VeredaRow4",getIntent().getExtras().getString("VeredaRow4"));
                    intent.putExtra("PredioRow4",getIntent().getExtras().getString("PredioRow4"));
                    intent.putExtra("NegrosRow4",getIntent().getExtras().getString("NegrosRow4"));
                    intent.putExtra("RojosRow4",getIntent().getExtras().getString("RojosRow4"));
                    intent.putExtra("EstadoCultivoRow4",getIntent().getExtras().getString("EstadoCultivoRow4"));
                    intent.putExtra("ObservacionesRow4",getIntent().getExtras().getString("ObservacionesRow4"));

                    intent.putExtra("DiaRow5",getIntent().getExtras().getString("DiaRow5"));
                    intent.putExtra("CodigoTrampaRow5",getIntent().getExtras().getString("CodigoTrampaRow5"));
                    intent.putExtra("MunicipioRow5",getIntent().getExtras().getString("MunicipioRow5"));
                    intent.putExtra("VeredaRow5",getIntent().getExtras().getString("VeredaRow5"));
                    intent.putExtra("PredioRow5",getIntent().getExtras().getString("PredioRow5"));
                    intent.putExtra("NegrosRow5",getIntent().getExtras().getString("NegrosRow5"));
                    intent.putExtra("RojosRow5",getIntent().getExtras().getString("RojosRow5"));
                    intent.putExtra("EstadoCultivoRow5",getIntent().getExtras().getString("EstadoCultivoRow5"));
                    intent.putExtra("ObservacionesRow5",getIntent().getExtras().getString("ObservacionesRow5"));

                    intent.putExtra("DiaRow6",getIntent().getExtras().getString("DiaRow6"));
                    intent.putExtra("CodigoTrampaRow6",getIntent().getExtras().getString("CodigoTrampaRow6"));
                    intent.putExtra("MunicipioRow6",getIntent().getExtras().getString("MunicipioRow6"));
                    intent.putExtra("VeredaRow6",getIntent().getExtras().getString("VeredaRow6"));
                    intent.putExtra("PredioRow6",getIntent().getExtras().getString("PredioRow6"));
                    intent.putExtra("NegrosRow6",getIntent().getExtras().getString("NegrosRow6"));
                    intent.putExtra("RojosRow6",getIntent().getExtras().getString("RojosRow6"));
                    intent.putExtra("EstadoCultivoRow6",getIntent().getExtras().getString("EstadoCultivoRow6"));
                    intent.putExtra("ObservacionesRow6",getIntent().getExtras().getString("ObservacionesRow6"));

                    intent.putExtra("DiaRow7",getIntent().getExtras().getString("DiaRow7"));
                    intent.putExtra("CodigoTrampaRow7",getIntent().getExtras().getString("CodigoTrampaRow7"));
                    intent.putExtra("MunicipioRow7",getIntent().getExtras().getString("MunicipioRow7"));
                    intent.putExtra("VeredaRow7",getIntent().getExtras().getString("VeredaRow7"));
                    intent.putExtra("PredioRow7",getIntent().getExtras().getString("PredioRow7"));
                    intent.putExtra("NegrosRow7",getIntent().getExtras().getString("NegrosRow7"));
                    intent.putExtra("RojosRow7",getIntent().getExtras().getString("RojosRow7"));
                    intent.putExtra("EstadoCultivoRow7",getIntent().getExtras().getString("EstadoCultivoRow7"));
                    intent.putExtra("ObservacionesRow7",getIntent().getExtras().getString("ObservacionesRow7"));

                    intent.putExtra("DiaRow8",getIntent().getExtras().getString("DiaRow8"));
                    intent.putExtra("CodigoTrampaRow8",getIntent().getExtras().getString("CodigoTrampaRow8"));
                    intent.putExtra("MunicipioRow8",getIntent().getExtras().getString("MunicipioRow8"));
                    intent.putExtra("VeredaRow8",getIntent().getExtras().getString("VeredaRow8"));
                    intent.putExtra("PredioRow8",getIntent().getExtras().getString("PredioRow8"));
                    intent.putExtra("NegrosRow8",getIntent().getExtras().getString("NegrosRow8"));
                    intent.putExtra("RojosRow8",getIntent().getExtras().getString("RojosRow8"));
                    intent.putExtra("EstadoCultivoRow8",getIntent().getExtras().getString("EstadoCultivoRow8"));
                    intent.putExtra("ObservacionesRow8",getIntent().getExtras().getString("ObservacionesRow8"));

                    intent.putExtra("DiaRow9",getIntent().getExtras().getString("DiaRow9"));
                    intent.putExtra("CodigoTrampaRow9",getIntent().getExtras().getString("CodigoTrampaRow9"));
                    intent.putExtra("MunicipioRow9",getIntent().getExtras().getString("MunicipioRow9"));
                    intent.putExtra("VeredaRow9",getIntent().getExtras().getString("VeredaRow9"));
                    intent.putExtra("PredioRow9",getIntent().getExtras().getString("PredioRow9"));
                    intent.putExtra("NegrosRow9",getIntent().getExtras().getString("NegrosRow9"));
                    intent.putExtra("RojosRow9",getIntent().getExtras().getString("RojosRow9"));
                    intent.putExtra("EstadoCultivoRow9",getIntent().getExtras().getString("EstadoCultivoRow9"));
                    intent.putExtra("ObservacionesRow9",getIntent().getExtras().getString("ObservacionesRow9"));

                    intent.putExtra("DiaRow10", Dia.getText() + "");
                    intent.putExtra("CodigoTrampaRow10", CodigoTrampa.getText() + "");
                    intent.putExtra("MunicipioRow10", Municipio.getText() + "");
                    intent.putExtra("VeredaRow10", Vereda.getText() + "");
                    intent.putExtra("PredioRow10", Predio.getText() + "");
                    intent.putExtra("NegrosRow10", Negros.getText() + "");
                    intent.putExtra("RojosRow10", Rojos.getText() + "");
                    intent.putExtra("EstadoCultivoRow10", EstadoCultivo.getText() + "");
                    intent.putExtra("ObservacionesRow10", Observaciones.getText() + "");
                }
            }
        Log.e("añadir formulario",añadir+"");
        intent.putExtra("page", añadir);
        startActivity(intent);
    }

    public void llenarpdf(String destino, String ruta, String ruta2){
        try {
            PdfReader pdfReader = new PdfReader(getResources().openRawResource(R.raw.forma3007));
            PdfStamper stamper = new PdfStamper(pdfReader, new FileOutputStream(destino + "/forma3007.pdf"));
            InputStream ims = getResources().openRawResource(R.raw.forma3007);
            AcroFields acroFields = stamper.getAcroFields();
            Log.e("Picudo", acroFields.getFields() + "");
            /**
             * llenar el pdf
             */
            acroFields.setField("Funcionario  que realiza la lectura",FuncionarioLectura.getText()+"");
            acroFields.setField("Año",Year.getText()+"");
            acroFields.setField("Se realizó cambio de feromona", CambioFeromona.getText()+"");
            acroFields.setField("Número de lote nueva feromona",NumeroFeromona.getText()+"");
            acroFields.setField("Mes",Mes.getText()+"");

            if (añadir==1) {
                acroFields.setField("DíaRow1", Dia.getText() + "");
                acroFields.setField("Código de trampaRow1", CodigoTrampa.getText() + "");
                acroFields.setField("MunicipioRow1", Municipio.getText() + "");
                acroFields.setField("VeredaRow1", Vereda.getText() + "");
                acroFields.setField("PredioRow1", Predio.getText() + "");
                acroFields.setField("NegrosRow1", Negros.getText() + "");
                acroFields.setField("RojosRow1", Rojos.getText() + "");
                acroFields.setField("Estado cultivoRow1", EstadoCultivo.getText() + "");
                acroFields.setField("ObservacionesRow1", Observaciones.getText() + "");
            }

            añadir = getIntent().getExtras().getInt("añadir");
            if (añadir==2){
                Log.e("añadir","entro a añadir 2");
                acroFields.setField("DíaRow1", getIntent().getExtras().getString("DiaRow1"));
                acroFields.setField("Código de trampaRow1", getIntent().getExtras().getString("CodigoTrampaRow1"));
                acroFields.setField("MunicipioRow1", getIntent().getExtras().getString("MunicipioRow1"));
                acroFields.setField("VeredaRow1", getIntent().getExtras().getString("VeredaRow1"));
                acroFields.setField("PredioRow1",  getIntent().getExtras().getString("PredioRow1"));
                acroFields.setField("NegrosRow1",  getIntent().getExtras().getString("NegrosRow1"));
                acroFields.setField("RojosRow1",  getIntent().getExtras().getString("RojosRow1"));
                acroFields.setField("Estado cultivoRow1",  getIntent().getExtras().getString("EstadoCultivoRow1"));
                acroFields.setField("ObservacionesRow1",  getIntent().getExtras().getString("ObservacionesRow1"));

                acroFields.setField("DíaRow2", Dia.getText() + "");
                acroFields.setField("Código de trampaRow2", CodigoTrampa.getText() + "");
                acroFields.setField("MunicipioRow2", Municipio.getText() + "");
                acroFields.setField("VeredaRow2", Vereda.getText() + "");
                acroFields.setField("PredioRow2", Predio.getText() + "");
                acroFields.setField("NegrosRow2", Negros.getText() + "");
                acroFields.setField("RojosRow2", Rojos.getText() + "");
                acroFields.setField("Estado cultivoRow2", EstadoCultivo.getText() + "");
                acroFields.setField("ObservacionesRow2", Observaciones.getText() + "");
            }
            if (añadir==3){
                acroFields.setField("DíaRow1",getIntent().getExtras().getString("DiaRow1"));
                acroFields.setField("Código de trampaRow1",getIntent().getExtras().getString("CodigoTrampaRow1"));
                acroFields.setField("MunicipioRow1",getIntent().getExtras().getString("MunicipioRow1"));
                acroFields.setField("VeredaRow1",getIntent().getExtras().getString("VeredaRow1"));
                acroFields.setField("PredioRow1",getIntent().getExtras().getString("PredioRow1"));
                acroFields.setField("NegrosRow1",getIntent().getExtras().getString("NegrosRow1"));
                acroFields.setField("RojosRow1",getIntent().getExtras().getString("RojosRow1"));
                acroFields.setField("Estado cultivoRow1",getIntent().getExtras().getString("EstadoCultivoRow1"));
                acroFields.setField("ObservacionesRow1",getIntent().getExtras().getString("ObservacionesRow1"));

                acroFields.setField("DíaRow2",getIntent().getExtras().getString("DiaRow2"));
                acroFields.setField("Código de trampaRow2",getIntent().getExtras().getString("CodigoTrampaRow2"));
                acroFields.setField("MunicipioRow2",getIntent().getExtras().getString("MunicipioRow2"));
                acroFields.setField("VeredaRow2",getIntent().getExtras().getString("VeredaRow2"));
                acroFields.setField("PredioRow2",getIntent().getExtras().getString("PredioRow2"));
                acroFields.setField("NegrosRow2",getIntent().getExtras().getString("NegrosRow2"));
                acroFields.setField("RojosRow2",getIntent().getExtras().getString("RojosRow2"));
                acroFields.setField("Estado cultivoRow2",getIntent().getExtras().getString("EstadoCultivoRow2"));
                acroFields.setField("ObservacionesRow2",getIntent().getExtras().getString("ObservacionesRow2"));

                acroFields.setField("DíaRow3", Dia.getText() + "");
                acroFields.setField("Código de trampaRow3", CodigoTrampa.getText() + "");
                acroFields.setField("MunicipioRow3", Municipio.getText() + "");
                acroFields.setField("VeredaRow3", Vereda.getText() + "");
                acroFields.setField("PredioRow3", Predio.getText() + "");
                acroFields.setField("NegrosRow3", Negros.getText() + "");
                acroFields.setField("RojosRow3", Rojos.getText() + "");
                acroFields.setField("Estado cultivoRow3", EstadoCultivo.getText() + "");
                acroFields.setField("ObservacionesRow3", Observaciones.getText() + "");
            }
            if (añadir ==4){
                acroFields.setField("DíaRow1",getIntent().getExtras().getString("DiaRow1"));
                acroFields.setField("Código de trampaRow1",getIntent().getExtras().getString("CodigoTrampaRow1"));
                acroFields.setField("MunicipioRow1",getIntent().getExtras().getString("MunicipioRow1"));
                acroFields.setField("VeredaRow1",getIntent().getExtras().getString("VeredaRow1"));
                acroFields.setField("PredioRow1",getIntent().getExtras().getString("PredioRow1"));
                acroFields.setField("NegrosRow1",getIntent().getExtras().getString("NegrosRow1"));
                acroFields.setField("RojosRow1",getIntent().getExtras().getString("RojosRow1"));
                acroFields.setField("Estado cultivoRow1",getIntent().getExtras().getString("EstadoCultivoRow1"));
                acroFields.setField("ObservacionesRow1",getIntent().getExtras().getString("ObservacionesRow1"));

                acroFields.setField("DíaRow2",getIntent().getExtras().getString("DiaRow2"));
                acroFields.setField("Código de trampaRow2",getIntent().getExtras().getString("CodigoTrampaRow2"));
                acroFields.setField("MunicipioRow2",getIntent().getExtras().getString("MunicipioRow2"));
                acroFields.setField("VeredaRow2",getIntent().getExtras().getString("VeredaRow2"));
                acroFields.setField("PredioRow2",getIntent().getExtras().getString("PredioRow2"));
                acroFields.setField("NegrosRow2",getIntent().getExtras().getString("NegrosRow2"));
                acroFields.setField("RojosRow2",getIntent().getExtras().getString("RojosRow2"));
                acroFields.setField("Estado cultivoRow2",getIntent().getExtras().getString("EstadoCultivoRow2"));
                acroFields.setField("ObservacionesRow2",getIntent().getExtras().getString("ObservacionesRow2"));

                acroFields.setField("DíaRow3",getIntent().getExtras().getString("DiaRow3"));
                acroFields.setField("Código de trampaRow3",getIntent().getExtras().getString("CodigoTrampaRow3"));
                acroFields.setField("MunicipioRow3",getIntent().getExtras().getString("MunicipioRow3"));
                acroFields.setField("VeredaRow3",getIntent().getExtras().getString("VeredaRow3"));
                acroFields.setField("PredioRow3",getIntent().getExtras().getString("PredioRow3"));
                acroFields.setField("NegrosRow3",getIntent().getExtras().getString("NegrosRow3"));
                acroFields.setField("RojosRow3",getIntent().getExtras().getString("RojosRow3"));
                acroFields.setField("Estado cultivoRow3",getIntent().getExtras().getString("EstadoCultivoRow3"));
                acroFields.setField("ObservacionesRow3",getIntent().getExtras().getString("ObservacionesRow3"));

                acroFields.setField("DíaRow4", Dia.getText() + "");
                acroFields.setField("Código de trampaRow4", CodigoTrampa.getText() + "");
                acroFields.setField("MunicipioRow4", Municipio.getText() + "");
                acroFields.setField("VeredaRow4", Vereda.getText() + "");
                acroFields.setField("PredioRow4", Predio.getText() + "");
                acroFields.setField("NegrosRow4", Negros.getText() + "");
                acroFields.setField("RojosRow4", Rojos.getText() + "");
                acroFields.setField("Estado cultivoRow4", EstadoCultivo.getText() + "");
                acroFields.setField("ObservacionesRow4", Observaciones.getText() + "");
            }
            if (añadir==5){
                acroFields.setField("DíaRow1",getIntent().getExtras().getString("DiaRow1"));
                acroFields.setField("Código de trampaRow1",getIntent().getExtras().getString("CodigoTrampaRow1"));
                acroFields.setField("MunicipioRow1",getIntent().getExtras().getString("MunicipioRow1"));
                acroFields.setField("VeredaRow1",getIntent().getExtras().getString("VeredaRow1"));
                acroFields.setField("PredioRow1",getIntent().getExtras().getString("PredioRow1"));
                acroFields.setField("NegrosRow1",getIntent().getExtras().getString("NegrosRow1"));
                acroFields.setField("RojosRow1",getIntent().getExtras().getString("RojosRow1"));
                acroFields.setField("Estado cultivoRow1",getIntent().getExtras().getString("EstadoCultivoRow1"));
                acroFields.setField("ObservacionesRow1",getIntent().getExtras().getString("ObservacionesRow1"));

                acroFields.setField("DíaRow2",getIntent().getExtras().getString("DiaRow2"));
                acroFields.setField("Código de trampaRow2",getIntent().getExtras().getString("CodigoTrampaRow2"));
                acroFields.setField("MunicipioRow2",getIntent().getExtras().getString("MunicipioRow2"));
                acroFields.setField("VeredaRow2",getIntent().getExtras().getString("VeredaRow2"));
                acroFields.setField("PredioRow2",getIntent().getExtras().getString("PredioRow2"));
                acroFields.setField("NegrosRow2",getIntent().getExtras().getString("NegrosRow2"));
                acroFields.setField("RojosRow2",getIntent().getExtras().getString("RojosRow2"));
                acroFields.setField("Estado cultivoRow2",getIntent().getExtras().getString("EstadoCultivoRow2"));
                acroFields.setField("ObservacionesRow2",getIntent().getExtras().getString("ObservacionesRow2"));

                acroFields.setField("DíaRow3",getIntent().getExtras().getString("DiaRow3"));
                acroFields.setField("Código de trampaRow3",getIntent().getExtras().getString("CodigoTrampaRow3"));
                acroFields.setField("MunicipioRow3",getIntent().getExtras().getString("MunicipioRow3"));
                acroFields.setField("VeredaRow3",getIntent().getExtras().getString("VeredaRow3"));
                acroFields.setField("PredioRow3",getIntent().getExtras().getString("PredioRow3"));
                acroFields.setField("NegrosRow3",getIntent().getExtras().getString("NegrosRow3"));
                acroFields.setField("RojosRow3",getIntent().getExtras().getString("RojosRow3"));
                acroFields.setField("Estado cultivoRow3",getIntent().getExtras().getString("EstadoCultivoRow3"));
                acroFields.setField("ObservacionesRow3",getIntent().getExtras().getString("ObservacionesRow3"));

                acroFields.setField("DíaRow4",getIntent().getExtras().getString("DiaRow4"));
                acroFields.setField("Código de trampaRow4",getIntent().getExtras().getString("CodigoTrampaRow4"));
                acroFields.setField("MunicipioRow4",getIntent().getExtras().getString("MunicipioRow4"));
                acroFields.setField("VeredaRow4",getIntent().getExtras().getString("VeredaRow4"));
                acroFields.setField("PredioRow4",getIntent().getExtras().getString("PredioRow4"));
                acroFields.setField("NegrosRow4",getIntent().getExtras().getString("NegrosRow4"));
                acroFields.setField("RojosRow4",getIntent().getExtras().getString("RojosRow4"));
                acroFields.setField("Estado cultivoRow4",getIntent().getExtras().getString("EstadoCultivoRow4"));
                acroFields.setField("ObservacionesRow4",getIntent().getExtras().getString("ObservacionesRow4"));

                acroFields.setField("DíaRow5", Dia.getText() + "");
                acroFields.setField("Código de trampaRow5", CodigoTrampa.getText() + "");
                acroFields.setField("MunicipioRow5", Municipio.getText() + "");
                acroFields.setField("VeredaRow5", Vereda.getText() + "");
                acroFields.setField("PredioRow5", Predio.getText() + "");
                acroFields.setField("NegrosRow5", Negros.getText() + "");
                acroFields.setField("RojosRow5", Rojos.getText() + "");
                acroFields.setField("Estado cultivoRow5", EstadoCultivo.getText() + "");
                acroFields.setField("ObservacionesRow5", Observaciones.getText() + "");
            }
            if (añadir==6){
                acroFields.setField("DíaRow1",getIntent().getExtras().getString("DiaRow1"));
                acroFields.setField("Código de trampaRow1",getIntent().getExtras().getString("CodigoTrampaRow1"));
                acroFields.setField("MunicipioRow1",getIntent().getExtras().getString("MunicipioRow1"));
                acroFields.setField("VeredaRow1",getIntent().getExtras().getString("VeredaRow1"));
                acroFields.setField("PredioRow1",getIntent().getExtras().getString("PredioRow1"));
                acroFields.setField("NegrosRow1",getIntent().getExtras().getString("NegrosRow1"));
                acroFields.setField("RojosRow1",getIntent().getExtras().getString("RojosRow1"));
                acroFields.setField("Estado cultivoRow1",getIntent().getExtras().getString("EstadoCultivoRow1"));
                acroFields.setField("ObservacionesRow1",getIntent().getExtras().getString("ObservacionesRow1"));

                acroFields.setField("DíaRow2",getIntent().getExtras().getString("DiaRow2"));
                acroFields.setField("Código de trampaRow2",getIntent().getExtras().getString("CodigoTrampaRow2"));
                acroFields.setField("MunicipioRow2",getIntent().getExtras().getString("MunicipioRow2"));
                acroFields.setField("VeredaRow2",getIntent().getExtras().getString("VeredaRow2"));
                acroFields.setField("PredioRow2",getIntent().getExtras().getString("PredioRow2"));
                acroFields.setField("NegrosRow2",getIntent().getExtras().getString("NegrosRow2"));
                acroFields.setField("RojosRow2",getIntent().getExtras().getString("RojosRow2"));
                acroFields.setField("Estado cultivoRow2",getIntent().getExtras().getString("EstadoCultivoRow2"));
                acroFields.setField("ObservacionesRow2",getIntent().getExtras().getString("ObservacionesRow2"));

                acroFields.setField("DíaRow3",getIntent().getExtras().getString("DiaRow3"));
                acroFields.setField("Código de trampaRow3",getIntent().getExtras().getString("CodigoTrampaRow3"));
                acroFields.setField("MunicipioRow3",getIntent().getExtras().getString("MunicipioRow3"));
                acroFields.setField("VeredaRow3",getIntent().getExtras().getString("VeredaRow3"));
                acroFields.setField("PredioRow3",getIntent().getExtras().getString("PredioRow3"));
                acroFields.setField("NegrosRow3",getIntent().getExtras().getString("NegrosRow3"));
                acroFields.setField("RojosRow3",getIntent().getExtras().getString("RojosRow3"));
                acroFields.setField("Estado cultivoRow3",getIntent().getExtras().getString("EstadoCultivoRow3"));
                acroFields.setField("ObservacionesRow3",getIntent().getExtras().getString("ObservacionesRow3"));

                acroFields.setField("DíaRow4",getIntent().getExtras().getString("DiaRow4"));
                acroFields.setField("Código de trampaRow4",getIntent().getExtras().getString("CodigoTrampaRow4"));
                acroFields.setField("MunicipioRow4",getIntent().getExtras().getString("MunicipioRow4"));
                acroFields.setField("VeredaRow4",getIntent().getExtras().getString("VeredaRow4"));
                acroFields.setField("PredioRow4",getIntent().getExtras().getString("PredioRow4"));
                acroFields.setField("NegrosRow4",getIntent().getExtras().getString("NegrosRow4"));
                acroFields.setField("RojosRow4",getIntent().getExtras().getString("RojosRow4"));
                acroFields.setField("Estado cultivoRow4",getIntent().getExtras().getString("EstadoCultivoRow4"));
                acroFields.setField("ObservacionesRow4",getIntent().getExtras().getString("ObservacionesRow4"));

                acroFields.setField("DíaRow5",getIntent().getExtras().getString("DiaRow5"));
                acroFields.setField("Código de trampaRow5",getIntent().getExtras().getString("CodigoTrampaRow5"));
                acroFields.setField("MunicipioRow5",getIntent().getExtras().getString("MunicipioRow5"));
                acroFields.setField("VeredaRow5",getIntent().getExtras().getString("VeredaRow5"));
                acroFields.setField("PredioRow5",getIntent().getExtras().getString("PredioRow5"));
                acroFields.setField("NegrosRow5",getIntent().getExtras().getString("NegrosRow5"));
                acroFields.setField("RojosRow5",getIntent().getExtras().getString("RojosRow5"));
                acroFields.setField("Estado cultivoRow5",getIntent().getExtras().getString("EstadoCultivoRow5"));
                acroFields.setField("ObservacionesRow5",getIntent().getExtras().getString("ObservacionesRow5"));

                acroFields.setField("DíaRow6", Dia.getText() + "");
                acroFields.setField("Código de trampaRow6", CodigoTrampa.getText() + "");
                acroFields.setField("MunicipioRow6", Municipio.getText() + "");
                acroFields.setField("VeredaRow6", Vereda.getText() + "");
                acroFields.setField("PredioRow6", Predio.getText() + "");
                acroFields.setField("NegrosRow6", Negros.getText() + "");
                acroFields.setField("RojosRow6", Rojos.getText() + "");
                acroFields.setField("Estado cultivoRow6", EstadoCultivo.getText() + "");
                acroFields.setField("ObservacionesRow6", Observaciones.getText() + "");
            }
            if (añadir==7){
                acroFields.setField("DíaRow1",getIntent().getExtras().getString("DiaRow1"));
                acroFields.setField("Código de trampaRow1",getIntent().getExtras().getString("CodigoTrampaRow1"));
                acroFields.setField("MunicipioRow1",getIntent().getExtras().getString("MunicipioRow1"));
                acroFields.setField("VeredaRow1",getIntent().getExtras().getString("VeredaRow1"));
                acroFields.setField("PredioRow1",getIntent().getExtras().getString("PredioRow1"));
                acroFields.setField("NegrosRow1",getIntent().getExtras().getString("NegrosRow1"));
                acroFields.setField("RojosRow1",getIntent().getExtras().getString("RojosRow1"));
                acroFields.setField("Estado cultivoRow1",getIntent().getExtras().getString("EstadoCultivoRow1"));
                acroFields.setField("ObservacionesRow1",getIntent().getExtras().getString("ObservacionesRow1"));

                acroFields.setField("DíaRow2",getIntent().getExtras().getString("DiaRow2"));
                acroFields.setField("Código de trampaRow2",getIntent().getExtras().getString("CodigoTrampaRow2"));
                acroFields.setField("MunicipioRow2",getIntent().getExtras().getString("MunicipioRow2"));
                acroFields.setField("VeredaRow2",getIntent().getExtras().getString("VeredaRow2"));
                acroFields.setField("PredioRow2",getIntent().getExtras().getString("PredioRow2"));
                acroFields.setField("NegrosRow2",getIntent().getExtras().getString("NegrosRow2"));
                acroFields.setField("RojosRow2",getIntent().getExtras().getString("RojosRow2"));
                acroFields.setField("Estado cultivoRow2",getIntent().getExtras().getString("EstadoCultivoRow2"));
                acroFields.setField("ObservacionesRow2",getIntent().getExtras().getString("ObservacionesRow2"));

                acroFields.setField("DíaRow3",getIntent().getExtras().getString("DiaRow3"));
                acroFields.setField("Código de trampaRow3",getIntent().getExtras().getString("CodigoTrampaRow3"));
                acroFields.setField("MunicipioRow3",getIntent().getExtras().getString("MunicipioRow3"));
                acroFields.setField("VeredaRow3",getIntent().getExtras().getString("VeredaRow3"));
                acroFields.setField("PredioRow3",getIntent().getExtras().getString("PredioRow3"));
                acroFields.setField("NegrosRow3",getIntent().getExtras().getString("NegrosRow3"));
                acroFields.setField("RojosRow3",getIntent().getExtras().getString("RojosRow3"));
                acroFields.setField("Estado cultivoRow3",getIntent().getExtras().getString("EstadoCultivoRow3"));
                acroFields.setField("ObservacionesRow3",getIntent().getExtras().getString("ObservacionesRow3"));

                acroFields.setField("DíaRow4",getIntent().getExtras().getString("DiaRow4"));
                acroFields.setField("Código de trampaRow4",getIntent().getExtras().getString("CodigoTrampaRow4"));
                acroFields.setField("MunicipioRow4",getIntent().getExtras().getString("MunicipioRow4"));
                acroFields.setField("VeredaRow4",getIntent().getExtras().getString("VeredaRow4"));
                acroFields.setField("PredioRow4",getIntent().getExtras().getString("PredioRow4"));
                acroFields.setField("NegrosRow4",getIntent().getExtras().getString("NegrosRow4"));
                acroFields.setField("RojosRow4",getIntent().getExtras().getString("RojosRow4"));
                acroFields.setField("Estado cultivoRow4",getIntent().getExtras().getString("EstadoCultivoRow4"));
                acroFields.setField("ObservacionesRow4",getIntent().getExtras().getString("ObservacionesRow4"));

                acroFields.setField("DíaRow5",getIntent().getExtras().getString("DiaRow5"));
                acroFields.setField("Código de trampaRow5",getIntent().getExtras().getString("CodigoTrampaRow5"));
                acroFields.setField("MunicipioRow5",getIntent().getExtras().getString("MunicipioRow5"));
                acroFields.setField("VeredaRow5",getIntent().getExtras().getString("VeredaRow5"));
                acroFields.setField("PredioRow5",getIntent().getExtras().getString("PredioRow5"));
                acroFields.setField("NegrosRow5",getIntent().getExtras().getString("NegrosRow5"));
                acroFields.setField("RojosRow5",getIntent().getExtras().getString("RojosRow5"));
                acroFields.setField("Estado cultivoRow5",getIntent().getExtras().getString("EstadoCultivoRow5"));
                acroFields.setField("ObservacionesRow5",getIntent().getExtras().getString("ObservacionesRow5"));

                acroFields.setField("DíaRow6",getIntent().getExtras().getString("DiaRow6"));
                acroFields.setField("Código de trampaRow6",getIntent().getExtras().getString("CodigoTrampaRow6"));
                acroFields.setField("MunicipioRow6",getIntent().getExtras().getString("MunicipioRow6"));
                acroFields.setField("VeredaRow6",getIntent().getExtras().getString("VeredaRow6"));
                acroFields.setField("PredioRow6",getIntent().getExtras().getString("PredioRow6"));
                acroFields.setField("NegrosRow6",getIntent().getExtras().getString("NegrosRow6"));
                acroFields.setField("RojosRow6",getIntent().getExtras().getString("RojosRow6"));
                acroFields.setField("Estado cultivoRow6",getIntent().getExtras().getString("EstadoCultivoRow6"));
                acroFields.setField("ObservacionesRow6",getIntent().getExtras().getString("ObservacionesRow6"));


                acroFields.setField("DíaRow7", Dia.getText() + "");
                acroFields.setField("Código de trampaRow7", CodigoTrampa.getText() + "");
                acroFields.setField("MunicipioRow7", Municipio.getText() + "");
                acroFields.setField("VeredaRow7", Vereda.getText() + "");
                acroFields.setField("PredioRow7", Predio.getText() + "");
                acroFields.setField("NegrosRow7", Negros.getText() + "");
                acroFields.setField("RojosRow7", Rojos.getText() + "");
                acroFields.setField("Estado cultivoRow7", EstadoCultivo.getText() + "");
                acroFields.setField("ObservacionesRow7", Observaciones.getText() + "");
            }if (añadir==8){
                acroFields.setField("DíaRow1",getIntent().getExtras().getString("DiaRow1"));
                acroFields.setField("Código de trampaRow1",getIntent().getExtras().getString("CodigoTrampaRow1"));
                acroFields.setField("MunicipioRow1",getIntent().getExtras().getString("MunicipioRow1"));
                acroFields.setField("VeredaRow1",getIntent().getExtras().getString("VeredaRow1"));
                acroFields.setField("PredioRow1",getIntent().getExtras().getString("PredioRow1"));
                acroFields.setField("NegrosRow1",getIntent().getExtras().getString("NegrosRow1"));
                acroFields.setField("RojosRow1",getIntent().getExtras().getString("RojosRow1"));
                acroFields.setField("Estado cultivoRow1",getIntent().getExtras().getString("EstadoCultivoRow1"));
                acroFields.setField("ObservacionesRow1",getIntent().getExtras().getString("ObservacionesRow1"));

                acroFields.setField("DíaRow2",getIntent().getExtras().getString("DiaRow2"));
                acroFields.setField("Código de trampaRow2",getIntent().getExtras().getString("CodigoTrampaRow2"));
                acroFields.setField("MunicipioRow2",getIntent().getExtras().getString("MunicipioRow2"));
                acroFields.setField("VeredaRow2",getIntent().getExtras().getString("VeredaRow2"));
                acroFields.setField("PredioRow2",getIntent().getExtras().getString("PredioRow2"));
                acroFields.setField("NegrosRow2",getIntent().getExtras().getString("NegrosRow2"));
                acroFields.setField("RojosRow2",getIntent().getExtras().getString("RojosRow2"));
                acroFields.setField("Estado cultivoRow2",getIntent().getExtras().getString("EstadoCultivoRow2"));
                acroFields.setField("ObservacionesRow2",getIntent().getExtras().getString("ObservacionesRow2"));

                acroFields.setField("DíaRow3",getIntent().getExtras().getString("DiaRow3"));
                acroFields.setField("Código de trampaRow3",getIntent().getExtras().getString("CodigoTrampaRow3"));
                acroFields.setField("MunicipioRow3",getIntent().getExtras().getString("MunicipioRow3"));
                acroFields.setField("VeredaRow3",getIntent().getExtras().getString("VeredaRow3"));
                acroFields.setField("PredioRow3",getIntent().getExtras().getString("PredioRow3"));
                acroFields.setField("NegrosRow3",getIntent().getExtras().getString("NegrosRow3"));
                acroFields.setField("RojosRow3",getIntent().getExtras().getString("RojosRow3"));
                acroFields.setField("Estado cultivoRow3",getIntent().getExtras().getString("EstadoCultivoRow3"));
                acroFields.setField("ObservacionesRow3",getIntent().getExtras().getString("ObservacionesRow3"));

                acroFields.setField("DíaRow4",getIntent().getExtras().getString("DiaRow4"));
                acroFields.setField("Código de trampaRow4",getIntent().getExtras().getString("CodigoTrampaRow4"));
                acroFields.setField("MunicipioRow4",getIntent().getExtras().getString("MunicipioRow4"));
                acroFields.setField("VeredaRow4",getIntent().getExtras().getString("VeredaRow4"));
                acroFields.setField("PredioRow4",getIntent().getExtras().getString("PredioRow4"));
                acroFields.setField("NegrosRow4",getIntent().getExtras().getString("NegrosRow4"));
                acroFields.setField("RojosRow4",getIntent().getExtras().getString("RojosRow4"));
                acroFields.setField("Estado cultivoRow4",getIntent().getExtras().getString("EstadoCultivoRow4"));
                acroFields.setField("ObservacionesRow4",getIntent().getExtras().getString("ObservacionesRow4"));

                acroFields.setField("DíaRow5",getIntent().getExtras().getString("DiaRow5"));
                acroFields.setField("Código de trampaRow5",getIntent().getExtras().getString("CodigoTrampaRow5"));
                acroFields.setField("MunicipioRow5",getIntent().getExtras().getString("MunicipioRow5"));
                acroFields.setField("VeredaRow5",getIntent().getExtras().getString("VeredaRow5"));
                acroFields.setField("PredioRow5",getIntent().getExtras().getString("PredioRow5"));
                acroFields.setField("NegrosRow5",getIntent().getExtras().getString("NegrosRow5"));
                acroFields.setField("RojosRow5",getIntent().getExtras().getString("RojosRow5"));
                acroFields.setField("Estado cultivoRow5",getIntent().getExtras().getString("EstadoCultivoRow5"));
                acroFields.setField("ObservacionesRow5",getIntent().getExtras().getString("ObservacionesRow5"));

                acroFields.setField("DíaRow6",getIntent().getExtras().getString("DiaRow6"));
                acroFields.setField("Código de trampaRow6",getIntent().getExtras().getString("CodigoTrampaRow6"));
                acroFields.setField("MunicipioRow6",getIntent().getExtras().getString("MunicipioRow6"));
                acroFields.setField("VeredaRow6",getIntent().getExtras().getString("VeredaRow6"));
                acroFields.setField("PredioRow6",getIntent().getExtras().getString("PredioRow6"));
                acroFields.setField("NegrosRow6",getIntent().getExtras().getString("NegrosRow6"));
                acroFields.setField("RojosRow6",getIntent().getExtras().getString("RojosRow6"));
                acroFields.setField("Estado cultivoRow6",getIntent().getExtras().getString("EstadoCultivoRow6"));
                acroFields.setField("ObservacionesRow6",getIntent().getExtras().getString("ObservacionesRow6"));

                acroFields.setField("DíaRow7",getIntent().getExtras().getString("DiaRow7"));
                acroFields.setField("Código de trampaRow7",getIntent().getExtras().getString("CodigoTrampaRow7"));
                acroFields.setField("MunicipioRow7",getIntent().getExtras().getString("MunicipioRow7"));
                acroFields.setField("VeredaRow7",getIntent().getExtras().getString("VeredaRow7"));
                acroFields.setField("PredioRow7",getIntent().getExtras().getString("PredioRow7"));
                acroFields.setField("NegrosRow7",getIntent().getExtras().getString("NegrosRow7"));
                acroFields.setField("RojosRow7",getIntent().getExtras().getString("RojosRow7"));
                acroFields.setField("Estado cultivoRow7",getIntent().getExtras().getString("EstadoCultivoRow7"));
                acroFields.setField("ObservacionesRow7",getIntent().getExtras().getString("ObservacionesRow7"));

                acroFields.setField("DíaRow8", Dia.getText() + "");
                acroFields.setField("Código de trampaRow8", CodigoTrampa.getText() + "");
                acroFields.setField("MunicipioRow8", Municipio.getText() + "");
                acroFields.setField("VeredaRow8", Vereda.getText() + "");
                acroFields.setField("PredioRow8", Predio.getText() + "");
                acroFields.setField("NegrosRow8", Negros.getText() + "");
                acroFields.setField("RojosRow8", Rojos.getText() + "");
                acroFields.setField("Estado cultivoRow8", EstadoCultivo.getText() + "");
                acroFields.setField("ObservacionesRow8", Observaciones.getText() + "");
            }
            if (añadir==9){
                acroFields.setField("DíaRow1",getIntent().getExtras().getString("DiaRow1"));
                acroFields.setField("Código de trampaRow1",getIntent().getExtras().getString("CodigoTrampaRow1"));
                acroFields.setField("MunicipioRow1",getIntent().getExtras().getString("MunicipioRow1"));
                acroFields.setField("VeredaRow1",getIntent().getExtras().getString("VeredaRow1"));
                acroFields.setField("PredioRow1",getIntent().getExtras().getString("PredioRow1"));
                acroFields.setField("NegrosRow1",getIntent().getExtras().getString("NegrosRow1"));
                acroFields.setField("RojosRow1",getIntent().getExtras().getString("RojosRow1"));
                acroFields.setField("Estado cultivoRow1",getIntent().getExtras().getString("EstadoCultivoRow1"));
                acroFields.setField("ObservacionesRow1",getIntent().getExtras().getString("ObservacionesRow1"));

                acroFields.setField("DíaRow2",getIntent().getExtras().getString("DiaRow2"));
                acroFields.setField("Código de trampaRow2",getIntent().getExtras().getString("CodigoTrampaRow2"));
                acroFields.setField("MunicipioRow2",getIntent().getExtras().getString("MunicipioRow2"));
                acroFields.setField("VeredaRow2",getIntent().getExtras().getString("VeredaRow2"));
                acroFields.setField("PredioRow2",getIntent().getExtras().getString("PredioRow2"));
                acroFields.setField("NegrosRow2",getIntent().getExtras().getString("NegrosRow2"));
                acroFields.setField("RojosRow2",getIntent().getExtras().getString("RojosRow2"));
                acroFields.setField("Estado cultivoRow2",getIntent().getExtras().getString("EstadoCultivoRow2"));
                acroFields.setField("ObservacionesRow2",getIntent().getExtras().getString("ObservacionesRow2"));

                acroFields.setField("DíaRow3",getIntent().getExtras().getString("DiaRow3"));
                acroFields.setField("Código de trampaRow3",getIntent().getExtras().getString("CodigoTrampaRow3"));
                acroFields.setField("MunicipioRow3",getIntent().getExtras().getString("MunicipioRow3"));
                acroFields.setField("VeredaRow3",getIntent().getExtras().getString("VeredaRow3"));
                acroFields.setField("PredioRow3",getIntent().getExtras().getString("PredioRow3"));
                acroFields.setField("NegrosRow3",getIntent().getExtras().getString("NegrosRow3"));
                acroFields.setField("RojosRow3",getIntent().getExtras().getString("RojosRow3"));
                acroFields.setField("Estado cultivoRow3",getIntent().getExtras().getString("EstadoCultivoRow3"));
                acroFields.setField("ObservacionesRow3",getIntent().getExtras().getString("ObservacionesRow3"));

                acroFields.setField("DíaRow4",getIntent().getExtras().getString("DiaRow4"));
                acroFields.setField("Código de trampaRow4",getIntent().getExtras().getString("CodigoTrampaRow4"));
                acroFields.setField("MunicipioRow4",getIntent().getExtras().getString("MunicipioRow4"));
                acroFields.setField("VeredaRow4",getIntent().getExtras().getString("VeredaRow4"));
                acroFields.setField("PredioRow4",getIntent().getExtras().getString("PredioRow4"));
                acroFields.setField("NegrosRow4",getIntent().getExtras().getString("NegrosRow4"));
                acroFields.setField("RojosRow4",getIntent().getExtras().getString("RojosRow4"));
                acroFields.setField("Estado cultivoRow4",getIntent().getExtras().getString("EstadoCultivoRow4"));
                acroFields.setField("ObservacionesRow4",getIntent().getExtras().getString("ObservacionesRow4"));

                acroFields.setField("DíaRow5",getIntent().getExtras().getString("DiaRow5"));
                acroFields.setField("Código de trampaRow5",getIntent().getExtras().getString("CodigoTrampaRow5"));
                acroFields.setField("MunicipioRow5",getIntent().getExtras().getString("MunicipioRow5"));
                acroFields.setField("VeredaRow5",getIntent().getExtras().getString("VeredaRow5"));
                acroFields.setField("PredioRow5",getIntent().getExtras().getString("PredioRow5"));
                acroFields.setField("NegrosRow5",getIntent().getExtras().getString("NegrosRow5"));
                acroFields.setField("RojosRow5",getIntent().getExtras().getString("RojosRow5"));
                acroFields.setField("Estado cultivoRow5",getIntent().getExtras().getString("EstadoCultivoRow5"));
                acroFields.setField("ObservacionesRow5",getIntent().getExtras().getString("ObservacionesRow5"));

                acroFields.setField("DíaRow6",getIntent().getExtras().getString("DiaRow6"));
                acroFields.setField("Código de trampaRow6",getIntent().getExtras().getString("CodigoTrampaRow6"));
                acroFields.setField("MunicipioRow6",getIntent().getExtras().getString("MunicipioRow6"));
                acroFields.setField("VeredaRow6",getIntent().getExtras().getString("VeredaRow6"));
                acroFields.setField("PredioRow6",getIntent().getExtras().getString("PredioRow6"));
                acroFields.setField("NegrosRow6",getIntent().getExtras().getString("NegrosRow6"));
                acroFields.setField("RojosRow6",getIntent().getExtras().getString("RojosRow6"));
                acroFields.setField("Estado cultivoRow6",getIntent().getExtras().getString("EstadoCultivoRow6"));
                acroFields.setField("ObservacionesRow6",getIntent().getExtras().getString("ObservacionesRow6"));

                acroFields.setField("DíaRow7",getIntent().getExtras().getString("DiaRow7"));
                acroFields.setField("Código de trampaRow7",getIntent().getExtras().getString("CodigoTrampaRow7"));
                acroFields.setField("MunicipioRow7",getIntent().getExtras().getString("MunicipioRow7"));
                acroFields.setField("VeredaRow7",getIntent().getExtras().getString("VeredaRow7"));
                acroFields.setField("PredioRow7",getIntent().getExtras().getString("PredioRow7"));
                acroFields.setField("NegrosRow7",getIntent().getExtras().getString("NegrosRow7"));
                acroFields.setField("RojosRow7",getIntent().getExtras().getString("RojosRow7"));
                acroFields.setField("Estado cultivoRow7",getIntent().getExtras().getString("EstadoCultivoRow7"));
                acroFields.setField("ObservacionesRow7",getIntent().getExtras().getString("ObservacionesRow7"));

                acroFields.setField("DíaRow8",getIntent().getExtras().getString("DiaRow8"));
                acroFields.setField("Código de trampaRow8",getIntent().getExtras().getString("CodigoTrampaRow8"));
                acroFields.setField("MunicipioRow8",getIntent().getExtras().getString("MunicipioRow8"));
                acroFields.setField("VeredaRow8",getIntent().getExtras().getString("VeredaRow8"));
                acroFields.setField("PredioRow8",getIntent().getExtras().getString("PredioRow8"));
                acroFields.setField("NegrosRow8",getIntent().getExtras().getString("NegrosRow8"));
                acroFields.setField("RojosRow8",getIntent().getExtras().getString("RojosRow8"));
                acroFields.setField("Estado cultivoRow8",getIntent().getExtras().getString("EstadoCultivoRow8"));
                acroFields.setField("ObservacionesRow8",getIntent().getExtras().getString("ObservacionesRow8"));

                acroFields.setField("DíaRow9", Dia.getText() + "");
                acroFields.setField("Código de trampaRow9", CodigoTrampa.getText() + "");
                acroFields.setField("MunicipioRow9", Municipio.getText() + "");
                acroFields.setField("VeredaRow9", Vereda.getText() + "");
                acroFields.setField("PredioRow9", Predio.getText() + "");
                acroFields.setField("NegrosRow9", Negros.getText() + "");
                acroFields.setField("RojosRow9", Rojos.getText() + "");
                acroFields.setField("Estado cultivoRow9", EstadoCultivo.getText() + "");
                acroFields.setField("ObservacionesRow9", Observaciones.getText() + "");
            }
            if (añadir==10){
                acroFields.setField("DíaRow1",getIntent().getExtras().getString("DiaRow1"));
                acroFields.setField("Código de trampaRow1",getIntent().getExtras().getString("CodigoTrampaRow1"));
                acroFields.setField("MunicipioRow1",getIntent().getExtras().getString("MunicipioRow1"));
                acroFields.setField("VeredaRow1",getIntent().getExtras().getString("VeredaRow1"));
                acroFields.setField("PredioRow1",getIntent().getExtras().getString("PredioRow1"));
                acroFields.setField("NegrosRow1",getIntent().getExtras().getString("NegrosRow1"));
                acroFields.setField("RojosRow1",getIntent().getExtras().getString("RojosRow1"));
                acroFields.setField("Estado cultivoRow1",getIntent().getExtras().getString("EstadoCultivoRow1"));
                acroFields.setField("ObservacionesRow1",getIntent().getExtras().getString("ObservacionesRow1"));

                acroFields.setField("DíaRow2",getIntent().getExtras().getString("DiaRow2"));
                acroFields.setField("Código de trampaRow2",getIntent().getExtras().getString("CodigoTrampaRow2"));
                acroFields.setField("MunicipioRow2",getIntent().getExtras().getString("MunicipioRow2"));
                acroFields.setField("VeredaRow2",getIntent().getExtras().getString("VeredaRow2"));
                acroFields.setField("PredioRow2",getIntent().getExtras().getString("PredioRow2"));
                acroFields.setField("NegrosRow2",getIntent().getExtras().getString("NegrosRow2"));
                acroFields.setField("RojosRow2",getIntent().getExtras().getString("RojosRow2"));
                acroFields.setField("Estado cultivoRow2",getIntent().getExtras().getString("EstadoCultivoRow2"));
                acroFields.setField("ObservacionesRow2",getIntent().getExtras().getString("ObservacionesRow2"));

                acroFields.setField("DíaRow3",getIntent().getExtras().getString("DiaRow3"));
                acroFields.setField("Código de trampaRow3",getIntent().getExtras().getString("CodigoTrampaRow3"));
                acroFields.setField("MunicipioRow3",getIntent().getExtras().getString("MunicipioRow3"));
                acroFields.setField("VeredaRow3",getIntent().getExtras().getString("VeredaRow3"));
                acroFields.setField("PredioRow3",getIntent().getExtras().getString("PredioRow3"));
                acroFields.setField("NegrosRow3",getIntent().getExtras().getString("NegrosRow3"));
                acroFields.setField("RojosRow3",getIntent().getExtras().getString("RojosRow3"));
                acroFields.setField("Estado cultivoRow3",getIntent().getExtras().getString("EstadoCultivoRow3"));
                acroFields.setField("ObservacionesRow3",getIntent().getExtras().getString("ObservacionesRow3"));

                acroFields.setField("DíaRow4",getIntent().getExtras().getString("DiaRow4"));
                acroFields.setField("Código de trampaRow4",getIntent().getExtras().getString("CodigoTrampaRow4"));
                acroFields.setField("MunicipioRow4",getIntent().getExtras().getString("MunicipioRow4"));
                acroFields.setField("VeredaRow4",getIntent().getExtras().getString("VeredaRow4"));
                acroFields.setField("PredioRow4",getIntent().getExtras().getString("PredioRow4"));
                acroFields.setField("NegrosRow4",getIntent().getExtras().getString("NegrosRow4"));
                acroFields.setField("RojosRow4",getIntent().getExtras().getString("RojosRow4"));
                acroFields.setField("Estado cultivoRow4",getIntent().getExtras().getString("EstadoCultivoRow4"));
                acroFields.setField("ObservacionesRow4",getIntent().getExtras().getString("ObservacionesRow4"));

                acroFields.setField("DíaRow5",getIntent().getExtras().getString("DiaRow5"));
                acroFields.setField("Código de trampaRow5",getIntent().getExtras().getString("CodigoTrampaRow5"));
                acroFields.setField("MunicipioRow5",getIntent().getExtras().getString("MunicipioRow5"));
                acroFields.setField("VeredaRow5",getIntent().getExtras().getString("VeredaRow5"));
                acroFields.setField("PredioRow5",getIntent().getExtras().getString("PredioRow5"));
                acroFields.setField("NegrosRow5",getIntent().getExtras().getString("NegrosRow5"));
                acroFields.setField("RojosRow5",getIntent().getExtras().getString("RojosRow5"));
                acroFields.setField("Estado cultivoRow5",getIntent().getExtras().getString("EstadoCultivoRow5"));
                acroFields.setField("ObservacionesRow5",getIntent().getExtras().getString("ObservacionesRow5"));

                acroFields.setField("DíaRow6",getIntent().getExtras().getString("DiaRow6"));
                acroFields.setField("Código de trampaRow6",getIntent().getExtras().getString("CodigoTrampaRow6"));
                acroFields.setField("MunicipioRow6",getIntent().getExtras().getString("MunicipioRow6"));
                acroFields.setField("VeredaRow6",getIntent().getExtras().getString("VeredaRow6"));
                acroFields.setField("PredioRow6",getIntent().getExtras().getString("PredioRow6"));
                acroFields.setField("NegrosRow6",getIntent().getExtras().getString("NegrosRow6"));
                acroFields.setField("RojosRow6",getIntent().getExtras().getString("RojosRow6"));
                acroFields.setField("Estado cultivoRow6",getIntent().getExtras().getString("EstadoCultivoRow6"));
                acroFields.setField("ObservacionesRow6",getIntent().getExtras().getString("ObservacionesRow6"));

                acroFields.setField("DíaRow7",getIntent().getExtras().getString("DiaRow7"));
                acroFields.setField("Código de trampaRow7",getIntent().getExtras().getString("CodigoTrampaRow7"));
                acroFields.setField("MunicipioRow7",getIntent().getExtras().getString("MunicipioRow7"));
                acroFields.setField("VeredaRow7",getIntent().getExtras().getString("VeredaRow7"));
                acroFields.setField("PredioRow7",getIntent().getExtras().getString("PredioRow7"));
                acroFields.setField("NegrosRow7",getIntent().getExtras().getString("NegrosRow7"));
                acroFields.setField("RojosRow7",getIntent().getExtras().getString("RojosRow7"));
                acroFields.setField("Estado cultivoRow7",getIntent().getExtras().getString("EstadoCultivoRow7"));
                acroFields.setField("ObservacionesRow7",getIntent().getExtras().getString("ObservacionesRow7"));

                acroFields.setField("DíaRow8",getIntent().getExtras().getString("DiaRow8"));
                acroFields.setField("Código de trampaRow8",getIntent().getExtras().getString("CodigoTrampaRow8"));
                acroFields.setField("MunicipioRow8",getIntent().getExtras().getString("MunicipioRow8"));
                acroFields.setField("VeredaRow8",getIntent().getExtras().getString("VeredaRow8"));
                acroFields.setField("PredioRow8",getIntent().getExtras().getString("PredioRow8"));
                acroFields.setField("NegrosRow8",getIntent().getExtras().getString("NegrosRow8"));
                acroFields.setField("RojosRow8",getIntent().getExtras().getString("RojosRow8"));
                acroFields.setField("Estado cultivoRow8",getIntent().getExtras().getString("EstadoCultivoRow8"));
                acroFields.setField("ObservacionesRow8",getIntent().getExtras().getString("ObservacionesRow8"));

                acroFields.setField("DíaRow9",getIntent().getExtras().getString("DiaRow9"));
                acroFields.setField("Código de trampaRow9",getIntent().getExtras().getString("CodigoTrampaRow9"));
                acroFields.setField("MunicipioRow9",getIntent().getExtras().getString("MunicipioRow9"));
                acroFields.setField("VeredaRow9",getIntent().getExtras().getString("VeredaRow9"));
                acroFields.setField("PredioRow9",getIntent().getExtras().getString("PredioRow9"));
                acroFields.setField("NegrosRow9",getIntent().getExtras().getString("NegrosRow9"));
                acroFields.setField("RojosRow9",getIntent().getExtras().getString("RojosRow9"));
                acroFields.setField("Estado cultivoRow9",getIntent().getExtras().getString("EstadoCultivoRow9"));
                acroFields.setField("ObservacionesRow9",getIntent().getExtras().getString("ObservacionesRow9"));

                acroFields.setField("DíaRow10", Dia.getText() + "");
                acroFields.setField("Código de trampaRow10", CodigoTrampa.getText() + "");
                acroFields.setField("MunicipioRow10", Municipio.getText() + "");
                acroFields.setField("VeredaRow10", Vereda.getText() + "");
                acroFields.setField("PredioRow10", Predio.getText() + "");
                acroFields.setField("NegrosRow10", Negros.getText() + "");
                acroFields.setField("RojosRow10", Rojos.getText() + "");
                acroFields.setField("Estado cultivoRow10", EstadoCultivo.getText() + "");
                acroFields.setField("ObservacionesRow10",Observaciones.getText()+"");
                acroFields.setField("ObservacionesRow10", Observaciones.getText() + "");
            }

            acroFields.setField("Observaciones generales",ObservacionesGenerales.getText()+"");
            acroFields.setField("Cedula Supervisor", CedulaSupervisor.getText()+"");

            PushbuttonField ad  = acroFields.getNewPushbuttonFromField("Firma Supervisor");
            ad.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
            ad.setProportionalIcon(false);
            if (ruta!=null) {
                if (!ruta.isEmpty()) {
                    ad.setImage(Image.getInstance(ruta));
                    acroFields.replacePushbuttonField("Firma Supervisor", ad.getField());
                }
            }
            PushbuttonField ad2 = acroFields.getNewPushbuttonFromField("Firma Funcionario");
            ad2.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
            ad2.setProportionalIcon(false);
            if (ruta2!=null) {
                if (!ruta2.isEmpty()) {
                    ad2.setImage(Image.getInstance(ruta2));
                    acroFields.replacePushbuttonField("Firma Funcionario", ad2.getField());
                }
            }
            stamper.close();
            pdfReader.close();


        }catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void verInforme(){
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getString("id") != null) {
                final String id = getIntent().getExtras().getString("id");
                informe = baseDatos.child("Formulario");
                informe.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                       ModeloForma3007 modeloForma3007 = dataSnapshot.getValue(ModeloForma3007.class);
                        if (modeloForma3007.codifotrampa.equals(id)){
                            FuncionarioLectura.setText(modeloForma3007.funcionario);
                            CambioFeromona.setText(modeloForma3007.cambioferomona);
                            NumeroFeromona.setText(modeloForma3007.leteferomona);
                            Year.setText(modeloForma3007.year);
                            Mes.setText(modeloForma3007.mes);
                            Dia.setText(modeloForma3007.dia);
                            CodigoTrampa.setText(modeloForma3007.codifotrampa);
                            Municipio.setText(modeloForma3007.municipio);
                            Vereda.setText(modeloForma3007.vereda);
                            Predio.setText(modeloForma3007.predio);
                            Negros.setText(modeloForma3007.negros);
                            Rojos.setText(modeloForma3007.rojos);
                            EstadoCultivo.setText(modeloForma3007.estado);
                            Observaciones.setText(modeloForma3007.observaciones);
                            addFormulario.setVisibility(View.INVISIBLE);
                            GuardarFirmaFuncionario.setVisibility(View.INVISIBLE);
                            GuardarFirmaSupervisor.setVisibility(View.INVISIBLE);
                            LimpiarFuncionario.setVisibility(View.INVISIBLE);
                            LimpiarSupervisor.setVisibility(View.INVISIBLE);
                            GuardarFormulario.setVisibility(View.INVISIBLE);
                            ObservacionesGenerales.setVisibility(View.INVISIBLE);
                            CedulaSupervisor.setVisibility(View.INVISIBLE);
                            FirmaFuncionario.setVisibility(View.INVISIBLE);
                            FirmaSupervisor.setVisibility(View.INVISIBLE);


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
    }

}
