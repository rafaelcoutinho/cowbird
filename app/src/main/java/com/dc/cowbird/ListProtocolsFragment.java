package com.dc.cowbird;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
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
    private ListAdapter mAdapter;
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
        c = cr.query(ContentConstants.ProtocolURLs.URLProtocol.asURL(), null, null, null, "date desc");
        mAdapter = new MyCursorAdapter(getActivity(), c, true);


    }

    private FloatingActionButton mFloatingBtn;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item, container, false);

        // Set the adapter
        mListView = (ListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks

        mListView.setOnItemClickListener(this);
        mListView.setEmptyView(view.findViewById(R.id.lblEmpty));
        mListView.addHeaderView(inflater.inflate(R.layout.row_title, null));
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
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        new AlertDialog.Builder(getActivity()).setTitle("Deletar").setMessage("Tem certeza que dejesa deletar este protocolo?").setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Cursor c = ((MyCursorAdapter) ((HeaderViewListAdapter) mListView.getAdapter()).getWrappedAdapter()).getCursor();
                c.moveToPosition(position - 1);
                Protocol protocol = new Protocol(c);
                getActivity().getContentResolver().delete(ContentConstants.ProtocolURLs.URLProtocol.asURL(), "_id=?", new String[]{String.valueOf(protocol.getId())});
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
            if ("VIVO".equalsIgnoreCase(p.getOperator())) {
                ((ImageView) view.findViewById(R.id.imageView)).setImageResource(R.mipmap.ic_vivo);
            } else if ("TIM".equalsIgnoreCase(p.getOperator())) {
                ((ImageView) view.findViewById(R.id.imageView)).setImageResource(R.mipmap.ic_tim);
            } else if ("CLARO".equalsIgnoreCase(p.getOperator())) {
                ((ImageView) view.findViewById(R.id.imageView)).setImageResource(R.mipmap.ic_claro);
            } else if ("OI".equalsIgnoreCase(p.getOperator())) {
                ((ImageView) view.findViewById(R.id.imageView)).setImageResource(R.mipmap.ic_oi);
            } else {
                ((ImageView) view.findViewById(R.id.imageView)).setImageDrawable(null);
            }
            ((TextView) view.findViewById(R.id.lblDate)).setText(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new Date(p.getDate())));
            ((TextView) view.findViewById(R.id.lblNumber)).setText(p.getNumber());
            ((TextView) view.findViewById(R.id.lblOperator)).setText(p.getOperator());
        }
    }

}
