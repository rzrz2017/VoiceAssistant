package com.szhklt.VoiceAssistant.multithreadeddownloader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import android.content.Context;
import android.os.Environment;

import com.szhklt.VoiceAssistant.util.LogUtil;


public class VersionUtil {
	private static VersionUtil instance = null;
	
	private VersionUtil(){}
	
	public static VersionUtil getInstance(){
		synchronized (VersionUtil.class) {
			if(instance == null){
				instance = new VersionUtil();
			}
		}
		return instance;
	}
	
	/**************************************************************************************************************/
	/*************************************************share相关*************************************************/
//    //储存下载完成的（当前）酷我版本号
//    public void saveCurKWversion(int kwver,Context mContext){
//    	LogUtil.e("kw","saveCurKWversion() kwver = "+String.valueOf(kwver));
//		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences
//				(mContext.getApplicationContext()).edit();
//		editor.putInt("kwver", kwver);
//		editor.commit();
//    }
//    
//    //获取当前酷我版本号
//    public int GetCurKWversion(Context mContext){
//		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
//		int curkwver = pref.getInt("kwver",0);
//		LogUtil.e("kw","GetCurKWversion() curkwver = "+String.valueOf(curkwver));
//		return curkwver;//本地的kw号
//    }
//    
//    //储存酷我的downloadID
//    public void saveKWDownloadID(long kwdownid,Context mContext){
//    	LogUtil.e("kw","saveKWDownloadID() kwdownid = "+String.valueOf(kwdownid));
//		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences
//				(mContext.getApplicationContext()).edit();
//		editor.putLong("kwdownloadid",kwdownid);
//		editor.commit();
//    }
//    
//    //获取酷我的downloadID
//    public long getKWDownloadID(Context mContext){
//		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
//		long kwdownId = pref.getLong("kwdownloadid",0x0000);
//		LogUtil.e("kw","getKWDownloadID() kwdownId = "+String.valueOf(kwdownId));
//		return kwdownId;
//    }
//    
//    public void clearKWDownloadID(Context mContext){
//		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences
//				(mContext.getApplicationContext()).edit();
//		editor.putLong("kwdownloadid",-1);
//		editor.commit();
//    }
//    
//    public void clearCurKWversion(Context mContext){
//		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences
//				(mContext.getApplicationContext()).edit();
//		editor.putInt("kwver",-1);
//		editor.commit();
//    }
    
    /*******************************************************************************************************/
    /**************************************************文件相关*********************************************/
    public void saveKWVer(Context context,int tmp){
    	LogUtil.e("kw","saveKWVer(Context context,int tmp)");
    	@SuppressWarnings("unused")
		FileOutputStream out = null;
    	BufferedWriter writer = null;
    	try {
        	if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
        		File sdcarddir = Environment.getExternalStorageDirectory();
        		File saveFile = new File(sdcarddir,"kwver.txt");
        		
//        		if(saveFile.exists()){
//        			saveFile.delete();
//        		}
        		
        		saveFile.createNewFile();
        		String path = saveFile.getAbsolutePath().toString();
        		LogUtil.e("kw",path);
        		
        		FileOutputStream outStream = new FileOutputStream(saveFile);
				//out = context.openFileOutput("kwver",Context.MODE_PRIVATE);
				writer = new BufferedWriter(new OutputStreamWriter(outStream));
				writer.write(String.valueOf(tmp));
        	}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			try{
				if(writer != null){
					writer.close();
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}
    }
    
    public void saveKWId(Context context,long kwdownloadID2){
    	LogUtil.e("kw","saveKWId(Context context,long kwdownloadID2)");
    	@SuppressWarnings("unused")
		FileOutputStream out = null;
    	BufferedWriter writer = null;
    	try {
    		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
    			File sdcarddir = Environment.getExternalStorageDirectory();
        		File saveFile = new File(sdcarddir,"kwid.txt");
        		
//        		if(saveFile.exists()){
//        			saveFile.delete();
//        		}
        	
        		saveFile.createNewFile();
        		String path = saveFile.getAbsolutePath().toString();
        		LogUtil.e("kw",path);
        		
        		FileOutputStream outStream = new FileOutputStream(saveFile);
    			//out = context.openFileOutput("kwid",Context.MODE_PRIVATE);
    			writer = new BufferedWriter(new OutputStreamWriter(outStream));
    			writer.write(String.valueOf(kwdownloadID2));
    		}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			try{
				if(writer != null){
					writer.close();
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}
    }
    
    public long getKwId(Context context){
    	LogUtil.e("kw","getKwId(Context context)");
    	FileInputStream in = null;
    	BufferedReader reader = null;
    	StringBuilder content = new StringBuilder();
    	try{
    		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
    			File sdcarddir = Environment.getExternalStorageDirectory();	
    			File saveFile = new File(sdcarddir,"kwid.txt");
    			in = new FileInputStream(saveFile);
    		}
    		//in = context.openFileInput("kwid");
    		reader = new BufferedReader(new InputStreamReader(in));
    		String line = "";
    		while((line = reader.readLine()) != null){
    			content.append(line);
    		}
    		LogUtil.e("kw","content = "+content);
    	}catch(IOException e){
    		e.printStackTrace();
    	}finally{
    		if(reader != null){
    			try{
    				reader.close();
    			}catch(IOException e){
    				e.printStackTrace();
    			}
    		}
    	}
    	return Long.valueOf(content.toString() == ""?"0":content.toString());
    }
    
    public int getKwVer(Context context){
    	LogUtil.e("kw","getKwVer(Context context)");
    	FileInputStream in = null;
    	BufferedReader reader = null;
    	StringBuilder content = new StringBuilder();
    	try{
    		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
    			File sdcarddir = Environment.getExternalStorageDirectory();	
    			File saveFile = new File(sdcarddir,"kwver.txt");
    			in = new FileInputStream(saveFile);
    		}
    		//in = context.openFileInput("kwver");
    		reader = new BufferedReader(new InputStreamReader(in));
    		String line = "";
    		while((line = reader.readLine()) != null){
    			content.append(line);
    		}
    		LogUtil.e("kw","content = "+content);
    	}catch(IOException e){
    		e.printStackTrace();
    	}finally{
    		if(reader != null){
    			try{
    				reader.close();
    			}catch(IOException e){
    				e.printStackTrace();
    			}
    		}
    	}
    	return Integer.valueOf(content.toString() == ""?"0":content.toString());
    }
    
    public static void clearInfoForFile(String fileName) {
        File file =new File(fileName);
        try {
            if(!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter =new FileWriter(file);
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
