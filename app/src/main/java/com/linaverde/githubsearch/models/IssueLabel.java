package com.linaverde.githubsearch.models;

import android.os.Parcel;
import android.os.Parcelable;

public class IssueLabel implements Parcelable {

    private String color;
    private String text;

    public IssueLabel(String color, String text) {
        this.color = color;
        this.text = text;
    }

    protected IssueLabel(Parcel in) {
        color = in.readString();
        text = in.readString();
    }

    public static final Creator<IssueLabel> CREATOR = new Creator<IssueLabel>() {
        @Override
        public IssueLabel createFromParcel(Parcel in) {
            return new IssueLabel(in);
        }

        @Override
        public IssueLabel[] newArray(int size) {
            return new IssueLabel[size];
        }
    };


    public String getColor() {
        return color;
    }

    public String getText() {
        return text;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{color, text});
    }


}
