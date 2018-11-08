package monterrosa.ricardo.aprendermaps.Servicios;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import monterrosa.ricardo.aprendermaps.Admin.AdminActivity;
import monterrosa.ricardo.aprendermaps.Admin.ChatAdminFragment;
import monterrosa.ricardo.aprendermaps.Inspector.LlegadaMapa;
import monterrosa.ricardo.aprendermaps.ModeloRegistro;
import monterrosa.ricardo.aprendermaps.Modelochat;
import monterrosa.ricardo.aprendermaps.R;

/**
 * Created by Ricardo Monterrosa H on 12/09/2018.
 */

public class ServiceNotificacion extends Service {
    private DatabaseReference databaseReference;
    private DatabaseReference mibasedatos;
    private NotificationManager notifManager;

    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("Servicio","Iniciado");
        vernotificacion();
        Notificacionmensaje();
        return  START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private String fechaactual(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();

        String fecha = dateFormat.format(date);

        return  fecha;
    }
    public void vernotificacion(){
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mibasedatos = databaseReference.child("Inspecciones");
        mibasedatos.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                LlegadaMapa llegadaMapa = dataSnapshot.getValue(LlegadaMapa.class);
               if (llegadaMapa.Fecha.equals(fechaactual())) {
                   Log.e("entra",llegadaMapa.NombreColector);
                   createNotification(llegadaMapa.NombreColector,"El colector ha hecho una visita",AdminActivity.class);
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
        });
    }
    private String ObtenerHora(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        String Hora = "";
        Calendar c = Calendar.getInstance();
        Hora = simpleDateFormat.format(c.getTime())+"";
        return Hora;
    }
    public void Notificacionmensaje(){
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mibasedatos = databaseReference.child("Chat");
        mibasedatos.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Modelochat llegadaMapa = dataSnapshot.getValue(Modelochat.class);
                if (llegadaMapa.getHoramensaje().equals(ObtenerHora())){
                    if (llegadaMapa.getNombre()!=Obtnernombre()) {
                        Log.e("entra", llegadaMapa.getNombre());
                        createNotification(llegadaMapa.getNombre(), "Tienes nuevos mensajes", ChatAdminFragment.class);
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
        });
    }
    public void createNotification(String aMessage,String Contencontex,Class clase) {
        final int NOTIFY_ID = 0; // ID of notification
        String id = "1234"; // default channel id
        String title ="TITULO"; // default channel
        Intent intent;
        PendingIntent pendingIntent;
        NotificationCompat.Builder builder;
        if (notifManager == null) {
            notifManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notifManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, title, importance);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(this, id);
            intent = new Intent(this, clase);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            builder.setContentTitle(aMessage)  // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder) // required
                    .setContentText(Contencontex)  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(aMessage)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        } else {
            builder = new NotificationCompat.Builder(this, id);
            intent = new Intent(this, clase);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            builder.setContentTitle(aMessage)                           // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder) // required
                    .setContentText(Contencontex)  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(aMessage)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                    .setPriority(Notification.PRIORITY_HIGH);
        }
        Notification notification = builder.build();
        notifManager.notify(NOTIFY_ID, notification);
    }
    public String Obtnernombre(){
        final String[] nombre = {""};
        DatabaseReference getnombre;
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        getnombre =  databaseReference.child("Usuarios");
        getnombre.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ModeloRegistro modeloRegistro = dataSnapshot.getValue(ModeloRegistro.class);
                if (auth.getCurrentUser().getUid().equals(modeloRegistro.IDguidDatabase)){
                    nombre[0] = modeloRegistro.Nombre;
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
        });

        return nombre[0];

    }
}
