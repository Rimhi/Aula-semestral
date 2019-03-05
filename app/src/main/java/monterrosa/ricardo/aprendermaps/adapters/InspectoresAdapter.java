package monterrosa.ricardo.aprendermaps.adapters;

import android.animation.Animator;
import android.content.Context;
import android.net.Uri;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import monterrosa.ricardo.aprendermaps.ModeloRegistro;
import monterrosa.ricardo.aprendermaps.R;

/**
 * Created by Ricardo Monterrosa H on 5/09/2018.
 */

public class InspectoresAdapter extends RecyclerView.Adapter<InspectoresAdapter.ViewHolderInspector> {
    ArrayList<ModeloRegistro>list;
    Context context;
    public InspectoresAdapter(ArrayList<ModeloRegistro>l,Context c) {
        list = l;
        context = c;
    }

    @Override
    public void onViewAttachedToWindow(ViewHolderInspector holder) {
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
    public ViewHolderInspector onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_lista_inspectores,null);
        return new InspectoresAdapter.ViewHolderInspector(view);
    }

    @Override
    public void onBindViewHolder(ViewHolderInspector holder, int position) {
        ModeloRegistro inspectores = list.get(position);
        holder.nombre.setText(inspectores.Nombre);
        holder.fecha.setText(inspectores.fechaRegistro);
        holder.correo.setText(inspectores.correo);
        holder.telefono.setText(inspectores.Telefono);
        holder.cedula.setText(inspectores.Cedula);
        Glide.with(context)
                .load(Uri.parse(inspectores.imagen))
                .fitCenter()
                .centerCrop()
                .into(holder.imagen);


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolderInspector extends RecyclerView.ViewHolder{
        TextView fecha,nombre,cedula,telefono,correo;
        ImageView imagen;
        public ViewHolderInspector(View itemView) {
            super(itemView);
            fecha = itemView.findViewById(R.id.CardViewFeachalistaInspector);
            cedula = itemView.findViewById(R.id.CardViewCeulalistaInspector);
            nombre = itemView.findViewById(R.id.CardviewNombrelistaInspector);
            telefono = itemView.findViewById(R.id.CardViewTelefonolistaInspector);
            correo = itemView.findViewById(R.id.CardViewcoreolistaInspector);
            imagen = itemView.findViewById(R.id.CardviewImagenlistaInspector);
        }
    }
}
