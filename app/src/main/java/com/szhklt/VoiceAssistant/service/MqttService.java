package com.szhklt.VoiceAssistant.service;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.view.WindowManager;

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
    private static final int MAX_SIZE = 10;//限制最大
    public Context mContext;

    private MqttAndroidClient client;
    private MqttConnectOptions conOpt;
    private static final String host = "tcp://4yrgvkn.mqtt.iot.gz.baidubce.com:1883";
    private String userName = "4yrgvkn/device03";
    private String passWord = "BctJVU9qb9q88AEk";

    private String clientId = UUID.randomUUID().toString();
    private static MyAIUI myAIUI;

    private static String sn;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.e(TAG, "onCreate方法");
        //test

        mContext = this;
        sn = CommonUtils.getSerialNumber();
        myAIUI = MyAIUI.getInstance();
        //连接MQTT(MQTT初始化)
        initMqttClient();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.e(TAG,"onStartCommand方法执行了");
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
        LogUtil.e(TAG, "publish---目标:"+topic+"内容:" + msg+"retained:"+retained);
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setQos(qos);
        mqttMessage.setRetained(retained);

        try {
            LogUtil.e(TAG,"发布信息topic:"+topic);
            LogUtil.e("setPayload", String.valueOf(msg.getBytes("utf-8")));
            mqttMessage.setPayload(msg.getBytes("utf-8"));
            client.publish(topic, mqttMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //初始化mqtt客户端
    private void initMqttClient() {
        LogUtil.e(TAG, "init执行了");
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
        //设置临终遗嘱
        // last will message
        boolean doConnect = true;
        String message = "Offline:machine|"+sn;
        String topic = sn;
        Integer qos = 2;
        Boolean retained = true;
        if ((!message.equals("")) || (!topic.equals(""))) {
            try {
                conOpt.setWill(topic, message.getBytes(), qos.intValue(), retained.booleanValue());
            } catch (Exception e) {
                LogUtil.e(TAG, "Exception Occured" + e.getMessage());
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
                LogUtil.e(TAG, "doClientConnection");
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
            LogUtil.e(TAG, "MQTT客户端连接成功");
            // 订阅sn话题
            LogUtil.e(TAG,"链接成功订阅sn:"+sn);
            subscribe(sn,0);
            Phone cp = PhonesLab.get(mContext).getCurPhone();
            if(cp != null){
                LogUtil.e(TAG,"从新订阅当前主题设备:"+cp.getTopic()+LogUtil.getLineInfo());
                subscribe(cp.getTopic(),1);
            }

            //清除自身retained
            publish(sn,"",1,true);

        }

        @Override
        public void onFailure(IMqttToken arg0, Throwable arg1) {
            LogUtil.e(TAG, "MQTT客户端连接失败:"+arg1.toString());
            arg1.printStackTrace();
            // 连接失败，重连
            AlertDialog diaLogUtil = new AlertDialog.Builder(getApplicationContext()).setTitle("客户端链接失败")
                    .setMessage("mqtt客户端连接不成功,请退出重试,或检查网络是否连接")
                    .setCancelable(false)
                    .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface diaLogUtil, int id) {
                            diaLogUtil.dismiss();
                        }
                    })
                    .create();
            diaLogUtil.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            diaLogUtil.show();

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
            LogUtil.e(TAG, "推送消息到达message:" + str);

            //获取内容
            if(str.contains(":")){
                receInfo = str.substring(str.indexOf(":")+1);
                LogUtil.e(TAG,"receInfo:"+receInfo);
            }

            //手机端掉线
            if(str.startsWith("Offline")){
                String[] arr = receInfo.split("\\|");
                String type = arr[0];
                String id = null;
                if(type.equals("Phone")){
                    id = arr[1];
                }
                LogUtil.e(TAG,"手机掉线");
                Phone cp = PhonesLab.get(mContext).getCurPhone();
                if(cp != null){
                    String cId = cp.getId();
                    if(cId.equals(id)){
                        LogUtil.e(TAG,"当前连接着的手机掉线了");
                        unsubscribe(cp.getTopic());
                        //灭灯
                        cp.setStatus(false);
                        PhonesLab.get(mContext).updatePhone(cp);
                        //刷新列表
                        mContext.sendBroadcast(new Intent(PhoneListDiaAct.SYNC_LIST));
                    }
                }
                return;
            }

            //coonect动作
            if(top.equals(sn)){
                if(str.startsWith("Connect")){
                    String[] arr = receInfo.split("\\|");
                    String name = arr[0];
                    LogUtil.e(TAG, "前来连接的手机的名字 name:" +name+ LogUtil.getLineInfo());
                    String topic = arr[1];
                    LogUtil.e(TAG, "通信的 topic:" +topic+ LogUtil.getLineInfo());
                    String id = topic.split("/")[1];//获取手机id
                    LogUtil.e(TAG, "id:" +id+ LogUtil.getLineInfo());


                    if(!isBound(topic)){//没有绑定过
                        if(PhonesLab.get(mContext).getPhones().size() >= MAX_SIZE){
                            LogUtil.e(TAG,"已经最大了");
                            return;
                        }
                        LogUtil.e(TAG,"新手机连接");
                        popDiaAct("新手机连接?",true,true,10000L, new PhoneDiaActivity.ClickCallBack() {
                            @Override
                            public void onOKClick() {

                                //判断之前有没有手机连接
                                if(PhonesLab.get(mContext).getCurPhone() != null){
                                    publish(PhonesLab.get(mContext).getCurPhone().getTopic(),
                                            "Disconnect:"+PhonesLab.get(mContext).getCurPhone().getTopic(),
                                            1,
                                            false);
                                    //刷新之前的状态
                                    Phone tmp = PhonesLab.get(mContext).getCurPhone();
                                    if(tmp != null){
                                        tmp.setStatus(false);
                                        PhonesLab.get(mContext).updatePhone(tmp);
                                    }
                                }

                                //做一些收到连接的操作

                                publish(id,"Accept:"+topic,1,true);
                                subscribe(topic,1);
                                subscribe(id,1);//监听手机掉线
                                if(PhonesLab.get(mContext).getPhones().size() < MAX_SIZE) {
                                    PhonesLab.get(mContext).addPhone(new Phone(name, id, topic, true));
                                }
                                mContext.sendBroadcast(new Intent(PhoneListDiaAct.SYNC_LIST));
                            }

                            @Override
                            public void onNoClick() {

                            }
                        });

                    }else{//有绑定过
                        LogUtil.e(TAG,"手机有绑定过");

                        //当前没有在线的手机
                        if(PhonesLab.get(mContext).getCurPhone() == null){
                            LogUtil.e(TAG,"有手机登录");
                            popDiaAct("有手机登录?",true,true,10000L, new PhoneDiaActivity.ClickCallBack() {
                                @Override
                                public void onOKClick() {
                                    //1 UI
                                    Phone tmp2 = PhonesLab.get(mContext).getPhone(topic);
                                    if(tmp2 != null) {
                                        tmp2.setStatus(true);
                                        PhonesLab.get(mContext).updatePhone(tmp2);
                                    }
                                    //2 Accept
                                    publish(id,"Accept:"+topic,1,true);
                                    subscribe(topic,1);
                                    mContext.sendBroadcast(new Intent(PhoneListDiaAct.SYNC_LIST));
                                }

                                @Override
                                public void onNoClick() {

                                }
                            });
                            return;
                        }

                        if(!PhonesLab.get(mContext).getCurPhone().getTopic().equals(topic)){
                            LogUtil.e(TAG,"切换连接");
                            popDiaAct("切换连接?",true,true,10000L, new PhoneDiaActivity.ClickCallBack() {
                                @Override
                                public void onOKClick() {
                                    //有手机连接保存数据库
                                    Phone cur = PhonesLab.get(mContext).getCurPhone();
                                    //使得之前连接的手机断开
                                    publish(cur.getTopic(),"Disconnect:"+PhonesLab.get(mContext).getCurPhone().getTopic(),1,false);
                                    unsubscribe(PhonesLab.get(mContext).getCurPhone().getTopic());

                                    publish(id,"Accept:"+topic,1,true);
                                    subscribe(topic,1);
                                    //刷新之前的状态
                                    Phone tmp = PhonesLab.get(mContext).getCurPhone();
                                    if(tmp != null) {
                                        tmp.setStatus(false);
                                        PhonesLab.get(mContext).updatePhone(tmp);
                                    }
                                    Phone tmp2 = PhonesLab.get(mContext).getPhone(topic);
                                    if(tmp2 != null) {
                                        tmp2.setStatus(true);
                                        PhonesLab.get(mContext).updatePhone(tmp2);
                                    }
                                    //刷新列表
                                    mContext.sendBroadcast(new Intent(PhoneListDiaAct.SYNC_LIST));
                                }

                                @Override
                                public void onNoClick() {

                                }
                            });
                        }else{
                            LogUtil.e(TAG,"前来连接的手机和当前连接着的的一样");
                            publish(id,"Accept:"+topic,1,true);
                            subscribe(topic,1);
                        }
                    }

                }
                return;
            }


            //手机端掉线了
            if(str.startsWith("Disconnect")){
               publish(top,"",1,true); //清除Disconnect掉线的预留
               LogUtil.e("Disconnect","Disconnect手机端解绑了 receInfo:"+receInfo);
               String type;
               String topic;
               String id;

               type = receInfo.split("|")[0];
               topic = receInfo;
               id = receInfo.split("|")[1];

               LogUtil.e("Disconnect","当前在线的topic:"+PhonesLab.get(mContext).getCurPhone().getTopic()+LogUtil.getLineInfo());
               LogUtil.e("Disconnect","解除连接的topic:"+topic+LogUtil.getLineInfo());
               if(PhonesLab.get(mContext).getCurPhone().getTopic().equals(topic)){
                   //清除当前在线的设备并刷新UI
                   Phone tmp = PhonesLab.get(mContext).getCurPhone();
                   if(tmp != null) {
                       tmp.setStatus(false);
                       PhonesLab.get(mContext).updatePhone(tmp);
                   }
                   mContext.sendBroadcast(new Intent(PhoneListDiaAct.SYNC_LIST));
               }
               //灭灯
               return;
            }

            LogUtil.e(TAG,"----------------------------------");
            LogUtil.e(TAG,PhonesLab.get(mContext).getCurPhone().getTopic()+LogUtil.getLineInfo());
            LogUtil.e(TAG,str+LogUtil.getLineInfo());

            //收到指令
            if(PhonesLab.get(mContext).getCurPhone().getTopic().equals(top)){
                if (str.startsWith("Directive")&& str.contains("data")) {
                    LogUtil.e(TAG,"---------Directive---------"+ LogUtil.getLineInfo());
                    String topic = top;

                    //截取json内容
                    String intent = getIntentPart(receInfo);
                    //调用AIUI处理intent
                    if(intent!= null){
                        myAIUI.handle(intent);
                    }
                    publish(topic,"Respond:收到信息",0,false);
                    return;
                }
                return;
            }
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken arg0) {
            LogUtil.e(TAG, "deliveryComplete");
        }

        @Override
        public void connectionLost(Throwable arg0) {
            // 失去连接，重连
            LogUtil.e(TAG, "连接失败 ");
            //重新链接
            doClientConnection();
        }
    };

    /**
     * 解绑手机,用方法封装一下方便list界面调用
     * @param topic
     */
    public synchronized void unbindPhone(String topic){
        LogUtil.e(TAG,"unbindPhone");
        popDiaAct("解除绑定", true, true, 0L,
                new PhoneDiaActivity.ClickCallBack() {
                    @Override
                    public void onOKClick() {
                        Phone phone = PhonesLab.get(mContext).getPhone(topic);
                        if(phone != null){
                            LogUtil.e(TAG,"收到解绑信息后,查询数据库,的确有数据:"+phone.toString());
                            //判断是不是当前正在连接着的设备
                            if(PhonesLab.get(mContext).getCurPhone() != null &&
                                    phone.equals(PhonesLab.get(mContext).getCurPhone())){
                                LogUtil.e(TAG,"删除的设备是当前正在连接着的设备:"+phone.toString());
                                publish(phone.getTopic(),"Disconnect:"+phone.getTopic(),1,true);
                            }
                            unsubscribe(phone.getTopic());
                            unsubscribe(phone.getId());
                            PhonesLab.get(mContext).deletePhone(phone);
                            //同步列表
                            mContext.sendBroadcast(new Intent(PhoneListDiaAct.SYNC_LIST));
                        }

                        unsubscribe(topic);
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
                LogUtil.e("data",data);
                List<Map<String,Object>> mapListJson = (List) JSONArray.parseArray(data);
                for (int i = 0; i < mapListJson.size(); i++) {
                    Map<String,Object> dataMap=mapListJson.get(i);
                    for(Map.Entry<String,Object> entry : dataMap.entrySet()){
                        String strkey1 = entry.getKey();
                        Object strval1 = entry.getValue();
                        LogUtil.e("key",strkey1);
                        if ("intent".equals(strkey1)&&strval1 != null&& strval1.toString().length()>2 ){
                            intent = strval1.toString();
                            LogUtil.e("value",intent);
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