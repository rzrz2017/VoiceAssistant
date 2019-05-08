package com.szhklt.VoiceAssistant.view;
import android.content.Context;
import android.util.AttributeSet;

public class SlipTextView extends android.support.v7.widget.AppCompatTextView {
    public SlipTextView(Context context) {
        super(context);
    }
    public SlipTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public SlipTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}