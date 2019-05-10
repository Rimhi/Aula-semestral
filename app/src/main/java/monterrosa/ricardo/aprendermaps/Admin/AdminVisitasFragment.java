package monterrosa.ricardo.aprendermaps.Admin;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import monterrosa.ricardo.aprendermaps.Inspector.LlegadaMapa;
import monterrosa.ricardo.aprendermaps.R;
import monterrosa.ricardo.aprendermaps.adapters.FechaInspeccionAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AdminVisitasFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AdminVisitasFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminVisitasFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Spinner decidirquemostrar;
    private RecyclerView listavisitasinspectores;
    private DatabaseReference inspecciones;
    private DatabaseReference databaseReference;
    private FechaInspeccionAdapter adapter;
    private ArrayList<LlegadaMapa> list = new ArrayList<>();
    private ArrayList<LlegadaMapa> listtodos = new ArrayList<>();
    private String [] contenido;
    private ArrayAdapter<String> adaptadorspinner;
    private ProgressDialog progressDialog;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AdminVisitasFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminVisitasFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminVisitasFragment newInstance(String param1, String param2) {
        AdminVisitasFragment fragment = new AdminVisitasFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_visitas, container, false);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        inspecciones = databaseReference.child("Inspecciones");
        decidirquemostrar = view.findViewById(R.id.MostrarVisitasdeinspectores);
        listavisitasinspectores = view.findViewById(R.id.listavisitasinspectores);
        contenido = new String[]{"Todos los tiempos","Hoy"};
        adaptadorspinner = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,contenido);
        decidirquemostrar.setAdapter(adaptadorspinner);
        progressDialog = new ProgressDialog(getContext());
        mostrardatos();
        //mostrartodoslosdatos();


        return view;
    }
    public void mostrartodoslosdatos(){
        list.clear();
        inspecciones.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                final LlegadaMapa llegadaMapa = dataSnapshot.getValue(LlegadaMapa.class);

                    Log.e("hoy","entro a si hay inspecciones");
                    list.add(llegadaMapa);
                    adapter = new FechaInspeccionAdapter(list,getContext());
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    listavisitasinspectores.setLayoutManager(linearLayoutManager);
                    listavisitasinspectores.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    progressDialog.dismiss();

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
    public void mostrardatos(){
        decidirquemostrar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 1:
                        list.clear();
                        inspecciones.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                progressDialog.setMessage("Cargando ...");
                                progressDialog.show();
                                final LlegadaMapa llegadaMapa = dataSnapshot.getValue(LlegadaMapa.class);
                                List<LlegadaMapa>lista = new ArrayList<>();
                                if (!llegadaMapa.Fecha.equals(fechaactual())) {
                                    lista.add(llegadaMapa);
                                    if (lista.size()<=0) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getContext(), "Hoy no hubo inspecciones", Toast.LENGTH_SHORT).show();
                                    }
                                }else {
                                    list.add(llegadaMapa);
                                    adapter = new FechaInspeccionAdapter(list,getContext());
                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                                    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                                    listavisitasinspectores.setLayoutManager(linearLayoutManager);
                                    listavisitasinspectores.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                    progressDialog.dismiss();
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
                        break;
                    case 0:
                        listtodos.clear();
                        inspecciones.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                progressDialog.setMessage("Cargando ...");
                                progressDialog.show();
                                final LlegadaMapa llegadaMapa = dataSnapshot.getValue(LlegadaMapa.class);
                                    listtodos.add(llegadaMapa);
                                    adapter = new FechaInspeccionAdapter(listtodos,getContext());
                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                                    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                                    listavisitasinspectores.setLayoutManager(linearLayoutManager);
                                    listavisitasinspectores.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                    progressDialog.dismiss();
                                    if (listtodos.size() == 0){
                                        Toast.makeText( getContext(),"No hay disponibles, intentalo mas tarde!", Toast.LENGTH_SHORT).show();
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
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search,menu);
        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView)item.getActionView();
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrartodoslosdatos();
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                final List<LlegadaMapa>search = items(list,newText);
                adapter.setFilter(search);
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }
    private List<LlegadaMapa>items(List<LlegadaMapa>item,String query){
        query.toLowerCase();
        final List<LlegadaMapa> filter = new ArrayList<>();

        for (LlegadaMapa model : item){
            final String text = model.Fecha;

            if (text.startsWith(query)){
                filter.add(model);
            }
        }
        return filter;
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
    public String fechaactual(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Date date = new Date();

        String fecha = dateFormat.format(date);

        return  fecha;
    }
}
