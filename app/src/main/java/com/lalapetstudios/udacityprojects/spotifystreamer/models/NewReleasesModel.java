package com.lalapetstudios.udacityprojects.spotifystreamer.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by g2ishan on 7/12/15.
 */
public class NewReleasesModel implements Parcelable {

    private String id;
    private String nrCover;
    private String nrName;

    NewReleasesModel(){}

    public NewReleasesModel(String id, String nrCover, String nrName) {
        this.id = id;
        this.nrCover = nrCover;
        this.nrName = nrName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNrCover() {
        return nrCover;
    }

    public void setNrCover(String nrCover) {
        this.nrCover = nrCover;
    }

    public String getNrName() {
        return nrName;
    }

    public void setNrName(String nrName) {
        this.nrName = nrName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getId());
        dest.writeString(getNrCover());
        dest.writeString(getNrName());
    }

    // Parcelable Creator Implementation
    public static final Creator<NewReleasesModel> CREATOR = new Creator<NewReleasesModel>() {

        public NewReleasesModel createFromParcel(Parcel in) {
            NewReleasesModel nrList = new NewReleasesModel();

            nrList.setId(in.readString());
            nrList.setNrCover(in.readString());
            nrList.setNrName(in.readString());

            return nrList;
        }

        public NewReleasesModel[] newArray(int size) {
            return new NewReleasesModel[size];
        }
    };

}
