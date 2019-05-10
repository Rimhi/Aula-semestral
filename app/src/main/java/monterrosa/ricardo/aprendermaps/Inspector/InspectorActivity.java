package monterrosa.ricardo.aprendermaps.Inspector;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import monterrosa.ricardo.aprendermaps.Admin.ChatAdminFragment;
import monterrosa.ricardo.aprendermaps.ContactameActivity;
import monterrosa.ricardo.aprendermaps.MainActivity;
import monterrosa.ricardo.aprendermaps.ModeloRegistro;
import monterrosa.ricardo.aprendermaps.R;
import monterrosa.ricardo.aprendermaps.TutorialInspectorActivity;

public class InspectorActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener ,
        PerfilInspectoFragment.OnFragmentInteractionListener, VisitasInspectorFragment.OnFragmentInteractionListener,
        ChatAdminFragment.OnFragmentInteractionListener{
    TextView correoInspector,NombreInspector;
    ImageView InspectorimageView;
    private DatabaseReference mibasedatos,databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener listener;
    private AlertDialog dialog;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspector);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View HeadView = navigationView.getHeaderView(0);
        NombreInspector = HeadView.findViewById(R.id.PerfilNombreInsector);
        correoInspector = HeadView.findViewById(R.id.PerfilcorreoInspector);
        InspectorimageView = HeadView.findViewById(R.id.PerfilimagenInspector);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mibasedatos = databaseReference.child("Usuarios");
        mibasedatos.addChildEventListener(addlistener);
        if (!user.isEmailVerified()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(InspectorActivity.this);
            builder.setTitle("Verificacion de email")
                    .setMessage("Por favor verifica tu correo electronico para continuar")
                    .setCancelable(false)
                    .setIcon(R.drawable.ic_email)
                    .setNegativeButton("Cerrar sesion", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mAuth.signOut();
                            startActivity(new Intent(InspectorActivity.this, MainActivity.class));
                        }
                    });
            dialog = builder.create();
            dialog.show();
        }
        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (!user.isEmailVerified()){
                    final AlertDialog.Builder builder = new AlertDialog.Builder(InspectorActivity.this);
                    builder.setTitle("Verificación de email")
                            .setMessage("Por favor verifica tu correo electrónico para continuar")
                            .setCancelable(false)
                            .setIcon(R.drawable.ic_email)
                            .setNegativeButton("Cerrar sesion", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    mAuth.signOut();
                                    startActivity(new Intent(InspectorActivity.this,MainActivity.class));
                                }
                            });
                    if (!InspectorActivity.super.isDestroyed()) {
                        dialog = builder.create();
                        dialog.show();
                    }
                }if (user.isEmailVerified()){
                    final AlertDialog.Builder builder = new AlertDialog.Builder(InspectorActivity.this);
                    builder.setTitle("Verificación de email")
                            .setMessage("Por favor verifica tu correo electrónico para continuar")
                            .setCancelable(false)
                            .setIcon(R.drawable.ic_email)
                            .setNegativeButton("Inciar sesion", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    mAuth.signOut();
                                    startActivity(new Intent(InspectorActivity.this,MainActivity.class));
                                }
                            });
                    dialog = builder.create();
                    if (dialog!=null || !InspectorActivity.super.isDestroyed() || !InspectorActivity.super.isFinishing())
                        dialog.show();
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }

            }
        };



    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(listener);
    }

    ChildEventListener addlistener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            final ModeloRegistro modeloRegistro = dataSnapshot.getValue(ModeloRegistro.class);
            if (mAuth.getCurrentUser().getUid().equals(modeloRegistro.IDguidDatabase)) {
                NombreInspector.setText(modeloRegistro.Nombre);
                correoInspector.setText(modeloRegistro.correo);
                String imagen = modeloRegistro.imagen;
                Glide.with(InspectorActivity.this)
                        .load(Uri.parse(imagen))
                        .fitCenter()
                        .centerCrop()
                        .into(InspectorimageView);
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
            startActivity(new Intent(InspectorActivity.this, TutorialInspectorActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
        if (id == R.id.inspector_contactame){
            startActivity(new Intent(InspectorActivity.this, ContactameActivity.class));
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
            intent.putExtra("nombreInspector",NombreInspector.getText()+"");
            Toast.makeText(InspectorActivity.this, "nombre inspector "+ NombreInspector.getText()+"", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        } else if (id == R.id.nav_VisitaInspector) {
            VisitasInspectorFragment fragment = new VisitasInspectorFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.contenedorInspector,fragment).commit();
        } else if (id == R.id.nav_salir) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(InspectorActivity.this,MainActivity.class));


        } else if (id == R.id.nav_perfilinspector) {
            PerfilInspectoFragment fragment = new PerfilInspectoFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.contenedorInspector,fragment).commit();
        } else if (id == R.id.nav_inspector_chat) {
            final ChatAdminFragment framento = new ChatAdminFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.contenedorInspector,framento).commit();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
