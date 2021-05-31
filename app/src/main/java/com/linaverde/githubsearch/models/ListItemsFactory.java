package com.linaverde.githubsearch.models;

import android.content.Context;

import com.linaverde.githubsearch.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListItemsFactory {

    public static List<SearchListItem> createSearchResultsFromJson(Context c, String json) throws JSONException {
        JSONObject fullAnswer = new JSONObject(json);
        JSONArray repos = fullAnswer.getJSONArray(c.getResources().getString(R.string.rep_array));
        List<SearchListItem> list = new ArrayList<>();
        for (int i = 0; i < repos.length(); i++) {
            JSONObject currItem = repos.getJSONObject(i);
            list.add(new SearchListItem(currItem.getString(c.getResources().getString(R.string.rep_full_name)),
                    currItem.getString(c.getResources().getString(R.string.rep_name)),
                    currItem.getString(c.getResources().getString(R.string.rep_description)),
                    currItem.getJSONObject(c.getResources().getString(R.string.owner_json_obj))
                            .getString(c.getResources().getString(R.string.owner_name)),
                    currItem.getJSONObject(c.getResources().getString(R.string.owner_json_obj))
                            .getString(c.getResources().getString(R.string.avatar)),
                    currItem.getString(c.getResources().getString(R.string.issues_url))));
        }
        return list;
    }

    public static IssueListItem[] createIssuesFromJson(Context c, String json) throws JSONException {
        JSONArray fullAnswer = new JSONArray(json);
        IssueListItem[] issues = new IssueListItem[fullAnswer.length()];
        for (int i = 0; i < fullAnswer.length(); i++) {
            JSONObject item = fullAnswer.getJSONObject(i);
            JSONArray jLabels = item.getJSONArray(c.getResources().getString(R.string.issue_labels));
            IssueLabel[] labels = new IssueLabel[jLabels.length()];
            for (int j = 0; j < jLabels.length(); j++) {
                JSONObject currLabel = jLabels.getJSONObject(j);
                labels[j] = new IssueLabel(
                        currLabel.getString(c.getResources().getString(R.string.issue_label_color)),
                        currLabel.getString(c.getResources().getString(R.string.issue_label_name)));
            }
            issues[i] = new IssueListItem(item.getString(c.getResources().getString(R.string.issue_number)),
                    item.getString(c.getResources().getString(R.string.issue_title)),
                    item.getString(c.getResources().getString(R.string.issue_body)),
                    item.getString(c.getResources().getString(R.string.issue_state)), labels);
        }
        return issues;
    }

}
