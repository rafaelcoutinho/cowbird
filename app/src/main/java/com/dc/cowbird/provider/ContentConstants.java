package com.dc.cowbird.provider;

import android.net.Uri;

/**
 * Created by coutinho on 27/08/15.
 */
public class ContentConstants {
    public static final String PROVIDER_NAME = "com.dc.protocols";
    public static final String URL = "content://" + PROVIDER_NAME + "/";

    public static Uri getUriFor(ProtocolURLs reportEquipmentPath) {
        return Uri.parse(URL + reportEquipmentPath.name());
    }

    ;

    public static Uri getUriFor(ProtocolURLs path, Long id) {

        return Uri.withAppendedPath(getUriFor(path), id.toString());

    }

    public static enum ProtocolURLs {
        URLProtocol;

        public Uri asURL() {
            return ContentConstants.getUriFor(this);
        }
    }
}
