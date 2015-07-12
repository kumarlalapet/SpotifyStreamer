package com.lalapetstudios.udacityprojects.spotifystreamer.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;

/**
 * Created by g2ishan on 7/12/15.
 */
public class SpotifyAccessToken  implements Parcelable {

    private String accessToken;
    private long mExpiesBy;

    public SpotifyAccessToken(String pAccessToken, int pExpiresIn) {
        this.accessToken = pAccessToken;

        Calendar calendar = Calendar.getInstance(); // gets a calendar using the default time zone and locale.
        calendar.add(Calendar.SECOND, -10);
        calendar.add(Calendar.SECOND,pExpiresIn);

        this.mExpiesBy = calendar.getTimeInMillis();
    }

    private SpotifyAccessToken(String pAccessToken, long pExpiresBy) {
        this.accessToken = pAccessToken;
        this.mExpiesBy = pExpiresBy;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public boolean isTokenExpired() {
        Calendar calendar = Calendar.getInstance();
        if(mExpiesBy > calendar.getTimeInMillis()) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(accessToken);
        dest.writeLong(mExpiesBy);
    }

    // Parcelable Creator Implementation
    public static final Creator<SpotifyAccessToken> CREATOR = new Creator<SpotifyAccessToken>() {

        public SpotifyAccessToken createFromParcel(Parcel in) {
            SpotifyAccessToken spotifyAccessToken = new SpotifyAccessToken(in.readString(),in.readLong());
            return spotifyAccessToken;
        }

        public SpotifyAccessToken[] newArray(int size) {
            return new SpotifyAccessToken[size];
        }
    };
}
