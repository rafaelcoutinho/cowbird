package com.dc.cowbird;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.dc.cowbird.parser.SMSParserFactory;
import com.dc.cowbird.vo.Protocol;

import java.util.ArrayList;
import java.util.List;


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


        lvMsg = (ListView) findViewById(R.id.lvMsg);

    }

    @Override
    public void onClick(View v) {

        if (v == btnInbox) {

            // Create Inbox box URI
            Uri inboxURI = Uri.parse("content://sms/inbox");

            // List required columns
            String[] reqCols = new String[]{"_id", "address", "body", "date", "subject"};

            // Get Content Resolver object, which will deal with Content Provider
            ContentResolver cr = getContentResolver();

            // Fetch Inbox SMS Message from Built-in Content Provider
            Cursor c = cr.query(inboxURI, reqCols, null, null, null);


            List<String> protocols = new ArrayList<String>();
            if (c.moveToFirst()) {
                do {
                    Protocol parser = SMSParserFactory.getInstance(c);
                    if (parser != null) {
                        protocols.add(parser.toString());
                    }
                } while (c.moveToNext());
            }
            // Attached Cursor with adapter and display in listview
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, protocols.toArray(new String[protocols.size()]));

            lvMsg.setAdapter(adapter);

        }


    }
}