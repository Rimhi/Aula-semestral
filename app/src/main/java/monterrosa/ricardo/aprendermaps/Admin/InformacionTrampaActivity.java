package monterrosa.ricardo.aprendermaps.Admin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
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
    private EditText CodigTrampa,posicionlat,posicionlng,fecha;
    private DatabaseReference miBaseDatos;
    private  DatabaseReference databaseReference,editartramparefeence;
    private ArrayList<LlegadaMapa> lista = new ArrayList<>();
    private FechaInspeccionAdapter adapter;
    private Button btn_editar_trampa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion_trampa);
        fechasdeInspeccion = findViewById(R.id.fechasInspeccion);
        CodigTrampa = findViewById(R.id.editaridtrampa);
        posicionlat = findViewById(R.id.editarlattrampa);
        posicionlng = findViewById(R.id.editarlngtrampa);
        fecha = findViewById(R.id.editarfechaingresotrampa);
        btn_editar_trampa = findViewById(R.id.btn_editar_trampa);
        CodigTrampa.setText(getIntent().getStringExtra("codigotrampa"));
        fecha.setText(getIntent().getStringExtra("indicio"));
        posicionlat.setText(getIntent().getStringExtra("lat"));
        posicionlng.setText(getIntent().getStringExtra("lng"));
        miBaseDatos = FirebaseDatabase.getInstance().getReference();
        editartramparefeence = miBaseDatos.child("trampas");
        databaseReference = miBaseDatos.child("trampas").child(CodigTrampa.getText().toString()).child("Inspeccion");
        databaseReference.addChildEventListener(eventListener);

        btn_editar_trampa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Trampa trampa = new Trampa(CodigTrampa.getText()+"",fecha.getText()+"",Double.parseDouble(posicionlat.getText()+""),Double.parseDouble(posicionlng.getText()+""),"Mosca de la fruta");
                editartramparefeence.child(CodigTrampa.getText().toString()).setValue(trampa);
            }
        });




    }
    ChildEventListener eventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                LlegadaMapa llegadaMapa = dataSnapshot.getValue(LlegadaMapa.class);
                lista.add(llegadaMapa);

            adapter = new FechaInspeccionAdapter(lista,getApplicationContext());
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
