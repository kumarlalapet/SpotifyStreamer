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
import kaaes.spotify.webapi.android.models.FeaturedPlaylists;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;

/**
 * Created by g2ishan on 7/12/15.
 */

public class FeaturedPlayListContentProvider extends ContentProvider {

    public static final String ID = "_ID";
    public static final String PLAY_LIST_COVER = "PLAY_LIST_COVER";
    public static final String PLAY_LIST_NAME = "PLAY_LIST_NAME";
    public static final String TRACK_TOTAL = "TRACK_TOTAL";
    public static final String MESSAGE = "MESSAGE";

    private static final String TAG = "FeaturedPlayListContentProvider";
    private static final int GET_FEATURED_PLAYLIST = 1;

    public static final String PROVIDER_AUTHORITY = "com.lalapetstudios.udacityprojects.spotifystreamer.contentproviders.FeaturedPlayListContentProvider";
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_AUTHORITY, "featured-playlists/*", GET_FEATURED_PLAYLIST);
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
            case GET_FEATURED_PLAYLIST:
                return getFeaturedPlayListFromSpotify();
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

    @SuppressLint("LongLogTag")
    private Cursor getFeaturedPlayListFromSpotify() {
        MatrixCursor mc = new MatrixCursor(new String[] {
            ID,
            PLAY_LIST_COVER,
            PLAY_LIST_NAME,
            TRACK_TOTAL,
            MESSAGE,
        });

        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(GenerateSpotifyAccessToken.generateAccessToken());

        SpotifyService spotify = api.getService();

        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("country", "US");

        FeaturedPlaylists playLists = spotify.getFeaturedPlaylists(queryMap);

        Pager<PlaylistSimple> playListPager = playLists.playlists;
        List<PlaylistSimple> playListSimpleList = playListPager.items;
        for(PlaylistSimple playlistSimple : playListSimpleList) {
            String id = playlistSimple.id;
            String url = (playlistSimple.images != null && playlistSimple.images.size() > 0) ? playlistSimple.images.get(0).url : "";
            String name = playlistSimple.name;
            String total = Integer.toString(playlistSimple.tracks.total);
            String message = playLists.message;

            Object data[] = {id,url,name,total,message};
            mc.addRow(data);
        }

        return mc;
    }

}
