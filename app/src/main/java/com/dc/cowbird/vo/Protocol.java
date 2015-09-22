package com.dc.cowbird.vo;

import android.content.ContentValues;
import android.database.Cursor;

import com.dc.cowbird.R;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by coutinho on 26/08/15.
 */
public class Protocol {
    //@formatter:off
    public static final String CREATE_TABLE = "create table protocol " +
            "( " +
            "_id INTEGER PRIMARY KEY," +
            "number text," +
            "date integer, " +
            "operator text," +
            "obs text, " +
            "auto integer default 1, " +
            "full_source text, " +
            "outcome text, " +
            "wasseen int default 0, " +

            "UNIQUE(number)" +
            ")";
    public static final String[] CREATE_INDEXES = {
            "CREATE INDEX protocol_0 ON protocol(number)",
            "CREATE INDEX protocol_1 ON protocol(date)",
            "CREATE INDEX protocol_2 ON protocol(operator)"
    };
    public static final String UPGRADE_2_3 = "ALTER TABLE protocol ADD COLUMN wasseen int";

    public static final String TABLE_NAME = "protocol";
    long id = -1;
    long date = 0;
    String operator = "";
    String obs = "";
    boolean auto = true;
    boolean wasSeen = false;
    String fullSource;
    Outcome outcome = Outcome.NA;
    private String number = "?";

    public boolean isAuto() {
        return auto;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    public Protocol(String number, String operator, long date, String fullsSms) {
        this.date = date;

        this.number = number;
        this.operator = operator;
        this.fullSource = fullsSms;
    }
    //@formatter:on

    public Protocol(Cursor c) {
        this.id = c.getLong(c.getColumnIndex("_id"));
        this.date = c.getLong(c.getColumnIndex("date"));
        this.number = c.getString(c.getColumnIndex("number"));
        this.fullSource = c.getString(c.getColumnIndex("full_source"));
        this.obs = c.getString(c.getColumnIndex("obs"));
        this.operator = c.getString(c.getColumnIndex("operator"));
        this.auto = c.getInt(c.getColumnIndex("auto")) == 1;
        this.wasSeen = c.getInt(c.getColumnIndex("wasseen")) == 1;
        this.outcome = Outcome.valueOf(c.getString(c.getColumnIndex("outcome")));

    }

    public long getId() {
        return id;
    }

    public long getDate() {
        return date;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public String getOperator() {
        return operator;
    }

    public String getObs() {
        return obs;
    }

    public String getFullSource() {
        return fullSource;
    }

    public Outcome getOutcome() {
        return outcome;
    }

    public String getNumber() {
        return number;
    }

    @Override
    public String toString() {

        return DateFormat.getDateInstance().format(new Date(date)) + ": " + operator + " - " + number;
    }

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put("date", date);
        cv.put("number", number);
        cv.put("full_source", fullSource);
        cv.put("obs", obs);
        cv.put("operator", operator);
        cv.put("outcome", outcome.name());
        cv.put("auto", auto ? 1 : 0);
        cv.put("wasseen", wasSeen ? 1 : 0);
        return cv;
    }

    public boolean isWasSeen() {
        return wasSeen;
    }

    public boolean hasObservations() {
        return obs!=null && !obs.isEmpty();
    }

    public void setIsSeen() {
        wasSeen=true;
    }

    @Override
    public int hashCode() {

        return (number.hashCode() * 10000 + obs.hashCode() * 1000 + operator.hashCode() * 100 + (int) date);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Protocol) {
            Protocol other = (Protocol) o;
            return other.operator.equals(operator) && number.equals(other.number) && obs.equals(other.obs) && other.date == date && id == other.id;
        }
        return super.equals(o);
    }

    public static int getIcon(String operator) {
        if (operator.equals("OI")) {
            return R.mipmap.ic_oi;
        } else if (operator.equals("TIM")) {
            return R.mipmap.ic_tim;
        } else if (operator.equals("CLARO")) {
            return R.mipmap.ic_claro;
        } else if (operator.equals("VIVO")) {
            return R.mipmap.ic_vivo;
        } else if (operator.equals("AMERICANAS")) {
            return R.mipmap.ic_americanas;
        } else {
            return -1;
        }
    }
}
