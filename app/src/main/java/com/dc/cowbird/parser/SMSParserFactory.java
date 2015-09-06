package com.dc.cowbird.parser;

import android.database.Cursor;
import android.util.Log;

import com.dc.cowbird.Constants;
import com.dc.cowbird.vo.Protocol;

/**
 * Created by coutinho on 26/08/15.
 */
public class SMSParserFactory {
    public static Protocol getInstance(Long date, String subject, String body, String address) {

        VivoSMS vivo = new VivoSMS();
        if (vivo.canParse(address, body)) {
            Log.d(Constants.LOG_TAG, "Vivo " + address + ": '" + body + "'");
            return vivo.getProtocol(address, body, date, subject);

        } else {
            TimSMS tim = new TimSMS();
            if (tim.canParse(address, body)) {
                Log.d(Constants.LOG_TAG, "TIM " + address + ": '" + body + "'");
                return tim.getProtocol(address, body, date, subject);

            } else {
                ClaroSMS claro = new ClaroSMS();
                if (claro.canParse(address, body)) {
                    Log.d(Constants.LOG_TAG, "Claro " + address + ": '" + body + "'");
                    return claro.getProtocol(address, body, date, subject);

                } else {
                    OiSMS oi = new OiSMS();
                    if (oi.canParse(address, body)) {
                        Log.d(Constants.LOG_TAG, "Oi " + address + ": '" + body + "'");
                        return oi.getProtocol(address, body, date, subject);
                    } else {
                        Log.d(Constants.LOG_TAG, "Não identificiado Parsing " + address + ": '" + body + "'");
                    }
                }
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
