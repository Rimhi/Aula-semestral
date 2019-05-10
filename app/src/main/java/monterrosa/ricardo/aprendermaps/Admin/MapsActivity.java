package monterrosa.ricardo.aprendermaps.Admin;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
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

import java.util.ArrayList;

import monterrosa.ricardo.aprendermaps.ModeloDarposicion;
import monterrosa.ricardo.aprendermaps.ModeloRegistro;
import monterrosa.ricardo.aprendermaps.R;
import monterrosa.ricardo.aprendermaps.adapters.InspectorInfoWindowAdapter;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker marcador;
    private Marker marcadorinspectores;
    private DatabaseReference baseDatos;
    private DatabaseReference trampaRef;
    private DatabaseReference posciosionesInspector;
    private static final int COD_PERMISOS = 3452;
    private LatLngBounds MONTERIA_CORDOBA_COLOMBIA = new LatLngBounds(new LatLng(8.726606067497622, -75.90802716634767),
            new LatLng(8.934689, -75.820078));
    private AlertDialog dialog;
    private DatabaseReference miBaseDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        pedirPermisosFaltantes();
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
        posciosionesInspector = baseDatos.child("Posiciones");
        posciosionesInspector.addChildEventListener(posicionesinspectorlistener);

        trampaRef = baseDatos.child("trampas");
        trampaRef.addChildEventListener(trampasHijoListener);



        miBaseDatos = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(8.7593615, -75.877503), 15));
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
                builder.setTitle("¿Esta es la posición de una trampa?")
                        .setCancelable(false)
                        .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                LayoutInflater layoutInflater = getLayoutInflater();
                                final View view = layoutInflater.inflate(R.layout.guardarpos,null);
                                final EditText fecha = view.findViewById(R.id.fechaingresotrampa);
                                final EditText idtrampa = view.findViewById(R.id.idtrampa);

                                final EditText lattrampa = view.findViewById(R.id.lattrampa);
                                final EditText lngtrampa = view.findViewById(R.id.lngtrampa);
                                final Button guardarmarcador = view.findViewById(R.id.guardartrampa);
                                final Spinner tipo_trampa = view.findViewById(R.id.Tipo_trampa);

                                lattrampa.setText(latLng.latitude+"");
                                lngtrampa.setText(latLng.longitude+"");
                                guardarmarcador.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();


                                        Trampa trampa = new Trampa(
                                                idtrampa.getText().toString(),
                                                fecha.getText().toString(),
                                                Double.parseDouble(lattrampa.getText()+""),
                                                Double.parseDouble(lngtrampa.getText()+""),
                                                tipo_trampa.getSelectedItem().toString()

                                        );

                                        miBaseDatos.child("trampas").child(idtrampa.getText()+"").setValue(trampa);
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
                /**
                 * Ver contraseña
                 */



            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                final String [] idtrampa= marker.getTitle().split(" ");
                final String [] snippet= marker.getSnippet().split(" ");
               AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
               builder.setTitle("Opciones Marcador")
                       .setNegativeButton("Eliminar Marcador", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {

                               //Toast.makeText(MapsActivity.this, idtrampa[1],Toast.LENGTH_LONG).show();
                             miBaseDatos.child("trampas").child(idtrampa[1]).setValue(null);
                               marker.remove();
                           }
                       }).setPositiveButton("Información", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                       /***
                        * qenviar datos a la actividad informacion de la trampa para su futuro listado
                        */
                       Intent intent = new Intent(getApplicationContext(),InformacionTrampaActivity.class);
                       intent.putExtra("codigotrampa",idtrampa[1]);
                       intent.putExtra("indicio",snippet[1]);
                       intent.putExtra("lat",marker.getPosition().latitude+"");
                       intent.putExtra("lng",marker.getPosition().longitude+"");
                       startActivity(intent);
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
                if(trampa.Tipo_trampa!=null) {
                    if (trampa.Tipo_trampa.equals("Picudo Algodon")) {
                        MarkerOptions markerOptions = new MarkerOptions()
                                .title("Identificación: " + trampa.id)
                                .snippet("Fecha: " + trampa.fechaInicio)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.jail_picudo))
                                .position(new LatLng(trampa.latitud, trampa.longitud));
                        mMap.addMarker(markerOptions);

                }
                    else {
                        MarkerOptions markerOptions = new MarkerOptions()
                                .title("Identificación: " + trampa.id)
                                .snippet("Fecha: " + trampa.fechaInicio)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.jail))
                                .position(new LatLng(trampa.latitud, trampa.longitud));
                        mMap.addMarker(markerOptions);
                }
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
    ChildEventListener posicionesinspectorlistener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            final ModeloDarposicion modeloRegistro = dataSnapshot.getValue(ModeloDarposicion.class);
            if (mMap != null && modeloRegistro !=null){
                    marcadorinspectores = mMap.addMarker(new MarkerOptions()
                            .title("Cédula: " + modeloRegistro.ceduladarposiciontelefonoinspector)
                            .snippet("Nombre: " + modeloRegistro.nombredarposicioninspector)
                            .position(new LatLng(modeloRegistro.latitud, modeloRegistro.longitud))
                            .icon(getBitmapFromVector(MapsActivity.this, R.drawable.ic_person_posicion, getResources().getColor(R.color.Ultimaposicion))));

            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            marcadorinspectores.remove();
            final ModeloDarposicion modeloRegistro = dataSnapshot.getValue(ModeloDarposicion.class);
            if (mMap != null && modeloRegistro !=null){
                marcadorinspectores = mMap.addMarker(new MarkerOptions()
                        .title("Cédula: "+modeloRegistro.ceduladarposiciontelefonoinspector)
                        .snippet("Nombre: "+modeloRegistro.nombredarposicioninspector)
                        .position(new LatLng(modeloRegistro.latitud,modeloRegistro.longitud))
                        .icon(getBitmapFromVector(MapsActivity.this,R.drawable.ic_person_posicion,getResources().getColor(R.color.colorPrimaryDark))));
            }

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
    public static BitmapDescriptor getBitmapFromVector(@NonNull Context context,
                                                       @DrawableRes int vectorResourceId,
                                                       @ColorInt int tintColor) {

        Drawable vectorDrawable = ResourcesCompat.getDrawable(
                context.getResources(), vectorResourceId, null);
        if (vectorDrawable == null) {
            String TAG = "tag";
            Log.e(TAG, "Requested vector resource was not found");
            return BitmapDescriptorFactory.defaultMarker();
        }
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        DrawableCompat.setTint(vectorDrawable, tintColor);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


}
