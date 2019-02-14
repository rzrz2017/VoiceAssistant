package com.szhklt.www.voiceassistant.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class HttpUtil {
    public void get(String u, HttpCallBack callback) {
        String resultStr = null;
        int responseCode = 0;
        HttpURLConnection urlConnection = null;
        String errorMsg = null;
        try {
            URL url = new URL(u);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);
            urlConnection.setRequestMethod("GET");
            responseCode = urlConnection.getResponseCode();
            if (responseCode == 200) {
                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    resultStr = readStream(in);
                } catch (Exception e) {
                    errorMsg = e.getMessage();
                }
            }
        } catch (Exception e) {
            errorMsg = e.toString();
        } finally {
            if (null != urlConnection)
                urlConnection.disconnect();
        }
        if (null != resultStr && null != callback) {
            callback.onSuccess(resultStr);
        } else if (null != callback) {
            callback.onFailed(responseCode, errorMsg);
        }
    }

  /**
   * 封装的请求信息
   * @param params
   * @param encode
   * @return
   */
    public StringBuffer getRequestData(Map<String, Object> params, String encode) {
        StringBuffer stringBuffer = new StringBuffer();        //存储封装好的请求体信息
        try {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                stringBuffer.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue() + "", encode)) .append("&");
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);    //删除最后的一个"&"
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer;
    }

	public void post(String u, Map<String, Object> params, HttpCallBack callback) {
        byte[] data = getRequestData(params, "utf-8").toString().getBytes();
        HttpURLConnection conn = null;
        String resultStr = null;
        String errorMsg = null;
        int responseCode = 0;
        StringBuffer stringBuffer = new StringBuffer();//存储封装好的请求体信息
            for (Map.Entry<String, Object> entry : params.entrySet()) {
            	LogUtil.d("**********", entry.getKey()+"="+entry.getValue());
                stringBuffer.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue() + "")).append("&");
            }
        try {
            URL mURL = new URL(u+stringBuffer);
            LogUtil.d("yrok-->URL=",u+stringBuffer);
            conn = (HttpURLConnection) mURL.openConnection();
            conn.setRequestMethod("POST");// 设置请求方法为post
            conn.setReadTimeout(5000);// 设置读取超时为2秒
            conn.setConnectTimeout(5000);// 设置连接网络超时为2秒
            conn.setDoOutput(true);// 设置此方法,允许向服务器输出内容
            conn.setUseCaches(false);
            //设置请求体的类型是文本类型
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //设置请求体的长度
            conn.setRequestProperty("Content-Length", String.valueOf(data.length));
            //获得输出流，向服务器写入数据
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(data);
            outputStream.flush();
            outputStream.close();
            responseCode = conn.getResponseCode();// 调用此方法就不必再使用conn.connect()方法
            if (responseCode == 200) {
                InputStream is = conn.getInputStream();
                resultStr = readStream(is);
            } else {
                errorMsg = readStream(conn.getErrorStream());
            }

        } catch (Exception e) {
            errorMsg = e.toString();
        } finally {
            if (conn != null) {
                conn.disconnect();// 关闭连接
            }
        }
        if (null != resultStr && null != callback) {
            callback.onSuccess(resultStr);
        } else if (null != callback) {
            callback.onFailed(responseCode, errorMsg);
        }
    }

    private String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while (i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }
}
