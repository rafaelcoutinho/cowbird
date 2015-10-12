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
public class Tim3SMS implements SMSParser {

    final static String regextStr = "Prezado Cliente sua (.*?) de Protocolo (\\d*) registrada em (\\d{0,2}/\\d{0,2}/\\d{0,4}) (\\d{0,2}:\\d{0,2}:\\d{0,2}) näo foi concluida. Ligue \\*144 para dar continuidade ao atendimento.";
    Pattern p = null;

    public Tim3SMS() {
        p = Pattern.compile(regextStr);

    }

    @Override
    public boolean canParse(String address, String body) {
        if ((address.contains("144") || address.contains("4198")|| address.contains("4196"))&& body.contains("*144")) {
            return p.matcher(body).matches();
        }
        return false;
    }

    @Override
    public Protocol getProtocol(String address, String body, Long date, String subject) {
        Matcher m = p.matcher(body);
        Protocol p = null;

        try {
            if (m.matches()) {

                if (m.groupCount() > 1) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");
                    date = sdf.parse(m.group(3) + " " + m.group(4)).getTime();
                    p = new Protocol(m.group(2), "TIM", date, body);
                    p.setObs(m.group(1));

                }
            }
        } catch (Exception e) {
            Log.i(Constants.LOG_TAG, "Could not get date from '" + body, e);
        }
        return p;
    }
}
