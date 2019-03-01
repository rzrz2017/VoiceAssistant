package com.szhklt.VoiceAssistant.util;

import android.view.View;

import com.rich.czlylibary.bean.MusicinfoResult;
import com.rich.czlylibary.bean.RecommendMusic;
import com.rich.czlylibary.bean.SearchAlbumsNewResult;
import com.rich.czlylibary.bean.SearchAllNewResult;
import com.rich.czlylibary.bean.SearchLyricNewResult;
import com.rich.czlylibary.bean.SearchSingerNewResult;
import com.rich.czlylibary.bean.SearchSingerSAMNewResult;
import com.rich.czlylibary.bean.SearchSongNewResult;
import com.rich.czlylibary.bean.SearchSongNewRsp;
import com.rich.czlylibary.bean.SearchSuggestNewResult;
import com.rich.czlylibary.bean.SearchTagSongNewResult;
import com.rich.czlylibary.sdk.HttpClientManager;
import com.rich.czlylibary.sdk.ResultCallback;

public class MiGuSearcher {
    private static final String TAG = "MiGuSearcher";
    /**
     * 搜索歌曲通过关键词
     * @param keyWord 关键词
     * @param pageIndex 当前页,默认为1
     * @param pageSize  每页条数,默认15
     * @param searchType  搜索类型:1智能搜索2关键词搜索3搜索歌手下歌曲
     * @param issemantic  语义判定1语义判定0不走语义
     * @param isCorrect  是否开启容错0关闭1开启(默认)
     * @param callback  回调接口
     */
    public static void searchMusicByKey(String keyWord,
                                        int pageIndex,
                                        int pageSize,
                                        int searchType,
                                        int issemantic,
                                        int isCorrect,
                                        ResultCallback<SearchSongNewResult> callback){
        HttpClientManager.searchMusicByKey(keyWord,pageIndex,pageSize,searchType,issemantic,isCorrect,callback);
    }

    /**
     * 通过关键词搜索歌手
     * @param keyWord
     * @param pageIndex
     * @param pageSize
     * @param issemantic
     * @param isCorrect
     * @param callback
     */
    public static void searchSingerByKey(String keyWord,
                                         int pageIndex,
                                         int pageSize,
                                         int issemantic,
                                         int isCorrect,
                                         ResultCallback<SearchSingerNewResult> callback){
        HttpClientManager.searchSingerByKey(keyWord,pageIndex,pageSize,issemantic,isCorrect,callback);
    }

    /**
     * 根据keyWord(专辑名)和其它限定参数搜索专辑信息
     * @param keyWord
     * @param pageIndex
     * @param pageSize
     * @param issemantic
     * @param isCorrect
     * @param callback
     */
    public static void searchAlbumByKey(String keyWord,
                                        int pageIndex,
                                        int pageSize,
                                        int issemantic,
                                        int isCorrect,
                                        ResultCallback<SearchAlbumsNewResult> callback){
        HttpClientManager.searchAlbumByKey(keyWord,pageIndex,pageSize,issemantic,isCorrect,callback);
    }

    /**
     * 根据keyWord(标签)和其它限定参数搜索标签下歌曲
     * @param keyWord
     * @param pageIndex
     * @param pageSize
     * @param issemantic
     * @param isCorrect
     * @param callback
     */
    public static void searchTagMusicByKey(String keyWord,
                                           int pageIndex,
                                           int pageSize,
                                           int issemantic,
                                           int isCorrect,
                                           ResultCallback<SearchTagSongNewResult> callback){
        HttpClientManager.searchTagMusicByKey(keyWord,pageIndex,pageSize,issemantic,isCorrect,callback);

    }

    /**
     * 无维度搜索
     * @param keyWord
     * @param pageIndex
     * @param pageSize
     * @param searchType
     * @param issemantic
     * @param isCorrect
     * @param callback
     */
    public static void searchAllByKey(String keyWord,
                                      int pageIndex,
                                      int pageSize,
                                      int searchType,
                                      int issemantic,
                                      int isCorrect,
                                      ResultCallback<SearchAllNewResult> callback){
        HttpClientManager.searchAllByKey(keyWord,pageIndex,pageSize,issemantic,isCorrect,callback);
    }

    /**
     * 联想搜索:根据keyWord和其它限定参数进行搜索
     * @param keyWord
     * @param pageIndex
     * @param pageSize
     * @param callback
     */
    public static void searchSuggestByKey(String keyWord,
                                          int pageIndex,
                                          int pageSize,
                                          ResultCallback<SearchSuggestNewResult> callback){
        HttpClientManager.searchSuggestByKey(keyWord,pageIndex,pageSize,callback);
    }

    /**
     * 搜索歌手下面单曲,专辑,MV
     * @param keyWord
     * @param pageIndex
     * @param pageSize
     * @param callback
     */
    public static void searchSingerSAMByKey(String keyWord,
                                            int pageIndex,
                                            int pageSize,
                                            ResultCallback<SearchSingerSAMNewResult> callback){
        HttpClientManager.searchSingerSAMByKey(keyWord,pageIndex,pageSize,callback);
    }

    /**
     * 歌词搜索歌曲
     * @param keyWord
     * @param pageIndex
     * @param pageSize
     * @param callback
     */
    public static void searchSongByLyric(String keyWord,
                                         int pageIndex,
                                         int pageSize,
                                         ResultCallback<SearchLyricNewResult> callback){
        HttpClientManager.searchSongByLyric(keyWord,pageIndex,pageSize,callback);
    }

    /**
     * 通过歌曲id查询歌曲信息
     * @param musicId
     * @param callback
     */
    public static void findMusicInfoByid(String musicId,ResultCallback<MusicinfoResult> callback) {
        HttpClientManager.findMusicInfoByid(musicId, callback);
    }

    /**
     *
     * @param startNum 分页起始位置
     * @param endNum 分页结束位
     * @param tempo 配速
     * @param callback
     */
    public static void findRecommendSong(int startNum,int endNum,int tempo,ResultCallback<RecommendMusic> callback){
        HttpClientManager.findRecommendSong(startNum, endNum, tempo,callback);
    }

//    new ResultCallback<MusicinfoResult>() {
//        @Override
//        public void onStart() {
//            LogUtil.e(TAG,"onstart");
//        }
//
//        @Override
//        public void onSuccess(MusicinfoResult s) {
//            LogUtil.e(TAG,"onSuccess=" + s);
//            LogUtil.e(TAG,"查询歌曲信息成功");
//        }
//
//        @Override
//        public void onFailed(String code, String failedInfo) {
//            LogUtil.e(TAG,"onFailed=" + code + "---" + failedInfo);
//            LogUtil.e(TAG,"查询歌曲信息失败");
//        }
//
//        @Override
//        public void onFinish() {
//            LogUtil.e(TAG,"onFinish");
//        }
//    })
}
