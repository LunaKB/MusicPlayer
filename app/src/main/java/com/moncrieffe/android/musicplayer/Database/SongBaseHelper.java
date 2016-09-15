package com.moncrieffe.android.musicplayer.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.moncrieffe.android.musicplayer.Database.DbSchema.SongsTable;

/**
 * Created by Chaz-Rae on 9/13/2016.
 */
public class SongBaseHelper extends SQLiteOpenHelper {
    private static  final int VERSION = 1;
    private static final String DATABASE_NAME = "songBase.db";

    public SongBaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + SongsTable.NAME + "(" +
                "_id integer primary key autoincrement, " +
                SongsTable.Cols.NAME + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
