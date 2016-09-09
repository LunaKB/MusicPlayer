package com.moncrieffe.android.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.moncrieffe.android.musicplayer.Credentials.Credentials;
import com.moncrieffe.android.musicplayer.Credentials.CredentialsManager;
import com.moncrieffe.android.musicplayer.FTP.ClientFunctions;
import com.moncrieffe.android.musicplayer.FTP.RunnableFunctions;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;


/**
 * Created by Chaz-Rae on 9/7/2016.
 */
public class DirectoryMenuActivity extends AppCompatActivity {
    private static final String TAG = "FTP";
    private ClientFunctions mFtpClient = null;
    private List<String> mDirectoryNames = new ArrayList<>();
    private UUID mUUID;
    private RunnableFunctions mRun;

    public static Intent newIntent(Context context, UUID id){
        Intent i = new Intent(context, DirectoryMenuActivity.class);
        i.putExtra("ID", id);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_ftp);
        mFtpClient = new ClientFunctions();
        mUUID = (UUID)getIntent().getSerializableExtra("ID");

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.ftp_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(DirectoryMenuActivity.this));
        mRun = new RunnableFunctions(mFtpClient);

        try {
            RunnableFunctions.ReadDirectory file = mRun.new ReadDirectory(
                    UUID.fromString("e8eabaf8-de77-4d16-acae-7c7269cc5d5e"), DirectoryMenuActivity.this);
            new Thread(file).start();
            file.l.await();
            mDirectoryNames = file.getList();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

   /*
        mRun = new RunnableFunctions(mFtpClient);
        RunnableFunctions.FTPConnect connect;
        try {
            connect = mRun.new FTPConnect(mUUID, DirectoryMenuActivity.this);
        }
        catch (Exception e){
            connect = mRun.new FTPConnect(UUID.fromString("e8eabaf8-de77-4d16-acae-7c7269cc5d5e"), DirectoryMenuActivity.this);
        }
        RunnableFunctions.FTPDisconnect disconnect = mRun.new FTPDisconnect();
        RunnableFunctions.FTPGetFileNames getFileNames = mRun.new FTPGetFileNames();

        try {
            new Thread(connect).start();
            connect.l.await();

            new Thread(getFileNames).start();
            getFileNames.l.await();
            mDirectoryNames = getFileNames.getStrings();

            new Thread(disconnect).start();
            disconnect.l.await();
        }
        catch (Exception e){
            Log.e(TAG, "Interrupted");
        }*/

        recyclerView.setAdapter(new FileAdapter());

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
                    .newIntent(DirectoryMenuActivity.this,
                            directoryName,
                            UUID.fromString("e8eabaf8-de77-4d16-acae-7c7269cc5d5e"));
            startActivity(i);
        }
    }

    private class FileAdapter extends RecyclerView.Adapter<FileHolder>{
        @Override
        public FileHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(DirectoryMenuActivity.this);
            return new FileHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(FileHolder holder, int position) {
            String filename = mDirectoryNames.get(position);
            holder.bindFile(filename);
        }

        @Override
        public int getItemCount() {
            Log.d(TAG, "The directory size is " + mDirectoryNames.size());
            return mDirectoryNames.size();
        }
    }
}
