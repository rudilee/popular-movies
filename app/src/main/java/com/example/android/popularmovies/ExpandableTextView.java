package com.example.android.popularmovies;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by rudilee on 7/20/17.
 */

public class ExpandableTextView extends android.support.v7.widget.AppCompatTextView {
    private final int DEFAULT_MAX_LINES = 4;

    private int mMaxLines;

    public ExpandableTextView(Context context) {
        super(context);
    }

    public ExpandableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mMaxLines = attrs.getAttributeIntValue("http://schemas.android.com/apk/res/android", "maxLines", DEFAULT_MAX_LINES);

        setEllipsize(TextUtils.TruncateAt.END);
        setMaxLines(mMaxLines);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setMaxLines(getMaxLines() == mMaxLines ? Integer.MAX_VALUE : mMaxLines);
            }
        });
    }
}
