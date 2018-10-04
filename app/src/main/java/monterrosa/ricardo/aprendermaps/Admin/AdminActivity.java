package monterrosa.ricardo.aprendermaps.Admin;

import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import monterrosa.ricardo.aprendermaps.Inspector.LlegadaMapa;
import monterrosa.ricardo.aprendermaps.MainActivity;
import monterrosa.ricardo.aprendermaps.ModeloRegistro;
import monterrosa.ricardo.aprendermaps.R;
import monterrosa.ricardo.aprendermaps.Servicios.ServiceNotificacion;

public class AdminActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,VerInspectoresFragment.OnFragmentInteractionListener,AdminPerfilFragment.OnFragmentInteractionListener, AdminVisitasFragment.OnFragmentInteractionListener,ChatAdminFragment.OnFragmentInteractionListener {
    private  static final int GALERY_INTENT = 1;
    private DatabaseReference mibasedatos,databaseReference;
    private FirebaseAuth auth;
    private TextView correo,nombre;
    private ImageView Adminimagen;
    private AlertDialog dialog;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        correo = header.findViewById(R.id.adminCorreo);
        nombre = header.findViewById(R.id.adminNombre);
        correo.setText(auth.getCurrentUser().getEmail());
        Adminimagen = header.findViewById(R.id.adminimageView);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mibasedatos = databaseReference.child("Usuarios");
        mibasedatos.addChildEventListener(addlistener);
        if (!user.isEmailVerified()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
            builder.setTitle("Verificacion de email")
                    .setMessage("Por favor verifica tu correo electronico para continuar")
                    .setCancelable(false)
                    .setIcon(R.drawable.ic_email)
                    .setNegativeButton("Cerrar sesion", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            auth.signOut();
                            startActivity(new Intent(AdminActivity.this, MainActivity.class));
                        }
                    });
            dialog = builder.create();
            dialog.show();
        }
        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (!user.isEmailVerified()){
                    final AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
                    builder.setTitle("Verificacion de email")
                            .setMessage("Por favor verifica tu correo electronico para continuar")
                            .setCancelable(false)
                            .setIcon(R.drawable.ic_email)
                            .setNegativeButton("Cerrar sesion", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    auth.signOut();
                                    startActivity(new Intent(AdminActivity.this,MainActivity.class));
                                }
                            });
                    dialog = builder.create();
                    dialog.show();
                }if (user.isEmailVerified()){
                    final AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
                    builder.setTitle("Verificacion de email")
                            .setMessage("Por favor verifica tu correo electronico para continuar")
                            .setCancelable(false)
                            .setIcon(R.drawable.ic_email)
                            .setNegativeButton("Inciar sesion", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    auth.signOut();
                                    startActivity(new Intent(AdminActivity.this,MainActivity.class));
                                }
                            });
                    dialog = builder.create();
                    dialog.show();
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }

            }
        };
        startService(new Intent(AdminActivity.this, ServiceNotificacion.class));




    }
    ChildEventListener addlistener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            final ModeloRegistro modeloRegistro = dataSnapshot.getValue(ModeloRegistro.class);
            if (auth.getCurrentUser().getUid().equals(modeloRegistro.IDguidDatabase)) {
                nombre.setText(modeloRegistro.Nombre);
                correo.setText(modeloRegistro.correo);
                String imagen = modeloRegistro.imagen;
                if (!AdminActivity.super.isDestroyed()) {
                    Glide.with(AdminActivity.this)
                            .load(Uri.parse(imagen))
                            .fitCenter()
                            .centerCrop()
                            .into(Adminimagen);
                }
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
        getMenuInflater().inflate(R.menu.admin, menu);
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
        if (id == R.id.nav_admin_mapa) {
            startActivity(new Intent(AdminActivity.this,MapsActivity.class));
        } else if (id == R.id.nav_admin_visitas) {
            AdminVisitasFragment fragment = new AdminVisitasFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.ContenedorAdmin,fragment).commit();


        } else if (id == R.id.nav_admin_salir) {
            auth.signOut();
            startActivity(new Intent(AdminActivity.this,MainActivity.class));

        } else if (id == R.id.nav_admin_inspectores) {
            VerInspectoresFragment fragment = new VerInspectoresFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.ContenedorAdmin,fragment).commit();

        } else if (id == R.id.nav_admin_modificar) {
            AdminPerfilFragment fragment = new AdminPerfilFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.ContenedorAdmin,fragment).commit();

        } else if (id == R.id.nav_admin_chat) {
            final ChatAdminFragment framento = new ChatAdminFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.ContenedorAdmin,framento).commit();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(listener);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
    private String fechaactual(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();

        String fecha = dateFormat.format(date);

        return  fecha;
    }


}
