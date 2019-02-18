package com.szhklt.VoiceAssistant.multithreadeddownloader;

import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;

public class PackageUtils {


    public  String getAppProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> infos = manager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            if (info.pid == pid)
                return info.processName;
        }
        return "";
    }

    public Drawable getAppIcon(Context context,String packname){    
      try {    
          PackageManager pm = context.getPackageManager();  
             ApplicationInfo info = pm.getApplicationInfo(packname, 0);     
             return info.loadIcon(pm);    
        } catch (NameNotFoundException e) {    
            e.printStackTrace();    
        }
    return null;    
    }    

    public String getAppVersion(Context context,String packname){    
      PackageManager pm = context.getPackageManager();  
          try {    
              PackageInfo packinfo = pm.getPackageInfo(packname, 0);    
              return packinfo.versionName;    
            } catch (NameNotFoundException e) {    
                e.printStackTrace();    

            }
        return packname;    
    }    


    public String getAppName(Context context,String packname){   
      PackageManager pm = context.getPackageManager();  
          try {    
                 ApplicationInfo info = pm.getApplicationInfo(packname, 0);     
                 return info.loadLabel(pm).toString();    
            } catch (NameNotFoundException e) {    
                // TODO Auto-generated catch block    
                e.printStackTrace();    

            }
        return packname;    
    }    
    
    public String[] getAllPermissions(Context context,String packname){    
          try {    
              PackageManager pm = context.getPackageManager(); 
              PackageInfo packinfo =    pm.getPackageInfo(packname, PackageManager.GET_PERMISSIONS);    
              return packinfo.requestedPermissions;    
            } catch (NameNotFoundException e) {    
                e.printStackTrace();    

            }
        return null;    
    }    


    public String getAppSignature(Context context,String packname){    
          try {    
              PackageManager pm = context.getPackageManager(); 
              PackageInfo packinfo = pm.getPackageInfo(packname, PackageManager.GET_SIGNATURES);    
              return packinfo.signatures[0].toCharsString();    

            } catch (NameNotFoundException e) {    
                e.printStackTrace();    

            }
        return packname;    
    }    
    
    @SuppressWarnings("unused")
	private String getCurrentActivityName(Context context){          
	        ActivityManager activityManager=(ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);  
	        String runningActivity=activityManager.getRunningTasks(1).get(0).topActivity.getClassName();  
	        return runningActivity;                 
	  }
	}
