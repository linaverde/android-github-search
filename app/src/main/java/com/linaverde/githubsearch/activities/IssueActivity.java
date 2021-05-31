package com.linaverde.githubsearch.activities;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ContentLoadingProgressBar;

import android.graphics.Bitmap;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.linaverde.githubsearch.R;
import com.linaverde.githubsearch.helpers.DialogBuilder;
import com.linaverde.githubsearch.helpers.RequestHelper;
import com.linaverde.githubsearch.interfaces.CompleteActionListener;
import com.linaverde.githubsearch.interfaces.RequestListener;
import com.linaverde.githubsearch.models.IssueAdapter;
import com.linaverde.githubsearch.models.IssueListItem;
import com.linaverde.githubsearch.models.ListItemsFactory;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;

public class IssueActivity extends AppCompatActivity {

    private static final String TAG = "IssueActivity";
    private static final String ISSUE_LIST = "IssueList";
    private static final String ISSUE_LIST_STATE = "IssueListState";

    private ContentLoadingProgressBar progressBar;
    private IssueAdapter adapter;
    private ListView issues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue);

        String owner = null;
        String repName = null;
        String description = null;
        String avatarUrl = null;
        String issuesUrl = null;

        Bundle b = getIntent().getExtras();
        if (b != null && b.containsKey(getResources().getString(R.string.owner_name)) &&
                b.containsKey(getResources().getString(R.string.rep_name)) &&
                b.containsKey(getResources().getString(R.string.rep_description)) &&
                b.containsKey(getResources().getString(R.string.avatar)) &&
                b.containsKey(getResources().getString(R.string.issues_url))) {
            owner = b.getString(getResources().getString(R.string.owner_name));
            repName = b.getString(getResources().getString(R.string.rep_name));
            description = b.getString(getResources().getString(R.string.rep_description));
            avatarUrl = b.getString(getResources().getString(R.string.avatar));
            issuesUrl = b.getString(getResources().getString(R.string.issues_url));
        } else {
            finish();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(repName);
        setSupportActionBar(toolbar);

        progressBar = findViewById(R.id.progress_bar);

        ((TextView) findViewById(R.id.tv_owner)).setText(owner);
        ((TextView) findViewById(R.id.tv_rep_desc)).setText(description);

        Picasso.get().load(avatarUrl).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                ((ImageView)findViewById(R.id.iv_avatar)).setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });

        issues = findViewById(R.id.lv_issues);
        progressBar.show();
        RequestHelper requestHelper = new RequestHelper(IssueActivity.this);
        if (savedInstanceState == null) {
            Log.d(TAG, "Create View");
            requestHelper.executeGetIssues(issuesUrl, new RequestListener() {
                @Override
                public void onComplete(String answer) {
                    progressBar.hide();
                    try {
                        adapter = new IssueAdapter(IssueActivity.this,
                                ListItemsFactory.createIssuesFromJson(IssueActivity.this, answer));
                        issues.setAdapter(adapter);
                        if (adapter.getCount() == 0){
                            ((LinearLayout) findViewById(R.id.ll_hint)).setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onError(int responseCode) {
                    progressBar.hide();
                    ((ImageView)findViewById(R.id.iv_hint)).setImageDrawable(ContextCompat.getDrawable(IssueActivity.this, R.drawable.icon_error));;
                    ((TextView) findViewById(R.id.tv_hint)).setText(getString(R.string.hint_error));
                }
            });
        } else {
            progressBar.hide();
            Log.d(TAG, "Restore from save");
            IssueListItem[] listIssues = (IssueListItem[]) savedInstanceState.getParcelableArray(ISSUE_LIST);
            adapter = new IssueAdapter(IssueActivity.this, listIssues);
            issues.setAdapter(adapter);
            issues.smoothScrollToPosition(savedInstanceState.getInt(ISSUE_LIST_STATE));

        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");
        outState.putParcelableArray(ISSUE_LIST, adapter.getValues());
        outState.putInt(ISSUE_LIST_STATE, issues.getFirstVisiblePosition());
    }

}
