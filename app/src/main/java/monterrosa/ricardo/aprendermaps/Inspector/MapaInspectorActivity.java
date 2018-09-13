package monterrosa.ricardo.aprendermaps.Inspector;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import monterrosa.ricardo.aprendermaps.ModeloDarposicion;
import monterrosa.ricardo.aprendermaps.ModeloRegistro;
import monterrosa.ricardo.aprendermaps.R;
import monterrosa.ricardo.aprendermaps.Admin.Trampa;

public class MapaInspectorActivity extends AppCompatActivity implements OnMapReadyCallback{
    private GoogleMap mMap;
    private DatabaseReference baseDatos;
    private DatabaseReference trampaRef;
    private DatabaseReference posicionesinspector;
    private DatabaseReference perfinInspector;
    private AlertDialog dialog;
    private LatLngBounds MONTERIA_CORDOBA_COLOMBIA = new LatLngBounds(new LatLng(8.726606067497622, -75.90802716634767),
            new LatLng(8.824689, -75.826778));
    private static final double RADIO_TIERRA = 6378.137;
    private static final String TAG = "UtilidadesCoordenadas";
    private int numero = 1;
    private LatLng userlocation = null;
    private MiLocationListener locationListener;
    private String TAG_POS = "PosicionTag";
    private ProgressDialog progressDialog;
    private FirebaseAuth auth;
    private String NombreInspector,CedulaInspector,TelefonoInspector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_inspector);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_inspector);
        mapFragment.getMapAsync(this);

        baseDatos = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        trampaRef = baseDatos.child("trampas");
        trampaRef.addChildEventListener(trampasHijoListener);
        perfinInspector = baseDatos.child("Usuarios");
        perfinInspector.addChildEventListener(perfillistener);
        posicionesinspector = baseDatos.child("Posiciones");
        progressDialog = new ProgressDialog(this);
        progressDialog.setIcon(R.drawable.ic_informacion);
        progressDialog.setTitle("Cargando Mapa");
        progressDialog.setMessage("Por favor espera");
        progressDialog.setCancelable(false);
        progressDialog.show();

        locationListener = new MiLocationListener();


    }
    ChildEventListener perfillistener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            ModeloRegistro modeloRegistro = dataSnapshot.getValue(ModeloRegistro.class);
            if (auth.getCurrentUser().getUid().equals(modeloRegistro.IDguidDatabase)){
                NombreInspector = modeloRegistro.Nombre;
                TelefonoInspector = modeloRegistro.Telefono;
                CedulaInspector = modeloRegistro.Cedula;

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

    private class MiLocationListener implements LocationListener{
        private AlertDialog dialogoActivarGPS;
        public MiLocationListener(){
            AlertDialog.Builder builderDialog = new AlertDialog.Builder(MapaInspectorActivity.this)
                    .setTitle("Debe activar el GPS")
                    .setIcon(R.drawable.ic_error)
                    .setMessage("Por favor Activa el GPS para poder usar la app")
                    .setCancelable(false);// No puede cancelar el dialogo

            dialogoActivarGPS = builderDialog.create();
        }

        @Override
        public void onLocationChanged(Location location) {
            userlocation = new LatLng(location.getLatitude(), location.getLongitude());
            ModeloDarposicion modeloDarposicion = new ModeloDarposicion(userlocation.latitude,userlocation.longitude,auth.getCurrentUser().getUid(),
                    NombreInspector,TelefonoInspector,CedulaInspector);
            posicionesinspector.child(auth.getCurrentUser().getUid()).setValue(modeloDarposicion);
            if (userlocation != null){
                    progressDialog.dismiss();
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {
            if( dialogoActivarGPS.isShowing() ){
                dialogoActivarGPS.dismiss();
            }
        }

        @Override
        public void onProviderDisabled(String s) {
            if( !MapaInspectorActivity.this.isFinishing() ) {
                dialogoActivarGPS.show();
            }
        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setMinZoomPreference(13.0f);
        mMap.setLatLngBoundsForCameraTarget(MONTERIA_CORDOBA_COLOMBIA);

        LocationManager lm = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,1,locationListener);
        if (userlocation!=null){mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userlocation, 14));}


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // Toast.makeText(getApplicationContext(),""+calcularDistancia(userlocation,marker.getPosition()),Toast.LENGTH_LONG).show();

                return false;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(final Marker marker) {
            if (userlocation!=null) {
                if (calcularDistancia(userlocation, marker.getPosition()) <= 5.5) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MapaInspectorActivity.this);
                    builder.setTitle("¿Desea llenar formulario?")
                            .setNegativeButton("no", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialog.dismiss();
                                }
                            }).setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            final String[] idtrampa = marker.getTitle().split(" ");
                            Intent intent = new Intent(MapaInspectorActivity.this, LlenarFormularioActivity.class);
                            intent.putExtra("codigotrampa", idtrampa[1]);
                            startActivity(intent);

                        }
                    });
                    dialog = builder.create();
                    dialog.show();


                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MapaInspectorActivity.this);
                    builder.setTitle("Aun no has llegado")
                            .setMessage("Por favor ir a " + marker.getPosition() + " como " + marker.getSnippet())
                            .setPositiveButton("Ir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialog.dismiss();
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialog.dismiss();
                        }
                    });
                    dialog = builder.create();
                    dialog.show();

                    //Toast.makeText(getApplicationContext(),"Porfavor Dirigete a "+marker.getPosition()+" como  "+marker.getSnippet(),Toast.LENGTH_LONG).show();
                }
            }else {
                AlertDialog.Builder builder = new AlertDialog.Builder(MapaInspectorActivity.this);
                builder.setTitle("Posicion nula")
                        .setMessage("Por favor espera a que se encuentre tu posicion")
                        .setCancelable(false);
                dialog = builder.create();
                dialog.show();

            }
            }
        });
    }


    ChildEventListener trampasHijoListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            final Trampa trampa = dataSnapshot.getValue(Trampa.class);

            if (mMap != null && trampa != null) {
                MarkerOptions markerOptions = new MarkerOptions()
                        .title("Identificacion: " + trampa.id)
                        .snippet("Indicio o Referencia: " + trampa.direccion)
                        .position(new LatLng(trampa.latitud, trampa.longitud));
                mMap.addMarker(markerOptions);
            }

//            Toast.makeText(MapaInspectorActivity.this, "Trampa añadida: "+trampa.toString(), Toast.LENGTH_SHORT).show();
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

    public static double calcularDistancia(LatLng a, LatLng b) {
        if (a != null && b != null) {
            // pasamos las diferencias de las coordenadas a radianes
            double difLat = toRadianes(b.latitude - a.latitude);
            double difLong = toRadianes(b.longitude - a.longitude);

            double dato1 = Math.sin(difLat / 2) * Math.sin(difLat / 2) + Math.cos(toRadianes(a.latitude)) *
                    Math.cos(toRadianes(b.latitude)) * Math.sin(difLong / 2) * Math.sin(difLong / 2);

            double dato2 = 2 * Math.atan2(Math.sqrt(dato1), Math.sqrt(1 - dato1));

            double dato3 = RADIO_TIERRA * dato2;

            Log.v(TAG, "Resutlado final (metros) " + dato3 * 1000);

            return dato3 * 1000;// lo pasamos a metros
        }

        return 0.0;// si a y b son nulos
    }

    private static double toRadianes(double coord) {
        return ((coord * Math.PI) / 180);
    }

}
