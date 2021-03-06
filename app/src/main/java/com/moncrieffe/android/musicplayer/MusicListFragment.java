package com.moncrieffe.android.musicplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;

import com.moncrieffe.android.musicplayer.Credentials.Credentials;
import com.moncrieffe.android.musicplayer.Credentials.CredentialsManager;
import com.moncrieffe.android.musicplayer.FTP.ClientFunctions;
import com.moncrieffe.android.musicplayer.FTP.RunnableFunctions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import com.moncrieffe.android.musicplayer.Music.MusicController;
import com.moncrieffe.android.musicplayer.Music.Song;
import com.moncrieffe.android.musicplayer.Music.SongManager;
import com.moncrieffe.android.musicplayer.MusicService.MusicBinder;

import wseemann.media.FFmpegMediaMetadataRetriever;

/**
 * Created by Chaz-Rae on 9/6/2016.
 */
public class MusicListFragment extends Fragment implements MediaController.MediaPlayerControl{
    private CountDownLatch latch = new CountDownLatch(1);
    private static final String TAG = "FTP";
    private ClientFunctions mFtpClient = null;
    private List<String> mFileNames = new ArrayList<>();
    private List<String> mArtistNames = new ArrayList<>();
    private String mDirectory;
    private UUID mUUID;
    private Credentials mCredentials;
    private RunnableFunctions mRun;
    private MusicService mMusicSrv;
    private Intent playIntent;
    private boolean musicBound=false;
    private MusicController mController;
    private boolean paused=false, playbackPaused=false;
    private RecyclerView mRecyclerView;
    private FileAdapter mAdapter;

    public static MusicListFragment newInstance(String directory, UUID id){
        Bundle args = new Bundle();
        args.putString("ARG_STRING", directory);
        args.putSerializable("ARG_ID", id);

        MusicListFragment fragment = new MusicListFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (playIntent == null){
            playIntent = new Intent(getActivity(), MusicService.class);
            getActivity().bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            getActivity().startService(playIntent);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mFtpClient = new ClientFunctions();
        mDirectory = getArguments().getString("ARG_STRING");
        mUUID = (UUID)getArguments().getSerializable("ARG_ID");
        mCredentials = CredentialsManager.get(getActivity()).getCredentials(mUUID);
        setController();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_ftp, container, false);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.ftp_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

  /*      mRun = new RunnableFunctions(mFtpClient);
        try {
            RunnableFunctions.ReadFile file = mRun.new ReadFile(mUUID, getActivity(), mDirectory);
            new Thread(file).start();
            file.l.await();
            mFileNames = file.getList();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } */

       updateUI();
        mController.setAnchorView(view.findViewById(R.id.ftp_recyclerview));

 /*       for(int a = 0; a < mFileNames.size(); a++){
            String url = mCredentials.getWebaddress() + mDirectory + "/" + mFileNames.get(a);
            FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
            mmr.setDataSource(url);
            String artist = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST);
            String album = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ALBUM);
            mmr.release();
            mArtistNames.add(artist);
        } */

        return view;
    }

    private void updateUI(){
        SongManager songManager = SongManager.get(getActivity());
        List<Song> songs = createSongList(songManager);
        if(mAdapter == null) {

            mAdapter = new FileAdapter(songs);
            mRecyclerView.setAdapter(mAdapter);
        }
        else{
            mAdapter.setFiles(songs);
            mAdapter.notifyDataSetChanged();
        }
    }

    private List<Song> createSongList(SongManager songManager){
        List<Song> s =songManager.getSongs(mDirectory);
        return s;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_ftp, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_end:
                getActivity().stopService(playIntent);
                mMusicSrv = null;
                System.exit(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        getActivity().stopService(playIntent);
        mMusicSrv = null;
        super.onDestroyView();
    }

    @Override
    public void onPause(){
        super.onPause();
        paused=true;
    }

    @Override
    public void onResume(){
        super.onResume();
        if(paused){
            setController();
            paused=false;
        }
    }

    @Override
    public void onStop() {
        mController.hide();
        super.onStop();
    }

    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicBinder binder = (MusicBinder)service;
            //get service
            mMusicSrv = binder.getService();
            //pass list
            mMusicSrv.setList(mFileNames, mCredentials.getWebaddress(), mDirectory);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    /* Media Player Control */
    private void setController(){
        mController = new MusicController(getActivity());
        mController.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });

        mController.setMediaPlayer(this);
        mController.setEnabled(true);
    }

    //play next
    private void playNext(){
        mMusicSrv.playNext();
        if(playbackPaused){
            setController();
            playbackPaused=false;
        }

        mController.show(0);
    }

    //play previous
    private void playPrev(){
        mMusicSrv.playPrev();
        if(playbackPaused){
            setController();
            playbackPaused=false;
        }

        mController.show(0);
    }

    @Override
    public void start() {
        mMusicSrv.go();
    }

    @Override
    public void pause() {
        playbackPaused=true;
        mMusicSrv.pausePlayer();
    }

    @Override
    public int getDuration() {
        if(mMusicSrv!=null && musicBound && mMusicSrv.isPng()) {
            return mMusicSrv.getDur();
        }
        else{
            return 0;
        }
    }

    @Override
    public int getCurrentPosition() {
        if(mMusicSrv!=null && musicBound && mMusicSrv.isPng()) {
            return mMusicSrv.getPosn();
        }
        else{
            return 0;
        }
    }

    @Override
    public void seekTo(int pos) {
        mMusicSrv.seek(pos);
    }

    @Override
    public boolean isPlaying() {
        if(mMusicSrv != null && musicBound){
            return mMusicSrv.isPng();
        }
        else{
            return false;
        }
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    /* Recycler View Holder and Adapter */
    private class FileHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView mImageView;
        private TextView mSongTitle;
        private TextView mSongArtist;
        private TextView mSongAlbum;

        public FileHolder(LayoutInflater inflater, ViewGroup container){
            super(inflater.inflate(R.layout.list_item_music, container, false));
            itemView.setOnClickListener(this);
            mImageView = (ImageView)itemView.findViewById(R.id.song_image);
            mSongTitle = (TextView)itemView.findViewById(R.id.song_title);
            mSongArtist = (TextView)itemView.findViewById(R.id.song_artist);
            mSongAlbum = (TextView)itemView.findViewById(R.id.song_album);
        }

        public void bindFile(String name, String artist, String album){
            mSongTitle.setText(name);
            mSongArtist.setText(artist);
            mSongAlbum.setText(album);
        }

        @Override
        public void onClick(View v) {
            int position = mFileNames.indexOf(mSongTitle.getText().toString());
            mMusicSrv.setSong(position);
            mMusicSrv.playSong();
            if(playbackPaused){
                setController();
                playbackPaused=false;
            }

            mController.show();
        }
    }

    private class FileAdapter extends RecyclerView.Adapter<FileHolder>{
        private List<Song> mStrings;

        public FileAdapter(List<Song> files){
            mStrings = files;
        }

        @Override
        public FileHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new FileHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(FileHolder holder, int position) {
            String filename = mStrings.get(position).getName();
            String artistname = mStrings.get(position).getArtist();
            String albumname = mStrings.get(position).getAlbum();

            holder.bindFile(filename, artistname, albumname);
        }

        @Override
        public int getItemCount() {
            Log.d(TAG, "The size is " + mStrings.size());
            return mStrings.size();
        }

        public void setFiles(List<Song> files){
            mStrings = files;
        }
    }
}
