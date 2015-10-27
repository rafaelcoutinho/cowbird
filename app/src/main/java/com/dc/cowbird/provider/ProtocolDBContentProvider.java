package com.dc.cowbird.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;


import com.dc.cowbird.vo.Protocol;

public class ProtocolDBContentProvider extends ContentProvider {
    static final UriMatcher sUriMatcher;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        for (int i = 0; i < ContentConstants.ProtocolURLs.values().length; i++) {

            sUriMatcher.addURI(ContentConstants.PROVIDER_NAME, ContentConstants.ProtocolURLs.values()[i].name(), i);
        }

    }

    ProtocolDBHelper db;


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        ContentConstants.ProtocolURLs vrUrl = convertURIToProtocolURL(uri);
        int count = 0;
        switch (vrUrl) {
            case URLProtocol:
                count = db.getWritableDatabase().delete(Protocol.TABLE_NAME, selection, selectionArgs);

                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);

        // Returns the number of rows deleted.
        return count;
    }

    @Override
    public String getType(Uri uri) {
        ContentConstants.ProtocolURLs vrUrl = convertURIToProtocolURL(uri);

        return "vnd.android.cursor.dir/vnd." + ContentConstants.PROVIDER_NAME + "." + vrUrl.name();
    }

    private ContentConstants.ProtocolURLs convertURIToProtocolURL(Uri uri) {
        int ordinalVR = sUriMatcher.match(uri);
        return ContentConstants.ProtocolURLs.values()[ordinalVR];
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        ContentConstants.ProtocolURLs vrUrl = convertURIToProtocolURL(uri);
        long rowId = -1;
        switch (vrUrl) {
            case URLProtocol:
                rowId = db.getWritableDatabase().insert(Protocol.TABLE_NAME, null, values);

                break;
            default:
                throw new SQLiteConstraintException("Failed to switch insert protocol "+uri );

        }

        // If the insert succeeded, the row ID exists.
        if (rowId > 0) {
            // Creates a URI with the note ID pattern and the new row ID appended to it.
            Uri noteUri = ContentConstants.getUriFor(vrUrl, rowId);

            // Notifies observers registered against this provider that the data changed.
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }else {
            throw new SQLiteConstraintException("Failed to insert row into " + uri);
        }
    }

    @Override
    public boolean onCreate() {
        db = new ProtocolDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor c = null;
        ContentConstants.ProtocolURLs vrUrl = convertURIToProtocolURL(uri);
        switch (vrUrl) {
            case URLProtocol:
                SQLiteDatabase sql = db.getReadableDatabase();
                c = sql.query(Protocol.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
        }
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int count = 0;
        ContentConstants.ProtocolURLs vrUrl = convertURIToProtocolURL(uri);
        switch (vrUrl) {
            case URLProtocol:
                SQLiteDatabase sql = db.getWritableDatabase();
                sql.update(Protocol.TABLE_NAME, values, selection, selectionArgs);
                break;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;

    }
}
