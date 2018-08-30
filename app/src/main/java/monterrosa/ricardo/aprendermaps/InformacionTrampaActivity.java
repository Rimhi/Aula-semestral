package monterrosa.ricardo.aprendermaps;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InformacionTrampaActivity extends AppCompatActivity {
    private RecyclerView fechasdeInspeccion;
    private EditText CodigTrampa,indicio,posicion;
    private DatabaseReference miBaseDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion_trampa);
        fechasdeInspeccion = findViewById(R.id.fechasInspeccion);
        CodigTrampa = findViewById(R.id.editaridtrampa);
        indicio = findViewById(R.id.editarLugardelatrampa);
        posicion = findViewById(R.id.editarlatlangtrampa);

        CodigTrampa.setText(getIntent().getStringExtra("codigotrampa"));
        indicio.setText(getIntent().getStringExtra("indicio"));
        posicion.setText(getIntent().getStringExtra("posicion"));
        miBaseDatos = FirebaseDatabase.getInstance().getReference();
        miBaseDatos.addChildEventListener(eventListener);

    }
    ChildEventListener eventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {


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
