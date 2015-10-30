package com.dc.cowbird.backup;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.app.backup.FileBackupHelper;
import android.app.backup.SharedPreferencesBackupHelper;
import android.content.Context;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.dc.cowbird.Constants;
import com.dc.cowbird.provider.ProtocolDBContentProvider;

import java.io.File;
import java.io.IOException;

/**
 * Created by coutinho on 29/10/15.
 */
public class APBackupAgent extends BackupAgentHelper {

    private static final String DB_NAME = "PROTOCOLNOTEDB";

    @Override
    public void onCreate() {
//        getDatabasePath(DB_NAME).getAbsolutePath()
        FileBackupHelper dbs = new FileBackupHelper(this, DB_NAME);
        SharedPreferencesBackupHelper helper =
                new SharedPreferencesBackupHelper(this, getPackageName() + "_preferences");
        addHelper("prefs", helper);
        addHelper("dbs", dbs);
    }

    @Override
    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState) throws IOException {
        super.onBackup(oldState, data, newState);

        Log.i(Constants.LOG_TAG, "Backupeadn " + super.getFilesDir().getAbsolutePath() + " e " + this.getFilesDir().getAbsolutePath() + " " + new File("/data/data/com.dc.cowbird.Protocolo/files/../databases/" + DB_NAME).exists());

    }

    @Override
    public void onRestore(BackupDataInput data, int appVersionCode, ParcelFileDescriptor newState) throws IOException {
        super.onRestore(data, appVersionCode, newState);
        Log.i(Constants.LOG_TAG, "REstoring " + data + " " + appVersionCode);

    }

    @Override
    public void onRestoreFile(ParcelFileDescriptor data, long size, File destination, int type, long mode, long mtime) throws IOException {
        super.onRestoreFile(data, size, destination, type, mode, mtime);
        Log.i(Constants.LOG_TAG, "destination " + destination.getAbsolutePath());
    }

    @Override
    public File getFilesDir() {
        File path = getDatabasePath(DB_NAME);
        return path.getParentFile();
    }
}
