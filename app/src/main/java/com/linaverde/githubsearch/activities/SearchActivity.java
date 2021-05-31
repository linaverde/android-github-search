package com.linaverde.githubsearch.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ContentLoadingProgressBar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.linaverde.githubsearch.R;
import com.linaverde.githubsearch.helpers.DialogBuilder;
import com.linaverde.githubsearch.helpers.RequestHelper;
import com.linaverde.githubsearch.interfaces.RequestListener;
import com.linaverde.githubsearch.models.SearchAdapter;
import com.linaverde.githubsearch.models.ListItemsFactory;
import com.linaverde.githubsearch.models.SearchListItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private static final int START_HINT = 0;
    private static final int ERROR_HINT = 1;
    private static final int NO_RESULT_HINT = 3;

    private final int typingDelay = 600;
    private final int requestDelay = 1500;
    private boolean timeFromLastRequest;
    private CountDownTimer typingTimer;
    private CountDownTimer requestTimer;

    private SearchView mSearchView;
    private RequestHelper requestHelper;
    private ContentLoadingProgressBar progressBar;
    private ListView listView;

    private SearchAdapter adapter;
    private LinearLayout backgroundHint;
    private ImageView ivHint;
    private TextView tvHint;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem mSearch = menu.findItem(R.id.menu_search);
        mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setQueryHint("Введите название репозитория");
        mSearchView.setOnQueryTextListener(this);
        mSearch.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                if (adapter != null) {
                    adapter.clear();
                    adapter.notifyDataSetChanged();
                    setHint(START_HINT);
                }
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressBar = findViewById(R.id.progress_bar);
        progressBar.hide();

        listView = findViewById(R.id.lv_search_results);
        backgroundHint = findViewById(R.id.ll_hint);
        ivHint = findViewById(R.id.iv_hint);
        tvHint = findViewById(R.id.tv_hint);

        requestHelper = new RequestHelper(this);

        timeFromLastRequest = true;

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                InputMethodManager inputManager = (InputMethodManager) SearchActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(SearchActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (visibleItemCount > 0 && firstVisibleItem + visibleItemCount == totalItemCount
                        && adapter.hasNextPage()) {
                    loadNextPage();
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchListItem clickedItem = adapter.getItem(position);
                Intent i = new Intent(SearchActivity.this, IssueActivity.class);
                Bundle info = new Bundle();
                info.putString(getResources().getString(R.string.owner_name), clickedItem.getOwner());
                info.putString(getResources().getString(R.string.avatar), clickedItem.getAvatarUrl());
                info.putString(getResources().getString(R.string.rep_name), clickedItem.getRepName());
                info.putString(getResources().getString(R.string.issues_url), clickedItem.getIssueUrl());
                info.putString(getResources().getString(R.string.rep_description), clickedItem.getDescription());
                i.putExtras(info);
                startActivity(i);
            }
        });

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if (typingTimer != null) {
            typingTimer.cancel();
        }

        if (!newText.isEmpty()) {

            typingTimer = new CountDownTimer(typingDelay, typingDelay) {

                public void onTick(long millisUntilFinished) {
                    //do nothing
                }

                public void onFinish() {
                    progressBar.show();
                    backgroundHint.setVisibility(View.GONE);
                    requestHelper.executeGetSearch(newText, 1, new RequestListener() {
                        @Override
                        public void onComplete(String answer) {
                            progressBar.hide();
                            try {
                                JSONObject json = new JSONObject(answer);
                                adapter = new SearchAdapter(SearchActivity.this,
                                        json.getInt(getResources().getString(R.string.rep_total)),
                                        ListItemsFactory.createSearchResultsFromJson(SearchActivity.this, answer));
                                listView.setAdapter(adapter);
                                if (adapter.getTotal() == 0) {
                                    setHint(NO_RESULT_HINT);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(int responseCode) {
                            progressBar.hide();
                            setHint(ERROR_HINT);
                            if (responseCode == 403)
                                DialogBuilder.createDefaultDialog(SearchActivity.this, getLayoutInflater(),
                                        getResources().getString(R.string.error_request_limit), null);
                        }
                    });
                }
            };
            typingTimer.start();
        }

        return false;
    }

    private void loadNextPage() {
        if (timeFromLastRequest) {
            setRequestTimer();
            progressBar.show();
            int loadPage = adapter.getCurrPage() + 1;
            requestHelper.executeGetSearch(mSearchView.getQuery().toString(), adapter.getCurrPage() + 1, new RequestListener() {
                @Override
                public void onComplete(String answer) {
                    progressBar.hide();
                    if (loadPage > adapter.getCurrPage()) {
                        try {
                            List<SearchListItem> nextPage = ListItemsFactory.createSearchResultsFromJson(SearchActivity.this, answer);
                            if (adapter.getItemsList().addAll(nextPage)) {
                                adapter.increaseCurrPage();
                                adapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    requestTimer.start();
                }

                @Override
                public void onError(int responseCode) {
                    progressBar.hide();
                    if (responseCode == 403)
                        DialogBuilder.createDefaultDialog(SearchActivity.this, getLayoutInflater(),
                                getResources().getString(R.string.error_request_limit), null);
                    requestTimer.start();
                }
            });
        }
    }

    private void setRequestTimer(){
        timeFromLastRequest = false;
        if (requestTimer != null)
            requestTimer.cancel();
        requestTimer = new CountDownTimer(requestDelay, requestDelay) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                timeFromLastRequest = true;
            }
        };
    }

    private void setHint(int status) {
        backgroundHint.setVisibility(View.VISIBLE);
        switch (status) {
            case START_HINT:
                ivHint.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icon_search));
                tvHint.setText(getString(R.string.hint_edit_query));
                break;
            case ERROR_HINT:
                ivHint.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icon_error));
                tvHint.setText(getString(R.string.hint_error));
                break;
            case NO_RESULT_HINT:
                ivHint.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icon_question));
                tvHint.setText(getString(R.string.hint_no_results));
                break;
        }
    }
}