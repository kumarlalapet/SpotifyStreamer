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
import com.lalapetstudios.udacityprojects.spotifystreamer.models.FeaturedPlayListModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by g2ishan on 7/12/15.
 */
public class FeaturedPlayListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = FeaturedPlayListRecyclerAdapter.class.getName();

    private List<FeaturedPlayListModel> dataSet;

    public FeaturedPlayListRecyclerAdapter (List<FeaturedPlayListModel> dataSet) {
        this.dataSet = dataSet;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.featured_playlist_item, parent, false);

        FeaturedPlayListViewHolder holder = new FeaturedPlayListViewHolder(view);
        view.setTag(holder);

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final FeaturedPlayListViewHolder viewHolder = (FeaturedPlayListViewHolder) holder;
        viewHolder.playListName.setText(dataSet.get(position).getPlayListName());
        viewHolder.numberOfTracksName.setText(dataSet.get(position).getPlayListTrackNumber()+" "+viewHolder.context.getString(R.string.tracks));
        //viewHolder.duration.setText(formatDuration(dataSet.get(position).getDurationInMs()));

        if(dataSet.get(position).getPlayListCover() != null &&
                dataSet.get(position).getPlayListCover().getClass().equals(String.class)) {
            final Context localContext = viewHolder.context;
            Picasso.with(localContext).load((String)dataSet.get(position).getPlayListCover())
                    .resizeDimen(R.dimen.playlist_image_width,R.dimen.playlist_image_height)
                    .centerCrop()
                    .into(viewHolder.playListCover, new Callback.EmptyCallback() {
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
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    private class FeaturedPlayListViewHolder extends RecyclerView.ViewHolder {

        public ImageView playListCover;
        public TextView playListName;
        public TextView numberOfTracksName;
        public Context context;

        public FeaturedPlayListViewHolder (View v) {
            super (v);

            this.context = v.getContext();
            this.playListCover = (ImageView) v.findViewById(R.id.fp_playlist_cover);
            this.playListName = (TextView) v.findViewById(R.id.fp_playlist_name_text);
            this.numberOfTracksName = (TextView) v.findViewById(R.id.fp_track_number_text);
        }
    }

}
