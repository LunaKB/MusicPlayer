package com.moncrieffe.android.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FTPActivity extends SingleFragmentActivity{
    private String mDirectory;
    private UUID mUUID;

    public static Intent newIntent(Context context, String directory, UUID id){
        Intent i = new Intent(context, FTPActivity.class);
        i.putExtra("DIRECTORY", directory);
        i.putExtra("ID", id);
        return  i;
    }

    @Override
    protected Fragment createFragment() {
        mDirectory = getIntent().getStringExtra("DIRECTORY");
        mUUID = (UUID)getIntent().getSerializableExtra("ID");
        return FTPFragment.newInstance(mDirectory, mUUID);
    }
}
