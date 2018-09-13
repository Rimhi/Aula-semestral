package monterrosa.ricardo.aprendermaps.adapters;

import android.content.ClipData;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import monterrosa.ricardo.aprendermaps.ModeloDarposicion;
import monterrosa.ricardo.aprendermaps.R;

/**
 * Created by Ricardo Monterrosa H on 9/09/2018.
 */

public class InspectorInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    LayoutInflater inflater =null;
    DatabaseReference inspectores;
    DatabaseReference databaseReference;
    String Nombre = "";
    String Cedula = "";
    String Telefono = "";
    final String Correo = "jajajajajajajajajajaja";
    public InspectorInfoWindowAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    @Override
    public View getInfoWindow(final Marker marker) {
        final View view = inflater.inflate(R.layout.inspectorinfowindow,null);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        inspectores = databaseReference.child("Posiciones");
        inspectores.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ModeloDarposicion darposicion = dataSnapshot.getValue(ModeloDarposicion.class);
                if (marker.getTitle().equals(darposicion.ceduladarposiciontelefonoinspector)) {
                    Nombre = darposicion.nombredarposicioninspector;
                    Cedula = darposicion.ceduladarposiciontelefonoinspector;
                    Telefono = darposicion.telefonodarposicioninspector;
                    Log.e("dato1",Nombre);



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
        ((TextView)view.findViewById(R.id.InfoWindowNombreInspector)).setText(Nombre);
        ((TextView)view.findViewById(R.id.InfoWindowCedulaInspector)).setText(Cedula);
        ((TextView)view.findViewById(R.id.InfoWindowTelefonoInspector)).setText(Telefono);
        ((TextView)view.findViewById(R.id.InfoWindowCorreoInpector)).setText(Correo);
        return view;
    }

    @Override
    public View getInfoContents(final Marker marker) {

        return null;

    }
}
