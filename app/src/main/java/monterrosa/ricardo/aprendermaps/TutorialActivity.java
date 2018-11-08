package monterrosa.ricardo.aprendermaps;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import monterrosa.ricardo.aprendermaps.Admin.AdminActivity;
import monterrosa.ricardo.aprendermaps.FramentTutorial.ChatFragment;
import monterrosa.ricardo.aprendermaps.FramentTutorial.ColectorFragment;
import monterrosa.ricardo.aprendermaps.FramentTutorial.Configuracion2Fragment;
import monterrosa.ricardo.aprendermaps.FramentTutorial.ConfiguracionFragment;
import monterrosa.ricardo.aprendermaps.FramentTutorial.HuellaFragment;
import monterrosa.ricardo.aprendermaps.FramentTutorial.Informacion2Fragment;
import monterrosa.ricardo.aprendermaps.FramentTutorial.InformacionFragment;
import monterrosa.ricardo.aprendermaps.FramentTutorial.InicioFragment;
import monterrosa.ricardo.aprendermaps.FramentTutorial.MapaFragment;
import monterrosa.ricardo.aprendermaps.FramentTutorial.ModificaringresoFragment;
import monterrosa.ricardo.aprendermaps.FramentTutorial.PerfilFragment;
import monterrosa.ricardo.aprendermaps.FramentTutorial.Trampa2Fragment;
import monterrosa.ricardo.aprendermaps.FramentTutorial.TrampaFragment;
import monterrosa.ricardo.aprendermaps.FramentTutorial.VisitasFragment;
import monterrosa.ricardo.aprendermaps.FramentTutorial.menuFragment;

public class TutorialActivity extends AppCompatActivity implements ChatFragment.OnFragmentInteractionListener,ColectorFragment.OnFragmentInteractionListener,Configuracion2Fragment.OnFragmentInteractionListener,ConfiguracionFragment.OnFragmentInteractionListener,HuellaFragment.OnFragmentInteractionListener,InformacionFragment.OnFragmentInteractionListener,Informacion2Fragment.OnFragmentInteractionListener,InicioFragment.OnFragmentInteractionListener,MapaFragment.OnFragmentInteractionListener,menuFragment.OnFragmentInteractionListener,ModificaringresoFragment.OnFragmentInteractionListener,PerfilFragment.OnFragmentInteractionListener,Trampa2Fragment.OnFragmentInteractionListener,TrampaFragment.OnFragmentInteractionListener,VisitasFragment.OnFragmentInteractionListener{

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
        setContentView(R.layout.activity_tutorial);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TutorialActivity.this, AdminActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                /*
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tutorial, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                    fragment = new menuFragment();
                    break;
                case 3:
                    fragment = new MapaFragment();
                    break;
                case 4:
                    fragment = new TrampaFragment();
                    break;
                case 5:
                    fragment = new Trampa2Fragment();
                    break;
                case 6:
                    fragment = new InformacionFragment();
                    break;
                case 7:
                    fragment = new Informacion2Fragment();
                    break;
                case 8:
                    fragment = new VisitasFragment();
                    break;
                case 9:
                    fragment = new ColectorFragment();
                    break;
                case 10:
                    fragment = new PerfilFragment();
                    break;
                case 11:
                    fragment = new ModificaringresoFragment();
                    break;
                case 12:
                    fragment = new ChatFragment();
                    break;
                case 13:
                    fragment = new ConfiguracionFragment();
                    break;
                case 14:
                    fragment = new Configuracion2Fragment();
                    break;
                case 15:
                    fragment = new HuellaFragment();
                    break;
            }
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);

            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_tutorial, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
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
        public CharSequence getPageTitle(int position) {
            return super.getPageTitle(position);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 15 total pages.
            return 15;
        }
    }
}
