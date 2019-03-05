package monterrosa.ricardo.aprendermaps;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import monterrosa.ricardo.aprendermaps.FramentTutorial.ChatFragment;
import monterrosa.ricardo.aprendermaps.FramentTutorial.ColectorFragment;
import monterrosa.ricardo.aprendermaps.FramentTutorial.Configuracion2Fragment;
import monterrosa.ricardo.aprendermaps.FramentTutorial.ConfiguracionFragment;
import monterrosa.ricardo.aprendermaps.FramentTutorial.HuellaFragment;
import monterrosa.ricardo.aprendermaps.FramentTutorial.Informacion2Fragment;
import monterrosa.ricardo.aprendermaps.FramentTutorial.InformacionFragment;
import monterrosa.ricardo.aprendermaps.FramentTutorial.InicioFragment;
import monterrosa.ricardo.aprendermaps.FramentTutorial.IratrampainspectorFragment;
import monterrosa.ricardo.aprendermaps.FramentTutorial.MapaFragment;
import monterrosa.ricardo.aprendermaps.FramentTutorial.ModificaringresoFragment;
import monterrosa.ricardo.aprendermaps.FramentTutorial.PerfilFragment;
import monterrosa.ricardo.aprendermaps.FramentTutorial.Trampa2Fragment;
import monterrosa.ricardo.aprendermaps.FramentTutorial.TrampaFragment;
import monterrosa.ricardo.aprendermaps.FramentTutorial.VisitasFragment;
import monterrosa.ricardo.aprendermaps.FramentTutorial.inspeccionesinspectorFragment;
import monterrosa.ricardo.aprendermaps.FramentTutorial.mapainspectorFragment;
import monterrosa.ricardo.aprendermaps.FramentTutorial.menuFragment;
import monterrosa.ricardo.aprendermaps.FramentTutorial.menuinspectorFragment;
import monterrosa.ricardo.aprendermaps.FramentTutorial.visitasinspectorFragment;
import monterrosa.ricardo.aprendermaps.Inspector.InspectorActivity;

public class TutorialInspectorActivity extends AppCompatActivity implements   InicioFragment.OnFragmentInteractionListener,menuinspectorFragment.OnFragmentInteractionListener,mapainspectorFragment.OnFragmentInteractionListener,IratrampainspectorFragment.OnFragmentInteractionListener,visitasinspectorFragment.OnFragmentInteractionListener,inspeccionesinspectorFragment.OnFragmentInteractionListener,PerfilFragment.OnFragmentInteractionListener,ModificaringresoFragment.OnFragmentInteractionListener,ChatFragment.OnFragmentInteractionListener,HuellaFragment.OnFragmentInteractionListener{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_inspector);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
              startActivity(new Intent(TutorialInspectorActivity.this, InspectorActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static Fragment newInstance(int sectionNumber) {
            Fragment fragment = null;
            switch (sectionNumber){
                case 1:
                    fragment = new InicioFragment();
                    break;
                case 2:
                    fragment = new menuinspectorFragment();
                    break;
                case 3:
                    fragment = new mapainspectorFragment();
                    break;
                case 4:
                    fragment = new IratrampainspectorFragment();
                    break;
                case 5:
                    fragment = new visitasinspectorFragment();
                    break;
                case 6:
                    fragment = new inspeccionesinspectorFragment();
                    break;
                case 7:
                    fragment = new PerfilFragment();
                    break;
                case 8:
                    fragment = new ModificaringresoFragment();
                    break;
                case 9:
                    fragment = new ChatFragment();
                    break;
                case 10:
                    fragment = new HuellaFragment();
                    break;
            }
            Bundle args = new Bundle();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_tutorial_inspector, container, false);
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 10 total pages.
            return 10;
        }
    }
}
