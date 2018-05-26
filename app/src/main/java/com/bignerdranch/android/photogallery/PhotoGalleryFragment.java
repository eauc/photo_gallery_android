package com.bignerdranch.android.photogallery;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PhotoGalleryFragment extends Fragment {
    private static final String TAG = "PhotoGalleryFragment";
    private RecyclerView mPhotoRecyclerView;
    private PhotoAdapter mPhotoAdapter;
    private List<GalleryItem> mGalleryItems = new ArrayList<>();
    private int mCurrentFlikrPage = 0;

    public static PhotoGalleryFragment newInstance() {
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchItemsTask().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

        mPhotoRecyclerView = (RecyclerView)v.findViewById(R.id.photo_recycler_view);
        final GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        mPhotoRecyclerView.setLayoutManager(layoutManager);
        mPhotoRecyclerView.getViewTreeObserver()
            .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    Integer nCols = mPhotoRecyclerView.getWidth() / 240;
                    Log.i(TAG, "Change layout nCols: " + nCols);
                    layoutManager.setSpanCount(nCols);
                }
            });
        setupAdapter();

        return v;
    }

    private class FetchItemsTask extends AsyncTask<Void,Void,List<GalleryItem>> {
        @Override
        protected List<GalleryItem> doInBackground(Void... params) {
            return new FlickrFetchr().fetchItems(mCurrentFlikrPage);
        }

        @Override
        protected void onPostExecute(List<GalleryItem> galleryItems) {
            mGalleryItems.addAll(galleryItems);
            mPhotoAdapter.setGalleryItems(mGalleryItems);
        }
    }

    private void setupAdapter() {
        if(isAdded()) {
            mPhotoAdapter = new PhotoAdapter(mGalleryItems);
            mPhotoRecyclerView.setAdapter(mPhotoAdapter);
            if (Build.VERSION.SDK_INT >= 23) {
                mPhotoRecyclerView.setOnScrollChangeListener(new RecyclerView.OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                        if (!mPhotoRecyclerView.canScrollVertically(1)) {
                            mCurrentFlikrPage++;
                            Log.i(TAG, "Load next page: " + mCurrentFlikrPage);
                            new FetchItemsTask().execute();
                        }
                    }
                });
            }
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder {
        private TextView mTitleTextView;

        public PhotoHolder(View itemView) {
            super(itemView);
            mTitleTextView = (TextView) itemView;
        }

        public void bindGalleryItem(GalleryItem item) {
            mTitleTextView.setText(item.toString());
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {
        private List<GalleryItem> mGalleryItems;

        public PhotoAdapter(List<GalleryItem> items) {
            mGalleryItems = items;
        }

        public void setGalleryItems(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
            notifyDataSetChanged();
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView v = new TextView(getActivity());
            return new PhotoHolder(v);
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            GalleryItem item = mGalleryItems.get(position);
            holder.bindGalleryItem(item);
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }
}
