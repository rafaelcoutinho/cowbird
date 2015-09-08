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
public class Tim2SMS implements SMSParser {

    final static String regextStr = "Prezado Cliente sua InformaÇäo de (.*) foi atendida em (\\d{0,2}/\\d{0,2}/\\d{0,4}) (\\d{0,2}:\\d{0,2}:\\d{0,2}) através do protocolo (\\d*)\\. Obrigad[oa] pelo contato! TIM.";
    Pattern p = null;

    public Tim2SMS() {
        p = Pattern.compile(regextStr);

    }

    @Override
    public boolean canParse(String address, String body) {
        if (address.contains("144") && body.endsWith("TIM.")) {
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
                    date = sdf.parse(m.group(2) + " " + m.group(3)).getTime();
                    p = new Protocol(m.group(4), "TIM", date, body);
                    p.setObs(m.group(1));

                }
            }
        } catch (Exception e) {
            Log.i(Constants.LOG_TAG, "Could not get date from '" + body, e);
        }
        return p;
    }
}
