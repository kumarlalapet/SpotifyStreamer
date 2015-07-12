package com.lalapetstudios.udacityprojects.spotifystreamer.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by g2ishan on 7/12/15.
 */
public class FeaturedPlayListModel implements Parcelable {
    private String id;
    private String playListCover;
    private String playListName;
    private String playListTrackNumber;
    private String message;
    FeaturedPlayListModel(){}
    public FeaturedPlayListModel(String id, String playListCover, String playListName, String playListTrackNumber, String message) {
        this.id = id;
        this.playListCover = playListCover;
        this.playListName = playListName;
        this.playListTrackNumber = playListTrackNumber;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlayListCover() {
        return playListCover;
    }

    public void setPlayListCover(String playListCover) {
        this.playListCover = playListCover;
    }

    public String getPlayListName() {
        return playListName;
    }

    public void setPlayListName(String playListName) {
        this.playListName = playListName;
    }

    public String getPlayListTrackNumber() {
        return playListTrackNumber;
    }

    public void setPlayListTrackNumber(String playListTrackNumber) {
        this.playListTrackNumber = playListTrackNumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getId());
        dest.writeString(getPlayListCover());
        dest.writeString(getPlayListName());
        dest.writeString(getPlayListTrackNumber());
        dest.writeString(getMessage());
    }

    // Parcelable Creator Implementation
    public static final Creator<FeaturedPlayListModel> CREATOR = new Creator<FeaturedPlayListModel>() {

        public FeaturedPlayListModel createFromParcel(Parcel in) {
            FeaturedPlayListModel playList = new FeaturedPlayListModel();

            playList.setId(in.readString());
            playList.setPlayListCover(in.readString());
            playList.setPlayListName(in.readString());
            playList.setPlayListTrackNumber(in.readString());
            playList.setMessage(in.readString());

            return playList;
        }

        public FeaturedPlayListModel[] newArray(int size) {
            return new FeaturedPlayListModel[size];
        }
    };

}
