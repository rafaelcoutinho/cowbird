package com.dc.cowbird.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dc.cowbird.vo.Protocol;

/**
 * Created by coutinho on 27/08/15.
 */
class ProtocolDBHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 4;
    public static String DB_NAME = "PROTOCOLNOTEDB";

    public ProtocolDBHelper(Context ctx) {
        super(ctx, DB_NAME, null, DB_VERSION);


    }


    @Override
    public void onCreate(SQLiteDatabase sql) {
        sql.execSQL(Protocol.CREATE_TABLE);
        for (int i = 0; i < Protocol.CREATE_INDEXES.length; i++) {
            sql.execSQL(Protocol.CREATE_INDEXES[i]);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int fromVersion, int toVerson) {
        if (toVerson == 3) {
            sqLiteDatabase.execSQL(Protocol.UPGRADE_2_3);
        } else if (toVerson == 4) {
            ContentValues cv = new ContentValues();
            cv.put("operator", "CLARO");
            sqLiteDatabase.update(Protocol.TABLE_NAME, cv, "operator=?", new String[]{"Claro"});

        }
    }


}
