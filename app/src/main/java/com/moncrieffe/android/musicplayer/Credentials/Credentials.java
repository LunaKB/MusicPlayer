package com.moncrieffe.android.musicplayer.Credentials;

import java.util.UUID;

/**
 * Created by Chaz-Rae on 9/7/2016.
 */
public class Credentials {
    private UUID mID;
    private String mIpaddress;
    private String mUsername;
    private String mPassword;
    private int mPort;
    private String mWebaddress;

 /*   public Credentials(String ip, String user, String password, int portNum, String web){
        mID = UUID.fromString("e8eabaf8-de77-4d16-acae-7c7269cc5d5e");
        mIpaddress = ip;
        mUsername = user;
        mPassword = password;
        mPort = portNum;
        mWebaddress = web;
    }

    public Credentials(UUID id, String ip, String user, String password, int portNum, String web){
        mID = id;
        mIpaddress = ip;
        mUsername = user;
        mPassword = password;
        mPort = portNum;
        mWebaddress = web;
    } */

    public Credentials(String  web){
        mID = UUID.fromString("e8eabaf8-de77-4d16-acae-7c7269cc5d5e");
        mWebaddress = web;
    }

    public Credentials(UUID id, String web){
        mID = id;
        mWebaddress = web;
    }

    public UUID getID() {
        return mID;
    }

 /*   public String getIpaddress() {
        return mIpaddress;
    }

    public void setIpaddress(String ipaddress) {
        mIpaddress = ipaddress;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public int getPort() {
        return mPort;
    }

    public void setPort(int port) {
        mPort = port;
    } */

    public String getWebaddress() {
        return mWebaddress;
    }

    public void setWebaddress(String webaddress) {
        mWebaddress = webaddress;
    }
}
