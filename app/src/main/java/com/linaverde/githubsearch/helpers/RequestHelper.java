package com.linaverde.githubsearch.helpers;

import android.content.Context;
import android.util.Log;

import com.linaverde.githubsearch.R;
import com.linaverde.githubsearch.interfaces.RequestListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import cz.msebera.android.httpclient.Header;

public class RequestHelper {

    private final static String TAG = "RequestHelper";

    private Context context;
    private AsyncHttpResponseHandler handler;

    public RequestHelper(Context context) {
        this.context = context;
    }

    private JSONObject getAnswerBytes(byte[] response) {
        if (response.length > 0)
            try {
                return new JSONObject(new String(response, StandardCharsets.UTF_8));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        return null;
    }

    public void executeGetSearch(String title, Integer page, RequestListener listener) {
        AsyncHttpClient client = new AsyncHttpClient();

        String method = context.getResources().getString(R.string.url_search);

//        ((Activity) context).getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        RequestParams params = new RequestParams();
        params.add("q", title);
        params.add("per_page", context.getResources().getString(R.string.results_per_request));
        if (page != null)
            params.add("page", page.toString());

        client.addHeader("User-Agent", context.getResources().getString(R.string.user_agent));

        client.get(context.getResources().getString(R.string.url_backend) + method, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                Log.d(TAG, method + " request successful");
                Log.d(TAG, "answer: " + new String(response, StandardCharsets.UTF_8));
                listener.onComplete(new String(response, StandardCharsets.UTF_8));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Log.d(TAG, method + " request error with code " + statusCode);
                if (errorResponse != null)
                    Log.d(TAG, method + " request failed with error " + new String(errorResponse, StandardCharsets.UTF_8));
                listener.onError(statusCode);
            }
        });
    }

    public void executeGetIssues(String url, RequestListener listener) {
        AsyncHttpClient client = new AsyncHttpClient();
        Log.d(TAG, "issues url " + url);
        client.addHeader("User-Agent", context.getResources().getString(R.string.user_agent));

        RequestParams params = new RequestParams();
        params.add("state", "all");
        params.add("per_page", context.getResources().getString(R.string.issue_count));

        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                Log.d(TAG, "issues request successful");
                Log.d(TAG, "answer: " + new String(response, StandardCharsets.UTF_8));
                listener.onComplete(new String(response, StandardCharsets.UTF_8));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable error) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Log.d(TAG, "issues request error with code " + statusCode);
                if (errorResponse != null)
                    Log.d(TAG, "issues request failed with error " + new String(errorResponse, StandardCharsets.UTF_8));
                listener.onError(statusCode);
            }
        });
    }
}
