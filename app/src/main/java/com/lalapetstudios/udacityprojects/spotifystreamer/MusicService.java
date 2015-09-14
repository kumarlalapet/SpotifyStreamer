package com.lalapetstudios.udacityprojects.spotifystreamer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.lalapetstudios.udacityprojects.spotifystreamer.models.PreviewUrlModel;

import java.util.List;

/**
 * Created by g2ishan on 8/22/15.
 */
public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    private MediaPlayer player;
    private List<PreviewUrlModel> songs;
    private int songPosn;
    private final IBinder musicBind = new MusicBinder();
    private int mCurrentPosition;
    public boolean restoredFromBundle = false;

    @Override
    public void onCreate() {
        super.onCreate();
        songPosn=0;
        player = new MediaPlayer();
        initMusicPlayer();
    }

    public void initMusicPlayer(){
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void setSongList(List<PreviewUrlModel> theSongs){
        songs=theSongs;
    }

    public void setSong(int songIndex){
        songPosn=songIndex;
    }

    public boolean isPlaying() {
        if(player != null)
            return player.isPlaying();
        return false;
    }

    public void seek(int progress) {
        if(player != null)
            player.seekTo(progress);
    }

    public void setOnPreparedListener(MediaPlayer.OnPreparedListener listener) {
        player.setOnPreparedListener(listener);
    }

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    public void playSong(){
        player.reset();
        Uri trackUri = Uri.parse(songs.get(songPosn).getPreviewUrl());
        try{
            player.setDataSource(getApplicationContext(), trackUri);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
            //TODO show this error to user
        }
        player.prepareAsync();
    }

    public void pauseSong() {
        if (player != null && player.isPlaying()) {
            player.pause();
            mCurrentPosition = player.getCurrentPosition();
        }
    }

    public void continueSong() {
        if (player != null && !player.isPlaying()) {
            player.start();
        }
    }

    public int getCurrentPosition(){
        if(player != null)
            return player.getCurrentPosition();
        return 0;
    }

    public int getDuration() {
        if(player != null)
            return player.getDuration();
        return 0;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.getExtras() != null)
            restoredFromBundle = intent.getExtras().getBoolean(PlayerActivity.RESTORED_FROM_BUNDLE);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onRebind(Intent intent) {
        if(intent.getExtras() != null)
            restoredFromBundle = intent.getExtras().getBoolean(PlayerActivity.RESTORED_FROM_BUNDLE);
        super.onRebind(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if(intent.getExtras() != null)
            restoredFromBundle = intent.getExtras().getBoolean(PlayerActivity.RESTORED_FROM_BUNDLE);
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }

}
