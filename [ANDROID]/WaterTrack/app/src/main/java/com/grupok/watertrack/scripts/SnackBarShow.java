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
    public void display(View rootVview, String text, int length, int maxLines, View anchorView, Context context){
        TypedValue background = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.menuBackgroundColor, background, true);
        TypedValue textColor = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.textColorPrimary, textColor, true);

        Snackbar.make(rootVview, text, length)
                .setBackgroundTint(background.data)
                .setTextColor(textColor.data)
                .setTextMaxLines(maxLines)
                .setAnchorView(anchorView)
                .show();
    }
}
