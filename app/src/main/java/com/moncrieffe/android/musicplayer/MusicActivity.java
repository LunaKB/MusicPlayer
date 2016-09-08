package com.moncrieffe.android.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by Chaz-Rae on 9/7/2016.
 */
public class MusicActivity extends SingleFragmentActivity {
    private String mUrl;

    public static Intent newIntent(Context context, String url){
        Intent i = new Intent(context, MusicActivity.class);
        i.putExtra("URL", url);

        return i;
    }

    @Override
    protected Fragment createFragment() {
        mUrl = getIntent().getStringExtra("URL");
        return MusicFragment.newInstance(mUrl);
    }
}
