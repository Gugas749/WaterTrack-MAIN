package com.grupok.watertrack.scripts;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.grupok.watertrack.R;

public class SnackBarShow {

    public void display(
            View rootView,
            String text,
            int length,
            int maxLines,
            View anchorView,
            Context context
    ) {
        if (context == null || rootView == null) {
            return; // evitar crash
        }

        TypedValue background = new TypedValue();
        context.getTheme().resolveAttribute(
                R.attr.menuBackgroundColor,
                background,
                true
        );

        TypedValue textColor = new TypedValue();
        context.getTheme().resolveAttribute(
                R.attr.textColorPrimary,
                textColor,
                true
        );

        Snackbar snackbar = Snackbar.make(rootView, text, length)
                .setBackgroundTint(background.data)
                .setTextColor(textColor.data)
                .setTextMaxLines(maxLines);

        if (anchorView != null) {
            snackbar.setAnchorView(anchorView);
        }

        snackbar.show();
    }
}

