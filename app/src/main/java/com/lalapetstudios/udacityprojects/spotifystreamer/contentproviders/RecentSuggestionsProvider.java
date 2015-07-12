package com.lalapetstudios.udacityprojects.spotifystreamer.contentproviders;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by g2ishan on 7/10/15.
 */
public class RecentSuggestionsProvider extends SearchRecentSuggestionsProvider {

    public final static String PROVIDER_AUTHORITY = "com.lalapetstudios.udacityprojects.spotifystreamer.contentproviders.RecentSuggestionsProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public RecentSuggestionsProvider() {
        setupSuggestions(PROVIDER_AUTHORITY, MODE);
    }
}