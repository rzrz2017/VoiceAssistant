package com.szhklt.VoiceAssistant.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.Toast;

import com.szhklt.VoiceAssistant.MainApplication;
import com.szhklt.VoiceAssistant.R;
import com.szhklt.VoiceAssistant.adapter.SuperAdapter;
import com.szhklt.VoiceAssistant.beam.Tool;
import com.szhklt.VoiceAssistant.floatWindow.FloatActionButtomView;
import com.szhklt.VoiceAssistant.floatWindow.FloatWindowManager;
import com.szhklt.VoiceAssistant.multithreadeddownloader.DownLoadUtils;
import com.szhklt.VoiceAssistant.multithreadeddownloader.UpdateActivity;
import com.szhklt.VoiceAssistant.service.checkAPKUpdataService;
import com.szhklt.VoiceAssistant.util.LogUtil;

import java.util.ArrayList;

public class ToolsActivity extends Activity {
    private static final String TAG = "ToolsActivity";
    private Context mContext;
    private GridView grid_tool;
    private BaseAdapter mAdapter = null;
    private ArrayList<Tool> mData = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tools);
        mContext = this;
        grid_tool = (GridView)findViewById(R.id.grid_tool);

        initData();
    }

    private void initData() {
        mData = new ArrayList<Tool>();
        mData.add(new Tool(R.drawable.icon_blepwd_visble_write,"隐藏图标"));
        mData.add(new Tool(R.drawable.ic_sp_notify,"闹钟列表"));
        mData.add(new Tool(R.drawable.ic_sp_updata,"检查更新"));
        mData.add(new Tool(R.drawable.ic_sp_forbid,getSwicthState("swstate")?"禁止唤醒":"允许唤醒"));
        mData.add(new Tool(R.drawable.ic_sp_clear,"清理设置"));
        mData.add(new Tool(R.drawable.ic_blepush_brown,"打开蓝牙"));
        mData.add(new Tool(R.drawable.ic_auxin,"打开Auxin"));
        mData.add(new Tool(R.drawable.ic_sp_bind,"绑定小程序"));

        mAdapter = new SuperAdapter<Tool>(mData,R.layout.item_grid_tool) {
            @Override
            public void bindView(ViewHolder holder, Tool obj) {
//                holder.getView(R.id.img_tool).setI
                holder.setImageResource(R.id.img_tool,obj.getIcon());
                holder.setText(R.id.txt_tool,obj.getName());
            }
        };

        grid_tool.setAdapter(mAdapter);
        grid_tool.setOnItemClickListener((parent, view, position, id) -> {
            if(position == 0){
                FloatWindowManager.getInstance().removeFloatButton(mContext);
            }else if(position == 1){
                Intent intent=new Intent(mContext, AlarmListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                mContext.startActivity(intent);
            }else if(position == 2){
                mContext.startService(new Intent(MainApplication.getContext(), checkAPKUpdataService.class));//启动apk更新检查服务
                LogUtil.e("updata","MainApplication.UpdataMark :"+MainApplication.UpdataMark+ LogUtil.getLineInfo());
                if(MainApplication.UpdataMark == true){
                    Intent intent=new Intent(mContext, UpdateActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                    mContext.startActivity(intent);
                }else{
                    Toast.makeText(mContext, "主人,当前版本为"+ DownLoadUtils.getAPPVersionCode(mContext)+",无需更新呢!", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(mContext, ADCandJetActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                    mContext.startActivity(intent);
                }
            }else if(position == 3){
                SuperAdapter.ViewHolder holder  = (SuperAdapter.ViewHolder) view.getTag();
                String txt = holder.getText(R.id.txt_tool);
                if("禁止唤醒".equals(txt)){
                    holder.setText(R.id.txt_tool,"允许唤醒");
                    Intent intent1=new Intent();
                    intent1.putExtra("count", "STOP_PCMRECORD");
                    intent1.setAction("com.szhklt.FloatWindow.FloatSmallView");
                    mContext.sendBroadcast(intent1);
                    saveSwicthState(false, "swstate");
                    Toast.makeText(mContext, "语音助手：已关闭唤醒", Toast.LENGTH_SHORT).show();
                }else{
                    holder.setText(R.id.txt_tool,"禁止唤醒");
                    saveSwicthState(true, "swstate");
                    Intent intent1=new Intent();
                    intent1.putExtra("count", "WRITE_AUDIO");
                    intent1.setAction("com.szhklt.FloatWindow.FloatSmallView");
                    mContext.sendBroadcast(intent1);
                    saveSwicthState(true, "swstate");
                }
            }else if(position == 4){
                Intent intent1=new Intent(mContext, RebootSetActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                mContext.startActivity(intent1);
            }else if(position == 5){
                FloatActionButtomView.disposeBluePush();
            }else if(position == 6){
                SuperAdapter.ViewHolder holder  = (SuperAdapter.ViewHolder) view.getTag();
                FloatActionButtomView.disposeAuxin();
                if(FloatActionButtomView.auxinStatus){
                    holder.setText(R.id.txt_tool,"关闭Auxin");
                }else{
                    holder.setText(R.id.txt_tool,"打开Auxin");
                }
            }else if(position == 7){
                Intent intent2 = new Intent(mContext, MqttActivity.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent2);
            }
        });
    }

    public void saveSwicthState(boolean state,String key){
        SharedPreferences.Editor editor = MainApplication.getContext().getSharedPreferences("keystatus",Context.MODE_PRIVATE).edit();
        if(state){
            editor.putString(key,"on");
        }else{
            editor.putString(key,"off");
        }
        editor.commit();
    }

    private boolean getSwicthState(String key){
        SharedPreferences pref = MainApplication.getContext().getSharedPreferences("keystatus",Context.MODE_PRIVATE);
        String state = pref.getString(key,"on");
        if(state.equals("on")){
            return true;
        }else{
            return false;
        }
    }

}
