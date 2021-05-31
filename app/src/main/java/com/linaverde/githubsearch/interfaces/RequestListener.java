package com.linaverde.githubsearch.interfaces;

public interface RequestListener {

    void onComplete(String answer);

    void onError(int responseCode);

}
