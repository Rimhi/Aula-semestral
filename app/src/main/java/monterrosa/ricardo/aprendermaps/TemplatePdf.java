package monterrosa.ricardo.aprendermaps;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Ricardo Monterrosa H on 19/09/2018.
 */

public class TemplatePdf {
    private Context context;
    private  static final String CARPETA_PROYECTO = "monterrosa.ricardo.aprendermaps";
    private  static final String CARPETA_ARCHIVO = "Reportes";
    private  String CodigoTrampa,fechaactual;
    private File pdfFile;
    private String NOMBRE_ARCHIVO;
    private Document document;
    private PdfWriter pdfWriter;

    public TemplatePdf(Context context, String codigoTrampa, String fechaactual) {
        this.context = context;
        this.CodigoTrampa = codigoTrampa;
        this.fechaactual = fechaactual;
        NOMBRE_ARCHIVO  = "Reporte"+CodigoTrampa+fechaactual+".pdf";
    }
    public void openDocument(){
        creatiFile();
        try {
            document = new Document(PageSize.A4);
            pdfWriter = PdfWriter.getInstance(document,new FileOutputStream(pdfFile));
            document.open();

        }catch (Exception e){
            Log.e("error",e.toString());
        }
    }
    private void creatiFile(){
        File folder = new File(Environment.getExternalStorageDirectory().toString(),"Reportes");

        if (!folder.exists()){
            folder.mkdirs();
            pdfFile = new File(folder,NOMBRE_ARCHIVO);
        }
    }
    public void closeDocument(){
        document.close();
    }
    public void addMetadata(){

    }
}
