package com.lalapetstudios.udacityprojects.spotifystreamer;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lalapetstudios.udacityprojects.spotifystreamer.adapters.FeaturedPlayListRecyclerAdapter;
import com.lalapetstudios.udacityprojects.spotifystreamer.adapters.NewReleasesRecyclerAdapter;
import com.lalapetstudios.udacityprojects.spotifystreamer.contentproviders.FeaturedPlayListContentProvider;
import com.lalapetstudios.udacityprojects.spotifystreamer.contentproviders.NewReleasesContentProvider;
import com.lalapetstudios.udacityprojects.spotifystreamer.models.FeaturedPlayListModel;
import com.lalapetstudios.udacityprojects.spotifystreamer.models.NewReleasesModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by g2ishan on 7/6/15.
 */
public class NewReleasesFragment extends Fragment {

    RecyclerView recyclerView;
    NewReleasesRecyclerAdapter nrRecyclerAdapter;

    public NewReleasesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_newreleases, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.nr_recyclerview);

        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(v.getContext(),2);
        recyclerView.setLayoutManager(gridLayoutManager);

        mapResultsFromNewReleasesContentProviderToList();

        return v;
    }

    private void mapResultsFromNewReleasesContentProviderToList() {
        new AsyncTask<Void, Void, List>() {
            @Override
            protected void onPostExecute(List resultList) {
                nrRecyclerAdapter = new NewReleasesRecyclerAdapter(resultList);
                recyclerView.setAdapter(nrRecyclerAdapter);
            }

            @Override
            protected List doInBackground(Void[] params) {
                Cursor results = results = queryNewReleasesProvider();
                List<NewReleasesModel> resultList = new ArrayList<>();

                Integer idIdx = results.getColumnIndex(NewReleasesContentProvider.ID);
                Integer nrCoverIdx = results.getColumnIndex(NewReleasesContentProvider.NR_COVER);
                Integer nrNameIdx = results.getColumnIndex(NewReleasesContentProvider.NR_NAME);

                while (results.moveToNext()) {
                    String id = results.getString(idIdx);
                    String nrCover = results.getString(nrCoverIdx);
                    String nrName = results.getString(nrNameIdx);

                    NewReleasesModel nrDtls = new NewReleasesModel(id,nrCover,nrName);
                    resultList.add(nrDtls);
                }

                results.close();
                return resultList;
            }
        }.execute();
    }

    private Cursor queryNewReleasesProvider() {
        Uri uri = Uri.parse("content://".concat(NewReleasesContentProvider.PROVIDER_AUTHORITY)
                .concat("/new-releases/1"));

        return NewReleasesFragment.this.getActivity().getContentResolver().query(
                uri,
                null,
                null,
                null,
                null
        );
    }

}