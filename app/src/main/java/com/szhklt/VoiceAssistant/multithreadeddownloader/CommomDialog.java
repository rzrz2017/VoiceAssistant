package com.szhklt.VoiceAssistant.multithreadeddownloader;

import com.szhklt.VoiceAssistant.R;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;


public class CommomDialog extends Dialog implements View.OnClickListener{
    private TextView content0;
    private TextView content1;
    private TextView content2;

    
    private TextView titleTxt;
    private TextView submitTxt;
    private TextView cancelTxt;

    private Context mContext;
    private String[] content;
    private OnCloseListener listener;
    private String positiveName;
    private String negativeName;
    private String title;

    public CommomDialog(Context context) {
        super(context);
        this.mContext = context;
    }
    public CommomDialog(Context context, String[] content) {
        super(context, R.style.dialog);
        this.mContext = context;
        this.content = content;
    }

    public CommomDialog(Context context, int themeResId, String[] content) {
        super(context, themeResId);
        this.mContext = context;
        this.content = content;
    }

    public CommomDialog(Context context, int themeResId, String[] content, OnCloseListener listener) {
        super(context, themeResId);
        this.mContext = context;
        this.content = content;
        this.listener = listener;
    }

    protected CommomDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    public CommomDialog setTitle(String title){
        this.title = title;
        return this;
    }

    public CommomDialog setPositiveButton(String name){
        this.positiveName = name;
        return this;
    }

    public CommomDialog setNegativeButton(String name){
        this.negativeName = name;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_commom);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
        initView();
    }

    private void initView(){
        content0 = (TextView)findViewById(R.id.content0);
        content1 = (TextView)findViewById(R.id.content1);
        content2 = (TextView)findViewById(R.id.content2);
        
        titleTxt = (TextView)findViewById(R.id.title);
        submitTxt = (TextView)findViewById(R.id.submit);
        submitTxt.setOnClickListener(this);
        cancelTxt = (TextView)findViewById(R.id.cancel);
        cancelTxt.setOnClickListener(this);

        content0.setText(content[0]);
        content1.setText(content[1]);
        content2.setText(content[2]);
        
        if(!TextUtils.isEmpty(positiveName)){
            submitTxt.setText(positiveName);
        }

        if(!TextUtils.isEmpty(negativeName)){
            cancelTxt.setText(negativeName);
        }

        if(!TextUtils.isEmpty(title)){
            titleTxt.setText(title);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel:
                if(listener != null){
                    listener.onClick(this, false);
                }
                this.dismiss();
                break;
            case R.id.submit:
                if(listener != null){
                    listener.onClick(this, true);
                }
                break;
        }
    }

    public interface OnCloseListener{
        void onClick(Dialog dialog, boolean confirm);
    }
}
