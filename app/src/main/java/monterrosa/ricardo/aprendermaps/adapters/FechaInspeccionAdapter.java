package monterrosa.ricardo.aprendermaps.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import monterrosa.ricardo.aprendermaps.Inspector.LlegadaMapa;
import monterrosa.ricardo.aprendermaps.R;

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
        holder.descripcion.setText("");
        holder.Cedula.setText(llegadaMapa.cedula);
        holder.id.setText(llegadaMapa.idTrampa);
        holder.correo.setText(llegadaMapa.correo);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView fecha,descripcion,nombre,id,correo,Cedula;
        public ViewHolder(View itemView) {
            super(itemView);
            fecha = itemView.findViewById(R.id.CardViewFeachaInspeccion);
            descripcion = itemView.findViewById(R.id.CardViewDescripcionInspeccion);
            nombre = itemView.findViewById(R.id.CardviewNombreInspector);
            id = itemView.findViewById(R.id.CardViewIDtrampa);
            correo = itemView.findViewById(R.id.CardViewCorreoInpector);
            Cedula = itemView.findViewById(R.id.CardViewCedulaInspector);
        }
    }
}
