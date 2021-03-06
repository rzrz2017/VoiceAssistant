package com.szhklt.VoiceAssistant.multithreadeddownloader;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import android.os.Handler;
import android.os.Message;

import com.szhklt.VoiceAssistant.util.LogUtil;

import okhttp3.Call;
import okhttp3.Response;

public class DownloadTask extends Handler{
	    private final int THREAD_COUNT = 1;//下载线程数量  
	    private FilePoint mPoint;  
	    private long mFileLength;//文件大小  
	  
	    private boolean isDownloading = false;//是否正在下载  
	    private int childCanleCount;//子线程取消数�?  
	    private int childPauseCount;//子线程暂停数�?  
	    private int childFinishCount;//子线程完成下载数�?  
	    private HttpUtil mHttpUtil;//http网络通信工具  
	    private long[] mProgress;//各个子线程下载进度集�?  
	    private File[] mCacheFiles;//各个子线程下载缓存数据文�?  
	    private File mTmpFile;//临时占位文件  
	    private boolean pause;//是否暂停  
	    private boolean cancel;//是否取消下载  
	  
	    private final int MSG_PROGRESS = 1;//进度  
	    private final int MSG_FINISH = 2;//完成下载  
	    private final int MSG_PAUSE = 3;//暂停  
	    private final int MSG_CANCEL = 4;//暂停  
	    private DownloadListner mListner;//下载回调监听  
	    
	    DownloadTask(FilePoint point, DownloadListner l) {  
	        this.mPoint = point;  
	        this.mListner = l;  
	        this.mProgress = new long[THREAD_COUNT];  
	        this.mCacheFiles = new File[THREAD_COUNT];  
	        this.mHttpUtil = HttpUtil.getInstance();  
	    }  
	    
	    /** 
	     * �?始下�? 
	     */  
	    public synchronized void start() { 
	    	LogUtil.e("ranzhen","start()");
	        try {  
	            if (isDownloading) return;  
	            isDownloading = true;  
	            mHttpUtil.getContentLength(mPoint.getUrl(), new okhttp3.Callback() {  
	                @Override  
	                public void onResponse(Call call, Response response) throws IOException {
	                	LogUtil.e("ranzhen","onResponse() callback");
	                    if (response.code() != 200) {  
	                        close(response.body());  
	                        resetStutus();  
	                        return;  
	                    }  
	                    // 获取资源大小  
	                    mFileLength = response.body().contentLength();
	                    LogUtil.e("ranzhen","mFileLength == "+String.valueOf(mFileLength));
	        	    	
	                    close(response.body());  
	                    // 在本地创建一个与资源同样大小的文件来占位  
	                    mTmpFile = new File(mPoint.getFilePath(), mPoint.getFileName() + ".tmp");  
	                    if (!mTmpFile.getParentFile().exists()) mTmpFile.getParentFile().mkdirs();  
	                    RandomAccessFile tmpAccessFile = new RandomAccessFile(mTmpFile, "rw");  
	                    tmpAccessFile.setLength(mFileLength);  
	                    /*将下载任务分配给每个线程*/  
	                    long blockSize = mFileLength / THREAD_COUNT;// 计算每个线程理论上下载的数量.  
	  
	                    /*为每个线程配置并分配任务*/  
	                    for (int threadId = 0; threadId < THREAD_COUNT; threadId++) {  
	                        long startIndex = threadId * blockSize; // 线程�?始下载的位置  
	                        long endIndex = (threadId + 1) * blockSize - 1; // 线程结束下载的位�?  
	                        if (threadId == (THREAD_COUNT - 1)) { // 如果是最后一个线�?,将剩下的文件全部交给这个线程完成  
	                            endIndex = mFileLength - 1;  
	                        }  
	                        download(startIndex, endIndex, threadId);// �?启线程下�?  
	                    }  
	                }  
	  
	                @Override  
	                public void onFailure(Call call, IOException e) {  
	                    resetStutus();  
	                }  
	            });  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	            resetStutus();  
	        }  
	    }  
	    
	    /** 
	     * 下载 
	     * @param startIndex 下载起始位置 
	     * @param endIndex  下载结束位置 
	     * @param threadId 线程id 
	     * @throws IOException 
	     */  
	    public void download(final long startIndex, final long endIndex, final int threadId) throws IOException {  
	    	LogUtil.e("ranzhen","download()");
	    	long newStartIndex = startIndex;  
	        // 分段请求网络连接,分段将文件保存到本地.  
	        // 加载下载位置缓存数据文件  
	        final File cacheFile = new File(mPoint.getFilePath(), "thread" + threadId + "_" + mPoint.getFileName() + ".cache");  
	        mCacheFiles[threadId] = cacheFile;  
	        final RandomAccessFile cacheAccessFile = new RandomAccessFile(cacheFile, "rwd");  
	        if (cacheFile.exists()) {// 如果文件存在  
	            String startIndexStr = cacheAccessFile.readLine();  
	            try {  
	                newStartIndex = Integer.parseInt(startIndexStr);//重新设置下载起点  
	            } catch (NumberFormatException e) {  
	                e.printStackTrace();  
	            }  
	        }  
	        final long finalStartIndex = newStartIndex;  
	        mHttpUtil.downloadFileByRange(mPoint.getUrl(), finalStartIndex, endIndex, new okhttp3.Callback() {  
	            @Override  
	            public void onResponse(Call call, Response response) throws IOException {  
	                if (response.code() != 206) {// 206：请求部分资源成功码，表示服务器支持断点续传  
	                    resetStutus();  
	                    return;  
	                }  
	                InputStream is = response.body().byteStream();// 获取�?  
	                RandomAccessFile tmpAccessFile = new RandomAccessFile(mTmpFile, "rw");// 获取前面已创建的文件.  
	                tmpAccessFile.seek(finalStartIndex);// 文件写入的开始位�?.  
	                  /*  将网络流中的文件写入本地*/  
	                byte[] buffer = new byte[1024 << 2];  
	                int length = -1;  
	                int total = 0;// 记录本次下载文件的大�?  
	                long progress = 0;  
	                while ((length = is.read(buffer)) > 0) {//读取�?  
	                    if (cancel) {  
	                        close(cacheAccessFile, is, response.body());//关闭资源  
	                        cleanFile(cacheFile);//删除对应缓存文件  
	                        sendMessage(MSG_CANCEL);  
	                        return;  
	                    }  
	                    if (pause) {  
	                        //关闭资源  
	                        close(cacheAccessFile, is, response.body());  
	                        //发�?�暂停消�?  
	                        sendMessage(MSG_PAUSE);  
	                        return;  
	                    }  
	                    tmpAccessFile.write(buffer, 0, length);  
	                    total += length;  
	                    progress = finalStartIndex + total;  
	  
	                    //将该线程�?新完成下载的位置记录并保存到缓存数据文件�?  
	                    cacheAccessFile.seek(0);  
	                    cacheAccessFile.write((progress + "").getBytes("UTF-8"));  
	                    //发�?�进度消�?  
	                    mProgress[threadId] = progress - startIndex;  
	                    sendMessage(MSG_PROGRESS);  
	                }  
	                //关闭资源  
	                close(cacheAccessFile, is, response.body());  
	                // 删除临时文件  
	                cleanFile(cacheFile);  
	                //发�?�完成消�?  
	                sendMessage(MSG_FINISH);  
	            }  
	  
	            @Override  
	            public void onFailure(Call call, IOException e) {  
	                isDownloading = false;  
	            }  
	        });  
	    }  
	    
	    /** 
	     * 轮回消息回调 
	     * 
	     * @param msg 
	     */  
	    @Override  
	    public void handleMessage(Message msg) {  
	        super.handleMessage(msg);  
	        if (null == mListner) {  
	            return;  
	        }  
	        switch (msg.what) {  
	            case MSG_PROGRESS://进度  
	                long progress = 0;  
	                for (int i = 0, length = mProgress.length; i < length; i++) {  
	                    progress += mProgress[i];  
	                }  
	                mListner.onProgress(progress * 1.0f / mFileLength);  
	                break;  
	            case MSG_PAUSE://暂停  
	                childPauseCount++;  
	                if (childPauseCount % THREAD_COUNT != 0) return;//等待�?有的线程完成暂停，真正意义的暂停，以下同�?  
	                resetStutus();  
	                mListner.onPause();  
	                break;  
	            case MSG_FINISH://完成  
	                childFinishCount++;  
	                if (childFinishCount % THREAD_COUNT != 0) return;  
	                mTmpFile.renameTo(new File(mPoint.getFilePath(), mPoint.getFileName()));//下载完毕后，重命名目标文件名  
	                resetStutus();  
	                mListner.onFinished();  
	                break;  
	            case MSG_CANCEL://取消  
	                childCanleCount++;  
	                if (childCanleCount % THREAD_COUNT != 0) return;  
	                resetStutus();  
	                mProgress = new long[THREAD_COUNT];  
	                mListner.onCancel();  
	                break;  
	        }  
	    }  
	    
	    /** 
	     * 发�?�消息到轮回�? 
	     * 
	     * @param what 
	     */  
	    private void sendMessage(int what) {  
	        Message message = new Message();  
	        message.what = what;  
	        sendMessage(message);  
	    } 
	    
	    /** 
	     * 关闭资源 
	     * 
	     * @param closeables 
	     */  
	    private void close(Closeable... closeables) {  
	        int length = closeables.length;  
	        try {  
	            for (int i = 0; i < length; i++) {  
	                Closeable closeable = closeables[i];  
	                if (null != closeable)  
	                    closeables[i].close();  
	            }  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        } finally {  
	            for (int i = 0; i < length; i++) {  
	                closeables[i] = null;  
	            }  
	        }  
	    }  
	    
	    /** 
	     * 暂停 
	     */  
	    public void pause() {  
	        pause = true;  
	    }  
	  
	    /** 
	     * 取消 
	     */  
	    public void cancel() {  
	        cancel = true;  
	        cleanFile(mTmpFile);  
	        if (!isDownloading) {//针对非下载状态的取消，如暂停  
	            if (null != mListner) {  
	                cleanFile(mCacheFiles);  
	                resetStutus();  
	                mListner.onCancel();  
	            }  
	        }  
	    }  
	    
	    /** 
	     * 重置下载状�?? 
	     */  
	    private void resetStutus() {  
	        pause = false;  
	        cancel = false;  
	        isDownloading = false;  
	    }  
	  
	    /** 
	     * 删除临时文件 
	     */  
	    private void cleanFile(File... files) {  
	        for (int i = 0, length = files.length; i < length; i++) {  
	            if (null != files[i])  
	                files[i].delete();  
	        }  
	    }  
	    
	    /** 
	     * 获取下载状�?? 
	     * @return boolean 
	     */  
	    public boolean isDownloading() {  
	        return isDownloading;  
	    }  
}
