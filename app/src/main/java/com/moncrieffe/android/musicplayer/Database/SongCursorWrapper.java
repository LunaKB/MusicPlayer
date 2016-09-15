package com.moncrieffe.android.musicplayer.Database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.moncrieffe.android.musicplayer.Database.DbSchema.SongsTable;
import com.moncrieffe.android.musicplayer.Music.Song;

/**
 * Created by Chaz-Rae on 9/13/2016.
 */
public class SongCursorWrapper extends CursorWrapper{
    public SongCursorWrapper(Cursor cursor){
        super(cursor);
    }

    public Song getSong(){
        String name = getString(getColumnIndex(SongsTable.Cols.NAME));
        String directory = getString(getColumnIndex(SongsTable.Cols.DIRECTORY));
        String artist = getString(getColumnIndex(SongsTable.Cols.ARTIST));
        String album = getString(getColumnIndex(SongsTable.Cols.ALBUM));

        Song s = new Song(directory, name, artist, album);
        return s;
    }
}
