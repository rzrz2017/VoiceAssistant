package com.szhklt.VoiceAssistant.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
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
import com.szhklt.VoiceAssistant.service.MqttService;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MqttListActivity extends Activity {
    private static final String TAG = "MqttListActivity";
    private List<Topic> topicList = new ArrayList<Topic>();//用来存放数据的数组
    private  MqttAndroidClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "MqttListActivity的oncreate方法启动了");
        setContentView(R.layout.activity_mqttlist);
        ListView listView = findViewById(R.id.mqttlist);



        init();
        WeiXinPairAdapter adapter = new WeiXinPairAdapter(MqttListActivity.this, R.layout.activity_list, topicList);
        Log.e(TAG, adapter.toString());
        listView.setAdapter(adapter);
        Intent myServiceIntent = new Intent(MqttListActivity.this, MqttService.class);
        bindService(myServiceIntent,mServiceConnection,Context.BIND_AUTO_CREATE);
        
    }



    ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MqttService myService = ((MqttService.MqttBinder) service).getMqttService();
             client = myService.getClient();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

    };


    private void init() {//初始化数据
//        topicList = weiXinDBHandler.queryTopicMsg();
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
            Button bindButton = view.findViewById(R.id.unbind);
            if(topic.getState() == 1){
                connectbutton.setText("取消连接");
            }
            headText.setText(topic.getSuTopic());


            /**
             * 连接按钮点击时间
             */
            connectbutton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Log.e(TAG, "点击了连接");
                    String number = headText.getText().toString();
                    Log.e(TAG, number);
                    if("连接".equals(connectbutton.getText()) ) {

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getApplicationContext());
                        alertDialog.setMessage("是否连接" + number);
                        alertDialog.setPositiveButton("否",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                        alertDialog.setNegativeButton("是",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            client.subscribe(number, 0);
                                            Toast.makeText(getApplicationContext(), "你链接了" + number + "",
                                                    Toast.LENGTH_SHORT).show();
//                                            weiXinDBHandler.queryTopicMsgState(number);
                                            refresh();
                                        } catch (MqttException e) {
                                            e.printStackTrace();
                                            Toast.makeText(getApplicationContext(), "出错了,请重试",
                                                    Toast.LENGTH_SHORT).show();
                                            refresh();
                                        }
                                        dialog.dismiss();
                                    }
                                });

                        ad = alertDialog.create();

                        ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                        ad.setCanceledOnTouchOutside(false);//点击外面区域不会让dialog消失
                        ad.show();

                    }

                    if("取消连接".equals(connectbutton.getText())){

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getApplicationContext());
                        alertDialog.setMessage("是否取消连接" + number);
                        alertDialog.setPositiveButton("否",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                        alertDialog.setNegativeButton("是",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            Log.e(TAG,"取消连接number:"+number);
                                            client.unsubscribe(number);
                                            Toast.makeText(getApplicationContext(), "你取消对" + number + "的链接",
                                                    Toast.LENGTH_SHORT).show();
//                                            weiXinDBHandler.updateTopicMsg(number,0);
                                            MqttMessage mqttMessage = new MqttMessage();
                                            mqttMessage.setQos(1);
                                            mqttMessage.setRetained(false);
                                            String msg = "设备端取消了对"+number+"的连接";
                                            mqttMessage.setPayload(msg.getBytes("utf-8"));
                                            client.publish(number, mqttMessage);
                                            refresh();
                                        } catch (MqttException e) {
                                            e.printStackTrace();
                                            Toast.makeText(getApplicationContext(), "出错了,请重试",
                                                    Toast.LENGTH_SHORT).show();
                                            refresh();
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }
                                        dialog.dismiss();
                                    }
                                });

                        ad = alertDialog.create();

                        ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                        ad.setCanceledOnTouchOutside(false);//点击外面区域不会让dialog消失
                        ad.show();

                    }

                }

            });


            /**
             * 绑定按钮点击时间
             */
            bindButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Log.e(TAG, "点击了取消绑定");
                    String number = headText.getText().toString();
                    Log.e(TAG, number);
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getApplicationContext());
                    alertDialog.setMessage("是否取消绑定"+number);
                    alertDialog.setPositiveButton("否",
                            new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    Toast.makeText(getApplicationContext(), "你未取消对"+number+"的绑定",
                                        Toast.LENGTH_SHORT).show();
                                    refresh();
                                    dialog.dismiss();
                                }
                            });

                    alertDialog.setNegativeButton("是",
                            new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int which)
                                {

                                    try {
//                                        weiXinDBHandler.deleteTopoicMsg(number);
                                        Toast.makeText(getApplicationContext(), "你取消了对"+number+"的绑定",
                                                Toast.LENGTH_SHORT).show();
                                        refresh();
                                    } catch ( Exception e) {
                                        e.printStackTrace();
                                        Toast.makeText(getApplicationContext(), "出错了,请重试",
                                                Toast.LENGTH_SHORT).show();
                                        refresh();
                                    }

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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
    }


    public void refresh() {
        onCreate(null);
        Log.e(TAG,"刷新了");

    }
}
