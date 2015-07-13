package com.lalapetstudios.udacityprojects.spotifystreamer.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.lalapetstudios.udacityprojects.spotifystreamer.R;

/**
 * Created by g2ishan on 7/10/15.
 */
public class ResultItem implements Parcelable {

    private String header;
    private String subHeader;
    private Object leftIcon;
    private Integer rightIcon;
    private String id;
    private String query;

    // Constructors
    public ResultItem () {
        this.header = "Error";
        this.subHeader = "Error";
        this.leftIcon = R.drawable.no_artist_image;
        this.rightIcon = R.drawable.ic_action_arrow_left_top;
        this.query = "";
    }

    public ResultItem (String id, String header, String subHeader, Object leftIcon, Integer rightIcon, String pQuery) {
        this.setId(id);
        this.setHeader(header);
        this.setSubHeader(subHeader);
        this.setLeftIcon(leftIcon);
        this.setRightIcon(rightIcon);
        this.query = pQuery;
    }

    // Getters and Setters
    public Object getLeftIcon() {
        return leftIcon;
    }

    public void setLeftIcon(Object leftIcon) {
        //if (leftIcon != null && leftIcon != 0 && leftIcon != -1) {
            this.leftIcon = leftIcon;
        //} else {
        //    this.leftIcon = R.drawable.no_artist_image;
        //}
    }

    public Integer getRightIcon() {
        return rightIcon;
    }

    public void setRightIcon(Integer rightIcon) {
        if (rightIcon != null && rightIcon != 0 && rightIcon != -1) {
            this.rightIcon = rightIcon;
        } else {
            this.rightIcon = R.drawable.ic_action_arrow_left_top;
        }
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getSubHeader() {
        return subHeader;
    }

    public void setSubHeader(String subHeader) {
        if (subHeader != null) {
            this.subHeader = subHeader;
        } else {
            this.subHeader = "a";
        }
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    // Parcelable contract implementation
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(header);
        dest.writeString(subHeader);
        if(leftIcon != null && leftIcon.getClass().equals(String.class)) {
            dest.writeInt(1);
            dest.writeString((String)leftIcon);
        } else {
            dest.writeInt(0);
            dest.writeInt((Integer)leftIcon);
        }
        dest.writeInt(rightIcon);
        dest.writeString(query);
    }

    // Parcelable Creator Implementation
    public static final Creator<ResultItem> CREATOR = new Creator<ResultItem>() {

        public ResultItem createFromParcel(Parcel in) {
            ResultItem resultItem = new ResultItem();

            resultItem.setId(in.readString());
            resultItem.setHeader(in.readString());
            resultItem.setSubHeader(in.readString());
            if(in.readInt() == 1) {
                resultItem.setLeftIcon(in.readString());
            } else {
                resultItem.setLeftIcon(in.readInt());
            }
            resultItem.setRightIcon(in.readInt());
            resultItem.setQuery(in.readString());

            return resultItem;
        }

        public ResultItem[] newArray(int size) {
            return new ResultItem[size];
        }
    };

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

}