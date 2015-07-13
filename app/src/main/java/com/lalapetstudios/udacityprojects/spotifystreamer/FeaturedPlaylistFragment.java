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
import com.lalapetstudios.udacityprojects.spotifystreamer.adapters.NewReleasesRecyclerAdapter;
import com.lalapetstudios.udacityprojects.spotifystreamer.adapters.TopTracksRecyclerAdapter;
import com.lalapetstudios.udacityprojects.spotifystreamer.contentproviders.FeaturedPlayListContentProvider;
import com.lalapetstudios.udacityprojects.spotifystreamer.contentproviders.TopTracksByArtistContentProvider;
import com.lalapetstudios.udacityprojects.spotifystreamer.models.FeaturedPlayListModel;
import com.lalapetstudios.udacityprojects.spotifystreamer.models.TrackDetailsModel;
import com.lalapetstudios.udacityprojects.spotifystreamer.util.OnAsyncTaskCompletedInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by g2ishan on 7/6/15.
 */
public class FeaturedPlaylistFragment extends Fragment implements OnAsyncTaskCompletedInterface {

    private static final String FEATURED_PLAYLIST_MODEL = "FEATURED_PLAYLIST_MODEL";

    RecyclerView recyclerView;
    FeaturedPlayListRecyclerAdapter fpRecyclerAdapter;
    ArrayList<FeaturedPlayListModel> mResultList;

    public FeaturedPlaylistFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_featuredplaylist, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.fp_recyclerview);

        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(v.getContext(),2);
        recyclerView.setLayoutManager(gridLayoutManager);

        if( savedInstanceState != null ){
            this.mResultList = savedInstanceState.getParcelableArrayList(FEATURED_PLAYLIST_MODEL);
            this.createFeaturedPlayListRecyclerAdapter();
        } else {
            mapResultsFromFeaturedPlayListContentProviderToList();
        }

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(FEATURED_PLAYLIST_MODEL, mResultList);
    }

    @Override
    public void onAsyncTaskCompleted() {
        this.createFeaturedPlayListRecyclerAdapter();
    }

    private void createFeaturedPlayListRecyclerAdapter() {
        fpRecyclerAdapter = new FeaturedPlayListRecyclerAdapter(mResultList);
        recyclerView.setAdapter(fpRecyclerAdapter);
    }

    private void mapResultsFromFeaturedPlayListContentProviderToList() {
        new AsyncTask<Void, Void, ArrayList>() {
            @Override
            protected void onPostExecute(ArrayList resultList) {
                mResultList = resultList;
                onAsyncTaskCompleted();
            }

            @Override
            protected ArrayList doInBackground(Void[] params) {
                Cursor results = results = queryFeaturedPlayListProvider();
                ArrayList<FeaturedPlayListModel> resultList = new ArrayList<>();

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
