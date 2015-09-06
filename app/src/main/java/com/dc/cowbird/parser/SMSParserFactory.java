package com.dc.cowbird.parser;

import android.database.Cursor;
import android.util.Log;

import com.dc.cowbird.Constants;
import com.dc.cowbird.vo.Protocol;

/**
 * Created by coutinho on 26/08/15.
 */
public class SMSParserFactory {
    private static SMSParserFactory instance;
    SMSParser[] parsers;

    SMSParserFactory() {
        parsers = new SMSParser[]{new VivoSMS(), new TimSMS(), new ClaroSMS(), new OiSMS()};
    }

    public static Protocol getInstance(Long date, String subject, String body, String address) {
        if (instance == null) {
            instance = new SMSParserFactory();
        }
        for (SMSParser parser : instance.parsers) {
            if (parser.canParse(address, body)) {
                Log.d(Constants.LOG_TAG, parser + " " + address + ": '" + body + "'");
                return parser.getProtocol(address, body, date, subject);
            }
        }


        return null;
    }

    public static Protocol getInstance(Cursor c) {

        Long date = c.getLong(c.getColumnIndex("date"));
        String subject = c.getString(c.getColumnIndex("subject"));
        String body = c.getString(c.getColumnIndex("body"));
        String address = c.getString(c.getColumnIndex("address"));
        return getInstance(date, subject, body, address);
    }
}
