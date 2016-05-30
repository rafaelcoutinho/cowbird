package com.dc.cowbird;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dc.cowbird.provider.ContentConstants;
import com.dc.cowbird.vo.Protocol;

import java.text.DateFormat;
import java.util.Date;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class ListProtocolsFragment extends Fragment implements AbsListView.OnItemClickListener, AdapterView.OnItemLongClickListener {


    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private ListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private MyCursorAdapter mAdapter;
    private Cursor c;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ListProtocolsFragment() {
    }

    // TODO: Rename and change types of parameters
    public static ListProtocolsFragment newInstance() {
        ListProtocolsFragment fragment = new ListProtocolsFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ContentResolver cr = getActivity().getContentResolver();

        // Fetch Inbox SMS Message from Built-in Content Provider
//        c = cr.query(ContentConstants.ProtocolURLs.URLProtocol.asURL(), null, null, null, "date desc");
//        mAdapter = new MyCursorAdapter(getActivity(), c, false);

        updateAdapter(PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(FILTER_PREF_NAME, true));
        setHasOptionsMenu(true);


    }

    private static final String FILTER_PREF_NAME = "filterOn";
    private FloatingActionButton mFloatingBtn;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_show_all) {
            item.setChecked(!item.isChecked());
            boolean includeCompleted = item.isChecked();
            updateAdapter(includeCompleted);
            PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean(FILTER_PREF_NAME, includeCompleted).commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateAdapter(boolean includeCompleted) {
        ContentResolver cr = getActivity().getContentResolver();
        String filter = null;
        if (!includeCompleted) {
            filter = "complete=0";
        }
        Cursor c = cr.query(ContentConstants.ProtocolURLs.URLProtocol.asURL(), null, filter, null, "date desc");
        if (mAdapter == null) {
            mAdapter = new MyCursorAdapter(getActivity(), c, true);
        } else {
            mAdapter.swapCursor(c);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item, container, false);

        // Set the adapter
        mListView = (ListView) view.findViewById(android.R.id.list);
        mListView.addHeaderView(inflater.inflate(R.layout.row_title, null));
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks

        mListView.setOnItemClickListener(this);
        mListView.setEmptyView(view.findViewById(R.id.lblEmpty));

        mListView.setOnItemLongClickListener(this);
        mFloatingBtn = (FloatingActionButton) view.findViewById(R.id.btnNew);
        mFloatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getFragmentManager().beginTransaction()
                        .replace(R.id.container, new ProtocolFragment()).addToBackStack("NewFrag")
                        .commit();

            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(id);
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_main, menu);


        menu.findItem(R.id.action_show_all).setChecked(PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(FILTER_PREF_NAME, true));
        Cursor c = getContext().getContentResolver().query(ContentConstants.ProtocolURLs.URLProtocol.asURL(), null, "complete=1", null, null);
        menu.findItem(R.id.action_show_all).setTitle("Exibir (" + c.getCount() + ") finalizados");
        c.close();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        new AlertDialog.Builder(getActivity()).setTitle("Deletar").setMessage("Tem certeza que deseja deletar este protocolo?").setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Cursor c = ((MyCursorAdapter) ((HeaderViewListAdapter) mListView.getAdapter()).getWrappedAdapter()).getCursor();
                c.moveToPosition(position - 1);
                Protocol protocol = new Protocol(c);
                getActivity().getContentResolver().delete(ContentConstants.ProtocolURLs.URLProtocol.asURL(), "_id=?", new String[]{String.valueOf(protocol.getId())});
                mAdapter.notifyDataSetChanged();
                mListView.deferNotifyDataSetChanged();
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //NAO FAZ NADA
            }
        }).show();

        return true;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Long id);
    }

    class MyCursorAdapter extends CursorAdapter {
        public MyCursorAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            return getActivity().getLayoutInflater().inflate(R.layout.row, null);
        }

        @Override
        public Object getItem(int position) {
            return super.getItem(position);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            Protocol p = new Protocol(cursor);
            ((TextView) view.findViewById(R.id.lblOperator)).setText("");
            int iconRes = Protocol.getIcon(p.getOperator());
            if (p.isComplete()) {

                view.findViewById(R.id.doneIV).setVisibility(View.VISIBLE);
            } else {
                view.findViewById(R.id.doneIV).setVisibility(View.INVISIBLE);
            }
            if (iconRes != -1) {
                try {
                    ((ImageView) view.findViewById(R.id.imageView)).setImageResource(iconRes);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {

                ((ImageView) view.findViewById(R.id.imageView)).setImageResource(R.mipmap.ic_blank);
                String operator = p.getOperator();
                if (operator.length() > 5) {
                    operator = operator.substring(0, 4) + "..";
                }
                ((TextView) view.findViewById(R.id.lblOperator)).setText(operator);
            }
            ((TextView) view.findViewById(R.id.lblDate)).setText(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new Date(p.getDate())));

            if (p.hasObservations()) {
                ((TextView) view.findViewById(R.id.lblObservation)).setVisibility(View.VISIBLE);
                String obs = p.getObs();
                if (obs.length() > 30) {
                    obs = obs.substring(0, 30) + "...";
                }
                ((TextView) view.findViewById(R.id.lblObservation)).setText(obs);
            } else {
                ((TextView) view.findViewById(R.id.lblObservation)).setVisibility(View.GONE);
            }
            String number = p.getNumber();
            if (number.length() > 20) {
                number = number.substring(0, 20) + "..";
            }
            if (p.isWasSeen()) {
                ((TextView) view.findViewById(R.id.lblNumber)).setText(number);
            } else {
                ((TextView) view.findViewById(R.id.lblNumber)).setText(Html.fromHtml("<b>" + number + "</b>"));
            }


        }
    }

}
