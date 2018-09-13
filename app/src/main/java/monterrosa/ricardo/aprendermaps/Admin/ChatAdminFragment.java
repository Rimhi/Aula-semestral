package monterrosa.ricardo.aprendermaps.Admin;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import monterrosa.ricardo.aprendermaps.ModeloRegistro;
import monterrosa.ricardo.aprendermaps.Modelochat;
import monterrosa.ricardo.aprendermaps.R;

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
    private LinearLayout layout;
    private RelativeLayout layout_2;
    private ImageView enviar;
    private EditText messageArea;
    private ScrollView scrollView;
    private DatabaseReference Chat;
    private DatabaseReference Chatleer;
    private DatabaseReference databaseReference;
    private DatabaseReference usuarios;
    private FirebaseAuth auth;
    private Spinner sujetoschat;
    private ArrayList<String> lista;
    private ArrayAdapter adapterSpiner;
    private String Nombre,id;
    private int Contador = 0;
    private  int contando = Contador;


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
        if (null == savedInstanceState){
            Log.v(TAG_CICLO_VIDA, "onCreate, contador es null");
            Contador =0;
        }else {
            contando = savedInstanceState.getInt("Contador",0);
            Log.v(TAG_CICLO_VIDA, "onCreate, contador"+savedInstanceState.getInt("Contador",0));
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState == null){
            Log.v(TAG_CICLO_VIDA, "onActivityCreated, contador es null");
            Contador =0;
        }else {
            contando = savedInstanceState.getInt("Contador",0);
            Log.v(TAG_CICLO_VIDA, "onActivityCreated, contador"+savedInstanceState.getInt("Contador",0));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_admin, container, false);
        if (null == savedInstanceState){
            Log.v(TAG_CICLO_VIDA, "onCreateView, contador es null");
            Contador =0;
        }else {
            contando = savedInstanceState.getInt("Contador",0);
            Log.v(TAG_CICLO_VIDA, "onCreateView, contador"+savedInstanceState.getInt("Contador",0));
        }
        layout = view.findViewById(R.id.layout1);
        layout_2 = view.findViewById(R.id.layout2);
        enviar = view.findViewById(R.id.sendButton);
        messageArea = view.findViewById(R.id.messageArea);
        scrollView = view.findViewById(R.id.scrollViewchat);
        sujetoschat = view.findViewById(R.id.sujetoschat);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        usuarios = databaseReference.child("Usuarios");
        usuarios.addChildEventListener(listenerusuarios);
        Chat = databaseReference.child("Chat");
        Chatleer = databaseReference.child("Chat").child(auth.getCurrentUser().getUid());
        Chatleer.addChildEventListener(listnerchat);

        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mensage = messageArea.getText().toString();
                if (!mensage.isEmpty()){
                    Modelochat modelochat = new Modelochat(Nombre,mensage);
                    Chat.child(auth.getCurrentUser().getUid()).child(id+"&"+auth.getCurrentUser().getUid()+"Mensaje:"+obtenerserial(20)).setValue(modelochat);
                    Chat.child(id).child(auth.getCurrentUser().getUid()+"&"+id+Nombre+"Mensaje:"+obtenerserial(20)).setValue(modelochat);
                    messageArea.setText("");
                    contando ++;
                    Log.e("contaor",contando+"");
                }
            }
        });


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
            if (!auth.getCurrentUser().getUid().equals(modeloRegistro.IDguidDatabase)){
                lista = new ArrayList<>();
                lista.add(modeloRegistro.Nombre);
                adapterSpiner = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,lista);
                sujetoschat.setAdapter(adapterSpiner);
                    if (sujetoschat.getSelectedItem().toString().equals(modeloRegistro.Nombre)) {
                        Nombre = modeloRegistro.Nombre;
                        id = modeloRegistro.IDguidDatabase;
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
    ChildEventListener listnerchat = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Modelochat modelochat = dataSnapshot.getValue(Modelochat.class);
            String message = modelochat.chatWith;
            String userName = modelochat.username;
            if (userName!= null) {
                if (userName.equals(Nombre)) {
                    addMessageBox("You:-\n" + message, 1);
                } else {
                    addMessageBox(Nombre + ":-\n" + message, 2);
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
    public void addMessageBox(String message, int type){
        if (getContext()!=null) {
            TextView textView = new TextView(getContext());
            textView.setText(message);

            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp2.weight = 1.0f;

            if (type == 1) {
                lp2.gravity = Gravity.LEFT;
                textView.setBackgroundResource(R.drawable.ic_chat_derecha);
                textView.setGravity(View.TEXT_ALIGNMENT_CENTER);

            } else {
                lp2.gravity = Gravity.RIGHT;
                textView.setBackgroundResource(R.drawable.ic_chat_izquierda);
                textView.setGravity(View.TEXT_ALIGNMENT_CENTER);
            }
            textView.setLayoutParams(lp2);
            layout.addView(textView);
            scrollView.fullScroll(View.FOCUS_DOWN);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Contador = contando;
    }

    @Override
    public void onResume() {
        super.onResume();
        Contador = contando;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (null == savedInstanceState){
            Log.v(TAG_CICLO_VIDA, "onViewStateRestored, contador es null");
            Contador =0;
        }else {
            contando = savedInstanceState.getInt("Contador",0);
            Log.v(TAG_CICLO_VIDA, "onViewStateRestored, contador"+savedInstanceState.getInt("Contador",0));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Contador = contando;

    }

    @Override
    public void onStop() {
        super.onStop();
        Contador = contando;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("Contador",contando);
        super.onSaveInstanceState(outState);
        Log.e(TAG_CICLO_VIDA, "onSaveInstanceState, contador "+outState.getInt("Contador",0));
    }
    String obtenerserial(int n){
        String serial="";
        for (int i =1; i<=n;i++){

            double num = (Math.random() * 12)+2;
            int numero = (int) num;
            switch (numero){
                case 10:
                    serial+="A";
                    break;
                case 11:
                    serial+="B";
                    break;
                case 12:
                    serial+="C";
                    break;
                case 13:
                    serial+="D";
                    break;
                case 14:
                    serial+="E";
                    break;
                case 15:
                    serial+="F";
                    break;
                case 16:
                    serial+="G";
                    break;
                case 17:
                    serial+="H";
                    break;
                case 18:
                    serial+="I";
                    break;
                case 19:
                    serial+="J";
                    break;
                case 20:
                    serial+="K";
                    break;

                default:
                    serial+=numero+"";
                    break;
            }



        }
        return serial;

    }
}
