package com.lalapetstudios.udacityprojects.spotifystreamer.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by g2ishan on 7/11/15.
 */
public class ArtistDetailModel {

    // TODO remove the folllowing once wired up
    public String name;
    public static final String[] data = {"Cupcake", "Donut", "Eclair",
            "Froyo", "Gingerbread", "Honeycomb",
            "Icecream Sandwich", "Jelly Bean", "Kitkat", "Lollipop"};
    ArtistDetailModel(String name){
        this.name=name;
    }
    // END remove

    //TODO remove the entire class
}