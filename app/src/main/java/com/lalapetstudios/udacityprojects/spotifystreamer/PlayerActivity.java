package com.lalapetstudios.udacityprojects.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.lalapetstudios.udacityprojects.spotifystreamer.models.TrackDetailsModel;

import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    TrackDetailsModel currentTrack;
    ArrayList<TrackDetailsModel> trackList;
    public ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.playerToolBar);
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currentTrack = (TrackDetailsModel) getIntent().getExtras().getParcelable(ArtistDetailActivityFragment.TRACK_DETAILS_MODEL);
        trackList = (ArrayList) getIntent().getExtras().getParcelableArrayList(ArtistDetailActivityFragment.TRACK_DETAILS_LIST_MODEL);

        viewPager = (ViewPager) findViewById(R.id.playerViewPager);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.setTrackList(trackList);

        //PagerAdapter wrappedAdapter = new InfinitePagerAdapter(adapter);
        //viewPager.setAdapter(wrappedAdapter);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(0);

        viewPager.setCurrentItem(currentTrack.getPosition());

        /**PlayerFragment playerFragment = new PlayerFragment();
        Bundle argumentsBundle = new Bundle();
        argumentsBundle.putParcelable(ArtistDetailActivityFragment.TRACK_DETAILS_MODEL, currentTrack);
        argumentsBundle.putParcelableArrayList(ArtistDetailActivityFragment.TRACK_DETAILS_LIST_MODEL, trackList);
        playerFragment.setArguments(argumentsBundle);

        adapter.addFragmet(playerFragment);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);**/
    }

    public void goToNextPage() {
        Runnable dirtyHack = new Runnable() {
            @Override
            public void run() {
                viewPager.setCurrentItem(viewPager.getCurrentItem()+1,true);
            }
        };
        Handler handler = new Handler();
        handler.postDelayed(dirtyHack, 100);
    }

    public void goToPreviousPage() {
        Runnable dirtyHack = new Runnable() {
            @Override
            public void run() {
                viewPager.setCurrentItem(viewPager.getCurrentItem()-1,true);
            }
        };
        Handler handler = new Handler();
        handler.postDelayed(dirtyHack, 100);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_search) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            return true;
        } else {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    static class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private ArrayList<TrackDetailsModel> trackList;

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            PlayerFragment playerFragment = new PlayerFragment();
            Bundle argumentsBundle = new Bundle();
            argumentsBundle.putParcelable(ArtistDetailActivityFragment.TRACK_DETAILS_MODEL, trackList.get(position));
            playerFragment.setArguments(argumentsBundle);
            return playerFragment;
        }

        @Override
        public int getCount() {
            return trackList.size();
        }

        public void setTrackList(ArrayList<TrackDetailsModel> trackList) {
            this.trackList = trackList;
        }
    }

}
