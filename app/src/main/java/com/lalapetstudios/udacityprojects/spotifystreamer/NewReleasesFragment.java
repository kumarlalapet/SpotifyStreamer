package com.lalapetstudios.udacityprojects.spotifystreamer;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lalapetstudios.udacityprojects.spotifystreamer.adapters.NewReleasesRecyclerAdapter;
import com.lalapetstudios.udacityprojects.spotifystreamer.contentproviders.NewReleasesContentProvider;
import com.lalapetstudios.udacityprojects.spotifystreamer.models.NewReleasesModel;
import com.lalapetstudios.udacityprojects.spotifystreamer.util.OnAsyncTaskCompletedInterface;

import java.util.ArrayList;

/**
 * Created by g2ishan on 7/6/15.
 */
public class NewReleasesFragment extends Fragment implements OnAsyncTaskCompletedInterface {

    private static final String NEW_RELEASES_MODEL = "NEW_RELEASES_MODEL";
    private static final String TAG = NewReleasesFragment.class.getName();

    RecyclerView recyclerView;
    NewReleasesRecyclerAdapter nrRecyclerAdapter;
    ArrayList<NewReleasesModel> mResultList;
    boolean mTabLayout = false;

    public NewReleasesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if( savedInstanceState != null ) {
            this.mTabLayout = savedInstanceState.getBoolean(MainActivity.TABLAYOUT);
        } else {
            Bundle argsBundle = getArguments();
            if (argsBundle != null) {
                mTabLayout = argsBundle.getBoolean(MainActivity.TABLAYOUT);
            } else {
                mTabLayout = false;
            }
        }

        View v = inflater.inflate(R.layout.fragment_newreleases, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.nr_recyclerview);
        recyclerView.setHasFixedSize(true);

        if(mTabLayout) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(v.getContext(),2);
            recyclerView.setLayoutManager(gridLayoutManager);
        } else {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(v.getContext(),LinearLayoutManager.HORIZONTAL,false);
            recyclerView.setLayoutManager(linearLayoutManager);
        }

        if( savedInstanceState != null ){
            this.mResultList = savedInstanceState.getParcelableArrayList(NEW_RELEASES_MODEL);
            this.createNewReleasesRecyclerAdapter();
        } else {
            mapResultsFromNewReleasesContentProviderToList();
        }

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(NEW_RELEASES_MODEL, mResultList);
    }

    @Override
    public void onAsyncTaskCompleted(boolean pErrorFlag) {
        if(pErrorFlag) {
            Snackbar
                    .make(this.getView(), R.string.no_interent_connection_text, Snackbar.LENGTH_LONG)
                    .show();
        } else {
            this.createNewReleasesRecyclerAdapter();
        }
    }

    private void createNewReleasesRecyclerAdapter() {
        nrRecyclerAdapter = new NewReleasesRecyclerAdapter(mResultList);
        recyclerView.setAdapter(nrRecyclerAdapter);
    }

    private void mapResultsFromNewReleasesContentProviderToList() {
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
                    results = queryNewReleasesProvider();
                } catch(Exception ex) {
                    Log.e(TAG, "Issues in getting data from provider");
                    return null;
                }
                ArrayList<NewReleasesModel> resultList = new ArrayList<>();

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