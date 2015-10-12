package com.dc.cowbird;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class MainControlActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, ListProtocolsFragment.OnFragmentInteractionListener {
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;


    public void onFragmentInteraction(Long id) {

        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.container, ProtocolFragment.newInstance(id))
                .addToBackStack("Protocolo")
                .commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_control);
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (position == 0) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, new ListProtocolsFragment())
                    .commit();
        } else {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                    .commit();
        }

    }


    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;


        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayUseLogoEnabled(true);
//        actionBar.setDisplayShowHomeEnabled(false);
//        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
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
            if (sectionNumber == 0) {
                ListProtocolsFragment fragment = new ListProtocolsFragment();
                return fragment;
            } else {
                PlaceholderFragment fragment = new PlaceholderFragment();
                Bundle args = new Bundle();
                args.putInt(ARG_SECTION_NUMBER, sectionNumber);
                fragment.setArguments(args);
                return fragment;
            }

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main_control, container, false);

            ((TextView) rootView.findViewById(R.id.full_text)).setText(Html.fromHtml(
                    "<p>O AnotaProtocolo permite que você mantenha organizados todos os números de protocolos de operadoras que você tiver.</p><p><b>Organizando protocolos</b><br/>" +
                            "Os seus protocolos recebidos por SMS são automaticamente carregados no aplicativo. Também é possível adicionar novos protocolos manualmente.</p>" +
                            "<p><b>Adicionar Comentários</b><br/>Os protocolos podem ter anotações pessoais para facilitar o seu controle de qual protocolo se refere a qual solicitação.</p>" +
                            "<p><b>Apagar Protocolos</b><br>Caso um protocolo não seja mais importante você pode apagá-lo. Pressione-o por 3 segundos na lista de protocolos para iniciar a remoção.</p>" +
                            "<p><b>Precisa de Ajuda</b><br/>Caso esteja com problemas para utilizar o aplicativo ou gostaria de ter uma nova operadora controlada automaticamente pelo aplicativo acesse estes <a href='https://github.com/rafaelcoutinho/cowbird/issues'>link</a> ou envie um e-mail para: <a href='mailto:anotaprotocolo@gmail.com'>anotaprotocolo@gmail.com</a></p>" +
                            "<hr/><p>Desenvolvido por <a href='https://github.com/DaviRSSilva'>Davi Ribeiro</a> e <a href='https://github.com/rafaelcoutinho'>Rafael Coutinho</a><br>Licenciado sob GPLv2</p>"));
            ((TextView) rootView.findViewById(R.id.full_text)).setMovementMethod(LinkMovementMethod.getInstance());
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainControlActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
