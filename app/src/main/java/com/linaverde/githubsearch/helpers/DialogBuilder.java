package com.linaverde.githubsearch.helpers;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.linaverde.githubsearch.R;
import com.linaverde.githubsearch.interfaces.CompleteActionListener;

public class DialogBuilder {

    public static void createDefaultDialog(Context context, LayoutInflater inflater, String text, final CompleteActionListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View viewDialog = inflater.inflate(R.layout.dialog_default, null);
        TextView tvText;
        Button btnOk;
        tvText = viewDialog.findViewById(R.id.tv_dialog);
        btnOk = viewDialog.findViewById(R.id.btn_ok);

        if (text != null)
            tvText.setText(text);

        builder.setView(viewDialog);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onOk();
                dialog.dismiss();
            }
        });

        if (!((Activity) context).isFinishing()) {
            dialog.show();
        }

    }
}
