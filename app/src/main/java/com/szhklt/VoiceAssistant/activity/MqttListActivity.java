package com.szhklt.VoiceAssistant.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.szhklt.VoiceAssistant.R;
import com.szhklt.VoiceAssistant.beam.Topic;
import com.szhklt.VoiceAssistant.db.WeiXinDBHandler;

import java.util.ArrayList;
import java.util.List;

public class MqttListActivity extends Activity {
    private static final String TAG = "MqttListActivity";
    private WeiXinDBHandler weiXinDBHandler;
    private List<Topic> topicList = new ArrayList<Topic>();//用来存放数据的数组
    private String clientId = "2644c2e4360b41f482238c1925559991";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "MqttListActivity的oncreate方法启动了");
        setContentView(R.layout.activity_mqttlist);
        weiXinDBHandler = new WeiXinDBHandler(MqttListActivity.this);
        ListView listView = findViewById(R.id.mqttlist);
        init();
        WeiXinPairAdapter adapter = new WeiXinPairAdapter(MqttListActivity.this, R.layout.activity_list, topicList);
        Log.e(TAG, adapter.toString());
        listView.setAdapter(adapter);
    }

    private void init() {//初始化数据
        topicList = weiXinDBHandler.queryTopicMsg();
    }

    public class WeiXinPairAdapter extends ArrayAdapter {

        private final int id;
        private  AlertDialog ad;

        public WeiXinPairAdapter(Context context, int headImage, List<Topic> obj) {
            super(context, headImage, obj);
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
            connectbutton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Log.e(TAG, "点击了连接");
                    String number = headText.getText().toString();
                    Log.e(TAG, number);


                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getApplicationContext());
                    alertDialog.setMessage("是否连接"+number);
                    alertDialog.setPositiveButton("否",
                            new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    dialog.dismiss();
                                }
                            });

                    alertDialog.setNegativeButton("是",
                            new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    Toast.makeText(getApplicationContext(), "点击了连接",
                                            Toast.LENGTH_SHORT).show();

                                    dialog.dismiss();
                                }
                            });

                    ad = alertDialog.create();

                    ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    ad.setCanceledOnTouchOutside(false);//点击外面区域不会让dialog消失
                    ad.show();



                }

            });
            unConnectbuton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Log.e(TAG, "点击了取消连接");
                    String number = headText.getText().toString();
                    Log.e(TAG, number);


                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getApplicationContext());
                    alertDialog.setMessage("是否连接"+number);
                    alertDialog.setPositiveButton("否",
                            new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    dialog.dismiss();
                                }
                            });

                    alertDialog.setNegativeButton("是",
                            new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    Toast.makeText(getApplicationContext(), "点击了取消连接",
                                            Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();

                                }
                            });

                    ad = alertDialog.create();

                    ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    ad.setCanceledOnTouchOutside(false);//点击外面区域不会让dialog消失
                    ad.show();

                }

            });

            return view;
        }


    }
}
