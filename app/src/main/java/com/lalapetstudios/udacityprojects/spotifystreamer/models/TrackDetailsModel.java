package com.lalapetstudios.udacityprojects.spotifystreamer.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by g2ishan on 7/11/15.
 */
public class TrackDetailsModel implements Parcelable {

    private String id;
    private String albumCover;
    private String trackName;
    private String albumName;
    private String durationInMs;

    TrackDetailsModel(){
    }
    public TrackDetailsModel(String pId, String pAlbumCover, String pTrackName, String pAlbumName, String pDurationInMs) {
        this.id = pId;
        this.albumCover = pAlbumCover;
        this.trackName = pTrackName;
        this.albumName = pAlbumName;
        this.durationInMs = pDurationInMs;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlbumCover() {
        return albumCover;
    }

    public void setAlbumCover(String albumCover) {
        this.albumCover = albumCover;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getDurationInMs() {
        return durationInMs;
    }

    public void setDurationInMs(String durationInMs) {
        this.durationInMs = durationInMs;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getId());
        dest.writeString(getAlbumCover());
        dest.writeString(getTrackName());
        dest.writeString(getAlbumName());
        dest.writeString(getDurationInMs());
    }

    // Parcelable Creator Implementation
    public static final Creator<TrackDetailsModel> CREATOR = new Creator<TrackDetailsModel>() {

        public TrackDetailsModel createFromParcel(Parcel in) {
            TrackDetailsModel trackDetails = new TrackDetailsModel();

            trackDetails.setId(in.readString());
            trackDetails.setAlbumCover(in.readString());
            trackDetails.setTrackName(in.readString());
            trackDetails.setAlbumName(in.readString());
            trackDetails.setDurationInMs(in.readString());

            return trackDetails;
        }

        public TrackDetailsModel[] newArray(int size) {
            return new TrackDetailsModel[size];
        }
    };

}