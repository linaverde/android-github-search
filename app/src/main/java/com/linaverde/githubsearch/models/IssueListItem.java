package com.linaverde.githubsearch.models;

import android.os.Parcel;
import android.os.Parcelable;

public class IssueListItem implements Parcelable {

    private String number;
    private String title;
    private String body;
    private String state;
    private IssueLabel [] labels;

    public IssueListItem(String number, String title, String body, String state, IssueLabel [] labels) {
        this.number = number;
        this.title = title;
        this.body = body;
        this.state = state;
        this.labels = labels;
    }

    protected IssueListItem(Parcel in) {
        number = in.readString();
        title = in.readString();
        body = in.readString();
        state = in.readString();
        labels = in.createTypedArray(IssueLabel.CREATOR);
    }

    public static final Creator<IssueListItem> CREATOR = new Creator<IssueListItem>() {
        @Override
        public IssueListItem createFromParcel(Parcel in) {
            return new IssueListItem(in);
        }

        @Override
        public IssueListItem[] newArray(int size) {
            return new IssueListItem[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getNumber() {
        return number;
    }

    public IssueLabel[] getLabels() {
        return labels;
    }

    public String getBody() {
        return body;
    }

    public String getState() {
        return state;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{number, title, body, state});
        dest.writeTypedArray(labels, 0);
    }


}
