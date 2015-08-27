package com.dc.cowbird.parser;

import com.dc.cowbird.vo.Protocol;

/**
 * Created by coutinho on 26/08/15.
 */
public interface SMSParser {

    public  boolean canParse(String address,String txt);
    public Protocol getProtocol(String address, String body, Long date, String subject);

}
