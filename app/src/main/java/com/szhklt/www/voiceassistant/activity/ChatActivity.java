
package com.szhklt.www.voiceassistant.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ListView;
import com.szhklt.www.voiceassistant.R;
import com.szhklt.www.voiceassistant.adapter.MessageAdapter;
import com.szhklt.www.voiceassistant.beam.Msg;
import com.szhklt.www.voiceassistant.service.MainService;
import com.szhklt.www.voiceassistant.util.LogUtil;
import com.szhklt.www.voiceassistant.view.VolumeView;

public class ChatActivity extends Activity {
	public static String TAG = "ChatActivity";
	public static Boolean PreStatus;
	private MyReceiver receiver = null;//广播接收者s
	private ListView mListView;
	private static List<Msg> messages;
	private static MessageAdapter mMessageAdapter;
	private Msg msg;
	public static Boolean ISCHATMODE = false; 
	private VolumeView mVolumeView;
	private RandomThreand mRandomThreand;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.e(TAG,"onCreate()");
		setContentView(R.layout.activity_duolun);
		ISCHATMODE = true;
		receiver= new MyReceiver();//注册广播接收者
		IntentFilter filter=new IntentFilter();
		filter.addAction("com.szhklt.msg.CHAT_MESSAGE_answer");
		filter.addAction("com.szhklt.msg.CHAT_MESSAGE_question");
		filter.addAction("com.szhklt.msg.closeActivity.ChatActivity");
		registerReceiver(receiver,filter);    
		initview();
		messages=new ArrayList<Msg>();
		msg = new Msg("亲！可以开始问我了", Msg.MSG_RECEIVE);
		messages.add(msg);
		mMessageAdapter  = new MessageAdapter(this, R.layout.view_item_chat, messages);
		mListView.setAdapter(mMessageAdapter);
		startRandomVolumeView();
	}
	
    private void startRandomVolumeView() {
        mVolumeView.start();
        
        //随机设置音量大小.
        if (mRandomThreand != null) {
            mRandomThreand.stopRunning();
            mRandomThreand = null;
        }
        mRandomThreand = new RandomThreand();
        mRandomThreand.start();
    }

    private void stopRandmVolumeView() {
        if (mRandomThreand != null) {
            mRandomThreand.stopRunning();
            mRandomThreand = null;
        }
        mVolumeView.stop();
    }
    
    private class RandomThreand extends Thread {
        private static final int MOVE_STOP = 1;

        private static final int MOVE_START = 0;

        private int state;

        @Override
        public void run() {
            state = MOVE_START;

            while (true) {
                if (state == MOVE_STOP) {
                    break;
                }
                try {
                    sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

//                mVolumeView.setVolume(getRandom(0, 100));
                mVolumeView.setVolume(MainService.volume_value*10);
            }
        }

        public void stopRunning() {
            state = MOVE_STOP;
        }
    }
    
    private int getRandom(int min, int max) {
        Random random = new Random();
        int r = random.nextInt(max) % (max - min + 1) + min;
        return r;
    }
    
	/**
	 * 广播接收者
	 * @author ysc
	 */
	public class MyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals("com.szhklt.msg.CHAT_MESSAGE_answer")){
				String answer = intent.getStringExtra("answer");
				Msg msg = new Msg(answer, Msg.MSG_RECEIVE);  
				messages.add(msg);
				mMessageAdapter.notifyDataSetChanged();
				mListView.setSelection(messages.size());
			}else if(intent.getAction().equals("com.szhklt.msg.CHAT_MESSAGE_question")){
				String question = intent.getStringExtra("question");
				Msg msg = new Msg(question, Msg.MSG_SEND);  
				messages.add(msg);
				mMessageAdapter.notifyDataSetChanged();
				mListView.setSelection(messages.size());
			}else if(intent.getAction().equals("com.szhklt.msg.closeActivity.ChatActivity")){
				LogUtil.e(TAG,"退出聊天"+LogUtil.getLineInfo());
				finish();
			}
		}
	}
	/**
	 * view初始化
	 */
	public void initview(){
		mListView=(ListView)findViewById(R.id.duolun_list); 
		mVolumeView = (VolumeView) findViewById(R.id.volume_view);
	}
	@Override
	protected void onResume() {		 
		super.onResume();
		LogUtil.e(TAG,"onResume()");
		
		ISCHATMODE = true;
	}
	@Override
	protected void onPause() {
		super.onPause();	
		LogUtil.e(TAG,"onPause()");
		ISCHATMODE = false;
		stopRandmVolumeView();
		finish();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.e(TAG,"onDestroy()");
		stopRandmVolumeView();
		if(receiver!=null){
			unregisterReceiver(receiver);
			receiver=null;
		}
	}
}