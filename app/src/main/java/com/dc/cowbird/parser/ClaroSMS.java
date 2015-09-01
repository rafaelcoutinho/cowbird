package com.dc.cowbird.parser;

import android.util.Log;

import com.dc.cowbird.Constants;
import com.dc.cowbird.vo.Protocol;

import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by coutinho on 26/08/15.
 */
public class ClaroSMS implements SMSParser {
    final static String regextStr = "Protocolo (\\d*) referente a Solicitacao em (\\d{0,2}/\\d{0,2}/\\d{0,4}) as (\\d{0,2}:\\d{0,2})\\.\\S?Claro";
    Pattern p = null;

    public ClaroSMS() {
        p = Pattern.compile(regextStr);

    }

    @Override
    public boolean canParse(String address, String body) {
        if ("25276".equals(address) && body.endsWith("Claro")) {
            return p.matcher(body).matches();
        }
        return false;
    }

    @Override
    public Protocol getProtocol(String address, String body, Long date, String subject) {
        Matcher m = p.matcher(body);

        try {
            if (m.matches()) {
                if (m.groupCount() > 1) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy kk:mm");
                    date = sdf.parse(m.group(2) + " " + m.group(3)).getTime();

                }
            }
        } catch (Exception e) {
            Log.i(Constants.LOG_TAG, "Could not get date from '" + body, e);
        }
        return new Protocol(m.group(1), "Claro", date, body);
    }
}
