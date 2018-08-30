package monterrosa.ricardo.aprendermaps;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

public class MapaInspectorActivity extends AppCompatActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    private DatabaseReference baseDatos;
    private DatabaseReference trampaRef;
    private AlertDialog dialog;
    private LatLngBounds MONTERIA_CORDOBA_COLOMBIA = new LatLngBounds(new LatLng(8.726606067497622, -75.90802716634767),
            new LatLng(8.824689, -75.826778));
    private static final double RADIO_TIERRA = 6378.137;
    private static final String TAG = "UtilidadesCoordenadas";
    private int numero = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_inspector);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_inspector);
        mapFragment.getMapAsync(this);

        baseDatos = FirebaseDatabase.getInstance().getReference();

        trampaRef = baseDatos.child("trampas");
        trampaRef.addChildEventListener(trampasHijoListener);

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
        LocationManager lm = (LocationManager)getSystemService(getApplicationContext().LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        final LatLng userlocation = new LatLng(location.getLatitude(),location.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userlocation, 14));
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
                if (calcularDistancia(userlocation,marker.getPosition())<=6){
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
                            final String [] idtrampa= marker.getTitle().split(" ");
                            Intent intent = new Intent(MapaInspectorActivity.this,LlenarFormularioActivity.class);
                            intent.putExtra("codigotrampa",idtrampa[1]);
                            startActivity(intent);

                        }
                    });
                   dialog = builder.create();
                   dialog.show();


                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MapaInspectorActivity.this);
                    builder.setTitle("Aun no has llegado")
                            .setMessage("Por favor ir a "+marker.getPosition()+" como "+marker.getSnippet())
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
            }
        });
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

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
    public static double calcularDistancia(LatLng a, LatLng b){
        if(a != null && b != null){
            // pasamos las diferencias de las coordenadas a radianes
            double difLat = toRadianes( b.latitude - a.latitude );
            double difLong = toRadianes( b.longitude - a.longitude );

            double dato1 = Math.sin( difLat/2) * Math.sin(difLat/2) + Math.cos( toRadianes(a.latitude) ) *
                    Math.cos( toRadianes(b.latitude) ) * Math.sin(difLong/2) * Math.sin(difLong/2);

            double dato2 = 2 * Math.atan2( Math.sqrt(dato1), Math.sqrt(1 - dato1) );

            double dato3 = RADIO_TIERRA * dato2;

            Log.v(TAG, "Resutlado final (metros) "+dato3 * 1000);

            return dato3 * 1000;// lo pasamos a metros
        }

        return 0.0;// si a y b son nulos
    }
    private static double toRadianes(double coord){
        return ( (coord * Math.PI) / 180 );
    }
    protected void getLocation() {
        if (isLocationEnabled(MapaInspectorActivity.this)) {
            locationManager = (LocationManager)  this.getSystemService(Context.LOCATION_SERVICE);
            criteria = new Criteria();
            bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();

            //You can still do this if you like, you might get lucky:
            Location location = locationManager.getLastKnownLocation(bestProvider);
            if (location != null) {
                Log.e("TAG", "GPS is on");
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Toast.makeText(MainActivity.this, "latitude:" + latitude + " longitude:" + longitude, Toast.LENGTH_SHORT).show();
                searchNearestPlace(voice2text);
            }
            else{
                //This is what you need:
                locationManager.requestLocationUpdates(bestProvider, 1000, 0, this);
            }
        }
        else
        {
            //prompt user to enable location....
            //.................
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);

    }

    @Override
    public void onLocationChanged(Location location) {
        //Hey, a non null location! Sweet!

        //remove location callback:
        locationManager.removeUpdates(this);

        //open the map:
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Toast.makeText(MainActivity.this, "latitude:" + latitude + " longitude:" + longitude, Toast.LENGTH_SHORT).show();
        searchNearestPlace(voice2text);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void searchNearestPlace(String v2txt) {
        //.....
    }

}
