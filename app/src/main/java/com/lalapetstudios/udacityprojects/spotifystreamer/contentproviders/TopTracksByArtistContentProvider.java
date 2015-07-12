package com.lalapetstudios.udacityprojects.spotifystreamer.contentproviders;

import android.annotation.SuppressLint;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.nfc.Tag;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * Created by g2ishan on 7/11/15.
 */
public class TopTracksByArtistContentProvider extends ContentProvider {

    public static final String ID = "_ID";
    public static final String ALBUM_COVER = "ALBUM_COVER";
    public static final String TRACK_NAME = "TRACK_NAME";
    public static final String ALBUM_NAME = "ALBUM_NAME";
    public static final String DURATION_IN_MS = "DURATION_IN_MS";

    private static final String TAG = "TopTracksByArtistContentProvider";
    private static final int GET_TOP_TRACKS = 1;

    public static final String PROVIDER_AUTHORITY = "com.lalapetstudios.udacityprojects.spotifystreamer.contentproviders.TopTracksByArtistContentProvider";
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_AUTHORITY, "top-tracks/*", GET_TOP_TRACKS);
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
            case GET_TOP_TRACKS:
                return getTopTracksByArtistFromSpotify(selection);
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
    private Cursor getTopTracksByArtistFromSpotify(String selectionArg) {
        MatrixCursor mc = new MatrixCursor(new String[] {
                ID,
                ALBUM_COVER,
                TRACK_NAME,
                ALBUM_NAME,
                DURATION_IN_MS
        });

        Log.i(TAG,"URI - "+selectionArg);
        SpotifyApi api = new SpotifyApi();
        SpotifyService spotify = api.getService();

        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("country","US");

        Tracks tracks = spotify.getArtistTopTrack(selectionArg,queryMap);
        List<Track> trackList = tracks.tracks;
        for(Track track : trackList) {
            Object data[] = null;
            if(track.album.images.size() > 0)
                data = new Object[]{track.id,track.album.images.get(0).url,track.name,track.album.name,Long.toString(track.duration_ms)};
            else
                data = new Object[]{track.id,null,track.name,track.album.name,Long.toString(track.duration_ms)};
            mc.addRow(data);
        }

        return mc;
    }

    /**
    public static void main(String args[]) {
        String url = "https://api.spotify.com/v1/artists/2DlGxzQSjYe5N6G9nkYghR/top-tracks?country=US";

    }
     **/
}
