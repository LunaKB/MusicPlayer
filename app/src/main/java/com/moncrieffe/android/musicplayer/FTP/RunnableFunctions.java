package com.moncrieffe.android.musicplayer.FTP;

import android.content.Context;
import android.util.Log;

import com.moncrieffe.android.musicplayer.Credentials.Credentials;
import com.moncrieffe.android.musicplayer.Credentials.CredentialsManager;
import com.moncrieffe.android.musicplayer.DirectoryMenuActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
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

    public class ReadFile implements Runnable{
        private UUID id;
        private List<String> strings = new ArrayList<>();
        private Context context;
        String d;
        public CountDownLatch l = new CountDownLatch(1);

        public ReadFile(UUID uuid, Context c, String directory){
            id = uuid;
            context = c;
            d = directory;
        }

        @Override
        public void run() {
            try {
                // Create a URL for the desired page
                Credentials c = CredentialsManager.get(context).getCredentials(id);
                URL url = new URL(c.getWebaddress() + d + "/" + "list.txt");

                // Read all the text returned by the server
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                String str;
                while ((str = in.readLine()) != null) {
                    strings.add(str);
                }
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            l.countDown();
        }

        public List<String> getList(){
            return strings;
        }
    }

    public class ReadDirectory implements Runnable{
        private UUID id;
        private List<String> strings = new ArrayList<>();
        private Context context;
        public CountDownLatch l = new CountDownLatch(1);

        public ReadDirectory(UUID uuid, Context c){
            id = uuid;
            context = c;
        }

        @Override
        public void run() {
            try {
                // Create a URL for the desired page
                Credentials c = CredentialsManager.get(context).getCredentials(id);
                URL url = new URL(c.getWebaddress() + "list.txt");

                // Read all the text returned by the server
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                String str;
                while ((str = in.readLine()) != null) {
                    strings.add(str);
                }
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            l.countDown();
        }

        public List<String> getList(){
            return strings;
        }
    }
}
