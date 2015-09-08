package com.dc.cowbird.service;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.dc.cowbird.Constants;
import com.dc.cowbird.parser.SMSParserFactory;
import com.dc.cowbird.provider.ContentConstants;
import com.dc.cowbird.vo.Protocol;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class CrawlSMSInbox extends IntentService {


    private static final String ACTION_CRAWL_SMS_INBOX = "com.dc.cowbird.service.action.CRAWLSMSINBOX";


    public CrawlSMSInbox() {
        super("CrawlSMSInbox");
    }

    public static void startCrawlingSMSInbox(Context context) {
        Intent intent = new Intent(context, CrawlSMSInbox.class);
        intent.setAction(ACTION_CRAWL_SMS_INBOX);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_CRAWL_SMS_INBOX.equals(action)) {

                crawlSMSInbox();
            }
        }
    }

    private void crawlSMSInbox() {
        Cursor smsCursor = null;

        // Create Inbox box URI
        try {
            Uri inboxURI = Uri.parse("content://sms/inbox");

            // List required columns
            String[] reqCols = new String[]{"_id", "address", "body", "date", "subject"};

            // Get Content Resolver object, which will deal with Content Provider
            ContentResolver cr = getContentResolver();
            Long lastExecution = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getLong("lastCheck", 0);
            Integer versionCode = 0;
            try {
                Integer lastVersion = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("lastVersion", 0);
                versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
                if (lastVersion != versionCode) {
                    lastExecution = 0l;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Fetch Inbox SMS Message from Built-in Content Provider
            smsCursor = cr.query(inboxURI, reqCols, "date>?", new String[]{
                    lastExecution.toString()
            }, null);

            if (smsCursor.moveToFirst()) {
                do {
                    Protocol protocol = SMSParserFactory.getInstance(smsCursor);
                    if (protocol != null) {
                        try {
                            cr.insert(ContentConstants.ProtocolURLs.URLProtocol.asURL(), protocol.toContentValues());
                        } catch (Exception e) {
                            Log.w(Constants.LOG_TAG, "Could not save SMS", e);
                        }
                    }
                } while (smsCursor.moveToNext());
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putLong("lastCheck", System.currentTimeMillis()).commit();
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putInt("lastVersion", versionCode).commit();
            }
        } finally {
            if (smsCursor != null) {
                smsCursor.close();
            }

        }
    }

}
