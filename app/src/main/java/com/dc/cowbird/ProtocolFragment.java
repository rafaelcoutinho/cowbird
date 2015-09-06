package com.dc.cowbird;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dc.cowbird.provider.ContentConstants;
import com.dc.cowbird.vo.Protocol;

import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * <p/>
 * to handle interaction events.
 * Use the {@link ProtocolFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProtocolFragment extends android.support.v4.app.Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "paramId";
    Protocol protocol;
    private Long mParam1;


    public ProtocolFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProtocolFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProtocolFragment newInstance(Long idProtocol) {
        ProtocolFragment fragment = new ProtocolFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM1, idProtocol);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getLong(ARG_PARAM1);

        }
    }

    @Override
    public void onDestroyView() {

        if (protocol != null) {
            ContentValues cv = new ContentValues();
            cv.put("obs", ((TextView) getView().findViewById(R.id.etObs)).getText().toString().trim());
            getActivity().getContentResolver().update(ContentConstants.ProtocolURLs.URLProtocol.asURL(), cv, "_id=?", new String[]{mParam1.toString()});
        }
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_protocol, container, false);
        if (mParam1 != null) {
            Cursor c = getActivity().getContentResolver().query(ContentConstants.ProtocolURLs.URLProtocol.asURL(), null, "_id=?", new String[]{mParam1.toString()}, null);
            if (c.moveToFirst()) {
                protocol = new Protocol(c);
                String data = DateFormat.getDateFormat(getActivity()).format(new Date(protocol.getDate()));
                String hora = DateFormat.getTimeFormat(getActivity()).format(new Date(protocol.getDate()));
                if (protocol.getOperator().equals("OI")) {
                    ((ImageView) v.findViewById(R.id.ic_operadora)).setImageResource(R.mipmap.ic_oi);
                } else if (protocol.getOperator().equals("TIM")) {
                    ((ImageView) v.findViewById(R.id.ic_operadora)).setImageResource(R.mipmap.ic_tim);
                } else if (protocol.getOperator().equals("CLARO")) {
                    ((ImageView) v.findViewById(R.id.ic_operadora)).setImageResource(R.mipmap.ic_claro);
                } else if (protocol.getOperator().equals("VIVO")) {
                    ((ImageView) v.findViewById(R.id.ic_operadora)).setImageResource(R.mipmap.ic_vivo);
                } else {
                    ((ImageView) v.findViewById(R.id.ic_operadora)).setImageDrawable(null);
                }
                ((TextView) v.findViewById(R.id.dateTV)).setText(data + " " + hora);
                ((TextView) v.findViewById(R.id.etNumber)).setText(protocol.getNumber());
                ((TextView) v.findViewById(R.id.etOperadora)).setText(protocol.getOperator());
                ((TextView) v.findViewById(R.id.etObs)).setText(protocol.getObs());
            } else {
                ((TextView) v.findViewById(R.id.dateTV)).setText("");
                ((TextView) v.findViewById(R.id.etNumber)).setText("");
                ((TextView) v.findViewById(R.id.etOperadora)).setText("");
                ((TextView) v.findViewById(R.id.etObs)).setText("");
                ((EditText) v.findViewById(R.id.etNumber)).setEnabled(true);
                ((EditText) v.findViewById(R.id.etOperadora)).setEnabled(true);
                ((EditText) v.findViewById(R.id.etObs)).setEnabled(true);
            }
            c.close();
        }
        return v;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }


}
