package com.dc.cowbird.parser;

import android.util.Log;

import com.dc.cowbird.vo.Protocol;

/**
 * Created by coutinho on 26/08/15.
 */
public class TimSMS implements SMSParser {


    @Override
    public boolean canParse(String address,String body) {
        if("4199".equals(address) && body.startsWith("Protocolo")){
            return true;

        }
        return false;
    }

    @Override
    public Protocol getProtocol(String address, String body, Long date, String subject) {
        Log.i("Proto","address "+address);
        Log.i("Proto","body "+body);
        Log.i("Proto","date "+date);
        Log.i("Proto","subject "+subject);
        return new Protocol("","TIM",date,body);
    }
}
