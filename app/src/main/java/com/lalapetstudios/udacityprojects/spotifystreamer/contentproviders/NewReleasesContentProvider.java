package com.lalapetstudios.udacityprojects.spotifystreamer.contentproviders;

import android.annotation.SuppressLint;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

import com.lalapetstudios.udacityprojects.spotifystreamer.util.GenerateSpotifyAccessToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.AlbumSimple;
import kaaes.spotify.webapi.android.models.NewReleases;
import kaaes.spotify.webapi.android.models.Pager;

/**
 * Created by g2ishan on 7/12/15.
 */
public class NewReleasesContentProvider extends ContentProvider {

    public static final String ID = "_ID";
    public static final String NR_COVER = "PLAY_LIST_COVER";
    public static final String NR_NAME = "PLAY_LIST_NAME";

    private static final String TAG = "NewReleasesContentProvider";
    private static final int NEW_RELEASES = 1;

    public static final String PROVIDER_AUTHORITY = "com.lalapetstudios.udacityprojects.spotifystreamer.contentproviders.NewReleasesContentProvider";
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_AUTHORITY, "new-releases/*", NEW_RELEASES);
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @SuppressLint("LongLogTag")
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.i(TAG, "received parameter in query: " + selection);
        switch (uriMatcher.match(uri)) {
            case NEW_RELEASES:
                return getNewReleasesFromSpotify();
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

    private Cursor getNewReleasesFromSpotify() {
        MatrixCursor mc = new MatrixCursor(new String[] {
                ID,
                NR_COVER,
                NR_NAME
        });

        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(GenerateSpotifyAccessToken.generateAccessToken());

        SpotifyService spotify = api.getService();

        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("country", "US");

        NewReleases newReleases = spotify.getNewReleases(queryMap);

        Pager<AlbumSimple> albumPager = newReleases.albums;
        List<AlbumSimple> albumSimpleList = albumPager.items;
        for(AlbumSimple albumSimple : albumSimpleList) {
            String id = albumSimple.id;
            String url = (albumSimple.images != null && albumSimple.images.size() > 0) ? albumSimple.images.get(0).url : "";
            String name = albumSimple.name;

            Object data[] = {id,url,name};
            mc.addRow(data);
        }

        return mc;
    }

}
