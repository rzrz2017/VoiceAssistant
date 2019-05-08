package com.szhklt.VoiceAssistant.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rich.czlylibary.bean.MusicInfo;
import com.rich.czlylibary.bean.SearchSongNewResult;
import com.rich.czlylibary.bean.SingerNew;
import com.rich.czlylibary.bean.SongNew;
import com.rich.czlylibary.sdk.ResultCallback;
import com.szhklt.VoiceAssistant.R;
import com.szhklt.VoiceAssistant.adapter.KeyHistoryAdapter;
import com.szhklt.VoiceAssistant.adapter.SuperAdapter;
import com.szhklt.VoiceAssistant.adapter.TagBaseAdapter;
import com.szhklt.VoiceAssistant.db.SearchRecordDBHandler;
import com.szhklt.VoiceAssistant.db.SearchRecordDBHelper;
import com.szhklt.VoiceAssistant.util.LogUtil;
import com.szhklt.VoiceAssistant.util.MiGuSearcher;
import com.szhklt.VoiceAssistant.view.TagCloudLayout;

import java.io.Serializable;
import java.util.ArrayList;

public class SearchActivity extends Activity implements View.OnClickListener{
    private static final String TAG = "SearchActivity";
    private static final int SHOW_UI = 1;
    private static final int DISAPPEAR_UI = 2;
    private ListView listView;
    private EditText keyEdt;
    private Button searchBtn;
    private ImageView clear;
    private TagCloudLayout mContainer;
    private ArrayList<String> mList;
    private TagBaseAdapter tagAdapter;
    private GridView historyGrid;
    private ArrayList<MusicInfo> musicInfoList;
    private SuperAdapter<MusicInfo> listAdapter;
    private SearchRecordDBHelper dbHelper;
    private SearchRecordDBHandler dbHandler;
    private KeyHistoryAdapter historyAdapter;
    private ProgressBar progressBar;
    private TextView textView;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtil.e(TAG,"onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if(msg.what == DISAPPEAR_UI){
                    if(textView != null && progressBar != null){
                        listView.setVisibility(View.VISIBLE);
                        textView.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                    }
                    if(listAdapter != null){
                        listAdapter.notifyDataSetChanged();
                    }
                }else if(msg.what == SHOW_UI){
                    if(textView != null && progressBar != null){
                        listView.setVisibility(View.GONE);
                        textView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.VISIBLE);
                    }
                }
                return false;
            }
        });
        musicInfoList=new ArrayList<MusicInfo>();
        initDB();
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void initDB(){
        LogUtil.e(TAG,"initDB()");
        dbHelper = new SearchRecordDBHelper(this,"Records.db",null,1);
        dbHandler = new SearchRecordDBHandler(dbHelper.getWritableDatabase());;
    }

    private void initViews() {
        textView = (TextView) findViewById(R.id.tip);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        clear = (ImageView) findViewById(R.id.clear);
        historyGrid = (GridView) findViewById(R.id.grid);
        refresh();
        listView= findViewById(R.id.list_search_result);
        keyEdt = (EditText) findViewById(R.id.search_key);
        searchBtn = (Button) findViewById(R.id.search);
        searchBtn.setOnClickListener(this);
        clear.setOnClickListener(this);
        mContainer = (TagCloudLayout) findViewById(R.id.container);
        mList = new ArrayList<String>();
        mList.add("体面");
        mList.add("走着走着就散了");
        mList.add("薛之谦");
        mList.add("张碧晨");
        mList.add("庄心妍");
        mList.add("毛不易");
        mList.add("鹿晗");
        mList.add("周杰伦");
        mList.add("陈奕迅");
        mList.add("张学友");
        mList.add("邓紫棋");

        tagAdapter = new TagBaseAdapter(this,mList);
        mContainer.setAdapter(tagAdapter);

        mContainer.setItemClickListener(new TagCloudLayout.TagItemClickListener() {
            @Override
            public void itemClick(int position) {
                keyEdt.setText(tagAdapter.getItem(position));
                onSearch(keyEdt.getText().toString());
                refresh();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                returnData(position);
                finish();
            }
        });

        historyGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                keyEdt.setText(historyAdapter.getItem(position));
                onSearch(historyAdapter.getItem(position));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    //刷新搜索记录
    private void refresh() {
        ArrayList<String> history;
        if(historyAdapter != null){
            historyAdapter.clear();
        }
        //1读取数据库
        history = dbHandler.query();
        for(int i = 0;i < history.size();i++){
            LogUtil.e(TAG,"history:"+history.get(i));
        }
        //2刷新UI
        if(historyAdapter == null){
            historyAdapter = new KeyHistoryAdapter(this, history,dbHandler);
            historyGrid.setAdapter(historyAdapter);
        }else{
            historyAdapter.addAll(history);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.search:
                if(TextUtils.isEmpty(keyEdt.getText().toString())){
                    Toast.makeText(this,"请输入关键词",Toast.LENGTH_SHORT).show();
                    return;
                }
                onSearch(keyEdt.getText().toString());
                break;
            case R.id.clear:
                keyEdt.setText("");
                break;
        }
    }

    /**
     * 回传数据
     */
    private void returnData(int position){
        Bundle bundle = new Bundle();
        bundle.putSerializable("migu",(Serializable)musicInfoList);
        LogUtil.e(TAG,"musicInfoList.size():"+musicInfoList.size());
        Intent intent = new Intent();
        intent.putExtras(bundle);
        LogUtil.e(TAG,"index:"+String.valueOf(musicInfoList.indexOf(listAdapter.getItem(position))));
        intent.putExtra("index",String.valueOf(musicInfoList.indexOf(listAdapter.getItem(position))));
        setResult(RESULT_OK,intent);
    }

    /**
     * 搜索
     */
    private void onSearch(String key){
        MiGuSearcher.searchMusicByKey(key, 1, 15, 1, 1, 1,callback);
        dbHandler.insert(keyEdt.getText().toString());

    }

    private ResultCallback<SearchSongNewResult> callback = new ResultCallback<SearchSongNewResult>() {
        @Override
        public void onStart() {
            handler.obtainMessage(SHOW_UI).sendToTarget();
            musicInfoList.clear();
        }

        @Override
        public void onSuccess(SearchSongNewResult searchSongNewResult) {
            handler.obtainMessage(DISAPPEAR_UI).sendToTarget();
            searchSongPlay(searchSongNewResult.getSearchSong().getData().getResult());
        }

        @Override
        public void onFailed(String s, String s1) {
            musicInfoList.clear();
        }

        @Override
        public void onFinish() {

        }
    };

    /**
     * 查询歌曲
     * @param songNews
     */
    private void searchSongPlay(SongNew[] songNews) {
        LogUtil.e(TAG,"songNews.length:"+songNews.length);
        try {
            musicInfoList.clear();

            disposeSongData(songNews);
            if (musicInfoList.isEmpty()) {
//                showToast("查询歌曲成功，但是歌曲没有版权ID，不能播放");
                LogUtil.e(TAG,"查询歌曲成功，但是歌曲没有版权ID，不能播放");
            } else {
                //刷新listview
                if(listAdapter == null){
                    listAdapter = new SuperAdapter<MusicInfo>(musicInfoList,R.layout.view_item_play_medie) {

                        @Override
                        public void bindView(ViewHolder holder, MusicInfo musicInfo) {
                            setHolder(holder,musicInfo);
                        }
                    };
                    listView.setAdapter(listAdapter);
                }else{
//                    listAdapter.addAll(musicInfoList);
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理数据
     */
    private void disposeSongData(SongNew[] songNews){
        for (SongNew songNew : songNews) {
            MusicInfo musicInfo = new MusicInfo();
            if (songNew.getFullSongs() != null && songNew.getFullSongs().length > 0) {
                musicInfo.setMusicId(songNew.getFullSongs()[0].getCopyrightId());
                LogUtil.e(TAG,"MusicId:"+songNew.getFullSongs()[0].getCopyrightId());
            } else {
                continue;
            }
            musicInfo.setMusicName(songNew.getName());
            LogUtil.e(TAG,"MusicName:"+songNew.getName());
            musicInfo.setSingerName(songNew.getSingers()[0].getName());
            LogUtil.e(TAG,"SingerName:"+songNew.getSingerName());
            SingerNew[] ss = songNew.getSingers();
                LogUtil.e(TAG,"SingerName:"+"ss.length:"+ss.length+ss[0].toString());
            if (songNew.getMvPic() != null && songNew.getMvPic().length != 0) {
                musicInfo.setPicUrl(songNew.getMvPic()[0].getPicPath());
                LogUtil.e(TAG,"PicUrl:"+songNew.getMvPic()[0].getPicPath());
            }
            if(songNew.getSingers()[0].getName() != null){
                musicInfo.setSingerName(songNew.getSingers()[0].getName());
                LogUtil.e(TAG,"SingerName:"+songNew.getSingers()[0].getName());
            }
            LogUtil.e(TAG,"LrcUrl:"+songNew.getLyricUrl());
            musicInfoList.add(musicInfo);
            LogUtil.e(TAG,"musicInfoList.size():"+musicInfoList.size()+LogUtil.getLineInfo());
            LogUtil.e(TAG,"=================================");
        }
    }

    private void setHolder(SuperAdapter.ViewHolder holder, MusicInfo musicInfo) {
        if(musicInfo.getMusicName() != null){
            LogUtil.e("setHolder","MusicName:"+musicInfo.getMusicName());
            holder.setText(R.id.tv_media_name,musicInfo.getMusicName());
        }
        if(musicInfo.getSingerName() != null){
            LogUtil.e("setHolder","SingerName:"+musicInfo.getSingerName());
            holder.setText(R.id.tv_media_auther,musicInfo.getSingerName());
        }
    }
}
