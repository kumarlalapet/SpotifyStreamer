package com.lalapetstudios.udacityprojects.spotifystreamer;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lalapetstudios.udacityprojects.spotifystreamer.adapters.TopTracksRecyclerAdapter;
import com.lalapetstudios.udacityprojects.spotifystreamer.contentproviders.TopTracksByArtistContentProvider;
import com.lalapetstudios.udacityprojects.spotifystreamer.models.ResultItem;
import com.lalapetstudios.udacityprojects.spotifystreamer.models.TrackDetailsModel;
import com.lalapetstudios.udacityprojects.spotifystreamer.util.OnAsyncTaskCompletedInterface;
import com.lalapetstudios.udacityprojects.spotifystreamer.util.RecyclerViewOnItemClickListener;

import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class ArtistDetailActivityFragment extends Fragment implements OnAsyncTaskCompletedInterface {

    private static final String TAG = ArtistDetailActivityFragment.class.getName();
    private static final String TRACK_DETAILS_MODEL = "TRACK_DETAILS_MODEL";

    RecyclerView recyclerView;
    TopTracksRecyclerAdapter topTracksRecyclerAdapter;
    ResultItem item;
    ArrayList<TrackDetailsModel> mResultList;
    View rootView;

    public ArtistDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_artist_detail, container, false);

        item = getActivity().getIntent().getExtras().getParcelable(SearchActivity.CLICKED_RESULT_ITEM);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.scrollableview);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        if( savedInstanceState != null ){
            this.mResultList = savedInstanceState.getParcelableArrayList(TRACK_DETAILS_MODEL);
            this.createTopTracksRecyclerAdapter();
        } else {
            mapResultsFromTopTracksContentProviderToList();
        }

        recyclerView.addOnItemTouchListener(
                new RecyclerViewOnItemClickListener(this.getActivity(), new RecyclerViewOnItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Toast.makeText(view.getContext(), mResultList.get(position).getTrackName(), Toast.LENGTH_SHORT).show();
                    }
                })
        );

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(TRACK_DETAILS_MODEL, mResultList);
    }

    private void createTopTracksRecyclerAdapter() {
        topTracksRecyclerAdapter = new TopTracksRecyclerAdapter(mResultList);
        recyclerView.setAdapter(topTracksRecyclerAdapter);
    }

    // Map the results into tracks models from the provider
    private void mapResultsFromTopTracksContentProviderToList () {
        new AsyncTask<Void, Void, ArrayList>() {
            @Override
            protected void onPostExecute(ArrayList resultList) {
                mResultList = resultList;
                boolean errorFlag = resultList == null ? true : false;
                onAsyncTaskCompleted(errorFlag);
            }

            @Override
            protected ArrayList doInBackground(Void[] params) {
                Cursor results = null;
                try {
                    results = queryTopTracksProvider();
                } catch(Exception ex) {
                    Log.e(TAG, "Issues in getting data from provider");
                    return null;
                }
                ArrayList<TrackDetailsModel> resultList = new ArrayList<>();

                Integer idIdx = results.getColumnIndex(TopTracksByArtistContentProvider.ID);
                Integer albumCoverIdx = results.getColumnIndex(TopTracksByArtistContentProvider.ALBUM_COVER);
                Integer trackNameIdx = results.getColumnIndex(TopTracksByArtistContentProvider.TRACK_NAME);
                Integer albumNameIdx = results.getColumnIndex(TopTracksByArtistContentProvider.ALBUM_NAME);
                Integer durationIdx = results.getColumnIndex(TopTracksByArtistContentProvider.DURATION_IN_MS);
                Integer previewUrlIdx = results.getColumnIndex(TopTracksByArtistContentProvider.PREVIEW_URL);

                while (results.moveToNext()) {
                    String id = results.getString(idIdx);
                    String albumCover = results.getString(albumCoverIdx);
                    String trackName = results.getString(trackNameIdx);
                    String albumName = results.getString(albumNameIdx);
                    String duration = results.getString(durationIdx);
                    String previewUrl = results.getString(previewUrlIdx);

                    TrackDetailsModel trackDtls = new TrackDetailsModel(id,albumCover,trackName,albumName,duration,previewUrl);
                    resultList.add(trackDtls);
                }

                results.close();
                return resultList;
            }
        }.execute();
    }

    @Override
    public void onAsyncTaskCompleted(boolean pErrorFlag) {
        if(pErrorFlag) {
            Snackbar
                    .make(rootView, R.string.no_interent_connection_text, Snackbar.LENGTH_LONG)
                    .show();
        } else {
            this.createTopTracksRecyclerAdapter();
        }
    }

    private Cursor queryTopTracksProvider() {
        Uri uri = Uri.parse("content://".concat(TopTracksByArtistContentProvider.PROVIDER_AUTHORITY)
                .concat("/top-tracks/").concat(item.getId()));

        return getActivity().getContentResolver().query(
                uri,
                null,
                item.getId(),
                null,
                null
        );
    }

}
