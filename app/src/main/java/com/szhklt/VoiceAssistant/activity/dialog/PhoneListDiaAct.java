package com.szhklt.VoiceAssistant.activity.dialog;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.szhklt.VoiceAssistant.R;
import com.szhklt.VoiceAssistant.beam.mqtt.Phone;
import com.szhklt.VoiceAssistant.beam.mqtt.PhonesLab;
import com.szhklt.VoiceAssistant.impl.onPhoneClearListener;
import com.szhklt.VoiceAssistant.service.MqttService;
import com.szhklt.VoiceAssistant.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class PhoneListDiaAct extends Activity implements onPhoneClearListener {
    private static final String TAG = "PhoneListDiaAct";
    private Context mContext;
    private ListView listView;
    private TextView headText;
    private List<Phone> phoneList = new ArrayList<>();
    private PhoneAdapter adapter;
    private MqttService.MqttBinder mqttBinder;
    private MyBroadcastReceiver broadcastReceiver;
    private MqttService mqttService;

    public static final String SYNC_LIST = "com.szhklt.VoiceAssistant.activity.dialog.SYNC_LIST";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phonelistdiaact);
        mContext = PhoneListDiaAct.this;

        getPhoneList();
        adapter = new PhoneAdapter(PhoneListDiaAct.this,R.layout.item_phone,phoneList);
        headText = (TextView) findViewById(R.id.headtext);
        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        headText.setText("手机列表");
        registerReceiver();

        Intent bindIntent = new Intent(this,MqttService.class);
        bindService(bindIntent,connection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mqttBinder = (MqttService.MqttBinder)service;
            mqttService = mqttBinder.getMqttService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private void registerReceiver() {
        broadcastReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SYNC_LIST);
        registerReceiver(broadcastReceiver,intentFilter);
    }

    private void getPhoneList() {
        phoneList = PhonesLab.get(mContext).getPhones();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void deletePosition(int pos) {
        adapter.remove(phoneList.get(pos));
    }

    class MyBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(SYNC_LIST.equals(action)){
                getPhoneList();
                adapter = new PhoneAdapter(PhoneListDiaAct.this,R.layout.item_phone,phoneList);
                listView.setAdapter(adapter);
            }
        }
    }

    class PhoneAdapter extends ArrayAdapter<Phone>{
        private String TAG = "PhoneAdapter";
        private Context mContext;
        private int resourceId;

        public PhoneAdapter(Context context, int resource, List<Phone> objects) {
            super(context, resource, objects);
            mContext = context;
            resourceId = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.e(TAG,"getView(final int "+position+")");
            Phone phone = getItem(position);
            LogUtil.e(TAG,"phone:"+phone.toString());
            View view;
            ViewHolder viewHolder;
            if(convertView == null){
                view = LayoutInflater.from(getContext()).inflate(resourceId,null);
                viewHolder = new ViewHolder();
                viewHolder.clear = (ImageView) view.findViewById(R.id.clear);
                viewHolder.name = (TextView) view.findViewById(R.id.name);
                viewHolder.id = (TextView) view.findViewById(R.id.id);
                viewHolder.status = (ImageView) view.findViewById(R.id.status);
                viewHolder.saveposition = position;
                view.setTag(viewHolder);
            }else{
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.name.setText(phone.getName());
            viewHolder.id.setText(phone.getId());
            if(phone.getStatus() == false){
                viewHolder.status.setVisibility(View.GONE);
            }
            viewHolder.clear.setTag(position);//每次刷新listview都要从新设置位置(很关键)

            viewHolder.clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Phone phone = getItem(viewHolder.saveposition);
                    LogUtil.e(TAG,"被点击的项对应的值:"+phone.toString());
                    viewHolder.saveposition = Integer.parseInt(v.getTag().toString());

                    mqttService.unbindPhone(phone.getTopic());
                }
            });

            return view;
        }

        class ViewHolder{
            ImageView icon;
            int saveposition;
            ImageView clear;
            ImageView status;
            TextView name;
            TextView id;
        }
    }
}
