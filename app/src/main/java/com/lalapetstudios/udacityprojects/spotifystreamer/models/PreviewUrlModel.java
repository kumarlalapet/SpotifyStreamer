package com.lalapetstudios.udacityprojects.spotifystreamer.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by g2ishan on 9/13/15.
 */
public class PreviewUrlModel implements Parcelable {

    private String previewUrl;

    public PreviewUrlModel(String pPreviewUrl) {
        this.previewUrl = pPreviewUrl;
    }

    public PreviewUrlModel() {
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(getPreviewUrl());
    }

    // Parcelable Creator Implementation
    public static final Creator<PreviewUrlModel> CREATOR = new Creator<PreviewUrlModel>() {

        public PreviewUrlModel createFromParcel(Parcel in) {
            PreviewUrlModel previewUrlModel = new PreviewUrlModel();

            previewUrlModel.setPreviewUrl(in.readString());

            return previewUrlModel;
        }

        public PreviewUrlModel[] newArray(int size) {
            return new PreviewUrlModel[size];
        }
    };

}
