package com.moncrieffe.android.musicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.Button;

import com.moncrieffe.android.musicplayer.Credentials.Credentials;
import com.moncrieffe.android.musicplayer.Credentials.CredentialsManager;
import com.moncrieffe.android.musicplayer.Directory.Directory;
import com.moncrieffe.android.musicplayer.Directory.DirectoryManager;
import com.moncrieffe.android.musicplayer.FTP.ClientFunctions;
import com.moncrieffe.android.musicplayer.FTP.RunnableFunctions;
import com.moncrieffe.android.musicplayer.FTPActivity;
import com.moncrieffe.android.musicplayer.Music.Song;
import com.moncrieffe.android.musicplayer.Music.SongManager;
import com.moncrieffe.android.musicplayer.R;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import wseemann.media.FFmpegMediaMetadataRetriever;

/**
 * Created by Chaz-Rae on 9/11/2016.
 */
public class DirectoryMenuFragment extends Fragment {
    private static final String TAG = "FTP";
    private ClientFunctions mFtpClient = null;
    private List<String> mDirectoryNames = new ArrayList<>();
    private UUID mUUID;
    private RunnableFunctions mRun;
    private FileAdapter mAdapter;
    private RecyclerView mRecyclerView;

    public static DirectoryMenuFragment newInstance(UUID id){
        Bundle args = new Bundle();
        args.putSerializable("ID", id);

        DirectoryMenuFragment fragment = new DirectoryMenuFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ftp, container, false);

        mRecyclerView = (RecyclerView)v.findViewById(R.id.ftp_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRun = new RunnableFunctions(mFtpClient);



        updateUI();

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mFtpClient = new ClientFunctions();
        mUUID = (UUID)getArguments().getSerializable("ID");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.directory_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.directory_refresh:

                updateUI();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void updateUI(){
        DirectoryManager directoryManager = DirectoryManager.get(getActivity());
        List<Directory> directories = createDirectory(directoryManager);
        if(mAdapter == null) {

            mAdapter = new FileAdapter(directories);
            mRecyclerView.setAdapter(mAdapter);
        }
        else{
            mAdapter.setFiles(directories);
            mAdapter.notifyDataSetChanged();
        }
    }

    /* Update Directory List */
    private List<Directory> createDirectory(DirectoryManager directoryManager){
        List<Directory> d = directoryManager.getDirectories();
        if(d.isEmpty()) {
            try {
                RunnableFunctions.ReadDirectory file = mRun.new ReadDirectory(
                        UUID.fromString("e8eabaf8-de77-4d16-acae-7c7269cc5d5e"), getActivity());
                new Thread(file).start();
                file.l.await();
                mDirectoryNames = file.getList();
                for(int i = 0; i < mDirectoryNames.size(); i++){
                    Directory directory = new Directory(mDirectoryNames.get(i));
                    directoryManager.addDirectory(directory);

                    createSongList(directory);
                }
                d = directoryManager.getDirectories();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return d;
    }

    /* Update Song List */
    private void createSongList(Directory directory){
        Credentials c = CredentialsManager.get(getActivity()).getCredentials(mUUID);
        SongManager songManager = SongManager.get(getActivity());
        List<String> files;
        try {
            RunnableFunctions.ReadFile file = mRun.new ReadFile(mUUID, getActivity(), directory.getName());
            new Thread(file).start();
            file.l.await();
            files = file.getList();

            for(int i = 0; i < files.size(); i++){
                String url = c.getWebaddress() + directory.getName() + "/" + files.get(i);
                FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
                mmr.setDataSource(url);
                String artist = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST);
                String album = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ALBUM);
                mmr.release();

                Song song = new Song(files.get(i), directory.getName(), artist, album);
                songManager.addSong(song);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /* Recycler View Holder and Adapter */
    private class FileHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private Button mButton;

        public FileHolder(LayoutInflater inflater, ViewGroup container){
            super(inflater.inflate(R.layout.list_item_file, container, false));
            mButton = (Button)itemView.findViewById(R.id.list_item_directory);
            mButton.setOnClickListener(this);
        }

        public void bindFile(String name){
            mButton.setText(name);
        }

        @Override
        public void onClick(View v) {
            String directoryName = mButton.getText().toString();
            Intent i = FTPActivity
                    .newIntent(getActivity(),
                            directoryName,
                            UUID.fromString("e8eabaf8-de77-4d16-acae-7c7269cc5d5e"));
            startActivity(i);
        }
    }

    private class FileAdapter extends RecyclerView.Adapter<FileHolder>{
        private List<Directory> mStrings;

        public FileAdapter(List<Directory> files){
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
            holder.bindFile(filename);
        }

        @Override
        public int getItemCount() {
            Log.d(TAG, "The directory size is " + mStrings.size());
            return mStrings.size();
        }

        public void setFiles(List<Directory> files){
            mStrings = files;
        }
    }
}
