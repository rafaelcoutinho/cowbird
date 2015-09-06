package com.dc.cowbird.parser;

import android.util.Log;

import com.dc.cowbird.Constants;
import com.dc.cowbird.vo.Protocol;

import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Davi on 05/09/2015.
 */
public class OiSMS implements SMSParser {
    final static String regextStr = "Oi, seguem os dados da sua solicitacao\\. Protocolo: (\\d*) do tipo (\\w*) foi aberto em (\\d{0,2}\\/\\d{0,2}\\/\\d{0,4}) (\\d{0,2}:\\d{0,2}:\\d{0,2})\\.";
    Pattern p = null;

    boolean enableDebugSms = false;

    public OiSMS() {
        p = Pattern.compile(regextStr);
    }

    @Override
    public boolean canParse(String address, String body) {
        if ((enableDebugSms || ("7588".equals(address)) && body.startsWith("Oi"))) {
            System.out.println("Parseando Oi " + body);

            System.out.println(p.matcher(body).matches() + "");
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
                    date = sdf.parse(m.group(3) + " " + m.group(4)).getTime();
                }
            }
        } catch (Exception e) {
            Log.i(Constants.LOG_TAG, "Could not get date from '" + body, e);
        }
        return new Protocol(m.group(1), "OI", date, body);
    }
}
