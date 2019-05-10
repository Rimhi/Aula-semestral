package monterrosa.ricardo.aprendermaps.Admin;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import monterrosa.ricardo.aprendermaps.ModeloRegistro;
import monterrosa.ricardo.aprendermaps.R;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AdminPerfilFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AdminPerfilFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminPerfilFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private DatabaseReference mibasedatos;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private ImageView editarimagenadmin;
    private String urlimagenadmin,fecharegistroadmin;
    private Button Editarperfiladmin,EditarIngresoAdmin;
    private EditText editarNombreadmin,editarcedulaadmin,editarcelularadmin,editardireccionadmin,editarcorreoelectronicoadmin,editarcontraseña1,editarcontraseña2;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AdminPerfilFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminPerfilFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminPerfilFragment newInstance(String param1, String param2) {
        AdminPerfilFragment fragment = new AdminPerfilFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_perfil_admin, container, false);
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mibasedatos = databaseReference.child("Usuarios");
        editarNombreadmin = view.findViewById(R.id.editarnombreadmin);
        editarcedulaadmin = view.findViewById(R.id.editarcedulaadmin);
        editarcelularadmin = view.findViewById(R.id.editarcelularadmin);
        editardireccionadmin = view.findViewById(R.id.editardireccionadmin);
        editarcorreoelectronicoadmin = view.findViewById(R.id.editarcorreoelectronicoadmin);
        editarcontraseña1 = view.findViewById(R.id.editarcontraseñaadmin);
        editarcontraseña2 = view.findViewById(R.id.editarrepetircontraseñaadmin);
        editarimagenadmin = view.findViewById(R.id.editarfotoadmin);
        Editarperfiladmin = view.findViewById(R.id.Editarpefiladmin);
        EditarIngresoAdmin = view.findViewById(R.id.editaringresoadmin);
        Editarperfiladmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edperfil();
            }
        });
        mibasedatos.addChildEventListener(listener);
        EditarIngresoAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cambiarcontraseña();
            }
        });

        return view;
    }

    ChildEventListener listener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            ModeloRegistro modeloRegistro = dataSnapshot.getValue(ModeloRegistro.class);
            if (auth.getCurrentUser().getUid().equals(modeloRegistro.IDguidDatabase)){
                editarNombreadmin.setText(modeloRegistro.Nombre);
                editarcedulaadmin.setText(modeloRegistro.Cedula);
                editarcelularadmin.setText(modeloRegistro.Telefono);
                editardireccionadmin.setText(modeloRegistro.Direccion);
                urlimagenadmin = modeloRegistro.imagen;
                editarcorreoelectronicoadmin.setText(modeloRegistro.correo);
                fecharegistroadmin = modeloRegistro.fechaRegistro;
                Glide.with(getContext())
                        .load(Uri.parse(modeloRegistro.imagen))
                        .fitCenter()
                        .centerCrop()
                        .into(editarimagenadmin);
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
    private void edperfil(){
        mibasedatos = databaseReference.child("Usuarios").child(auth.getCurrentUser().getUid());
        ModeloRegistro modeloRegistro = new ModeloRegistro(auth.getCurrentUser().getUid()+"",editarNombreadmin.getText()+"",
                editarcedulaadmin.getText()+"",editarcelularadmin.getText()+"",editarcorreoelectronicoadmin.getText()+"",
                editardireccionadmin.getText()+"",urlimagenadmin,fecharegistroadmin);
        mibasedatos.setValue(modeloRegistro);
    }
    private void cambiarcontraseña(){
        if (editarcontraseña1.getText().toString().equals(editarcontraseña2.getText().toString())) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            user.updatePassword(editarcontraseña1.getText()+"")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getContext(),"Contraseña Cambiada Con éxito",Toast.LENGTH_SHORT).show();
                                Log.e(TAG,"Contraseña Cambiada");
                                startActivity(new Intent(getContext(),AdminActivity.class));
                            }else {
                                Toast.makeText(getContext(), "no se pudo modificar la contraseña", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }else {
            Toast.makeText(getContext(), "Contraseñas incorrectas", Toast.LENGTH_SHORT).show();
        }
    }
}
