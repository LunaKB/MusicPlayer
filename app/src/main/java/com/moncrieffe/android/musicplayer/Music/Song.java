package com.moncrieffe.android.musicplayer.Music;

/**
 * Created by Chaz-Rae on 9/13/2016.
 */
public class Song {
    private String mName;
    private String mDirectory;
    private String mArtist;
    private String mAlbum;

    public Song(String name, String directory, String artist, String album){
        mName = name;
        mDirectory = directory;
        mArtist = artist;
        mAlbum = album;
    }

    public String getName() {
        return mName;
    }

    public String getDirectory() {
        return mDirectory;
    }

    public String getArtist() {
        return mArtist;
    }

    public String getAlbum() {
        return mAlbum;
    }
}
