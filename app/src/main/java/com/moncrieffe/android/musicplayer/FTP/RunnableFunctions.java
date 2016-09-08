package com.moncrieffe.android.musicplayer.FTP;

import android.content.Context;
import android.util.Log;

import com.moncrieffe.android.musicplayer.Credentials.Credentials;
import com.moncrieffe.android.musicplayer.Credentials.CredentialsManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Chaz-Rae on 9/6/2016.
 */
public class RunnableFunctions {
    private static final String TAG = "FTP";
    private static ClientFunctions mFtp;

    public RunnableFunctions(ClientFunctions f){
        mFtp = f;
    }

    public class FTPConnect implements Runnable{
        private Credentials c;
        public CountDownLatch l = new CountDownLatch(1);

        public FTPConnect(UUID id, Context context){
            c = CredentialsManager.get(context).getCredentials(id);
            //l = latch;
        }

        public void run() {
            boolean status = false;
            // host – your FTP address
            // username & password – for your secured login
            // 21 default gateway for FTP
            status = mFtp.ftpConnect(c.getIpaddress(), c.getUsername(), c.getPassword(), c.getPort());
            if (status == true) {
                Log.d(TAG, "Connection Success");
            } else {
                Log.d(TAG, "Connection failed");
            }
            l.countDown();
        }
    }

    public class FTPDisconnect implements Runnable{
        public CountDownLatch l = new CountDownLatch(1);

        @Override
        public void run() {
            mFtp.ftpDisconnect();
            l.countDown();
        }
    }

    public class FTPGetFileNames implements Runnable{
        private List<String> strings;
        public CountDownLatch l = new CountDownLatch(1);

        public FTPGetFileNames(){
            strings = new ArrayList<>();
        }

        @Override
        public void run() {
            strings = mFtp.ftpPrintFilesList(mFtp.ftpGetCurrentWorkingDirectory());
            l.countDown();
        }

        public List<String> getStrings(){
            return strings;
        }
    }

    public class FTPChangeDirectory implements Runnable{
        private String name;
        public CountDownLatch l = new CountDownLatch(1);

        public FTPChangeDirectory(String dName){
            name = dName;
        }

        @Override
        public void run() {
            mFtp.ftpChangeDirectory(name);
            l.countDown();
        }
    }
}
