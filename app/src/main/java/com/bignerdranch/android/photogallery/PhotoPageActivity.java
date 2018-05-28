package com.bignerdranch.android.photogallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

public class PhotoPageActivity extends SingleFragmentActivity {
    public static Intent newIntent(Context context, Uri uri) {
        Intent i = new Intent(context, PhotoPageActivity.class);
        i.setData(uri);
        return i;
    }

    @Override
    protected Fragment createFragment() {
        return PhotoPageFragment.newInstance(getIntent().getData());
    }

    @Override
    public void onBackPressed() {
        PhotoPageFragment fragment = (PhotoPageFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        if (fragment != null && fragment.canGoBack()) {
            fragment.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
