package monterrosa.ricardo.aprendermaps;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Connection;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker marcador;
    private DatabaseReference baseDatos;
    private DatabaseReference trampaRef;
    private String borrarmarcador = "";
    double lat = 0.0;
    double lng = 0.0;
    private static final int COD_PERMISOS = 3452;
    private LatLngBounds MONTERIA_CORDOBA_COLOMBIA = new LatLngBounds(new LatLng(8.726606067497622, -75.90802716634767),
            new LatLng(8.824689, -75.826778));
    private AlertDialog dialog;
    private DatabaseReference miBaseDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        if (status == ConnectionResult.SUCCESS) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        } else {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, (Activity) getApplicationContext(), 10);
            dialog.show();
        }
        baseDatos = FirebaseDatabase.getInstance().getReference();

        trampaRef = baseDatos.child("trampas");
        trampaRef.addChildEventListener(trampasHijoListener);



        miBaseDatos = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(8.7593615, -75.877503), 13));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setMinZoomPreference(13.0f);
        mMap.setLatLngBoundsForCameraTarget(MONTERIA_CORDOBA_COLOMBIA);
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng latLng) {
                marcador = mMap.addMarker(new MarkerOptions().anchor(0.0f,1.0f).position(latLng));
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                builder.setTitle("¿Esta es la posicion de una trampa?")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                LayoutInflater layoutInflater = getLayoutInflater();
                                final View view = layoutInflater.inflate(R.layout.guardarpos,null);
                                final EditText fecha = view.findViewById(R.id.fechaingresotrampa);
                                final EditText idtrampa = view.findViewById(R.id.idtrampa);
                                final EditText lugartrampa = view.findViewById(R.id.Lugardelatrampa);
                                final ImageView ifo = view.findViewById(R.id.indicacion1);
                                final EditText latlangtrampa = view.findViewById(R.id.latlangtrampa);
                                final Button guardarmarcador = view.findViewById(R.id.guardartrampa);
                                ifo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Toast.makeText(getApplicationContext(),"Info: debes guardar indicios del lugar como por ejemplo: barrio buenavista calle 4, o algun otro indicio",Toast.LENGTH_LONG).show();
                                    }
                                });
                                latlangtrampa.setText(latLng+"");
                                guardarmarcador.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();


                                        Long id = Long.parseLong( idtrampa.getText().toString() );

                                        Trampa trampa = new Trampa(
                                                id,
                                                fecha.getText().toString(),
                                                lugartrampa.getText().toString(),
                                                latLng.latitude,
                                                latLng.longitude
                                        );

                                        miBaseDatos.child("trampas").child(id+"").setValue(trampa);
                                    }
                                });



                                AlertDialog.Builder savemarker = new AlertDialog.Builder(MapsActivity.this);
                                savemarker.setView(view).setTitle("Guardar Marcador").setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        marcador.remove();
                                        dialog.dismiss();

                                    }
                                });
                                dialog = savemarker.create();
                                dialog.show();

                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                      marcador.remove();
                      dialog.dismiss();
                    }
                });
                dialog = builder.create();
                dialog.show();



            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                final String [] idtrampa= marker.getTitle().split(" ");
               AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
               builder.setTitle("Opciones Marcador")
                       .setPositiveButton("Eliminar Marcador", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {

                               //Toast.makeText(MapsActivity.this, idtrampa[1],Toast.LENGTH_LONG).show();
                             miBaseDatos.child("trampas").child(idtrampa[1]).setValue(null);
                               marker.remove();
                           }
                       }).setNegativeButton("Informacion", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {
                               AlertDialog.Builder builder1 = new AlertDialog.Builder(MapsActivity.this);
                               builder1.setPositiveButton("Editar", new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialogInterface, int i) {

                                   }
                               }).setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialogInterface, int i) {

                                   }
                               }).setTitle("Informacion trampa "+idtrampa[1]);
                               dialog.dismiss();
                           }
                       });
               dialog = builder.create();
               dialog.show();
                return false;
            }

        });


    }


    private boolean pedirPermisosFaltantes(){
        boolean todosConsedidos = true;
        ArrayList<String> permisosFaltantes = new ArrayList<>();

        boolean permisoCoarse = ( ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED);

        boolean permisoFine = ( ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED);


        if(!permisoCoarse){
            todosConsedidos = false;
            permisosFaltantes.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if(!permisoFine){
            todosConsedidos = false;
            permisosFaltantes.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }


        if(!todosConsedidos) {
            String[] permisos = new String[permisosFaltantes.size()];
            permisos = permisosFaltantes.toArray(permisos);

            ActivityCompat.requestPermissions(this, permisos, COD_PERMISOS);
        }

        return todosConsedidos;
    }
    ChildEventListener trampasHijoListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            final Trampa trampa = dataSnapshot.getValue(Trampa.class);

            if( mMap != null && trampa != null){
                MarkerOptions markerOptions = new MarkerOptions()
                        .title("Identificacion: "+trampa.id)
                        .snippet("Indicio o Referencia: "+trampa.direccion)
                        .position( new LatLng(trampa.latitud, trampa.longitud) );
                mMap.addMarker(markerOptions);
            }

//            Toast.makeText(MapaInspectorActivity.this, "Trampa añadida: "+trampa.toString(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            final Trampa trampa= dataSnapshot.getValue(Trampa.class);

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

}
