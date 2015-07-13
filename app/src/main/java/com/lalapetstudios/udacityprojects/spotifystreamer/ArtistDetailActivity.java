package com.lalapetstudios.udacityprojects.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.lalapetstudios.udacityprojects.spotifystreamer.adapters.ArtistDetailRecyclerAdapter;
import com.lalapetstudios.udacityprojects.spotifystreamer.adapters.TopTracksRecyclerAdapter;
import com.lalapetstudios.udacityprojects.spotifystreamer.contentproviders.TopTracksByArtistContentProvider;
import com.lalapetstudios.udacityprojects.spotifystreamer.models.ResultItem;
import com.lalapetstudios.udacityprojects.spotifystreamer.models.TrackDetailsModel;
import com.lalapetstudios.udacityprojects.spotifystreamer.util.OnAsyncTaskCompletedInterface;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ArtistDetailActivity extends AppCompatActivity implements OnAsyncTaskCompletedInterface {

    private static final String TAG = ArtistDetailActivity.class.getName();
    private static final String TRACK_DETAILS_MODEL = "TRACK_DETAILS_MODEL";

    CollapsingToolbarLayout collapsingToolbar;
    RecyclerView recyclerView;
    int mutedColor = R.attr.colorPrimary;
    ArtistDetailRecyclerAdapter simpleRecyclerAdapter;
    TopTracksRecyclerAdapter topTracksRecyclerAdapter;
    ResultItem item;
    ArrayList<TrackDetailsModel> mResultList;
    View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        item = getIntent().getExtras().getParcelable(SearchActivity.CLICKED_RESULT_ITEM);

        setContentView(R.layout.activity_artist_detail);

        //TODO include the fragment in this activity

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        Toolbar toolbar = (Toolbar) findViewById(R.id.anim_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(item.getHeader());
        collapsingToolbar.setContentDescription("Top tracks");
        collapsingToolbar.setSystemUiVisibility(CollapsingToolbarLayout.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        final ImageView header = (ImageView) findViewById(R.id.header);

        Resources r = getResources();
        final float heightInPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                r.getDimension(R.dimen.artist_header_image_height), r.getDisplayMetrics());
        final float widthInPx = r.getDisplayMetrics().widthPixels;

        if(item.getLeftIcon().getClass().equals(String.class)) {
            final Context localContext = this;
            header.setPadding(0,0,0,0);
            Picasso.with(this).load((String) item.getLeftIcon())
                    .resizeDimen(R.dimen.artist_header_image_weight,R.dimen.artist_header_image_height)
                    .onlyScaleDown()
                    //.centerCrop()
                    .into(header, new Callback.EmptyCallback() {
                        @Override
                        public void onSuccess() {
                            //TODO to add progress bar progressBar.setVisibility(View.GONE);
                            Log.e(TAG, "yeah success");
                            //use the palete api to set the title text color appropriately
                            Bitmap bitmap = drawableToBitmap(header.getDrawable());
                            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(Palette palette) {
                                    mutedColor = palette.getDarkMutedColor(R.attr.colorPrimary);
                                    collapsingToolbar.setExpandedTitleColor(mutedColor);
                                }
                            });
                        }

                        @Override
                        public void onError() {
                            //TODO to add progress bar progressBar.setVisibility(View.GONE);
                            Log.e(TAG,"error while loading image");
                        }
                    });
        }

        rootView = findViewById(R.id.artisti_detail_rootview);

        recyclerView = (RecyclerView) findViewById(R.id.scrollableview);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        if( savedInstanceState != null ){
            this.mResultList = savedInstanceState.getParcelableArrayList(TRACK_DETAILS_MODEL);
            this.createTopTracksRecyclerAdapter();
        } else {
            mapResultsFromTopTracksContentProviderToList();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(TRACK_DETAILS_MODEL, mResultList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_artist_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            return true;
        } else {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAsyncTaskCompleted(boolean pErrorFlag) {
        if(pErrorFlag) {
            Snackbar
                    .make(rootView, R.string.no_interent_connection_text, Snackbar.LENGTH_LONG)
                    .show();
        } else {
            this.createTopTracksRecyclerAdapter();
        }
    }

    private void createTopTracksRecyclerAdapter() {
        topTracksRecyclerAdapter = new TopTracksRecyclerAdapter(mResultList);
        recyclerView.setAdapter(topTracksRecyclerAdapter);
    }

    // Map the results into tracks models from the provider
    private void mapResultsFromTopTracksContentProviderToList () {
        new AsyncTask<Void, Void, ArrayList>() {
            @Override
            protected void onPostExecute(ArrayList resultList) {
                mResultList = resultList;
                boolean errorFlag = resultList == null ? true : false;
                onAsyncTaskCompleted(errorFlag);
            }

            @Override
            protected ArrayList doInBackground(Void[] params) {
                Cursor results = null;
                try {
                    results = queryTopTracksProvider();
                } catch(Exception ex) {
                    Log.e(TAG, "Issues in getting data from provider");
                    return null;
                }
                ArrayList<TrackDetailsModel> resultList = new ArrayList<>();

                Integer idIdx = results.getColumnIndex(TopTracksByArtistContentProvider.ID);
                Integer albumCoverIdx = results.getColumnIndex(TopTracksByArtistContentProvider.ALBUM_COVER);
                Integer trackNameIdx = results.getColumnIndex(TopTracksByArtistContentProvider.TRACK_NAME);
                Integer albumNameIdx = results.getColumnIndex(TopTracksByArtistContentProvider.ALBUM_NAME);
                Integer durationIdx = results.getColumnIndex(TopTracksByArtistContentProvider.DURATION_IN_MS);

                while (results.moveToNext()) {
                    String id = results.getString(idIdx);
                    String albumCover = results.getString(albumCoverIdx);
                    String trackName = results.getString(trackNameIdx);
                    String albumName = results.getString(albumNameIdx);
                    String duration = results.getString(durationIdx);

                    TrackDetailsModel trackDtls = new TrackDetailsModel(id,albumCover,trackName,albumName,duration);
                    resultList.add(trackDtls);
                }

                results.close();
                return resultList;
            }
        }.execute();
    }

    private Cursor queryTopTracksProvider() {
        Uri uri = Uri.parse("content://".concat(TopTracksByArtistContentProvider.PROVIDER_AUTHORITY)
                .concat("/top-tracks/").concat(item.getId()));

        return ArtistDetailActivity.this.getContentResolver().query(
                uri,
                null,
                item.getId(),
                null,
                null
        );
    }

    private Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

}
