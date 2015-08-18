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


/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerFragment extends Fragment {

    View rootView;
    TrackDetailsModel currentTrack;
    ImageView trackCoverImage;
    TextView trackNameTextView;
    TextView artistNameTextView;
    TextView albumNameTextView;
    ImageView thumbNailImageView;
    RippleView playpauseButton;
    SeekBar seekbar;

    public PlayerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_player, container, false);
        currentTrack = (TrackDetailsModel) getActivity().getIntent().getExtras().getParcelable(ArtistDetailActivityFragment.TRACK_DETAILS_MODEL);

        setupView();
        return rootView;
    }

    private void setupView() {
        trackNameTextView = (TextView) rootView.findViewById(R.id.trackName);
        trackNameTextView.setText(currentTrack.getTrackName());

        albumNameTextView = (TextView) rootView.findViewById(R.id.albumName);
        albumNameTextView.setText(currentTrack.getAlbumName());

        artistNameTextView = (TextView) rootView.findViewById(R.id.artistName);
        artistNameTextView.setText(currentTrack.getArtists());

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
                        //seekbar.setVisibility(View.VISIBLE);
                    }

                });

        thumbNailImageView = (ImageView) rootView.findViewById(R.id.thumbNailImageView);
        trackCoverImage = (ImageView) rootView.findViewById(R.id.trackCoverImage);

        if (currentTrack.getAlbumCover() != null &&
                currentTrack.getAlbumCover().getClass().equals(String.class)) {
            Picasso.with(getActivity()).load(currentTrack.getAlbumCover()).fit().into(thumbNailImageView);
            Picasso.with(getActivity()).load(currentTrack.getAlbumCover()).fit().into(trackCoverImage);

            /**new Target() {
            @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            rootView.setBackground(new BitmapDrawable(getActivity().getResources(), bitmap));
            }

            @Override public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
            });**/
        }
    }

}
