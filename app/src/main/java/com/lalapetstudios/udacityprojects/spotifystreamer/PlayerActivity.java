package com.lalapetstudios.udacityprojects.spotifystreamer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;

import com.lalapetstudios.udacityprojects.spotifystreamer.models.TrackDetailsModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerActivity extends AppCompatActivity {

    private static final String TAG = PlayerActivity.class.getName();

    TrackDetailsModel currentTrack;
    ArrayList<TrackDetailsModel> trackList;
    public ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;

    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound=false;
    private AtomicBoolean atomicMusicBound = new AtomicBoolean(false);

    private List<String> songList;

    Thread updateProgressThread =  new Thread(new ProgressUpdateThread());
    private Handler updateProgressThreadHandler = new Handler();

    /**
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message inputMessage) {
            Log.d(TAG,"Inside handleMessage");
            viewPagerAdapter.currentFragment.updatePlayingUIState();

            updateProgressThreadHandler.removeCallbacks(updateProgressThread);
            updateProgressThreadHandler.postDelayed(updateProgressThread, 1000);
        }
    };
     **/

    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            //get service
            musicSrv = binder.getService();
            //pass list
            musicSrv.setSongList(songList);
            musicBound = true;
            Log.d(TAG,"onServiceConnected method "+"music service bound flag "+musicBound);

            atomicMusicBound.set(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
            atomicMusicBound.set(false);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.playerToolBar);
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currentTrack = (TrackDetailsModel) getIntent().getExtras().getParcelable(ArtistDetailActivityFragment.TRACK_DETAILS_MODEL);
        trackList = (ArrayList) getIntent().getExtras().getParcelableArrayList(ArtistDetailActivityFragment.TRACK_DETAILS_LIST_MODEL);
        songList = new ArrayList<>();
        for(TrackDetailsModel trackDetailsModel : trackList) {
            songList.add(trackDetailsModel.getPreviewUrl());
        }

        viewPager = (ViewPager) findViewById(R.id.playerViewPager);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.setTrackList(trackList);

        //PagerAdapter wrappedAdapter = new InfinitePagerAdapter(adapter);
        //viewPager.setAdapter(wrappedAdapter);
        viewPager.setAdapter(viewPagerAdapter);
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
        (new Thread(new MyRunnable(atomicMusicBound))).start();
        //Thread updateProgressThread =  new Thread(new ProgressUpdateThread(atomicMusicBound));
        //updateProgressThread.start();

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                playSong(viewPager.getCurrentItem());
                //viewPagerAdapter.
                Log.d(TAG, "Current fragment " + viewPagerAdapter.getCurrentFragment().currentTrack.getTrackName());
            }
        });

        //updateProgressThreadHandler.removeCallbacks(updateProgressThread);
        //updateProgressThreadHandler.postDelayed(updateProgressThread, 100);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this, MusicService.class);
            getApplicationContext().bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
    }

    @Override
    protected void onDestroy() {
        stopService(playIntent);
        updateProgressThreadHandler.removeCallbacks(updateProgressThread);
        musicSrv=null;
        /**if(updateProgressThread != null)
            updateProgressThread.interrupt();**/
        super.onDestroy();
    }

    public void goToNextPage() {
        Runnable dirtyHack = new Runnable() {
            @Override
            public void run() {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
            }
        };
        Handler handler = new Handler();
        handler.postDelayed(dirtyHack, 100);
    }

    public void goToPreviousPage() {
        Runnable dirtyHack = new Runnable() {
            @Override
            public void run() {
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
            }
        };
        Handler handler = new Handler();
        handler.postDelayed(dirtyHack, 100);
    }

    public void playSong(int position) {
        Log.d(TAG, "playSong method" + " music service bound flag " + musicBound);
        musicSrv.setSong(position);
        musicSrv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                //viewPagerAdapter.currentFragment.updatePlayingUIState();
                updateProgressThreadHandler.removeCallbacks(updateProgressThread);
                updateProgressThreadHandler.post(updateProgressThread);
            }
        });
        musicSrv.playSong();
        //mHandler.obtainMessage().sendToTarget();
    }

    public void pauseMusic() {
        //updateProgressThreadHandler.removeCallbacks(updateProgressThread);
        musicSrv.pauseSong();
    }

    public void continueMusic() {
        updateProgressThreadHandler.postDelayed(updateProgressThread, 100);
        musicSrv.continueSong();
    }

    public void seek(int progress) {
        musicSrv.seek(progress);
    }

    class MyRunnable implements Runnable {
        AtomicBoolean condition;
        public MyRunnable(AtomicBoolean condition2) {
            this.condition = condition2;
        }

        @Override
        public void run() {
            while (true) {
                if (condition.get() == true) {
                    Log.d(TAG,"run "+"music service bound flag "+musicBound);
                    playSong(currentTrack.getPosition());
                    break;
                }
            }
        }
    }

    class ProgressUpdateThread implements Runnable{
        @Override
        public void run() {
            Log.d(TAG, "Inside ProgressUpdateThread run");
            if (musicSrv != null && musicSrv.isPlaying()) {
                Log.d(TAG, "Inside ProgressUpdateThread run isPlaying");
                viewPagerAdapter.currentFragment.updateSeekBarUIState(musicSrv.getCurrentPosition());
                updateProgressThreadHandler.postDelayed(this, 100);
            }
        }
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
        private PlayerFragment currentFragment;

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            PlayerFragment newFragment = new PlayerFragment();
            Bundle argumentsBundle = new Bundle();
            argumentsBundle.putParcelable(ArtistDetailActivityFragment.TRACK_DETAILS_MODEL, trackList.get(position));
            newFragment.setArguments(argumentsBundle);
            return newFragment;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Object obj = super.instantiateItem(container, position);
            //currentFragment = (PlayerFragment)obj;
            return obj;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            currentFragment = (PlayerFragment)object;
        }

        @Override
        public int getCount() {
            return trackList.size();
        }

        public void setTrackList(ArrayList<TrackDetailsModel> trackList) {
            this.trackList = trackList;
        }

        public PlayerFragment getCurrentFragment(){
            return currentFragment;
        }
    }

}
