package com.szhklt.VoiceAssistant.service;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.szhklt.VoiceAssistant.activity.MqttActivity;
import com.szhklt.VoiceAssistant.beam.Topic;
import com.szhklt.VoiceAssistant.component.MyAIUI;
import com.szhklt.VoiceAssistant.db.WeiXinDBHandler;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.List;
import java.util.Map;


public class MqttService extends Service {
    public static final String TAG = "MqttService";

    private MqttAndroidClient client;
    private MqttConnectOptions conOpt;
    private static final String host = "tcp://4yrgvkn.mqtt.iot.gz.baidubce.com:1883";
    private String userName = "4yrgvkn/device03";
    private String passWord = "BctJVU9qb9q88AEk";
    private static String topic;
    private String clientId = "2644c2e4360b41f482238c1925559991";
    private static MyAIUI myAIUI;
    private static String receInfo;     //接收到的订阅信息
    private static String wxCode= null;    //微信openid
    private static String sn;
    private static MqttActivity mqttActivity;
    private static boolean isOnline = true;
    private static AlertDialog ad;
    private static WeiXinDBHandler weiXinDBHandler;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate方法");
        myAIUI = MyAIUI.getInstance();
        weiXinDBHandler = new WeiXinDBHandler(MqttService.this);
        init();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG,"onStartCommand方法执行了");

        return super.onStartCommand(intent, flags, startId);
    }


    public static void startservice(Context c){
        mqttActivity = (MqttActivity) c;
        Intent iService = new Intent(c,MqttService.class);
        iService.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        c.startService(iService);

    }

    public static void startservice(Context c ,String msg){
        mqttActivity = (MqttActivity) c;
        Intent iService = new Intent(c,MqttService.class);
        iService.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        c.startService(iService);
        if(msg !=null){
            sn =msg;
            Log.e(TAG,"接收到activity的sn:"+sn);
        }

    }

    //发布消息
    private void publish(String msg,Integer qos) {
        Log.e("MQTTService", "publish=" + msg);
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setQos(qos);
        mqttMessage.setRetained(false);

        try {
            Log.e("setPayload", String.valueOf(msg.getBytes("utf-8")));
            mqttMessage.setPayload(msg.getBytes("utf-8"));
            client.publish(topic, mqttMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    //初始化mqtt服务器
    private void init() {
        Log.e(TAG, "init执行了");
       // 服务器地址（协议+地址+端口号）
        String uri = host;
        client = new MqttAndroidClient(this, uri, clientId);
        // 设置MQTT监听并且接受消息
        client.setCallback(mqttCallback);
        conOpt = new MqttConnectOptions();
        // 清除缓存
        conOpt.setCleanSession(false);
        // 设置超时时间，单位：秒
        conOpt.setConnectionTimeout(10);
        // 心跳包发送间隔，单位：秒
        conOpt.setKeepAliveInterval(10);
        //自动重连
        conOpt.setAutomaticReconnect(true);
        // 用户名
        conOpt.setUserName(userName);
        // 密码
        conOpt.setPassword(passWord.toCharArray());

        // last will message
        boolean doConnect = true;
        String message = "Offline:machine|"+sn;
        Log.e("掉线发送的信息",message);
        String topic = "offline";
        Integer qos = 2;
        Boolean retained = false;
        if ((!message.equals("")) || (!topic.equals(""))) {
            // 最后的遗嘱
            try {
                Log.e(TAG, "设置最终遗嘱");
                conOpt.setWill(topic, message.getBytes(), qos.intValue(), retained.booleanValue());
            } catch (Exception e) {
                Log.e(TAG, "Exception Occured" + e.getMessage());
                doConnect = false;
                iMqttActionListener.onFailure(null, e);
            }
        }

        if (doConnect) {
            doClientConnection();
        }

    }

    //销毁
    @Override
    public void onDestroy() {
        try {
            client.disconnect();
            if(handler != null){
                handler.removeCallbacksAndMessages(null);
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    /**
     * 客户端连接MQTT服务器
     */
    private void doClientConnection() {
        if (!client.isConnected() && isConnectIsNomarl()) {
            try {
                Log.e(TAG, "doClientConnection");
                client.connect(conOpt, null, iMqttActionListener);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

    }

    // MQTT是否连接成功
    private IMqttActionListener iMqttActionListener = new IMqttActionListener() {

        @Override
        public void onSuccess(IMqttToken arg0) {
            Log.e(TAG, "连接成功........");
            try {
                // 订阅sn话题
                Log.e(TAG,"链接成功订阅sn:"+sn);
                client.subscribe("/"+sn, 0);
                client.subscribe("offline",1);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(IMqttToken arg0, Throwable arg1) {
            arg1.printStackTrace();
            // 连接失败，重连
            Log.e(TAG, "连接失败 ");
            AlertDialog dialog = new AlertDialog.Builder(getApplicationContext()).setTitle("客户端链接失败")
                    .setMessage("mqtt客户端连接不成功,请退出重试,或检查网络是否连接")
                    .setCancelable(false)
                    .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    })
                    .create();
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            dialog.show();

        }
    };


    /**
     * 回调时使用
     */
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            return  false;

        }
    });


    // MQTT监听并且接受消息
    private  MqttCallback mqttCallback = new MqttCallback() {
        @Override
        public void messageArrived(String top, MqttMessage message) throws Exception {
            try {
                //推送消息到达
                String str = new String(message.getPayload(), "utf-8");
                Log.e(TAG, "推送消息到达message:" + str);

                //获取内容
                if(str.contains(":")){
                    receInfo = str.substring(str.indexOf(":")+1);

                }

                //配对设备,将设备ID和sn存入数据库
                if(str.startsWith("Bind")){
                    Log.e(TAG,"bind");
                    mqttActivity.finish();
                    //弹出确认对话框
                    AlertDialog.Builder alertDialog=new AlertDialog.Builder(MqttService.this);
                    alertDialog.setMessage("确认配对吗?");
                    alertDialog.setPositiveButton("确认", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.e(TAG,"确认");
                            Topic subTopic = new Topic(receInfo,0) ;
                            weiXinDBHandler.insertALineOfTopic(subTopic);
                            topic = str.substring(str.indexOf("/")+1,str.lastIndexOf("/"));
                            if (isOnline){

                                publish("Bound:"+receInfo,1);
                            }


                        }

                    });
                    alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.e(TAG,"取消");
                            if (isOnline){
                                publish("对方取消了配对",1);
                            }
                        }

                    });

                     ad = alertDialog.create();

                    ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    ad.setCanceledOnTouchOutside(false);//点击外面区域不会让dialog消失
                    ad.show();;
                    return;

                }


                //发送链接,订阅/ID/SN,并设置手机在线
                else if(str.startsWith("Connect")){
                    Log.e(TAG,"connect");
                    //弹出确认对话框
                    AlertDialog.Builder alertDialog=new AlertDialog.Builder(MqttService.this);
                    alertDialog.setMessage("确认链接吗?");
                    alertDialog.setPositiveButton("确认", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.e(TAG,"确认");
                            try {
                                isOnline = true;
                                topic = receInfo;
                                Log.e("确认链接后topic",topic);
                                client.subscribe(topic,0);
                            } catch (MqttException e) {
                                e.printStackTrace();
                            }
                            if (isOnline){
                                publish("Accept:"+receInfo,1);
                            }
                        }

                    });
                    alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.e(TAG,"取消");
                            if (isOnline){
                                publish("对方取消了链接",1);
                            }
                        }

                    });

                    ad = alertDialog.create();

                    ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    ad.setCanceledOnTouchOutside(false);//点击外面区域不会让dialog消失
                    ad.show();;


                    publish("",0);
                    return;
                }


                //解除配对,退订/ID/SN
                else if(str.startsWith("Unbind")){
                    Log.e(TAG,"unbind");


                    //弹出确认对话框
                    AlertDialog.Builder alertDialog=new AlertDialog.Builder(MqttService.this);
                    alertDialog.setMessage("确认解除绑定吗?");
                    alertDialog.setPositiveButton("确认", new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Log.e(TAG,"确认");
                            try {
                                client.unsubscribe(topic);
                            } catch (MqttException e) {
                                e.printStackTrace();
                            }
                            if (isOnline){
                                publish("Disconnect:"+receInfo,1);
                            }
                        }

                    });
                    alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.e(TAG,"取消");
                            if (isOnline){
                                publish("对方取消了解绑",1);
                            }
                        }

                    });

                    ad = alertDialog.create();

                    ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    ad.setCanceledOnTouchOutside(false);//点击外面区域不会让dialog消失
                    ad.show();;
                    return;
                }


                //手机端掉线了
               else if(str.equals("Phone|"+wxCode)){
                   Log.e(TAG,"offline手机端掉线了");
                    isOnline = false;
                }


                //收到信息
                else {
                    if (str.startsWith("Directive")&& str.contains("data")) {
                        //截取json内容
                        String intent = getIntentPart(receInfo);
                        //调用AIUI处理intent
                        if(intent!= null){
                            myAIUI.handle(intent);
                            if(getText(intent)!= null){
                                Log.e(TAG,"getText:"+getText(intent));
                            }

                        }
                        publish("Respond:收到信息",0);
                    }

                    /*Log.e("finlish", "activity处理完成");
                    return false;
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = str;
                    handler.sendMessage(msg);*/
                }



            } catch (Exception e) {
                Log.e(TAG, "出错了");
                e.printStackTrace();
            }
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken arg0) {

            Log.e(TAG, "deliveryComplete");
        }

        @Override
        public void connectionLost(Throwable arg0) {
            // 失去连接，重连
            Log.e(TAG, "连接失败 ");
            doClientConnection();
        }
    };







    /**
     * 判断网络是否连接
     */
    private boolean isConnectIsNomarl() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            String type = info.getTypeName();
            Log.e(TAG, "MQTT当前网络类型：" + type);
            return true;
        } else {
            Log.e(TAG, "MQTT 没有可用网络");
            return false;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     提取intent部分
     *
     */
    private String getIntentPart(String msg) {
        String intent = null;
        //提取msg中的itent部分
        Map mapTypes = JSON.parseObject(msg);
        for (Object obj : mapTypes.keySet()){
            if(obj.toString() == "data"){
                String data = mapTypes.get(obj).toString();
                Log.e("data",data);
                List<Map<String,Object>> mapListJson = (List) JSONArray.parseArray(data);
                for (int i = 0; i < mapListJson.size(); i++) {
                    Map<String,Object> dataMap=mapListJson.get(i);
                    for(Map.Entry<String,Object> entry : dataMap.entrySet()){
                        String strkey1 = entry.getKey();
                        Object strval1 = entry.getValue();
                        Log.e("key",strkey1);
                        if ("intent".equals(strkey1)&&strval1 != null&& strval1.toString().length()>2 ){
                            intent = strval1.toString();
                            Log.e("value",intent);
                            return  intent;

                        }
                    }
                }
            }
        }
        return  null;
    }


    /**
     * 提取inten中的text   获取用户问题
     */
    public String getText(String intent){
        Map intentMap =  JSON.parseObject(intent);
        for(Object obj: intentMap.keySet()){
            if (obj.toString() =="text"){
                String text = intentMap.get(obj).toString();
                return text;
            }
        }
        return  null;
    }



}