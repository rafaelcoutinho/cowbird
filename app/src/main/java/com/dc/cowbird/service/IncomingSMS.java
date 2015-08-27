package com.dc.cowbird.service;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.dc.cowbird.Constants;
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
                        cr.insert(ContentConstants.ProtocolURLs.URLProtocol.asURL(), protocol.toContentValues());
                    } catch (Exception e) {
                        Log.w(Constants.LOG_TAG, "Could not save SMS", e);
                    }
                }
            }


        }
    }
}
