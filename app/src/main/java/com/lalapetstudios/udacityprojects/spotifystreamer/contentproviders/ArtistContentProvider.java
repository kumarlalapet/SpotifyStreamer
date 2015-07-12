package com.lalapetstudios.udacityprojects.spotifystreamer.contentproviders;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

import com.lalapetstudios.udacityprojects.spotifystreamer.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Pager;

/**
 * Created by g2ishan on 7/11/15.
 */
public class ArtistContentProvider extends ContentProvider {

    public static final String ID = "_ID";

    private static final String TAG = "ArtistContentProvider";
    private static final int GET_SAMPLE = 0;
    private static final int GET_CUSTOM_SUGGESTIONS = 1;

    private static final String PROVIDER_NAME = "com.lalapetstudios.udacityprojects.spotifystreamer.contentproviders.ArtistContentProvider";
    public static final String PROVIDER_AUTHORITY = "com.lalapetstudios.udacityprojects.spotifystreamer.contentproviders.ArtistContentProvider";
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "GET_SAMPLE", GET_SAMPLE);
        uriMatcher.addURI(PROVIDER_NAME, "suggestions/*", GET_CUSTOM_SUGGESTIONS);
        uriMatcher.addURI(PROVIDER_NAME, "suggestions", GET_CUSTOM_SUGGESTIONS);
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.i(TAG, "received parameter in query: " + uriMatcher.match(uri)+" : "+selectionArgs[0].length()+" : "+selectionArgs.length);
        switch (uriMatcher.match(uri)) {
            case GET_SAMPLE:
                return getFakeData();
            case GET_CUSTOM_SUGGESTIONS:
                return searchSpotifyForArtist(selectionArgs[0]);
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    private Cursor searchSpotifyForArtist(String query) {

        // Columns explanation: http://developer.android.com/guide/topics/search/adding-custom-suggestions.html#HandlingSuggestionQuery
        final MatrixCursor mc = new MatrixCursor(new String[] {
                ID,
                SearchManager.SUGGEST_COLUMN_TEXT_1,
                SearchManager.SUGGEST_COLUMN_TEXT_2,
                SearchManager.SUGGEST_COLUMN_ICON_1,
                SearchManager.SUGGEST_COLUMN_ICON_2,
                SearchManager.SUGGEST_COLUMN_INTENT_ACTION,
                SearchManager.SUGGEST_COLUMN_INTENT_DATA,
                SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID,
                SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA,
                SearchManager.SUGGEST_COLUMN_QUERY,
        });

        SpotifyApi api = new SpotifyApi();
        SpotifyService spotify = api.getService();

        ArtistsPager artistsPager = spotify.searchArtists(query);
        Pager<Artist> artists = artistsPager.artists;
        List<Artist> artistsList = artists.items;

        int i = 1;
        for(final Artist a : artistsList) {
            Object[] data = null;
            //String secondLabel = getGenre(a);
            String secondLabel = getPopularityAndFollowers(a);
            if(a.images.size() == 0) {
                data = new Object[]{a.id, a.name, secondLabel, R.drawable.no_artist_image, null, null, null, null, null, null};
            } else {
                data = new Object[]{a.id, a.name, secondLabel, a.images.get(1).url, null, null, null, null, null, null};
            }
            mc.addRow(data);
            i++;
        }

        Log.d("Artists success", ""+mc.getCount());

        /**spotify.searchArtists(query, new Callback<ArtistsPager>() {
            @Override
            public void success(ArtistsPager artistsPager, Response response) {
                Pager<Artist> artists = artistsPager.artists;
                List<Artist> artistsList = artists.items;

                int i = 1;
                for(Artist a : artistsList) {
                    Object[] data = {Integer.toString(i),a.name,a.name,null, null, null, null, null, null, null};
                    mc.addRow(data);
                    i++;
                }

                Log.d("Artists success", ""+mc.getCount());
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("Album failure", error.toString());
            }
        }); **/
        return mc;
    }

    private String getPopularityAndFollowers(Artist a) {
        String totalFollowers = NumberFormat.getNumberInstance(Locale.US).format(a.followers.total);
        String popularity = a.popularity.toString();
        return totalFollowers +" "+getContext().getString(R.string.followers)+", "+
                getContext().getString(R.string.popularity)+" - "+popularity;
    }

    private String getGenre(Artist pArtist) {
        String genre = "";
        for(String localGenre : pArtist.genres) {
            if(genre.length() == 0)
                genre = localGenre;
            else
                genre = genre+", "+localGenre;
        }
        return genre;
    }

    // Util
    private Cursor getFakeData () {
        // Columns explanation: http://developer.android.com/guide/topics/search/adding-custom-suggestions.html#HandlingSuggestionQuery
        MatrixCursor mc = new MatrixCursor(new String[] {
                ID,
                SearchManager.SUGGEST_COLUMN_TEXT_1,
                SearchManager.SUGGEST_COLUMN_TEXT_2,
                SearchManager.SUGGEST_COLUMN_ICON_1,
                SearchManager.SUGGEST_COLUMN_ICON_2,
                SearchManager.SUGGEST_COLUMN_INTENT_ACTION,
                SearchManager.SUGGEST_COLUMN_INTENT_DATA,
                SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID,
                SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA,
                SearchManager.SUGGEST_COLUMN_QUERY,
        });

        Object[] fakeRow1 = {"ID1", "Mock data 1", "Sub mock data 1", R.drawable.ic_action_cancel, R.drawable.ic_action_arrow_left, null, null, null, null, null};
        Object[] fakeRow2 = {"ID2", "Mock data 2", "Sub mock data 2", R.drawable.ic_action_mic, R.drawable.ic_action_cancel, null, null, null, null, null};
        Object[] fakeRow3 = {"ID3", "Mock data 3", "Sub mock data 3", null, null, null, null, null, null, null};

        mc.addRow(fakeRow1);
        mc.addRow(fakeRow2);
        mc.addRow(fakeRow3);

        return mc;
    }

}
