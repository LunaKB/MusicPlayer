package com.moncrieffe.android.musicplayer.Database;

/**
 * Created by Chaz-Rae on 9/7/2016.
 */
public class CredentialsDbSchema {
    public static final class CredentialsTable{
        public static final String NAME ="credentials";

        public static final class Cols{
            public static final String ID = "id";
            public static final String IPADDRESS = "ipaddress";
            public static final String USERNAME = "username";
            public static final String PASSWORD = "password";
            public static final String PORT = "port";
            public static final String WEBADDRESS = "webaddress";
        }
    }
}
