package com.dc.cowbird.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.util.Log;

import com.dc.cowbird.Constants;
import com.dc.cowbird.MainControlActivity;
import com.dc.cowbird.R;
import com.dc.cowbird.parser.SMSParserFactory;
import com.dc.cowbird.provider.ContentConstants;
import com.dc.cowbird.vo.Protocol;

public class IncomingSMS extends BroadcastReceiver {
    public IncomingSMS() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();

        SmsMessage[] msgs = null;


        if (bundle != null) {
            // Retrieve the SMS Messages received
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            ContentResolver cr = context.getContentResolver();
            // For every SMS message received
            for (int i = 0; i < msgs.length; i++) {
                // Convert Object array
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);


                Protocol protocol = SMSParserFactory.getInstance(msgs[i].getTimestampMillis(), "", msgs[i].getMessageBody(), msgs[i].getOriginatingAddress());
                if (protocol != null) {
                    try {
                        Uri uri = cr.insert(ContentConstants.ProtocolURLs.URLProtocol.asURL(), protocol.toContentValues());
                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(context)
                                        .setSmallIcon(R.drawable.ic_new_protocol)
                                        .setContentTitle("Novo protocolo")
                                        .setContentText("Identificamos um novo protocol! Clique aqui para adicionar mais informações a ele.");
                        Intent resultIntent = new Intent(context, MainControlActivity.class);
                        resultIntent.putExtra("uri", uri.getLastPathSegment());

                        // Because clicking the notification opens a new ("special") activity, there's
                        // no need to create an artificial back stack.
                        PendingIntent resultPendingIntent =
                                PendingIntent.getActivity(
                                        context,
                                        0,
                                        resultIntent,
                                        PendingIntent.FLAG_UPDATE_CURRENT
                                );
                        mBuilder.setContentIntent(resultPendingIntent);
                        NotificationManager mNotifyMgr =
                                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
// Builds the notification and issues it.
                        mNotifyMgr.notify(213, mBuilder.build());

                    } catch (Exception e) {
                        Log.w(Constants.LOG_TAG, "Could not save SMS", e);
                    }
                }
            }


        }
    }
}
