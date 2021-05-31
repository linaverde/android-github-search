package com.linaverde.githubsearch.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.linaverde.githubsearch.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

public class SearchAdapter extends ArrayAdapter<SearchListItem> {

    private Context context;
    private int total;
    private List<SearchListItem> values;
    private int mSize;
    private LruCache<String, Bitmap> mMemoryCache;
    private int currPage;
    private String query;

    public SearchAdapter(Context context, int total, List<SearchListItem> values) {
        super(context, R.layout.search_item, values);
        this.context = context;
        this.values = values;
        this.total = total;

        currPage = 1;
        mSize = context.getResources().getDimensionPixelSize(R.dimen.image_size);

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory());
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.search_item, parent, false);

        SearchListItem item = getItem(position);
        ((TextView) rowView.findViewById(R.id.tv_rep_name)).setText(item.getTitle());
        ((TextView) rowView.findViewById(R.id.tv_rep_desc)).setText(item.getDescription());

        String url = item.getAvatarUrl();
        ImageView avatar = (ImageView) rowView.findViewById(R.id.iv_avatar);
        Bitmap b = getBitmapFromMemCache(url);
        if (b != null) {
            avatar.setImageBitmap(b);

        } else {
            loadImageFromUrl(url, avatar);
        }

        return rowView;
    }

    private void loadImageFromUrl(String url, ImageView iv) {
        Picasso.get().load(url).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                addBitmapToMemoryCache(url, bitmap);
                iv.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }


    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    private Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public SearchListItem getItem(int position) {
        return values.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public boolean hasNextPage(){
        return total > getCount();
    }

    public List<SearchListItem> getItemsList(){
        return values;
    }

    public int getCurrPage(){
        return currPage;
    }

    public void increaseCurrPage(){
        currPage++;
    }

    public int getTotal() {
        return total;
    }
}
