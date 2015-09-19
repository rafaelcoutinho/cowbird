package com.dc.cowbird;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.dc.cowbird.provider.ContentConstants;
import com.dc.cowbird.vo.Protocol;

import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * <p/>
 * to handle interaction events.
 * Use the {@link ProtocolFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProtocolFragment extends android.support.v4.app.Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

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
            if (!protocol.isAuto()) {
                cv.put("number", ((TextView) getView().findViewById(R.id.etNumber)).getText().toString().trim());
                cv.put("obs", ((TextView) getView().findViewById(R.id.etObs)).getText().toString().trim());
                cv.put("operator", ((TextView) getView().findViewById(R.id.etOperadora)).getText().toString().trim().toUpperCase());
            }
            getActivity().getContentResolver().update(ContentConstants.ProtocolURLs.URLProtocol.asURL(), cv, "_id=?", new String[]{mParam1.toString()});
            Toast.makeText(getActivity(), "Protocolo atualizado", Toast.LENGTH_SHORT).show();
        } else {


            String number = ((TextView) getView().findViewById(R.id.etNumber)).getText().toString().trim();
            if (number.length() > 0) {

                Protocol p = new Protocol(number, ((TextView) getView().findViewById(R.id.etOperadora)).getText().toString().trim().toUpperCase(), protocolDate.getTimeInMillis(), "");
                p.setAuto(false);
                p.setIsSeen();
                getActivity().getContentResolver().insert(ContentConstants.ProtocolURLs.URLProtocol.asURL(), p.toContentValues());
                Toast.makeText(getActivity(), "Protocolo salvo", Toast.LENGTH_SHORT).show();
            }
        }
        super.onDestroyView();
    }

    public static class DatePickerFragment extends DialogFragment {
        DatePickerDialog.OnDateSetListener listener;

        public static DatePickerFragment newInstance(Calendar c, DatePickerDialog.OnDateSetListener list) {
            DatePickerFragment fragment = new DatePickerFragment();
            Bundle args = new Bundle();
            args.putInt("year", c.get(Calendar.YEAR));
            args.putInt("month", c.get(Calendar.MONTH));
            args.putInt("day", c.get(Calendar.DAY_OF_MONTH));

            fragment.setArguments(args);
            fragment.listener = list;
            return fragment;
        }


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Bundle bundle = getArguments();

            return new DatePickerDialog(getActivity(), listener, bundle.getInt("year"), bundle.getInt("month"), bundle.getInt("day"));
        }


    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        protocolDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
        protocolDate.set(Calendar.MINUTE, minute);
        protocolDate.set(Calendar.SECOND, 0);
        protocolDate.set(Calendar.MILLISECOND, 0);
        updateDate(getView(), protocolDate.getTimeInMillis());
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        protocolDate.set(Calendar.YEAR, year);
        protocolDate.set(Calendar.MONTH, month);
        protocolDate.set(Calendar.DAY_OF_MONTH, day);

        updateDate(getView(), protocolDate.getTimeInMillis());
        TimePickerFragment newFragment = TimePickerFragment.newInstance(protocolDate, ProtocolFragment.this);

        newFragment.show(getActivity().getFragmentManager(), "timePicker");
    }

    Calendar protocolDate = Calendar.getInstance();

    public static class TimePickerFragment extends DialogFragment {
        TimePickerDialog.OnTimeSetListener list;

        public static TimePickerFragment newInstance(Calendar c, TimePickerDialog.OnTimeSetListener list) {
            TimePickerFragment fragment = new TimePickerFragment();
            Bundle args = new Bundle();
            args.putInt("hour", c.get(Calendar.HOUR_OF_DAY));
            args.putInt("minute", c.get(Calendar.MINUTE));


            fragment.setArguments(args);
            fragment.list = list;
            return fragment;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker

            Bundle bundle = getArguments();
            int hour = bundle.getInt("hour");
            int minute = bundle.getInt("minute");

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), list, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }


    }

    private void updateDate(View v, long date) {
        String data = DateFormat.getDateFormat(getActivity()).format(new Date(date));
        String hora = DateFormat.getTimeFormat(getActivity()).format(new Date(date));
        ((TextView) v.findViewById(R.id.dateTV)).setText(data + " " + hora);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_protocol, container, false);
        if (mParam1 != null) {


            Cursor c = null;
            try {
                c = getActivity().getContentResolver().query(ContentConstants.ProtocolURLs.URLProtocol.asURL(), null, "_id=?", new String[]{mParam1.toString()}, null);
                if (c.moveToFirst()) {
                    protocol = new Protocol(c);
                    if (protocol.isAuto()) {
                        ((AutoCompleteTextView) v.findViewById(R.id.etOperadora)).setFocusable(false);
                        ((AutoCompleteTextView) v.findViewById(R.id.etOperadora)).setClickable(false);
                        ((TextView) v.findViewById(R.id.etNumber)).setFocusable(false);
                        ((TextView) v.findViewById(R.id.etNumber)).setClickable(false);

                    }
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
                    updateDate(v, protocol.getDate());
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
            } finally {
                if (c != null) {
                    c.close();
                }
            }
            protocol.setIsSeen();
            getActivity().getContentResolver().update(ContentConstants.ProtocolURLs.URLProtocol.asURL(), protocol.toContentValues(), "number=?", new String[]{protocol.getNumber()});

        }

        if (mParam1 == null || (protocol != null && !protocol.isAuto())) {
            updateDate(v, protocolDate.getTimeInMillis());
            v.findViewById(R.id.etOperadora).setEnabled(true);
            ((TextView) v.findViewById(R.id.etNumber)).setEnabled(true);
            ((TextView) v.findViewById(R.id.dateTV)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatePickerFragment.newInstance(protocolDate, ProtocolFragment.this).show(getActivity().getFragmentManager(), "datePicker");
                }
            });

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_dropdown_item_1line, OPERATORS);
            AutoCompleteTextView textView = (AutoCompleteTextView)
                    v.findViewById(R.id.etOperadora);
            textView.setThreshold(1);
            //TODO make the selection to load from db
//            textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                    View v = getView();
//                    if (OPERATORS[i].equals("OI")) {
//                        ((ImageView) v.findViewById(R.id.ic_operadora)).setImageResource(R.mipmap.ic_oi);
//                    } else if (OPERATORS[i].equals("TIM")) {
//                        ((ImageView) v.findViewById(R.id.ic_operadora)).setImageResource(R.mipmap.ic_tim);
//                    } else if (OPERATORS[i].equals("CLARO")) {
//                        ((ImageView) v.findViewById(R.id.ic_operadora)).setImageResource(R.mipmap.ic_claro);
//                    } else if (OPERATORS[i].equals("VIVO")) {
//                        ((ImageView) v.findViewById(R.id.ic_operadora)).setImageResource(R.mipmap.ic_vivo);
//                    } else {
//                        ((ImageView) v.findViewById(R.id.ic_operadora)).setImageDrawable(null);
//                    }
//                }
//            });
            textView.setAdapter(adapter);

        }
        return v;
    }

    String[] OPERATORS = new String[]{"CLARO", "OI", "TIM", "VIVO"};

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
