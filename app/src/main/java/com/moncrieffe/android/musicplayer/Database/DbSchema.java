package com.moncrieffe.android.musicplayer.Database;

/**
 * Created by Chaz-Rae on 9/7/2016.
 */
public class DbSchema {
    public static final class CredentialsTable{
        public static final String NAME = "credentials";

        public static final class Cols{
            public static final String ID = "id";
            public static final String IPADDRESS = "ipaddress";
            public static final String USERNAME = "username";
            public static final String PASSWORD = "password";
            public static final String PORT = "port";
            public static final String WEBADDRESS = "webaddress";
        }
    }

    public static final class DirectoryTable{
        public static final String NAME = "directory";

        public static final class Cols{
            public static final String DIRECTORY = "directory";
        }
    }

    public static final class SongsTable{
        public static final String NAME = "songs";

        public static final class Cols{
            public static final String DIRECTORY = "directory";
            public static final String NAME = "name";
            public static final String ARTIST = "artist";
            public static final String ALBUM = "album";
        }
    }
}
