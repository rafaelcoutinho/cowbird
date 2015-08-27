package com.dc.cowbird;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.dc.cowbird.provider.ContentConstants;
import com.dc.cowbird.service.CrawlSMSInbox;


public class MainActivity extends Activity implements View.OnClickListener {

    //  GUI Widget
    Button btnInbox;

    ListView lvMsg;

    // Cursor Adapter
    ListAdapter adapter;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init GUI Widget
        btnInbox = (Button) findViewById(R.id.btnInbox);
        btnInbox.setOnClickListener(this);

        CrawlSMSInbox.startCrawlingSMSInbox(getApplicationContext());
        lvMsg = (ListView) findViewById(R.id.lvMsg);

    }

    @Override
    public void onClick(View v) {

        if (v == btnInbox) {
            // Get Content Resolver object, which will deal with Content Provider
            ContentResolver cr = getContentResolver();

            // Fetch Inbox SMS Message from Built-in Content Provider
            Cursor c = cr.query(ContentConstants.ProtocolURLs.URLProtocol.asURL(), null, null, null, null);
            adapter = new SimpleCursorAdapter(this, R.layout.row, c,
                    new String[]{"date", "number", "operator"}, new int[]{
                    R.id.lblDate,
                    R.id.lblNumber, R.id.lblOperator});


            lvMsg.setAdapter(adapter);

        }


    }
}