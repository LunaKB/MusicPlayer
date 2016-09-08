package com.moncrieffe.android.musicplayer;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.UUID;

import wseemann.media.FFmpegMediaMetadataRetriever;

/**
 * Created by Chaz-Rae on 9/7/2016.
 */
public class MusicFragment extends Fragment {
    private TextView mMusicTitle;
    private Button mPlay;
    private Button mPause;
    private Button mUnpause;
    private Button mStop;
    private MediaPlayer mMediaPlayer = new MediaPlayer();
    private String mUrl;

    public static MusicFragment newInstance(String url){
        Bundle args = new Bundle();
        args.putString("URL", url);

        MusicFragment fragment = new MusicFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mUrl = getArguments().getString("URL");
        try {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(mUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
        mmr.setDataSource(mUrl);
        String album = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ALBUM);
        mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST);

        View v = inflater.inflate(R.layout.fragment_music, container, false);
        mMusicTitle = (TextView)v.findViewById(R.id.music_title);
        mMusicTitle.setText(album);

        mPlay = (Button)v.findViewById(R.id.play_button);
        mPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    mMediaPlayer.prepare();
                    mMediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        mPause = (Button)v.findViewById(R.id.pause_button);
        mPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.pause();
            }
        });

        mUnpause = (Button)v.findViewById(R.id.unpause_button);
        mUnpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // mMediaPlayer.
            }
        });

        mStop = (Button)v.findViewById(R.id.stop_button);
        mStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.release();
            }
        });

        return v;
    }
}
