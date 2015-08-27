package com.dc.cowbird;

import android.app.Application;

import com.dc.cowbird.service.CrawlSMSInbox;

/**
 * Created by coutinho on 27/08/15.
 */
public class AnotaProtocolo extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrawlSMSInbox.startCrawlingSMSInbox(getApplicationContext());
    }
}
