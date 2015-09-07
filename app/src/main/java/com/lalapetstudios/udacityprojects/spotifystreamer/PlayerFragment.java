package com.lalapetstudios.udacityprojects.spotifystreamer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.lalapetstudios.udacityprojects.spotifystreamer.models.TrackDetailsModel;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerFragment extends Fragment {

    TrackDetailsModel currentTrack;

    View rootView;
    ImageView trackCoverImage;
    TextView trackNameTextView;
    TextView artistNameTextView;
    TextView albumNameTextView;
    ImageView thumbNailImageView;
    TextView rightTimeTrackerText;
    TextView leftTimeTrackerText;
    RippleView playpauseButton;
    SeekBar seekbar;
    RippleView nextButton;
    RippleView previousButton;
    ViewPager viewPager;
    boolean songPlaying = false;
    int maxPosition = 30000;

    private static final String TAG = PlayerFragment.class.getName();

    public PlayerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_player, container, false);

        currentTrack = (TrackDetailsModel) getArguments().getParcelable(ArtistDetailActivityFragment.TRACK_DETAILS_MODEL);

        rightTimeTrackerText = (TextView) rootView.findViewById(R.id.rightTimeTrackerText);
        leftTimeTrackerText = (TextView) rootView.findViewById(R.id.leftTimeTrackerText);

        trackNameTextView = (TextView) rootView.findViewById(R.id.trackName);
        albumNameTextView = (TextView) rootView.findViewById(R.id.albumName);
        artistNameTextView = (TextView) rootView.findViewById(R.id.artistName);
        playpauseButton = (RippleView) rootView.findViewById(R.id.playpauseButton);
        this.updatePlayingUIState();
        playpauseButton.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if(songPlaying) {
                    ((ImageView) rippleView.getChildAt(0)).setImageDrawable(getResources().getDrawable(R.drawable.ic_av_play_circle_fill));
                    ((PlayerActivity)getActivity()).pauseMusic();
                    songPlaying = false;
                } else {
                    ((ImageView) rippleView.getChildAt(0)).setImageDrawable(getResources().getDrawable(R.drawable.ic_av_pause_circle_fill));
                    ((PlayerActivity)getActivity()).continueMusic();
                    songPlaying = true;
                }
            }
        });
        seekbar = (SeekBar) rootView.findViewById(R.id.seekBar);
        seekbar.setMax(maxPosition);
        seekbar.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        seekbar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) seekbar.getLayoutParams();
                        params.setMargins(0, -seekbar.getHeight() / 2, 0, -seekbar.getHeight() / 2);
                        seekbar.setLayoutParams(params);
                    }

                });

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG,"Current Seekbar position "+seekBar.getProgress());
                ((PlayerActivity)getActivity()).seek(seekBar.getProgress());
                updateSeekBarUIState(seekBar.getProgress());
            }
        });

        thumbNailImageView = (ImageView) rootView.findViewById(R.id.thumbNailImageView);
        trackCoverImage = (ImageView) rootView.findViewById(R.id.trackCoverImage);

        nextButton = (RippleView) rootView.findViewById(R.id.nextButton);
        nextButton.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                ((PlayerActivity)getActivity()).goToNextPage();
            }
        });

        previousButton = (RippleView) rootView.findViewById(R.id.previousButton);
        previousButton.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                ((PlayerActivity)getActivity()).goToPreviousPage();
            }
        });

        viewPager = (ViewPager) getActivity().findViewById(R.id.playerViewPager);

        setupView();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Do your Work
        } else {
            // Do your Work
        }
    }

    private void setupView() {
        trackNameTextView.setText(currentTrack.getTrackName());
        albumNameTextView.setText(currentTrack.getAlbumName());
        artistNameTextView.setText(currentTrack.getArtists());
        if (currentTrack.getAlbumCover() != null &&
                currentTrack.getAlbumCover().getClass().equals(String.class)) {
            Picasso.with(getActivity()).load(currentTrack.getAlbumCover()).fit().into(thumbNailImageView);
            Picasso.with(getActivity()).load(currentTrack.getAlbumCover()).fit().into(trackCoverImage);
        }
    }

    void updateSeekBarUIState(int currentPosition) {

        String currentPositionStr = "";
        if(currentPosition < 1000) {
            //still in zeors
            currentPositionStr = "0";
        } else if(currentPosition < 10000) {
            //less than 10
            currentPositionStr = currentPositionStr + Integer.toString(currentPosition).charAt(0);
        } else {
            //10 or more seconds
            currentPositionStr = currentPositionStr + Integer.toString(currentPosition).charAt(0);
            currentPositionStr = currentPositionStr + Integer.toString(currentPosition).charAt(1);
        }
        if(currentPosition < 10000)
            leftTimeTrackerText.setText("0:0"+currentPositionStr);
        else
            leftTimeTrackerText.setText("0:"+currentPositionStr);

        //seekbar.setProgress(Integer.parseInt(currentPositionStr));
        seekbar.setProgress(currentPosition);

        Log.d(TAG, "Inside updateSeekBarUIState " + currentPosition + " " + currentPositionStr);
    }

    void updatePlayingUIState(){
        ImageView playPauseImageView = (ImageView)playpauseButton.getChildAt(0);
        Picasso.with(this.getActivity()).load(R.drawable.ic_av_pause_circle_fill).into(playPauseImageView);
        songPlaying = true;
    }

    /**
    void updatePausedUIState(){
        ((ImageView)playpauseButton.getChildAt(0)).setImageDrawable(getResources().getDrawable(R.drawable.ic_av_play_circle_fill));
        songPlaying = false;
    }
     **/
}
