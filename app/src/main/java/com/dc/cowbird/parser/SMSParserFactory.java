package com.dc.cowbird.parser;

import android.database.Cursor;

import com.dc.cowbird.vo.Protocol;

/**
 * Created by coutinho on 26/08/15.
 */
public class SMSParserFactory {

    public static Protocol getInstance(Cursor c) {

        Long date = c.getLong(c.getColumnIndex("date"));
        String subject = c.getString(c.getColumnIndex("subject"));
        String body = c.getString(c.getColumnIndex("body"));
        String address = c.getString(c.getColumnIndex("address"));
        VivoSMS vivo = new VivoSMS();
        if (vivo.canParse(address, body)) {
            return vivo.getProtocol(address, body, date, subject);

        } else {
            TimSMS tim = new TimSMS();
            if (tim.canParse(address, body)) {
                return tim.getProtocol(address, body, date, subject);

            }
        }


        return null;
    }
}
