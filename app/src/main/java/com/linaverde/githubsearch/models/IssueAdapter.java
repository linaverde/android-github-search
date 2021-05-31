package com.linaverde.githubsearch.models;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.linaverde.githubsearch.R;


public class IssueAdapter extends ArrayAdapter<IssueListItem> {

    private Context context;
    private IssueListItem[] values;
    private static final String TAG = "IssueAdapter";

    public IssueAdapter(Context context, IssueListItem[] values) {
        super(context, R.layout.issue_item, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.issue_item, parent, false);

        IssueListItem item = getItem(position);
        ((TextView) rowView.findViewById(R.id.tv_number)).setText(item.getNumber());
        ((TextView) rowView.findViewById(R.id.tv_title)).setText(item.getTitle());

        TextView state = (TextView) rowView.findViewById(R.id.tv_state);
        state.setText(item.getState());
        if (item.getState().equals("open")) {
            state.setTextColor(context.getColor(R.color.green));
        } else {
            state.setTextColor(context.getColor(R.color.red));
        }

        IssueLabel[] labels = item.getLabels();
        if (labels.length > 0) {
            LinearLayout llTags = rowView.findViewById(R.id.ll_tags);
            LinearLayout currLL = new LinearLayout(context);
            currLL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            currLL.setOrientation(LinearLayout.HORIZONTAL);
            llTags.addView(currLL);
            for (IssueLabel label : labels) {
                TextView tag = new TextView(context);
                tag.setText(label.getText());
                tag.setTextColor(context.getResources().getColor(R.color.white, null));
                tag.setBackgroundResource(R.drawable.label_background);
                GradientDrawable drawable = (GradientDrawable) tag.getBackground();
                drawable.setColor(Color.parseColor("#" + label.getColor()));
                currLL.addView(tag);
//                if (!isVisible(tag)) {
//                    currLL.removeView(tag);
//                    currLL = new LinearLayout(context);
//                    currLL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//                    currLL.setOrientation(LinearLayout.HORIZONTAL);
//                    llTags.addView(currLL);
//                    currLL.addView(tag);
//                }
            }
        } else {
            rowView.findViewById(R.id.tv_labels).setVisibility(View.GONE);
        }

        return rowView;
    }

    //не работает, тк до вызывается до отрисовки
    private boolean isVisible(final View view) {
        if (view == null) {
            Log.d(TAG, "view is null");
            return false;
        }
        if (!view.isShown()) {
            Log.d(TAG, "view isnt shown");
            return false;
        }
        Log.d(TAG, "check actual position");
        final Rect actualPosition = new Rect();
        view.getGlobalVisibleRect(actualPosition);
        final Rect screen = new Rect(0, 0,
                Resources.getSystem().getDisplayMetrics().widthPixels,
                Resources.getSystem().getDisplayMetrics().heightPixels);
        return screen.contains(actualPosition.right, actualPosition.bottom);
    }

    @Override
    public int getCount() {
        return values.length;
    }

    @Override
    public IssueListItem getItem(int position) {
        return values[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public IssueListItem[] getValues() {
        return values;
    }

}
