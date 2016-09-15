package com.moncrieffe.android.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
import java.util.zip.Inflater;


/**
 * Created by Chaz-Rae on 9/7/2016.
 */
public class DirectoryMenuActivity extends SingleFragmentActivity {

    public static Intent newIntent(Context context, UUID id){
        Intent i = new Intent(context, DirectoryMenuActivity.class);
        i.putExtra("ID", id);
        return i;
    }


    @Override
    protected Fragment createFragment() {
        UUID id = (UUID)getIntent().getSerializableExtra("ID");
        return DirectoryMenuFragment.newInstance(id);
    }
}
