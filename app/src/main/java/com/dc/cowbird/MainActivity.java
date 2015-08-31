package com.dc.cowbird;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dc.cowbird.provider.ContentConstants;
import com.dc.cowbird.vo.Protocol;

import java.text.DateFormat;
import java.util.Date;


public class MainActivity extends Activity {
    Cursor c = null;

    ListView lvMsg;
    ListAdapter adapter;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (c != null) {
            c.close();
        }
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        lvMsg = (ListView) findViewById(R.id.lvMsg);
        // Get Content Resolver object, which will deal with Content Provider
        ContentResolver cr = getContentResolver();

        // Fetch Inbox SMS Message from Built-in Content Provider
        c = cr.query(ContentConstants.ProtocolURLs.URLProtocol.asURL(), null, null, null, "date desc");
        adapter = new MyCursorAdapter(getApplicationContext(), c, true);


        lvMsg.setAdapter(adapter);
        lvMsg.setEmptyView(findViewById(R.id.lblEmpty));
        lvMsg.addHeaderView(getLayoutInflater().inflate(R.layout.row_title, null));

    }

    class MyCursorAdapter extends CursorAdapter {
        public MyCursorAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            return getLayoutInflater().inflate(R.layout.row, null);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            Protocol p = new Protocol(cursor);
            ((TextView) view.findViewById(R.id.lblDate)).setText(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new Date(p.getDate())));
            ((TextView) view.findViewById(R.id.lblNumber)).setText(p.getNumber());
            ((TextView) view.findViewById(R.id.lblOperator)).setText(p.getOperator());
        }
    }

}