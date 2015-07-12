package com.lalapetstudios.udacityprojects.spotifystreamer.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lalapetstudios.udacityprojects.spotifystreamer.ArtistDetailActivity;
import com.lalapetstudios.udacityprojects.spotifystreamer.R;
import com.lalapetstudios.udacityprojects.spotifystreamer.models.ResultItem;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

/**
 * Created by g2ishan on 7/10/15.
 */
public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final String CLICKED_RESULT_ITEM = "clicked_result_item";

    private List<ResultItem> dataSet;

    public SearchAdapter (List<ResultItem> dataSet) {
        this.dataSet = dataSet;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.custom_searchable_row_details, parent, false);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = ((ViewHolder)v.getTag()).getLayoutPosition();
                ResultItem item = dataSet.get(position);

                Intent sendIntent = new Intent(v.getContext(), ArtistDetailActivity.class);
                Bundle b = new Bundle();
                b.putParcelable(CLICKED_RESULT_ITEM, item);

                sendIntent.putExtras(b);
                v.getContext().startActivity(sendIntent);

                //((Activity)v.getContext()).finish();
            }
        });

        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);

        // Change layout based on the user option of one or two-lines mode
        //TODO if (!CustomSearchableInfo.getIsTwoLineExhibition()) {
            //holder.subHeader.setVisibility(TextView.GONE);
            //holder.header.setTypeface(Typeface.DEFAULT);
            //view.invalidate();

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.header.setText(dataSet.get(position).getHeader());
        viewHolder.subHeader.setText(dataSet.get(position).getSubHeader());

        if(dataSet.get(position).getLeftIcon().getClass().equals(String.class)) {
            final Context localContext = viewHolder.context;
            Picasso.with(localContext).load((String)dataSet.get(position).getLeftIcon())
                    .resizeDimen(R.dimen.artist_image_width,R.dimen.artist_image_width)
                    .centerCrop()
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            Drawable drawImage = new BitmapDrawable(localContext.getResources(), bitmap);
                            viewHolder.leftIcon.setImageDrawable(drawImage);
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                            // Nothing to do
                        }
            });
        } else {
            viewHolder.leftIcon.setImageResource((Integer)dataSet.get(position).getLeftIcon());
        }

        //viewHolder.rightIcon.setImageResource(dataSet.get(position).getRightIcon());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public ResultItem getItem (Integer position) {
        return dataSet.get(position);
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        public TextView header;
        public TextView subHeader;
        public ImageView leftIcon;
        //public ImageView rightIcon;
        public Context context;

        public ViewHolder (View v) {
            super (v);

            this.context = v.getContext();
            this.header = (TextView) v.findViewById(R.id.rd_header_text);
            this.subHeader = (TextView) v.findViewById(R.id.rd_sub_header_text);
            this.leftIcon = (ImageView) v.findViewById(R.id.rd_left_icon);
            //this.rightIcon = (ImageView) v.findViewById(R.id.rd_right_icon);
        }
    }


}