package com.szhklt.VoiceAssistant.multithreadeddownloader;

import java.io.File;
import java.lang.reflect.Method;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.szhklt.VoiceAssistant.MainApplication;
import com.szhklt.VoiceAssistant.util.LogUtil;

public class DownLoadUtils {
    private final String XmlUrl = "http://119.23.221.31/apkupdate/background_music/xml/VersionInfo.xml";
    private Context mContext;
    public DownloadManager mDownloadManager;
    private long kwdownloadID;//酷我downloadID
    
    public DownLoadUtils(Context context) {
        this.mContext = context.getApplicationContext();
        mDownloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    public String getDownloadPath(long downloadId) {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor c = mDownloadManager.query(query);
        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    return c.getString(c.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI));
                }
            } finally {
                c.close();
            }
        }
        return null;
    }

    public Uri getDownloadUri(long downloadId) {
        return mDownloadManager.getUriForDownloadedFile(downloadId);
    }

    public void DownLoadVerXML() {
    	LogUtil.e("ranzhen","DownLoadVerXML()");
    	FilesUtil.getInstance().deleteDirWihtFile(new File(Environment.getExternalStorageDirectory().getPath()
    			+DownloadCompleteReceiver.XmlPath));
    	LogUtil.e("ranzhen", "o.o");
    	MainApplication.isCheckUpdata = true;
        DownloadManager.Request downVerXML = new DownloadManager.Request(Uri.parse(XmlUrl));  
        downVerXML.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI); 
        downVerXML.setNotificationVisibility(Request.VISIBILITY_HIDDEN);  
        downVerXML.setVisibleInDownloadsUi(false);  
        downVerXML.setDestinationInExternalFilesDir(mContext,Environment.DIRECTORY_DOWNLOADS, "VersionInfo.xml");
        mDownloadManager.enqueue(downVerXML); 
	}
    
    public void Domj4_VersionXML_Prase() {
    	LogUtil.e("ranzhen","Domj4_VersionXML_Prase()");
        SAXReader reader = new SAXReader();    
        try {
			Document document = reader.read(new File("/sdcard/Android/data/com.szhklt.VoiceAssistant/files/Download","VersionInfo.xml"));
	        Element rootnode = document.getRootElement();  
	        
	        LogUtil.e("updata","解析对应的元素名:"+MainApplication.ADCTPYE+"version"+MainApplication.JETTPYE);
	        Element element_version = rootnode.element(MainApplication.ADCTPYE+"version"+MainApplication.JETTPYE);//重要 
	        String string_version = element_version.getTextTrim();
	        LogUtil.e("ranzhen","appversion = "+string_version);
			int version = Integer.valueOf(string_version);
    		LogUtil.e("updata","当前app版本号为:"+getAPPVersionCode(mContext));
    		LogUtil.e("updata","服务器上xml里面版本号为:"+version);
    		
			if(version > getAPPVersionCode(mContext)){
				MainApplication.theLastVer.setAPKVersion(version);
			}else{
				MainApplication.UpdataMark = false;
			}
			
			//解析酷我版本号
			Element element_kw_version = rootnode.element("hkkwversion");
			String string_kw_version = element_kw_version.getTextTrim();
			LogUtil.e("ranzhen","string_kw_version = "+string_kw_version);
			int kw_version = Integer.valueOf(string_kw_version);//网上的kw号//重要
			//获取当前酷我版本号
//			MainApplication.netKWVer = kw_version;//存储网上的酷我版本号
					
			//解析描述
			Element[] element_description = new Element[10] ;
			String[] description = new String[10];			
			for(int i = 0;i < 10;i++){
		        element_description[i] = rootnode.element("description"+String.valueOf(i));
		        if(element_description[i] == null){
		        	break;
		        }
		        description[i] = element_description[i].getTextTrim();
		        LogUtil.e("ranzhen","description = "+description[i]);
			}
			MainApplication.theLastVer.setAPKdescription(description);
			
			//解析apkurl
			//String firmware = getProperty("ro.product.firmware","unknown");
			Element element_apkurl = null;
			element_apkurl = rootnode.element(MainApplication.ADCTPYE+"apkurl"+MainApplication.JETTPYE);
			LogUtil.e("updata","元素名字:"+MainApplication.ADCTPYE+"apkurl"+MainApplication.JETTPYE);
			LogUtil.e("updata","url标签为:"+element_apkurl);
	        String apkurl = element_apkurl.getTextTrim();
	        LogUtil.e("updata","url为:"+apkurl);
	        LogUtil.e("ranzhen","apkurl:"+apkurl);
			if(!apkurl.equals(MainApplication.theLastVer.getAPKUrl())){
				LogUtil.e("ranzhen","new APKUrl not the same as old!!");
				MainApplication.theLastVer.setAPKUrl(apkurl);
			}
			//解析kwurl
			Element element_kw_url = rootnode.element("kwapkurl");
			String kw_url = element_kw_url.getTextTrim();
			LogUtil.e("ranzhen","kwurl:"+kw_url);
			MainApplication.kwUrl = kw_url;
			MainApplication.isCheckUpdata = false;
        } catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}

	public static int getAPPVersionCode(Context ctx) {  
        int currentVersionCode = 0;  
        PackageManager manager = ctx.getPackageManager();  
        try {  
            PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);  
            String appVersionName = info.versionName; 
            currentVersionCode = info.versionCode; 
        } catch (NameNotFoundException e) {  
            e.printStackTrace();  
        }  
        return currentVersionCode;  
    } 
    
	public void startUpdateActivity(){
		LogUtil.e("ranzhen","startUpdateActivity()");
		Intent UpdateActintent = new Intent(mContext, UpdateActivity.class);
		UpdateActintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		mContext.startActivity(UpdateActintent);
	}
	
	public static String getProperty(String key, String defaultValue) {    
	    String value = defaultValue;  
	    try {  
	        Class<?> c = Class.forName("android.os.SystemProperties");  
	        Method get = c.getMethod("get", String.class, String.class);
	        value = (String)(get.invoke(c, key, "unknown" ));
	    } catch (Exception e) {  
	        e.printStackTrace();
	    }finally {  
	        return value;  
	    }
	}  

}
