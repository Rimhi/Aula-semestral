package monterrosa.ricardo.aprendermaps.Admin;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import monterrosa.ricardo.aprendermaps.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AjustesAdminFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AjustesAdminFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AjustesAdminFragment extends Fragment {
    private EditText correocolector,permitiracceso;
    private DatabaseReference databaseReference,habilitar,inhabilitar;
    private Button btn_inhabilitar,btn_habilitar,btn_acceso;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AjustesAdminFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AjustesAdminFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AjustesAdminFragment newInstance(String param1, String param2) {
        AjustesAdminFragment fragment = new AjustesAdminFragment();
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
        View view = inflater.inflate(R.layout.fragment_ajustes_admin, container, false);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        inhabilitar = databaseReference.child("inhabilitarCorreos");
        habilitar = databaseReference.child("habilitarCorreos");
        correocolector = view.findViewById(R.id.colectorinhabilitado);
        permitiracceso = view.findViewById(R.id.permititacceso);
        btn_inhabilitar = view.findViewById(R.id.inhabilitar);
        btn_inhabilitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inhabilitarcolector();
            }
        });
        btn_acceso = view.findViewById(R.id.acesso);
        btn_acceso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permitirAcceso();
            }
        });
     btn_habilitar = view.findViewById(R.id.habilitar);
     btn_habilitar.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            habilitarColector();
         }
     });

        return  view;
    }
    public void inhabilitarcolector(){
        inhabilitar.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String correo = dataSnapshot.getValue(String.class);
                if (correo.equals(correocolector.getText()+"")){
                    Toast.makeText(getContext(), "El colector ya esta inhabilitado", Toast.LENGTH_SHORT).show();
                }else {
                    inhabilitar.push().setValue(correocolector.getText()+"");
                    Toast.makeText(getContext(), "inhabilitado", Toast.LENGTH_SHORT).show();
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
    public void habilitarColector(){
        inhabilitar.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String correo = dataSnapshot.getValue(String.class);
                if (correo.equals(correocolector.getText()+"")){
                    dataSnapshot.getRef().setValue(null);
                    Toast.makeText(getContext(), "Habilitado", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getContext(), "Ya se encuentra habilitado", Toast.LENGTH_SHORT).show();
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
    public void permitirAcceso(){
        habilitar.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String correo = dataSnapshot.getValue(String.class);
                if (correo.equals(permitiracceso.getText().toString())){
                    Toast.makeText(getContext(), "Ya tiene acceso.", Toast.LENGTH_SHORT).show();
                }
                else {
                    habilitar.push().setValue(permitiracceso.getText().toString());
                    Toast.makeText(getContext(), "Acceso concedido.", Toast.LENGTH_SHORT).show();
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
}
