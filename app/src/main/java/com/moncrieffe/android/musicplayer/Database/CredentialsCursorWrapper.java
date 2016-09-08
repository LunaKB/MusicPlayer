package com.moncrieffe.android.musicplayer.Database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.moncrieffe.android.musicplayer.Credentials.Credentials;
import com.moncrieffe.android.musicplayer.Database.CredentialsDbSchema.CredentialsTable;

import java.util.UUID;

/**
 * Created by Chaz-Rae on 9/7/2016.
 */
public class CredentialsCursorWrapper extends CursorWrapper {
    public CredentialsCursorWrapper(Cursor cursor){
        super(cursor);
    }

    public Credentials getCredentials(){
        String id = getString(getColumnIndex(CredentialsTable.Cols.ID));
        String ipaddress = getString(getColumnIndex(CredentialsTable.Cols.IPADDRESS));
        String username = getString(getColumnIndex(CredentialsTable.Cols.USERNAME));
        String password = getString(getColumnIndex(CredentialsTable.Cols.PASSWORD));
        int port = getInt(getColumnIndex(CredentialsTable.Cols.PORT));
        String webaddress = getString(getColumnIndex(CredentialsTable.Cols.WEBADDRESS));

        Credentials c = new Credentials(UUID.fromString(id), ipaddress, username, password, port, webaddress);
        return c;

    }
}