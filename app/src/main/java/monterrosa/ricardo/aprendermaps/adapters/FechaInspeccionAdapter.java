package monterrosa.ricardo.aprendermaps.adapters;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;


import monterrosa.ricardo.aprendermaps.Inspector.LlegadaMapa;
import monterrosa.ricardo.aprendermaps.Inspector.LlenarFormularioActivity;
import monterrosa.ricardo.aprendermaps.R;

/**
 * Created by Ricardo Monterrosa H on 29/08/2018.
 */

public class FechaInspeccionAdapter extends RecyclerView.Adapter<FechaInspeccionAdapter.ViewHolder>{
    ArrayList<LlegadaMapa> lista;
    Context context;
    public  FechaInspeccionAdapter(ArrayList<LlegadaMapa>b, Context context){
        this.lista= b;
        this.context = context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_fechas_inspecciones,null);
        return new ViewHolder(view);
    }

    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        animateCircularReveal(holder.itemView);
    }
    public void animateCircularReveal(View view){
        int CenterX = 0;
        int CenterY = 0;
        int StartRadius = 0;
        int EndRadius = Math.max(view.getWidth(),view.getHeight());
        Animator animator = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            animator = ViewAnimationUtils.createCircularReveal(view,CenterX,CenterY,StartRadius,EndRadius);
        }
        view.setVisibility(View.VISIBLE);
        animator.start();


    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final LlegadaMapa llegadaMapa = lista.get(position);
        holder.nombre.setText(llegadaMapa.NombreColector);
        holder.fecha.setText(llegadaMapa.Fecha);
        holder.descripcion.setText("");
        holder.Cedula.setText(llegadaMapa.cedula);
        holder.id.setText(llegadaMapa.idTrampa);
        holder.correo.setText(llegadaMapa.correo);
        holder.tipo.setText(llegadaMapa.tipo);
        holder.vermas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, LlenarFormularioActivity.class);
                intent.putExtra("id",llegadaMapa.idTrampa+"");
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView fecha,descripcion,nombre,id,correo,Cedula,tipo;
        Button vermas;
        public ViewHolder(View itemView) {
            super(itemView);
            fecha = itemView.findViewById(R.id.CardViewFeachaInspeccion);
            descripcion = itemView.findViewById(R.id.CardViewDescripcionInspeccion);
            nombre = itemView.findViewById(R.id.CardviewNombreInspector);
            id = itemView.findViewById(R.id.CardViewIDtrampa);
            correo = itemView.findViewById(R.id.CardViewCorreoInpector);
            Cedula = itemView.findViewById(R.id.CardViewCedulaInspector);
            tipo = itemView.findViewById(R.id.CardViewTipotrampa);
            vermas = itemView.findViewById(R.id.vermasforma);
        }
    }
}
