package com.robam.roki.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.robam.roki.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class EmojiEmptyView extends FrameLayout {

    @InjectView(R.id.txtDesc)
    TextView txtDesc;

    public EmojiEmptyView(Context context) {
        super(context);
        init(context, null);
    }

    public EmojiEmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public EmojiEmptyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    void init(Context cx, AttributeSet attrs) {

        View view = LayoutInflater.from(cx).inflate(R.layout.view_emoji_empty,
                this, true);
        if (!view.isInEditMode()) {
            ButterKnife.inject(this, view);

            if (attrs != null) {
                TypedArray ta = cx.obtainStyledAttributes(attrs, R.styleable.EmojiEmpty);
                String desc = ta.getString(R.styleable.EmojiEmpty_description);
                ta.recycle();

                setText(desc);
            }
        }
    }

    public void setText(String text) {
        txtDesc.setText(text);
    }

}
