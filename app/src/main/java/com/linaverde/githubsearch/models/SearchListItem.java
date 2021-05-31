package com.linaverde.githubsearch.models;

import org.json.JSONException;

public class SearchListItem {

    private String title;
    private String owner;
    private String repName;
    private String description;
    private String avatarUrl;
    private String issueUrl;

    public SearchListItem(String title, String repName, String description, String owner, String avatarUrl, String issueUrl) throws JSONException {

        this.title = title;
        this.repName = repName;
        this.owner = owner;
        this.description = description;
        this.avatarUrl = avatarUrl;
        this.issueUrl = issueUrl.substring(0, issueUrl.indexOf("{"));
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getIssueUrl() {
        return issueUrl;
    }

    public String getOwner() {
        return owner;
    }

    public String getRepName() {
        return repName;
    }
}
