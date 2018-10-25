package monterrosa.ricardo.aprendermaps.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import monterrosa.ricardo.aprendermaps.R;

/**
 * Created by Ricardo Monterrosa H on 25/10/2018.
 */

public class AdapterPermisos  extends  RecyclerView.Adapter<AdapterPermisos.adapterpermisosviewholder>{
    private ArrayList<String>inspector;

    public AdapterPermisos(ArrayList<String> inspector) {
        this.inspector = inspector;
    }

    @Override
    public adapterpermisosviewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new adapterpermisosviewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_permisos,parent,false));
    }

    @Override
    public void onBindViewHolder(adapterpermisosviewholder holder, int position) {
        holder.nombre.setText(inspector.get(position));
    }

    @Override
    public int getItemCount() {
        return inspector.size();
    }

    public class adapterpermisosviewholder extends RecyclerView.ViewHolder{
        private TextView nombre;

        public adapterpermisosviewholder(View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.ajustesnombrecolector);
        }
    }
}
