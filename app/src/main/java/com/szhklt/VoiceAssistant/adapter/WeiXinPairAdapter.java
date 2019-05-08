/*
package com.szhklt.VoiceAssistant.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.szhklt.VoiceAssistant.R;
import com.szhklt.VoiceAssistant.beam.Topic;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.List;


public class WeiXinPairAdapter extends ArrayAdapter {

    private  static String TAG ="WeiXinPairAdapter";
    private final int id;
    private MqttAndroidClient client;


    public WeiXinPairAdapter(Context context, int headImage, List<Topic> obj){
        super(context,headImage,obj);
        id = headImage;//这个是传入我们自己定义的界面
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Topic topic = (Topic) getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(id, null);//实例化一个对象
        LinearLayout linearLayout = view.findViewById(R.id.list);
        TextView headText = view.findViewById(R.id.connectname);
        Button connectbutton = view.findViewById(R.id.connect);
        Button unConnectbuton = view.findViewById(R.id.unconnect);
        headText.setText(topic.getSuTopic());
        connectbutton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Log.e(TAG,"点击了连接");
                String number = headText.getText().toString();
                Log.e(TAG,number);

                try {
                    client.subscribe(number,0);


                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }

        });
        unConnectbuton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Log.e(TAG,"点击了取消连接");
                String number = headText.getText().toString();
                Log.e(TAG,number);
                try {
                    client.unsubscribe(number);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }

        });

        return view;
    }





}




*/
