package monterrosa.ricardo.aprendermaps.Admin;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import monterrosa.ricardo.aprendermaps.ModeloRegistro;
import monterrosa.ricardo.aprendermaps.Modelochat;
import monterrosa.ricardo.aprendermaps.R;
import monterrosa.ricardo.aprendermaps.adapters.AdapterMensaje;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChatAdminFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChatAdminFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatAdminFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private EditText messageArea;
    private DatabaseReference usuarios;
    private FirebaseAuth auth;
    private Spinner sujetoschat;

    private ArrayAdapter adapterSpiner;
    private String Nombre,id;
    private RecyclerView listamensajes;
    private List<Modelochat> mensaje = new ArrayList<>();
    private AdapterMensaje adapterMensaje;
    private ImageView enviar;
    private EditText escribirmensaje;
    private DatabaseReference databaseReference;
    private DatabaseReference chat;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private String TAG_CICLO_VIDA = "ciclovida";

    public ChatAdminFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatAdminFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatAdminFragment newInstance(String param1, String param2) {
        ChatAdminFragment fragment = new ChatAdminFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_admin, container, false);

        enviar = view.findViewById(R.id.sendButton);
        messageArea = view.findViewById(R.id.messageArea);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        usuarios = databaseReference.child("Usuarios");
        usuarios.addChildEventListener(listenerusuarios);
        enviar = view.findViewById(R.id.sendButton);
        escribirmensaje = view.findViewById(R.id.messageArea);
        listamensajes = view.findViewById(R.id.chat);
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        listamensajes.setLayoutManager(lm);

        adapterMensaje = new AdapterMensaje(mensaje,getContext());
        listamensajes.setAdapter(adapterMensaje);
        setScrollchat();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        chat = databaseReference.child("Chat");

        escribirmensaje.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                setScrollchat();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        eliminarchat();
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!escribirmensaje.getText().toString().isEmpty() && Nombre!=null) {
                    Crearmensjae(escribirmensaje.getText() + "", ObtenerHora(), Nombre);
                }
            }
        });
        chat.addChildEventListener(listenermensajes);


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    ChildEventListener listenerusuarios = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            ModeloRegistro modeloRegistro = dataSnapshot.getValue(ModeloRegistro.class);
            if (auth.getCurrentUser().getUid().equals(modeloRegistro.IDguidDatabase)){
                Nombre = modeloRegistro.Nombre;
                id = modeloRegistro.IDguidDatabase;


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
    public void Crearmensjae(String Mensaje, String Hora, String Nombre) {
        Modelochat modelochat = new Modelochat();
        modelochat.setMensaje(Mensaje);
        modelochat.setHoramensaje(Hora);
        modelochat.setNombre(Nombre);
        modelochat.setId(auth.getCurrentUser().getUid());
        chat.push().setValue(modelochat);
        escribirmensaje.setText("");
        setScrollchat();

    }

    public void setScrollchat() {
        listamensajes.scrollToPosition(adapterMensaje.getItemCount() - 1);
    }

    ChildEventListener listenermensajes = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Modelochat modelochat = dataSnapshot.getValue(Modelochat.class);
            mensaje.add(modelochat);
            adapterMensaje.notifyDataSetChanged();
            setScrollchat();

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
    private String ObtenerHora(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        String Hora = "";
        Calendar c = Calendar.getInstance();
        Hora = simpleDateFormat.format(c.getTime())+"";
        return Hora;
    }
    private void eliminarchat(){
        if (mensaje.size()>=300){
            chat.setValue(null);
        }
    }
}
