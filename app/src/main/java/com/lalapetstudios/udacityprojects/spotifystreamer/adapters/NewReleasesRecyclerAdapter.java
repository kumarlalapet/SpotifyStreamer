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
import com.lalapetstudios.udacityprojects.spotifystreamer.models.NewReleasesModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by g2ishan on 7/12/15.
 */
public class NewReleasesRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = NewReleasesRecyclerAdapter.class.getName();

    private List<NewReleasesModel> dataSet;

    public NewReleasesRecyclerAdapter (List<NewReleasesModel> dataSet) {
        this.dataSet = dataSet;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.new_releases_item, parent, false);

        NewReleasesViewHolder holder = new NewReleasesViewHolder(view);
        view.setTag(holder);

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final NewReleasesViewHolder viewHolder = (NewReleasesViewHolder) holder;
        viewHolder.playListName.setText(dataSet.get(position).getNrName());

        if(dataSet.get(position).getNrCover() != null &&
                dataSet.get(position).getNrCover().getClass().equals(String.class)) {
            final Context localContext = viewHolder.context;
            Picasso.with(localContext).load((String)dataSet.get(position).getNrCover())
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

    private class NewReleasesViewHolder extends RecyclerView.ViewHolder {

        public ImageView playListCover;
        public TextView playListName;
        public Context context;

        public NewReleasesViewHolder (View v) {
            super (v);

            this.context = v.getContext();
            this.playListCover = (ImageView) v.findViewById(R.id.nr_playlist_cover);
            this.playListName = (TextView) v.findViewById(R.id.nr_playlist_name_text);
        }
    }

}
