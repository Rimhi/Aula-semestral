package monterrosa.ricardo.aprendermaps;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InspectorActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    String nombre,correo;
    TextView correoInspector,NombreInspector;
    ImageView InspectorimageView;
    private DatabaseReference mibasedatos,databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspector);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View HeadView = navigationView.getHeaderView(0);
        correo = getIntent().getStringExtra("correo");
        nombre = getIntent().getStringExtra("nombre");
        NombreInspector = HeadView.findViewById(R.id.PerfilNombreInsector);
        correoInspector = HeadView.findViewById(R.id.PerfilcorreoInspector);
        InspectorimageView = HeadView.findViewById(R.id.PerfilimagenInspector);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mibasedatos = databaseReference.child("Usuarios");
        mibasedatos.addChildEventListener(addlistener);
    }
    ChildEventListener addlistener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            final ModeloRegistro modeloRegistro = dataSnapshot.getValue(ModeloRegistro.class);
            NombreInspector.setText(modeloRegistro.Nombre);
            correoInspector.setText(modeloRegistro.correo);
            String imagen = modeloRegistro.imagen;
            Toast.makeText(InspectorActivity.this,imagen,Toast.LENGTH_LONG).show();
            Glide.with(InspectorActivity.this)
                    .load(Uri.parse(imagen))
                    .fitCenter()
                    .centerCrop()
                    .into(InspectorimageView);


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
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.inspector, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_MapaInspector) {
            Intent intent = new Intent(InspectorActivity.this,MapaInspectorActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_VisitaInspector) {

        } else if (id == R.id.nav_salir) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(InspectorActivity.this,MainActivity.class));


        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
