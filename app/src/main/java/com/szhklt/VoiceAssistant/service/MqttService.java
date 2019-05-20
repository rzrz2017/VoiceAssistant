package com.szhklt.VoiceAssistant.service;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.szhklt.VoiceAssistant.activity.dialog.PhoneDiaActivity;
import com.szhklt.VoiceAssistant.activity.dialog.PhoneListDiaAct;
import com.szhklt.VoiceAssistant.beam.mqtt.Phone;
import com.szhklt.VoiceAssistant.beam.mqtt.PhonesLab;
import com.szhklt.VoiceAssistant.component.MyAIUI;
import com.szhklt.VoiceAssistant.util.CommonUtils;
import com.szhklt.VoiceAssistant.util.LogUtil;
import com.szhklt.VoiceAssistant.util.NetworkUtil;

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
import java.util.UUID;


public class MqttService extends Service {
    public static final String TAG = "MqttService";
    public Context mContext;
    private MqttAndroidClient client;
    private MqttConnectOptions conOpt;
    private String connectingTopic;
    private static final String host = "tcp://4yrgvkn.mqtt.iot.gz.baidubce.com:1883";
    private String userName = "4yrgvkn/device03";
    private String passWord = "BctJVU9qb9q88AEk";

    private String clientId = UUID.randomUUID().toString();
    private static MyAIUI myAIUI;

    private static String sn;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate方法");
        //test

        mContext = this;
        sn = CommonUtils.getSerialNumber();
        myAIUI = MyAIUI.getInstance();
        initMqttClient();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG,"onStartCommand方法执行了");
        doClientConnection();
        return super.onStartCommand(intent, flags, startId);
    }

    private PhoneDiaActivity.ClickCallBack clickCallBack;

    /**
     * 订阅消息
     */
    private void subscribe(String topic,int qos){
        LogUtil.e(TAG,"subscribe():"+topic + "qos:"+qos);
        try {
            client.subscribe(topic,qos);
        }catch (MqttException e){
            LogUtil.e(TAG,"订阅消息失败!:"+e);
        }
    }

    /**
     * 解除订阅
     * @param topic
     */
    private void unsubscribe(String topic){
        try {
            client.unsubscribe(topic);
        } catch (MqttException e) {
            e.printStackTrace();
            LogUtil.e(TAG,"解除订阅失败!"+e);
        }
    }

    //发布消息
    private void publish(String topic,String msg,Integer qos,Boolean retained) {
        Log.e(TAG, "目标:"+topic+"内容:" + msg);
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setQos(qos);
        mqttMessage.setRetained(retained);

        try {
            Log.e(TAG,"发布信息topic:"+topic);
            Log.e("setPayload", String.valueOf(msg.getBytes("utf-8")));
            mqttMessage.setPayload(msg.getBytes("utf-8"));
            client.publish(topic, mqttMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //初始化mqtt客户端
    private void initMqttClient() {
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
        String topic = sn;
        Integer qos = 2;
        Boolean retained = false;
        if ((!message.equals("")) || (!topic.equals(""))) {
            try {
                conOpt.setWill(topic, message.getBytes(), qos.intValue(), retained.booleanValue());
            } catch (Exception e) {
                Log.e(TAG, "Exception Occured" + e.getMessage());
                doConnect = false;
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
        } catch (MqttException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    /**
     * 客户端连接MQTT服务器
     */
    private void doClientConnection() {
        if (!client.isConnected() && NetworkUtil.isNetworkConnected(this)) {
            try {
                Log.e(TAG, "doClientConnection");
                client.connect(conOpt, null, iMqttActionListener);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * mqtt客户端启动回调
     */
    private IMqttActionListener iMqttActionListener = new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken arg0) {
            Log.e(TAG, "MQTT客户端连接成功");
            // 订阅sn话题
            Log.e(TAG,"链接成功订阅sn:"+sn);
            subscribe(sn,0);
        }

        @Override
        public void onFailure(IMqttToken arg0, Throwable arg1) {
            Log.e(TAG, "MQTT客户端连接失败:"+arg1.toString());
            arg1.printStackTrace();
            // 连接失败，重连
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

            doClientConnection();
        }
    };

    /**
     * mqtt客户端回调
     */
    private MqttCallback mqttCallback = new MqttCallback() {
        String receInfo;     //接收到的订阅信息//成员变量有会有初始值
        @Override
        public void messageArrived(String top, MqttMessage message) throws Exception {
            //推送消息到达
            String str = new String(message.getPayload(), "utf-8");
            Log.e(TAG, "推送消息到达message:" + str);

            //获取内容
            if(str.contains(":")){
                receInfo = str.substring(str.indexOf(":")+1);
                LogUtil.e(TAG,"receInfo:"+receInfo);
            }

            //绑定动作
            if(top.equals(sn)){
                //配对设备,将设备ID和sn存入数据库
                if(str.startsWith("Bind")){
                    LogUtil.e(TAG,"有手机端发起绑定");
                    //解析
                    String arr[];
                    arr = receInfo.split("/");
                    String id = arr[1];
                    String sn = arr[2];
                    LogUtil.e(TAG,"id:"+id);
                    LogUtil.e(TAG,"sn:"+sn);

                    if(isBound(receInfo)){
                        LogUtil.e(TAG,"已经绑定过了");
                        Toast.makeText(mContext,"已经绑定过了",Toast.LENGTH_LONG).show();
                        popDiaListAct();
                    }else{
                        //判断是否还有存储空间
                        if(isFull()){
                            popDiaListAct();
                        }else{
                            PhonesLab.get(mContext).addPhone(new Phone("rz",id,receInfo));
                            sendBroadcast(new Intent(PhoneListDiaAct.SYNC_LIST));//同步列表
                            publish(id,"Bound:"+receInfo,0,false);
                            subscribe(receInfo,0);
                        }
                    }
                    return;
                }
            }

            //发送链接,订阅/ID/SN,并设置手机在线
            if(str.startsWith("Connect")){
                Log.e(TAG,"connect");
                if(connectingTopic != null){
                    unsubscribe(connectingTopic);
                    publish(connectingTopic,"Disconnect:"+receInfo,1,false);

                }

                popDiaAct("有手机请求连接?",true,true,10000L, new PhoneDiaActivity.ClickCallBack() {
                    @Override
                    public void onOKClick() {
                        publish(receInfo,"Accept:"+receInfo,0,false);
                        connectingTopic = receInfo;
                    }

                    @Override
                    public void onNoClick() {

                    }
                });
                return;
            }

            //解除配对,退订/ID/SN
            if(str.startsWith("Unbind")){
                Phone phone = PhonesLab.get(mContext).getPhone(receInfo);
                if(phone != null){
                    LogUtil.e(TAG,"收到解绑信息后,查询数据库,的确有数据:"+phone.toString());
                    PhonesLab.get(mContext).deletePhone(phone);
                    //同步列表
                    mContext.sendBroadcast(new Intent(PhoneListDiaAct.SYNC_LIST));
                }
                if(connectingTopic != null && connectingTopic.equals(receInfo)){
                    connectingTopic = null;
                }
                unsubscribe(receInfo);
                publish(receInfo,"Unbind:"+receInfo,0,false);
                return;
            }

            //手机端掉线了
            if(str.startsWith("Offline")){
               Log.e(TAG,"offline手机端掉线了");
               String type;
               String id;

               type = receInfo.split("|")[0];
               id = receInfo.split("|")[1];
               //判断是否是当前连接着的手机掉线了
//               if(connectingTopic.split("/")[1].equals(id)){
//                   connectingTopic = null;
//               }

               return;
            }

            //收到信息
            if (str.startsWith("Directive")&& str.contains("data")) {
                //如果答案的top不等于当前主题,表示当前手机并没有和其该主机连接,立即返回
                if(!connectingTopic.equals(top)){
                    return;
                }

                //截取json内容
                String intent = getIntentPart(receInfo);
                //调用AIUI处理intent
                if(intent!= null){
                    myAIUI.handle(intent);
                }
                publish(connectingTopic,"Respond:收到信息",0,false);
                return;
            }

            if(str.startsWith("Disconnect")){
                Log.e(TAG,"断开连接Disconnect:"+LogUtil.getLineInfo());
                String topic = receInfo;
                if(connectingTopic.equals(topic)){
                    connectingTopic = null;
                }
                return;
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
            //重新链接
            doClientConnection();
        }
    };

    /**
     * 解绑手机,用方法封装一下方便list界面调用
     * @param topic
     */
    public synchronized void unbindPhone(String topic){
        Log.e(TAG,"unbind");
        popDiaAct("解除绑定", true, true, 0L,
                new PhoneDiaActivity.ClickCallBack() {
                    @Override
                    public void onOKClick() {
                        Phone phone = PhonesLab.get(mContext).getPhone(topic);
                        if(phone != null){
                            LogUtil.e(TAG,"收到解绑信息后,查询数据库,的确有数据:"+phone.toString());
                            PhonesLab.get(mContext).deletePhone(phone);
                            //同步列表
                            mContext.sendBroadcast(new Intent(PhoneListDiaAct.SYNC_LIST));
                        }
                        if(connectingTopic != null && connectingTopic.equals(topic)){
                            connectingTopic = null;
                        }
                        unsubscribe(topic);
                        publish(topic,"Unbind:"+topic,0,false);
                    }

                    @Override
                    public void onNoClick() {

                    }
                });
    }

    /**
     * 同时使用两次时会显示前一次
     * @param message
     * @param positiveBt
     * @param negativeBt
     * @param millisInFuture
     * @param clickCallBack
     */
    private void popDiaAct(String message,Boolean positiveBt,Boolean negativeBt,Long millisInFuture,
                           PhoneDiaActivity.ClickCallBack clickCallBack){
        if(message == null){
            return;
        }
        this.clickCallBack = clickCallBack;
        Intent intent = new Intent(this,PhoneDiaActivity.class);
        intent.putExtra("message",message);
        intent.putExtra("positive",positiveBt);
        intent.putExtra("negative",negativeBt);
        intent.putExtra("millisInFuture",millisInFuture);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void popDiaListAct(){
        Intent intent = new Intent(this, PhoneListDiaAct.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * 提取intent部分
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
     * 判断是否绑定过了
     * @return
     */
    private boolean isBound(String topic){
        List<Phone> phoneArrayList = PhonesLab.get(mContext).getPhones();
        for(Phone p:phoneArrayList){
            if(p.getTopic().equals(topic)){
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否满了
     * @return
     */
    private boolean isFull(){
        List<Phone> phoneArrayList = PhonesLab.get(mContext).getPhones();
        LogUtil.e(TAG,"phoneArrayList.size():"+phoneArrayList.size());
        if(phoneArrayList.size() > 5){
            return true;
        }
        return false;
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

    /**
     * 与MqttlistActivity进行数据传递client对象
     * @param intent
     * @return
     */

    @Override
    public IBinder onBind(Intent intent) {
        return new MqttBinder();
    }


    public class MqttBinder extends Binder {
        public MqttService getMqttService(){
            return  MqttService.this;
        }
        public PhoneDiaActivity.ClickCallBack getClickCallBack() {
            return clickCallBack;
        }
    }

    public MqttAndroidClient getClient(){
        return client;
    }
}