package com.lalapetstudios.udacityprojects.spotifystreamer.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lalapetstudios.udacityprojects.spotifystreamer.R;
import com.lalapetstudios.udacityprojects.spotifystreamer.models.TrackDetailsModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by g2ishan on 7/11/15.
 */
public class TopTracksRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = TopTracksRecyclerAdapter.class.getName();
    private static final String DEFAULT_DURATION_STR = "0:00";

    private List<TrackDetailsModel> dataSet;

    public TopTracksRecyclerAdapter (List<TrackDetailsModel> dataSet) {
        this.dataSet = dataSet;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.track_row_details, parent, false);

        TopTracksViewHolder holder = new TopTracksViewHolder(view);
        view.setTag(holder);

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final TopTracksViewHolder viewHolder = (TopTracksViewHolder) holder;
        viewHolder.trackName.setText(dataSet.get(position).getTrackName());
        viewHolder.albumName.setText(dataSet.get(position).getAlbumName());
        viewHolder.duration.setText(formatDuration(dataSet.get(position).getDurationInMs()));

        if(dataSet.get(position).getAlbumCover() != null &&
                dataSet.get(position).getAlbumCover().getClass().equals(String.class)) {
            final Context localContext = viewHolder.context;
            Picasso.with(localContext).load((String)dataSet.get(position).getAlbumCover())
                    .resizeDimen(R.dimen.artist_image_width,R.dimen.artist_image_width)
                    .centerCrop()
                    .into(viewHolder.albumCover, new Callback.EmptyCallback() {
                        @Override
                        public void onSuccess() {
                            Log.i(TAG, "yeah success");
                        }

                        @Override
                        public void onError() {
                            Log.e(TAG,"error while loading image");
                        }
                    });
        }
        //else {
        //   viewHolder.leftIcon.setImageResource((Integer)dataSet.get(position).getLeftIcon());
        //}

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    private class TopTracksViewHolder extends RecyclerView.ViewHolder {

        public TextView trackName;
        public TextView albumName;
        public ImageView albumCover;
        public TextView duration;
        public Context context;

        public TopTracksViewHolder (View v) {
            super (v);

            this.context = v.getContext();
            this.trackName = (TextView) v.findViewById(R.id.track_name_text);
            this.albumName = (TextView) v.findViewById(R.id.album_name_text);
            this.albumCover = (ImageView) v.findViewById(R.id.album_cover);
            this.duration = (TextView) v.findViewById(R.id.track_duration);
        }
    }

    private String formatDuration(String millisString) {
        try {
            long millis = Long.parseLong(millisString);
            String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            return hms.substring(4, hms.length());
        } catch(Exception e) {
            Log.e(TAG,e.getMessage());
        }
        return DEFAULT_DURATION_STR;
    }

}
