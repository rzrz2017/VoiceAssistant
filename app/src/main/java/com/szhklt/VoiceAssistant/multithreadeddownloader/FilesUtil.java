package com.szhklt.VoiceAssistant.multithreadeddownloader;

import com.szhklt.VoiceAssistant.util.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FilesUtil {
	
	//单例
	private static FilesUtil mdeletefiles = new FilesUtil();
	private FilesUtil(){};
	public static FilesUtil getInstance(){
		return mdeletefiles;
	}
	
	public static void deletes(File fi) {  
        if (fi.exists()) {  
            if (fi.isFile())  
                    //fi.delete();  
                    System.out.println("删除：" + fi.getName());  
            else {  
                if (fi.isDirectory()) {  
                    File[] fis = fi.listFiles();  
                    for (int i = 0; i < fis.length; i++)  
                        deletes(fis[i]);  
                        //fi.delete();  
                        System.out.println("删除：" + fi.getName());  
                }  
            }  
        }else  
            System.out.println("文件不存在："+fi.getAbsolutePath());  
    }  
  
    public static void deleteSigns(File mainDir, String file) {   
        String subStr = "";  
        List<File> fls = new ArrayList<File>();  
        int pre = file.indexOf("/");          
        if (pre > -1) {  
            String dirName = file.substring(0, pre);  
            subStr = file.substring(pre + 1);  
            fls = find(mainDir, dirName);  
            for (File f : fls) {  
                if (f.isDirectory()) {  
                    deleteSigns(f, subStr);  
                }  
            }  
        } else {  
            fls = find(mainDir, file);  
            for (File f : fls) {  
                deletes(f);  
            }  
        }  
    }  
  
    public static List<File> find(File mainDir, String str) {  
        System.out.println("当前目录" + mainDir.getAbsolutePath() + " " + str);  
        File[] files = mainDir.listFiles();  
        List<File> fls = new ArrayList<File>();  
        int length = 0;  
        int num = 0;  
        int start = -1;  
        int end = -1;  
        length = str.length();  
        start = str.indexOf("*");  
        end = str.lastIndexOf("*");  
        String[] ss = str.split("[*]");  
        for (int i = 0; i < length; i++) {  
            if ('*' == str.charAt(i)) {  
                num++;  
            }  
        }  
        //System.out.println("num---->" + num);  
        switch (num) {  
        case 0:  
            File fff = new File(mainDir, str);  
            fls.add(fff);  
            //System.out.println("添加:"+fff.getName());  
            break;  
  
        case 1: // 只有一个*号的情况  
            if (0 == start) { // 在开始的位置  
                for (File f : files) {  
                    if (f.getName().endsWith(ss[1])) {  
                        fls.add(f);  
                        //System.out.println("添加:"+f.getName());  
                    }  
                }  
            } else if (length - 1 == start) { // 在结束的位置  
                for (File f : files) {  
                    if (f.getName().startsWith(ss[0])) {  
                        fls.add(f);  
                        // System.out.println("添加:"+f.getName()+"---"+ss[0]);  
                    }  
                }  
            } else { // 在中间  
                for (File f : files) {  
                    if (f.getName().startsWith(ss[0])  
                            && f.getName().endsWith(ss[1])) {  
                        fls.add(f);  
                        //System.out.println("添加:"+f.getName());  
                    }  
                }  
            }  
            break;  
  
        default: // 有两个或多个*号的情况  
            if (0 == start && length - 1 == end) { // 在开始的位置和结束的位置都有*号  
                for (File f : files) {  
                    boolean add = false;  
                    String name = f.getName();  
                    for (String s : ss) {  
                        int i = name.indexOf(s);  
                        if (i > -1) {  
                            name = name.substring(i + s.length());  
                            // System.out.println("当前:"+name);  
                            add = true;  
                        } else {  
                            add = false;  
                            break;  
                        }  
                    }  
                    if (add) {  
                        fls.add(f);  
                        // System.out.println("添加:"+f.getName());  
                    }  
                }  
            } else if (0 == start && length - 1 != end) { // 开始的位置有*号且结束的位置没有*号  
                for (File f : files) {  
                    boolean add = false;  
                    String name = f.getName();  
                    if (name.endsWith(ss[ss.length - 1])) {  
                        for (String s : ss) {  
                            int i = name.indexOf(s);  
                            if (i > -1) {  
                                name = name.substring(i + s.length());  
                                add = true;  
                            } else {  
                                add = false;  
                                break;  
                            }  
                        }  
                        if (add) {  
                            fls.add(f);  
                            // System.out.println("添加:"+f.getName());  
                        }  
                    }  
                }  
            } else if (0 != start && length - 1 == end) { // 开始的位置没有*号且结束的位置有*号  
                for (File f : files) {  
                    boolean add = false;  
                    String name = f.getName();  
                    if (name.startsWith(ss[0])) {  
                        for (String s : ss) {  
                            int i = name.indexOf(s);  
                            if (i > -1) {  
                                name = name.substring(i + s.length());  
                                add = true;  
                            } else {  
                                add = false;  
                                break;  
                            }  
                        }  
                        if (add) {  
                            fls.add(f);  
                            // System.out.println("添加:"+f.getName());  
                        }  
                    }  
                }  
            } else { // *号在中间 的情况（开始和结束都没有*号）  
                for (File f : files) {  
                    boolean add = false;  
                    String name = f.getName();  
                    if (name.startsWith(ss[0])  
                            && name.endsWith(ss[ss.length - 1]))  
                        for (String s : ss) {  
                            int i = name.indexOf(s);  
                            if (i > -1) {  
                                name = name.substring(i + s.length());  
                                add = true;  
                            } else {  
                                add = false;  
                                break;  
                            }  
                        }  
                    if (add) {  
                        fls.add(f);  
                        // System.out.println("添加:"+f.getName());  
                    }  
                }  
            }  
        }  
        return fls;  
    }  
    
    //删除文件夹内全部文件
    public void deleteDirWihtFile(File dir) {//删除全部文件
    	LogUtil.e("ranzhen","deleteDirWihtFile()");
        if (dir == null || !dir.exists() || !dir.isDirectory()){
        	LogUtil.e("ranzhen","dir == null || !dir.exists() || !dir.isDirectory()");
            return;
        }
        for (File file : dir.listFiles()) {
        	LogUtil.e("ranzhen",file.toString());
            if (file.isFile())
            	if(file.getName().contains("VersionInfo"))//add by ranzhen
                file.delete(); 
//            else if (file.isDirectory())
//                deleteDirWihtFile(file); 
        }
        //dir.delete();
    }
    
    //更久文件名删除文件
	public boolean deleteFile(String fileName) {
	    File file = new File(fileName);
	    // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
	    if (file.exists() && file.isFile()) {
	        if (file.delete()) {
	        	LogUtil.e("ranzhen","delete "+fileName+" succee");
	            return true;
	        } else {
	        	LogUtil.e("ranzhen","delete "+fileName+" faild");
	            return false;
	        }
	    } else {
	    	LogUtil.e("ranzhen",fileName+"isn't exists!!");
	        return false;
	    }
	}
    
    //判断文件是否存在是否
    public boolean judeFileExists(String filePath) {
  	    File file = new File(filePath);
        if (file.exists()) {
        	LogUtil.e("ranzhen",""+"file exists");
            return true;
        }else{
      	    return false; 
        }
    }
    
    public boolean judeFileExists(File file) {
        if (file.exists()) {
        	LogUtil.e("ranzhen",""+"file exists");
            return true;
        }else{
        	return false; 
        }
    }
}
