package monterrosa.ricardo.aprendermaps.Admin;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import monterrosa.ricardo.aprendermaps.R;
import monterrosa.ricardo.aprendermaps.adapters.AdapterPermisos;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AjustesAdminFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AjustesAdminFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AjustesAdminFragment extends Fragment {
    private EditText correocolector, permitiracceso;
    private DatabaseReference databaseReference, habilitar, inhabilitar,usuario_final;
    private Button btn_inhabilitar, btn_habilitar, btn_acceso;
    private RecyclerView colectoresinhabilitados, colectoreshabilitados;
    private ArrayList<String> inhabilitados = new ArrayList<String>();
    private ArrayList<String> habilitados = new ArrayList<String>();
    private boolean verdad1 = false;
    private boolean verdad2 = false;
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
        usuario_final = databaseReference.child("usuario_final");
        correocolector = view.findViewById(R.id.colectorinhabilitado);
        permitiracceso = view.findViewById(R.id.permititacceso);
        btn_inhabilitar = view.findViewById(R.id.inhabilitar);
        colectoresinhabilitados = view.findViewById(R.id.ColectoresInhabiitados);
        colectoreshabilitados = view.findViewById(R.id.Colectoreshabilitados);
        colectoresinhabilitados.setHasFixedSize(true);
        colectoreshabilitados.setHasFixedSize(true);
        mostrar();
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

        return view;
    }

    public void inhabilitarcolector() {
        inhabilitar.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.e("entro a inhabilitados", "adentro we");
                String correo = dataSnapshot.getValue(String.class);
                if (correo.equals(correocolector.getText() + "")) {
                    Toast.makeText(getContext(), "El colector ya esta inhabilitado", Toast.LENGTH_SHORT).show();
                } else {
                    verdad1 = true;
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
        if (verdad1) {
            inhabilitar.push().setValue(correocolector.getText() + "");
            Toast.makeText(getContext(), "inhabilitado", Toast.LENGTH_SHORT).show();
        }

    }

    public void habilitarColector() {
        inhabilitar.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.e("entro a habilitados", "adentro we");
                String correo = dataSnapshot.getValue(String.class);
                if (correo.equals(correocolector.getText() + "")) {
                    dataSnapshot.getRef().setValue(null);
                    Toast.makeText(getContext(), "Habilitado", Toast.LENGTH_SHORT).show();
                } else {
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

    public void permitirAcceso() {
        habilitar.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String correo = dataSnapshot.getValue(String.class);
                if (correo.equals(permitiracceso.getText().toString())) {
                    Toast.makeText(getContext(), "Ya tiene acceso.", Toast.LENGTH_SHORT).show();
                } else {
                    verdad2 = true;
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
        if (verdad2) {
            habilitar.push().setValue(permitiracceso.getText().toString());
            usuario_final.push().setValue(permitiracceso.getText().toString());
            Toast.makeText(getContext(), "Acceso concedido.", Toast.LENGTH_SHORT).show();
        }
    }
    private void mostrar(){
        inhabilitar.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                inhabilitados.add(dataSnapshot.getValue(String.class));
                colectoresinhabilitados.setAdapter(new AdapterPermisos(inhabilitados));
                colectoresinhabilitados.setLayoutManager(new LinearLayoutManager(getContext()));

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
        habilitar.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                habilitados.add(dataSnapshot.getValue(String.class));
                colectoreshabilitados.setAdapter(new AdapterPermisos(habilitados));
                colectoreshabilitados.setLayoutManager(new LinearLayoutManager(getContext()));
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
