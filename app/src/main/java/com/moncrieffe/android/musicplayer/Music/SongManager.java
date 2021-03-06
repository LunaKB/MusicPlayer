package com.moncrieffe.android.musicplayer.Music;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.moncrieffe.android.musicplayer.Database.DbSchema;
import com.moncrieffe.android.musicplayer.Database.DbSchema.SongsTable;
import com.moncrieffe.android.musicplayer.Database.SongBaseHelper;
import com.moncrieffe.android.musicplayer.Database.SongCursorWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chaz-Rae on 9/13/2016.
 */
public class SongManager {
    private static SongManager sSongManager;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static SongManager get(Context context){
        if(sSongManager == null){
            sSongManager = new SongManager(context);
        }
        return sSongManager;
    }

    /* Constructor */
    private SongManager(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new SongBaseHelper(mContext).getWritableDatabase();
    }

    /* Add, delete, update */
    public void addSong(Song song){
        ContentValues values = getContentValues(song);
        mDatabase.insert(SongsTable.NAME, null, values);
    }

    public void deleteSong(Song song){
        mDatabase.delete(SongsTable.NAME, SongsTable.Cols.NAME + " = ?",
                new String[]{song.getName()});
    }

    public void updateSong(Song song){
        String name = song.getName();
        ContentValues values = getContentValues(song);

        mDatabase.update(SongsTable.NAME, values,
                SongsTable.Cols.NAME + " = ?", new String[]{name});
    }

    /* Getters */
    public List<Song> getSongs(String directory){
        List<Song> songs = new ArrayList<>();
        SongCursorWrapper cursor = querySongs(
                SongsTable.Cols.DIRECTORY + " = ?", new String[]{directory});

        try{
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                songs.add(cursor.getSong());
                cursor.moveToNext();
            }
        }
        finally {
            cursor.close();
        }
        return songs;
    }

    public Song getSong(String name){
        SongCursorWrapper cursor = querySongs(
                SongsTable.Cols.NAME + " = ?",
                new String[]{name}
        );

        try{
            if(cursor.getCount() == 0){
                return null;
            }

            cursor.moveToFirst();
            return cursor.getSong();
        }
        finally {
            cursor.close();
        }
    }

    /* SQLiteDatabase Methods */
    private static ContentValues getContentValues(Song song){
        ContentValues values = new ContentValues();
        values.put(SongsTable.Cols.NAME, song.getName());
        values.put(SongsTable.Cols.DIRECTORY, song.getDirectory());
        values.put(SongsTable.Cols.ARTIST, song.getArtist());
        values.put(SongsTable.Cols.ALBUM, song.getAlbum());

        return values;
    }

    private SongCursorWrapper querySongs(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                SongsTable.NAME,
                null, // Columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );

        return new SongCursorWrapper(cursor);
    }
}
/*
dependencies {
    compile fileTree(include: ['*.jar'], dir: 'libs')
    testCompile 'junit:junit:4.12'
    compile 'com.android.support:appcompat-v7:23.4.0'
    compile 'com.android.support:recyclerview-v7:24.0.0'
    compile files('libs/commons-net-3.5.jar')
    compile 'com.github.wseemann:FFmpegMediaMetadataRetriever:1.0.11'
}
 */