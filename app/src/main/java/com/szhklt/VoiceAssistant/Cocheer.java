package com.szhklt.VoiceAssistant;

import android.util.Base64;

import com.szhklt.CocheerCallBack;
import com.szhklt.VoiceAssistant.Common.CaseConverter;
import com.szhklt.VoiceAssistant.Common.HMAC_SHA1;
import com.szhklt.VoiceAssistant.Common.MD5Utils;
import com.szhklt.VoiceAssistant.Common.SHA1;
import com.szhklt.VoiceAssistant.util.LogUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Cocheer {
    private static String TAG = "Cocheer";
    private final String app_key = "21550546642042";
    private final String app_secret = "d2ac05961060263c6b9f4e5bb45d7e17";

    private String sign;
    private String timestamp;
    private String nonce;

    private String result;

    OkHttpClient okHttpClient;

    public Cocheer(){
        okHttpClient = new OkHttpClient();
    }

    /**
     * 获取专辑类
     */
    public void getAlbumGategories() {
        timestamp = String.valueOf(System.currentTimeMillis());
        LogUtil.e(TAG,"timestamp:"+timestamp);
        nonce = String.valueOf((char)(int)(Math.random()*26+97));

        createSign(timestamp,nonce);

        HttpUrl.Builder url = HttpUrl.parse("https://www.cocheer.net/cloud/auto/categories").newBuilder();
        url.addQueryParameter("timestamp", timestamp)
                .addQueryParameter("nonce", nonce);
        LogUtil.e(TAG, "url:" + url.toString());
        LogUtil.e(TAG, "sign:" + sign);
        LogUtil.e(TAG, "app_key:" + app_key);
        final Request request = new Request.Builder()
//                .url("www.cocheer.net/cloud/auto/categories?timestamp="+timestamp+"&nonce=d")//获取专辑分类
                .url(url.build())
                .addHeader("sign", sign)
                .addHeader("app_key", app_key)
                .get()//默认就是GET请求，可以不写
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e(TAG, "onFailure:");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                result = response.body().string();
                LogUtil.e(TAG, "onResponse:" + result);

//                Bundle bundle = new Bundle();
//                bundle.putString("result", result);
//                Message msg = new Message();
//                msg.what = 1;
//                msg.setData(bundle);
//                handler.sendMessage(msg);
            }
        });
    }

    /**
     * 获取分类下专辑
     */
    public void getAlbumBelowCategory() {
        timestamp = String.valueOf(System.currentTimeMillis());
        LogUtil.e(TAG,"timestamp:"+timestamp);
        nonce = String.valueOf((char)(int)(Math.random()*26+97));

        createSign(timestamp,nonce);

        HttpUrl.Builder url = HttpUrl.parse("https://www.cocheer.net/cloud/auto/cocheer_albums").newBuilder();
        url.addQueryParameter("timestamp", timestamp)
                .addQueryParameter("nonce", nonce);
        LogUtil.e(TAG, "url:" + url.toString());
        LogUtil.e(TAG, "sign:" + sign);
        LogUtil.e(TAG, "app_key:" + app_key);
        final Request request = new Request.Builder()
//                .url("www.cocheer.net/cloud/auto/categories?timestamp="+timestamp+"&nonce=d")//获取专辑分类
                .url(url.build())
                .addHeader("sign", sign)
                .addHeader("app_key", app_key)
                .get()//默认就是GET请求，可以不写
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e(TAG, "onFailure:");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                result = response.body().string();
                LogUtil.e(TAG, "onResponse:" + result);

            }
        });
    }

    /**
     * 根据专辑id获取专辑详情
     */
    public void getAlbumDetailsBaseOnID(String albumId,CocheerCallBack callBack) {
        timestamp = String.valueOf(System.currentTimeMillis());
        LogUtil.e(TAG,"timestamp:"+timestamp);
        nonce = String.valueOf((char)(int)(Math.random()*26+97));

        createSign(timestamp,nonce);

        HttpUrl.Builder url = HttpUrl.parse("https://www.cocheer.net/cloud/auto/cocheer_albums/" + albumId).newBuilder();
        url.addQueryParameter("timestamp", timestamp)
                .addQueryParameter("nonce", nonce);
        LogUtil.e(TAG, "url:" + url.toString());
        LogUtil.e(TAG, "sign:" + sign);
        LogUtil.e(TAG, "app_key:" + app_key);
        final Request request = new Request.Builder()
//                .url("www.cocheer.net/cloud/auto/categories?timestamp="+timestamp+"&nonce=d")//获取专辑分类
                .url(url.build())
                .addHeader("sign", sign)
                .addHeader("app_key", app_key)
                .get()//默认就是GET请求，可以不写
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e(TAG, "onFailure:");
                callBack.searchFailed("onFailure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                result = response.body().string();
                LogUtil.e(TAG, "onResponse:" + result);
                callBack.searchSuccess(result);
            }
        });
    }

    /**
     * 获取专辑下面的内容
     */
    public void getFollowingContentFromAlbum(String album_id,CocheerCallBack callBack) {
        timestamp = String.valueOf(System.currentTimeMillis());
        LogUtil.e(TAG,"timestamp:"+timestamp);
        nonce = String.valueOf((char)(int)(Math.random()*26+97));

        createSign(timestamp,nonce);

        HttpUrl.Builder url = HttpUrl.parse("https://www.cocheer.net/cloud/auto/audios").newBuilder();
        url.addQueryParameter("timestamp", timestamp)
                .addQueryParameter("nonce", nonce)
                .addQueryParameter("album_id", album_id);
        LogUtil.e(TAG, "url:" + url.toString());
        LogUtil.e(TAG, "sign:" + sign);
        LogUtil.e(TAG, "app_key:" + app_key);
        final Request request = new Request.Builder()
//                .url("www.cocheer.net/cloud/auto/categories?timestamp="+timestamp+"&nonce=d")//获取专辑分类
                .url(url.build())
                .addHeader("sign", sign)
                .addHeader("app_key", app_key)
                .get()//默认就是GET请求，可以不写
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e(TAG, "onFailure:");
                callBack.searchFailed("onFailure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                result = response.body().string();
                LogUtil.e(TAG, "onResponse:" + result);
                callBack.searchSuccess(result);
            }
        });
    }

    /**
     * 搜索专辑
     */
    public void searchAlbum(String album,CocheerCallBack callBack) {
        timestamp = String.valueOf(System.currentTimeMillis());
        LogUtil.e(TAG,"timestamp:"+timestamp);
        nonce = String.valueOf((char)(int)(Math.random()*26+97));

        createSign(timestamp,nonce);

        HttpUrl.Builder url = HttpUrl.parse("https://www.cocheer.net/cloud/auto/search/album").newBuilder();
        url.addQueryParameter("timestamp", timestamp)
                .addQueryParameter("nonce", nonce)
                .addQueryParameter("key", album);
        LogUtil.e(TAG, "url:" + url.toString());
        LogUtil.e(TAG, "sign:" + sign);
        LogUtil.e(TAG, "app_key:" + app_key);
        final Request request = new Request.Builder()
//                .url("www.cocheer.net/cloud/auto/categories?timestamp="+timestamp+"&nonce=d")//获取专辑分类
                .url(url.build())
                .addHeader("sign", sign)
                .addHeader("app_key", app_key)
                .get()//默认就是GET请求，可以不写
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e(TAG, "onFailure:");
                callBack.searchFailed("onFailure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                result = response.body().string();
                LogUtil.e(TAG, "onResponse:" + result);
                callBack.searchSuccess(result);
            }
        });
    }

    /**
     * 搜索单曲
     */
    public void sreachSongs(String key, CocheerCallBack callBack) {
        timestamp = String.valueOf(System.currentTimeMillis());
        LogUtil.e(TAG,"timestamp:"+timestamp);
        nonce = String.valueOf((char)(int)(Math.random()*26+97));

        createSign(timestamp,nonce);

        HttpUrl.Builder url = HttpUrl.parse("https://www.cocheer.net/cloud/auto/search/audio").newBuilder();
        url.addQueryParameter("timestamp", timestamp)
                .addQueryParameter("nonce", nonce)
                .addQueryParameter("key", key);
        LogUtil.e(TAG, "url:" + url.toString());
        LogUtil.e(TAG, "sign:" + sign);
        LogUtil.e(TAG, "app_key:" + app_key);
        final Request request = new Request.Builder()
//                .url("www.cocheer.net/cloud/auto/categories?timestamp="+timestamp+"&nonce=d")//获取专辑分类
                .url(url.build())
                .addHeader("sign", sign)
                .addHeader("app_key", app_key)
                .get()//默认就是GET请求，可以不写
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e(TAG, "onFailure:");
                callBack.searchFailed("onFailure");

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                result = response.body().string();
                LogUtil.e(TAG, "onResponse:" + result);
                callBack.searchSuccess(result);
            }
        });
    }

    /**
     * 鉴权机制
     * @return
     */
    private String createSign(String timestamp,String nonce){
        //2、	将排序后的参数键值对用&拼接，即拼接成app_key =val1 & timestamp=val2& nonce =val3
        String str1 = "app_key="+app_key+"&timestamp="+timestamp+"&nonce="+nonce;
        LogUtil.e(TAG,"未加密的第2步："+str1);
        //3、将2步骤得到的字符串进行Base64编码
        String str1Base64 = Base64.encodeToString(str1.getBytes(),Base64.DEFAULT);
        LogUtil.e(TAG,str1Base64+"+++");
        //4、将app_key + app_secret作为哈希key对3步骤得到的Base64编码后的字符串进行HMAC-SHA1哈希运算得到字节数组
        byte [] byteSHA1 = HMAC_SHA1.genHMACtoBytes(str1Base64.trim(),app_key + app_secret);
        LogUtil.e(TAG,"第4步：密匙："+app_key+app_secret);
        //5、对4步骤得到的字节数组进行sha1加密
        String str5 = new SHA1().getDigestOfString(byteSHA1);
        LogUtil.e(TAG,"字节数组进行sha1加密："+str5+"++");
        //6、对5步骤得到的字符串进行md5运算得到32位字符串，即为sign
        sign = MD5Utils.md5(CaseConverter.convertString(str5,false));
        LogUtil.e(TAG,"最后一步,计算MD5："+sign);
        return sign;
    }

}
