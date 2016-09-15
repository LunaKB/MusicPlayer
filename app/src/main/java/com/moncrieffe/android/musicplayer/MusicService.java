package com.moncrieffe.android.musicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.util.List;
import java.util.UUID;

/**
 * Created by Chaz-Rae on 9/8/2016.
 */
public class MusicService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    private static final int NOTIFY_ID = 1;
    private final IBinder mMusicBind = new MusicBinder();
    private MediaPlayer mMediaPlayer;
    private List<String> mStrings;
    private int mPosition;
    private String mUrl;
    private String mDirectory;
    private UUID mUUID;
    private String mSongTitle;

    @Override
    public void onCreate() {
        super.onCreate();
        mPosition = 0;
        mMediaPlayer = new MediaPlayer();
        initMusicPlayer();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMusicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mMediaPlayer.stop();
        mMediaPlayer.release();
        return false;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();

   /*     Intent notIntent = new Intent(this, FTPActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.play)
                .setTicker(mSongTitle)
                .setOngoing(true)
                .setContentTitle("Playing")
        .setContentText(mSongTitle);
        Notification not = builder.build();

        startForeground(NOTIFY_ID, not); */
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(mMediaPlayer.getCurrentPosition() > 0){
            mp.reset();
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    public void playSong(){
        mMediaPlayer.reset();
        //get song
        String playUri = mUrl + mStrings.get(mPosition);
        mSongTitle = mStrings.get(mPosition);
        try {
            mMediaPlayer.setDataSource(playUri);
        }
        catch (Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        mMediaPlayer.prepareAsync();

    }

    public void setSong(int songIndex){
        mPosition = songIndex;
    }

    public void initMusicPlayer(){
        mMediaPlayer.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
    }

    public void setList(List<String> strings, String webAddress, String directory){
        mStrings = strings;
        mUrl = webAddress + directory + "/";
        mDirectory = directory;
     //   mUUID = id; , UUID id
    }

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    /* Media Controller */
    public int getPosn(){
        return mMediaPlayer.getCurrentPosition();
    }

    public int getDur(){
        return mMediaPlayer.getDuration();
    }

    public boolean isPng(){
        return mMediaPlayer.isPlaying();
    }

    public void pausePlayer(){
        mMediaPlayer.pause();
    }

    public void seek(int posn){
        mMediaPlayer.seekTo(posn);
    }

    public void go(){
        mMediaPlayer.start();
    }

    public void playPrev(){
        mPosition--;
        if(mPosition < 0){
            mPosition = mStrings.size()-1;
        }
        playSong();
    }
    public void playNext(){
        mPosition++;
        if(mPosition >= mStrings.size()){
            mPosition = 0;
        }
        playSong();
    }
}
