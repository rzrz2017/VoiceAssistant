package com.szhklt.VoiceAssistant.adapter;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.szhklt.VoiceAssistant.R;
import com.szhklt.VoiceAssistant.beam.Result;
import com.szhklt.VoiceAssistant.db.SearchRecordDBHandler;
import com.szhklt.VoiceAssistant.util.LogUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KeyHistoryAdapter extends BaseAdapter {
    private static final String TAG = "KeyHistoryAdapter";
    Context context;
    List<String> keys;
    SearchRecordDBHandler handler;

    public KeyHistoryAdapter(Context context, List<String> keys, SearchRecordDBHandler handler){
        this.context = context;
        this.keys = keys;
        this.handler = handler;
    }

    public void addAll(Collection<String> collection){
        if(keys == null){
            keys = new ArrayList<>();
        }
        keys.addAll(collection);
        notifyDataSetChanged();
    }

    public void clear() {
        if (keys != null) {
            keys.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return keys.size();
    }

    @Override
    public String getItem(int position) {
        return keys.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_key_history,null);
            vh = new ViewHolder();
            vh.keyTxt = (TextView)convertView.findViewById(R.id.key_txt);
            vh.deleteImg = (ImageView) convertView.findViewById(R.id.delete);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }

        final String text = getItem(position);

        vh.keyTxt.setText(text);

        vh.deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.e(TAG,"onClick()");
                handler.delect(text);
                keys.remove(text);
                notifyDataSetChanged();
            }
        });


        return convertView;
    }

    static class ViewHolder{
        TextView keyTxt;
        ImageView deleteImg;

    }
}
