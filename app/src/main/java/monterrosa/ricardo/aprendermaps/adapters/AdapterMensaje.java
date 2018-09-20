package monterrosa.ricardo.aprendermaps.adapters;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import monterrosa.ricardo.aprendermaps.Modelochat;
import monterrosa.ricardo.aprendermaps.R;

/**
 * Created by Ricardo Monterrosa H on 14/09/2018.
 */

public class AdapterMensaje extends RecyclerView.Adapter<AdapterMensaje.ViewHolderAdapterMensajes>{
    private List<Modelochat> modelochats;
    private Context context;
    private FirebaseAuth auth;

    public AdapterMensaje(List<Modelochat>l, Context context){
        modelochats = l;
        this.context = context;

    }

    @Override
    public ViewHolderAdapterMensajes onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardviewmensajes,null);
        return new ViewHolderAdapterMensajes(view);
    }

    @Override
    public void onBindViewHolder(ViewHolderAdapterMensajes holder, int position) {
        auth = FirebaseAuth.getInstance();
        Modelochat modelo = modelochats.get(position);
        RelativeLayout.LayoutParams rl =(RelativeLayout.LayoutParams)holder.cardView.getLayoutParams();
        FrameLayout.LayoutParams fl = (FrameLayout.LayoutParams)holder.fondo.getLayoutParams();
        LinearLayout.LayoutParams llhora = (LinearLayout.LayoutParams) holder.hora.getLayoutParams();
        LinearLayout.LayoutParams llmensje = (LinearLayout.LayoutParams) holder.hora.getLayoutParams();
        LinearLayout.LayoutParams llnombre = (LinearLayout.LayoutParams)holder.Nombre.getLayoutParams();
        if (modelo.getId().equals(auth.getCurrentUser().getUid())){//emisor
            holder.fondo.setBackgroundResource(R.drawable.in_message_bg);
            rl.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            rl.addRule(RelativeLayout.ALIGN_PARENT_LEFT,0);
            fl.gravity = Gravity.RIGHT;
            llhora.gravity = Gravity.RIGHT;
            llmensje.gravity = Gravity.RIGHT;
            llnombre.gravity = Gravity.RIGHT;

        }else if(!modelo.getId().equals(auth.getCurrentUser().getUid())){//receptor
            holder.fondo.setBackgroundResource(R.drawable.out_message_bg);
            rl.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            rl.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,0);
            fl.gravity = Gravity.LEFT;
            llhora.gravity = Gravity.LEFT;
            llmensje.gravity = Gravity.LEFT;
            llnombre.gravity = Gravity.LEFT;

        }
        holder.cardView.setLayoutParams(rl);
        holder.fondo.setLayoutParams(fl);
        holder.hora.setLayoutParams(llhora);
        holder.Nombre.setLayoutParams(llnombre);

        holder.mensaje.setLayoutParams(llmensje);
        holder.mensaje.setText(modelo.getMensaje());
        holder.hora.setText(modelo.getHoramensaje());
        holder.Nombre.setText(modelo.getNombre());
        if (Build.VERSION.SDK_INT< Build.VERSION_CODES.LOLLIPOP){holder.cardView.getBackground().setAlpha(0);}
        else {holder.cardView.setBackgroundColor(ContextCompat.getColor(context,android.R.color.transparent));}



    }

    @Override
    public int getItemCount() {
        return modelochats.size();
    }

    public class ViewHolderAdapterMensajes extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView mensaje,hora,Nombre;
        LinearLayout fondo;
        public ViewHolderAdapterMensajes(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.CV);
            mensaje = itemView.findViewById(R.id.CardViewMensaje);
            hora = itemView.findViewById(R.id.CardViewHora);
            fondo = itemView.findViewById(R.id.fondomensaje);
            Nombre = itemView.findViewById(R.id.CarviewNombrechat);
        }
    }
}
