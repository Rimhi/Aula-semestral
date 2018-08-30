package monterrosa.ricardo.aprendermaps.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import monterrosa.ricardo.aprendermaps.LlegadaMapa;
import monterrosa.ricardo.aprendermaps.R;
import monterrosa.ricardo.aprendermaps.Trampa;

/**
 * Created by Ricardo Monterrosa H on 29/08/2018.
 */

public class FechaInspeccionAdapter extends RecyclerView.Adapter<FechaInspeccionAdapter.ViewHolder>{
    ArrayList<LlegadaMapa> lista;
    public  FechaInspeccionAdapter(ArrayList<LlegadaMapa>b){
        this.lista= b;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_fechas_inspecciones,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        LlegadaMapa llegadaMapa = lista.get(position);
        holder.nombre.setText(llegadaMapa.NombreColector);
        holder.fecha.setText(llegadaMapa.Fecha);
        holder.descripcion.setText(llegadaMapa.idTrampa);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView fecha,descripcion,nombre;
        public ViewHolder(View itemView) {
            super(itemView);
            fecha = itemView.findViewById(R.id.CardViewFeachaInspeccion);
            descripcion = itemView.findViewById(R.id.CardViewDescripcionInspeccion);
            nombre = itemView.findViewById(R.id.CardviewNombreInspector);
        }
    }
}
