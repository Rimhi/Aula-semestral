package monterrosa.ricardo.aprendermaps.Admin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import monterrosa.ricardo.aprendermaps.Inspector.LlegadaMapa;
import monterrosa.ricardo.aprendermaps.R;
import monterrosa.ricardo.aprendermaps.adapters.FechaInspeccionAdapter;

public class InformacionTrampaActivity extends AppCompatActivity {
    private RecyclerView fechasdeInspeccion;
    private EditText CodigTrampa,posicion,fecha;
    private DatabaseReference miBaseDatos;
    private  DatabaseReference databaseReference;
    private ArrayList<LlegadaMapa> lista = new ArrayList<>();
    private FechaInspeccionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion_trampa);
        fechasdeInspeccion = findViewById(R.id.fechasInspeccion);
        CodigTrampa = findViewById(R.id.editaridtrampa);
        posicion = findViewById(R.id.editarlatlangtrampa);
        fecha = findViewById(R.id.editarfechaingresotrampa);

        CodigTrampa.setText(getIntent().getStringExtra("codigotrampa"));
        fecha.setText(getIntent().getStringExtra("indicio"));
        posicion.setText(getIntent().getStringExtra("posicion"));
        miBaseDatos = FirebaseDatabase.getInstance().getReference();
        databaseReference = miBaseDatos.child("trampas").child(CodigTrampa.getText().toString()).child("Inspeccion");
        databaseReference.addChildEventListener(eventListener);




    }
    ChildEventListener eventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                LlegadaMapa llegadaMapa = dataSnapshot.getValue(LlegadaMapa.class);
                lista.add(llegadaMapa);

            adapter = new FechaInspeccionAdapter(lista);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(InformacionTrampaActivity.this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            fechasdeInspeccion.setLayoutManager(linearLayoutManager);
            fechasdeInspeccion.setAdapter(adapter);
            adapter.notifyDataSetChanged();





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
    };
}
