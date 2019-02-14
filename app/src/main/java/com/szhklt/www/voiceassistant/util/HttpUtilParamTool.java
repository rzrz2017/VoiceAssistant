package com.szhklt.www.voiceassistant.util;

import java.util.HashMap;

public class HttpUtilParamTool {
    final HashMap<String, Object> params = new HashMap<>();

    public static HttpUtilParamTool createMusic(int pagesize,int page,String keyword, int iscorrent) {
        final HttpUtilParamTool httpUtilParamTool = new HttpUtilParamTool();
      httpUtilParamTool.add("pagesize", pagesize).add("page", page).add("keyword",keyword).add("iscorrent", iscorrent);
        return httpUtilParamTool;
    }
    public static HttpUtilParamTool createSong(String cmd, String hash) {
        final HttpUtilParamTool httpUtilParamTool = new HttpUtilParamTool();
        httpUtilParamTool.add("cmd",cmd).add("hash",hash);
        return httpUtilParamTool;
    }
    public HttpUtilParamTool add(String key, Object val) {
        this.params.put(key, val);
        return this;
    }
    public HashMap<String, Object> getParams() {
        return params;
    }
}
