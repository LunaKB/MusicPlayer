package com.moncrieffe.android.musicplayer.Database;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.provider.ContactsContract;

import com.moncrieffe.android.musicplayer.Database.DbSchema.DirectoryTable;
import com.moncrieffe.android.musicplayer.Directory.Directory;

/**
 * Created by Chaz-Rae on 9/11/2016.
 */
public class DirectoryCursorWrapper extends CursorWrapper{
    public DirectoryCursorWrapper(Cursor cursor){
        super(cursor);
    }

    public Directory getDirectory(){
        String name = getString(getColumnIndex(DirectoryTable.Cols.DIRECTORY));

        Directory d = new Directory(name);
        return d;
    }
}
