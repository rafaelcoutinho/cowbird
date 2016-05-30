package com.dc.cowbird;

import android.app.Application;
import android.app.backup.BackupManager;
import android.app.backup.RestoreObserver;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.dc.cowbird.service.CrawlSMSInbox;

/**
 * Created by coutinho on 27/08/15.
 */
public class AnotaProtocolo extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Long lastCheck = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getLong("lastCheck", -1);
        if(lastCheck==-1){
            new BackupManager(getApplicationContext()).requestRestore(new RestoreObserver() {
                @Override
                public void restoreStarting(int numPackages) {
                    super.restoreStarting(numPackages);


                }

                @Override
                public void onUpdate(int nowBeingRestored, String currentPackage) {
                    super.onUpdate(nowBeingRestored, currentPackage);


                }

                @Override
                public void restoreFinished(int error) {
                    super.restoreFinished(error);
                    Log.i(Constants.LOG_TAG, "REstore finalizou " + error);
                    Toast.makeText(getApplicationContext(), "Restoure completou " + error, Toast.LENGTH_SHORT).show();
                    CrawlSMSInbox.startCrawlingSMSInbox(getApplicationContext());
                }
            });
        }else {
            CrawlSMSInbox.startCrawlingSMSInbox(getApplicationContext());
        }
    }
}
