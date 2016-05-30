package com.dc.cowbird;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
        setHasOptionsMenu(true);
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
            setHasOptionsMenu(true);
        }

    }

    @Override
    public void onDestroyView() {

        if (protocol != null) {
            ContentValues cv = new ContentValues();
            cv.put("obs", ((TextView) getView().findViewById(R.id.etObs)).getText().toString().trim());
            if (!protocol.isAuto()) {
                cv.put("number", ((TextView) getView().findViewById(R.id.etNumber)).getText().toString().trim());
                cv.put("date", protocolDate.getTimeInMillis());
                cv.put("operator", ((TextView) getView().findViewById(R.id.etOperadora)).getText().toString().trim().toUpperCase());
            }
            cv.put("obs", ((TextView) getView().findViewById(R.id.etObs)).getText().toString().trim());
            cv.put("complete", protocol.isComplete());
            cv.put("completeDate", protocol.getCompleteDate());


            getActivity().getContentResolver().update(ContentConstants.ProtocolURLs.URLProtocol.asURL(), cv, "_id=?", new String[]{mParam1.toString()});
            Cursor c = null;
            Protocol newProtocol = null;
            try {
                c = getActivity().getContentResolver().query(ContentConstants.ProtocolURLs.URLProtocol.asURL(), null, "_id=?", new String[]{String.valueOf(protocol.getId())}, null);
                if (c.moveToFirst()) {
                    newProtocol = new Protocol(c);
                }
            } finally {
                c.close();
            }
            if (!protocol.equals(newProtocol)) {
                Toast.makeText(getActivity(), "Protocolo atualizado", Toast.LENGTH_SHORT).show();
            }
        } else {


            String number = ((TextView) getView().findViewById(R.id.etNumber)).getText().toString().trim();
            if (number.length() > 0) {

                Protocol p = new Protocol(number, ((TextView) getView().findViewById(R.id.etOperadora)).getText().toString().trim().toUpperCase(), protocolDate.getTimeInMillis(), "");
                p.setAuto(false);
                p.setIsSeen();
                try {
                    Uri inserted = getActivity().getContentResolver().insert(ContentConstants.ProtocolURLs.URLProtocol.asURL(), p.toContentValues());

                    mParam1 = Long.valueOf(inserted.getLastPathSegment());
                    protocol = getById(mParam1);
                    Toast.makeText(getActivity(), "Protocolo salvo", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        super.onDestroyView();
    }

    private Protocol getById(Long id) {
        Protocol localProtocol = null;


        Cursor c = null;
        try

        {
            c = getActivity().getContentResolver().query(ContentConstants.ProtocolURLs.URLProtocol.asURL(), null, "_id=?", new String[]{id.toString()}, null);
            if (c.moveToFirst()) {
                localProtocol = new Protocol(c);
            }
        } finally

        {
            if (c != null) {
                c.close();
            }
        }

        return localProtocol;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 333) {
            if (protocol != null) {
                protocol.setComplete(true);
                Toast.makeText(getActivity(), "Protocolo finalizado", Toast.LENGTH_SHORT).show();
                updateSuccessSection(getView());
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (protocol != null) {
            menu.add(0, 333, 0, "Finalizar");
        }
        super.onCreateOptionsMenu(menu, inflater);
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

    String obs = null;
    TextView obsTV = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_protocol, container, false);
        ((TextView) v.findViewById(R.id.etObs)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.support.v4.app.Fragment f = MyDialogFragment.newInstance(((TextView) view).getText().toString());
                f.setTargetFragment(ProtocolFragment.this, 213);
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, f).addToBackStack("Protocol")
                        .commit();
            }
        });
        obsTV = ((TextView) v.findViewById(R.id.etObs));

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
                    int iconRes = Protocol.getIcon(protocol.getOperator());
                    if (iconRes != -1) {
                        ((ImageView) v.findViewById(R.id.ic_operadora)).setImageResource(iconRes);
                    } else {
                        ((ImageView) v.findViewById(R.id.ic_operadora)).setImageDrawable(null);
                    }

                    updateDate(v, protocol.getDate());
                    protocolDate.setTimeInMillis(protocol.getDate());
                    ((TextView) v.findViewById(R.id.etNumber)).setText(protocol.getNumber());
                    ((TextView) v.findViewById(R.id.etOperadora)).setText(protocol.getOperator());
                    ((TextView) v.findViewById(R.id.etObs)).setText(protocol.getObs());
                    updateSuccessSection(v);
                } else {
                    ((TextView) v.findViewById(R.id.dateTV)).setText("");
                    ((TextView) v.findViewById(R.id.etNumber)).setText("");
                    ((TextView) v.findViewById(R.id.etOperadora)).setText("");
                    ((TextView) v.findViewById(R.id.etObs)).setText("");
                    ((EditText) v.findViewById(R.id.etNumber)).setEnabled(true);
                    ((EditText) v.findViewById(R.id.etOperadora)).setEnabled(true);
                    ((TextView) v.findViewById(R.id.etObs)).setEnabled(true);
                }
            } finally {
                if (c != null) {
                    c.close();
                }
            }
            if (protocol != null && !protocol.isWasSeen()) {
                protocol.setIsSeen();
                getActivity().getContentResolver().update(ContentConstants.ProtocolURLs.URLProtocol.asURL(), protocol.toContentValues(), "number=?", new String[]{protocol.getNumber()});
            }

        } else {
            updateDate(v, protocolDate.getTimeInMillis());
        }

        if (mParam1 == null || (protocol != null && !protocol.isAuto())) {

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
            textView.setAdapter(adapter);

        }
        if (obs != null) {
            ((TextView) v.findViewById(R.id.etObs)).setText(obs);
        }
        return v;
    }

    private void updateSuccessSection(View v) {
        if (protocol.isComplete()) {
            View view = v.findViewById(R.id.doneSection);
            // Prepare the View for the animation
            view.setTranslationY(-140);
            view.setVisibility(View.VISIBLE);

            view.setAlpha(0.5f);

            // Start the animation
            view.animate().setDuration(1000)
                    .translationY(0)
                    .alpha(1.0f);
            Log.d("A", "H" + view.getHeight());

        }
    }


    public static class MyDialogFragment extends android.support.v4.app.DialogFragment {
        String obs;

        /**
         * Create a new instance of MyDialogFragment, providing "num"
         * as an argument.
         */
        static MyDialogFragment newInstance(String obs) {
            MyDialogFragment f = new MyDialogFragment();

            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putString("obs", obs);

            f.setArguments(args);

            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            obs = getArguments().getString("obs");
            int style = DialogFragment.STYLE_NORMAL, theme = 0;
            setStyle(android.support.v4.app.DialogFragment.STYLE_NO_TITLE, theme);
            // Pick a style based on the num.

//            switch ((mNum-1)%6) {
//                case 1: style = DialogFragment.STYLE_NO_TITLE; break;
//                case 2: style = DialogFragment.STYLE_NO_FRAME; break;
//                case 3: style = DialogFragment.STYLE_NO_INPUT; break;
//                case 4: style = DialogFragment.STYLE_NORMAL; break;
//                case 5: style = DialogFragment.STYLE_NORMAL; break;
//                case 6: style = DialogFragment.STYLE_NO_TITLE; break;
//                case 7: style = DialogFragment.STYLE_NO_FRAME; break;
//                case 8: style = DialogFragment.STYLE_NORMAL; break;
//            }
//            switch ((mNum-1)%6) {
//                case 4: theme = android.R.style.Theme_Holo; break;
//                case 5: theme = android.R.style.Theme_Holo_Light_Dialog; break;
//                case 6: theme = android.R.style.Theme_Holo_Light; break;
//                case 7: theme = android.R.style.Theme_Holo_Light_Panel; break;
//                case 8: theme = android.R.style.Theme_Holo_Light; break;
//            }

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_edit_obs_dialog, container, false);
            EditText tv = (EditText) v.findViewById(R.id.editText);

//            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_S‌​TATE_VISIBLE);
            tv.requestFocus();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            ((TextView) tv).setText(obs);
            ((EditText) tv).setSelection(obs.length());


            return v;
        }

        @Override
        public void onDestroyView() {
            String obs = ((EditText) getView().findViewById(R.id.editText)).getText().toString();
            ((ProtocolFragment) getTargetFragment()).updateText(obs);
            super.onDestroyView();

        }

    }

    public void updateText(String string) {
        this.obs = string;

    }

    String[] OPERATORS = new String[]{"CLARO", "OI", "TIM", "VIVO", "NETCOMBO", "ANATEL", "AZUL LINHAS AEREAS"};

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
