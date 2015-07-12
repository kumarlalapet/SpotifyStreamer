package com.lalapetstudios.udacityprojects.spotifystreamer;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lalapetstudios.udacityprojects.spotifystreamer.adapters.FeaturedPlayListRecyclerAdapter;
import com.lalapetstudios.udacityprojects.spotifystreamer.adapters.TopTracksRecyclerAdapter;
import com.lalapetstudios.udacityprojects.spotifystreamer.contentproviders.FeaturedPlayListContentProvider;
import com.lalapetstudios.udacityprojects.spotifystreamer.contentproviders.TopTracksByArtistContentProvider;
import com.lalapetstudios.udacityprojects.spotifystreamer.models.FeaturedPlayListModel;
import com.lalapetstudios.udacityprojects.spotifystreamer.models.TrackDetailsModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by g2ishan on 7/6/15.
 */
public class FeaturedPlaylistFragment extends Fragment {

    RecyclerView recyclerView;
    FeaturedPlayListRecyclerAdapter fpRecyclerAdapter;

    public FeaturedPlaylistFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_featuredplaylist, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.fp_recyclerview);

        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(v.getContext(),2);
        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(v.getContext());
        recyclerView.setLayoutManager(gridLayoutManager);

        mapResultsFromFeaturedPlayListContentProviderToList();

        return v;
    }

    private void mapResultsFromFeaturedPlayListContentProviderToList() {
        new AsyncTask<Void, Void, List>() {
            @Override
            protected void onPostExecute(List resultList) {
                fpRecyclerAdapter = new FeaturedPlayListRecyclerAdapter(resultList);
                recyclerView.setAdapter(fpRecyclerAdapter);
            }

            @Override
            protected List doInBackground(Void[] params) {
                Cursor results = results = queryFeaturedPlayListProvider();
                List<FeaturedPlayListModel> resultList = new ArrayList<>();

                Integer idIdx = results.getColumnIndex(FeaturedPlayListContentProvider.ID);
                Integer playListCoverIdx = results.getColumnIndex(FeaturedPlayListContentProvider.PLAY_LIST_COVER);
                Integer playListNameIdx = results.getColumnIndex(FeaturedPlayListContentProvider.PLAY_LIST_NAME);
                Integer trackNumberIdx = results.getColumnIndex(FeaturedPlayListContentProvider.TRACK_TOTAL);
                Integer durationIdx = results.getColumnIndex(FeaturedPlayListContentProvider.MESSAGE);

                while (results.moveToNext()) {
                    String id = results.getString(idIdx);
                    String playListCover = results.getString(playListCoverIdx);
                    String playListName = results.getString(playListNameIdx);
                    String traskNumber = results.getString(trackNumberIdx);
                    String message = results.getString(durationIdx);

                    FeaturedPlayListModel trackDtls = new FeaturedPlayListModel(id,playListCover,playListName,traskNumber,message);
                    resultList.add(trackDtls);
                }

                results.close();
                return resultList;
            }
        }.execute();
    }

    private Cursor queryFeaturedPlayListProvider() {
        Uri uri = Uri.parse("content://".concat(FeaturedPlayListContentProvider.PROVIDER_AUTHORITY)
                .concat("/featured-playlists/1"));

        return FeaturedPlaylistFragment.this.getActivity().getContentResolver().query(
                uri,
                null,
                null,
                null,
                null
        );
    }

}
