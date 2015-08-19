package com.lalapetstudios.udacityprojects.spotifystreamer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
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
    RippleView playpauseButton;
    SeekBar seekbar;
    RippleView nextButton;
    RippleView previousButton;

    public PlayerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_player, container, false);

        currentTrack = (TrackDetailsModel) getArguments().getParcelable(ArtistDetailActivityFragment.TRACK_DETAILS_MODEL);

        trackNameTextView = (TextView) rootView.findViewById(R.id.trackName);
        albumNameTextView = (TextView) rootView.findViewById(R.id.albumName);
        artistNameTextView = (TextView) rootView.findViewById(R.id.artistName);
        playpauseButton = (RippleView) rootView.findViewById(R.id.playpauseButton);
        playpauseButton.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                ((ImageView) rippleView.getChildAt(0)).setImageDrawable(getResources().getDrawable(R.drawable.ic_av_pause_circle_fill));
            }
        });
        seekbar = (SeekBar) rootView.findViewById(R.id.seekBar);
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

        setupView();
        return rootView;
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

}
