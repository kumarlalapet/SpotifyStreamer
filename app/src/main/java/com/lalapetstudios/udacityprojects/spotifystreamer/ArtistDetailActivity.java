package com.lalapetstudios.udacityprojects.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.lalapetstudios.udacityprojects.spotifystreamer.models.ResultItem;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class ArtistDetailActivity extends AppCompatActivity {

    private static final String TAG = ArtistDetailActivity.class.getName();

    CollapsingToolbarLayout collapsingToolbar;
    int mutedColor = R.attr.colorPrimary;
    ResultItem item;
    View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);

        super.onCreate(savedInstanceState);

        item = getIntent().getExtras().getParcelable(SearchActivity.CLICKED_RESULT_ITEM);

        setContentView(R.layout.activity_artist_detail);

        //TODO include the fragment in this activity

        //android:fitsSystemWindows="true"
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

        final ViewPager viewPager = (ViewPager) findViewById(R.id.artistDetailViewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragmet(new ArtistDetailActivityFragment());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
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

    static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragmet(Fragment fragment) {
            mFragmentList.add(fragment);
        }

    }

}
