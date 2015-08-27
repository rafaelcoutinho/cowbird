package com.dc.cowbird.vo;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by coutinho on 26/08/15.
 */
public class Protocol {
    private String number="?";
    long date=0;
    String operator;
    String obs;
    String fullSms;
    Outcome outcome;

    public Protocol(String number, String operator, long date,String fullsSms) {
        this.date = date;
        this.number = number;
        this.operator = operator;
        this.fullSms = fullsSms;
    }

    @Override
    public String toString() {
        return DateFormat.getDateInstance().format(new Date(date))+": "+operator+" - "+number;
    }
}
