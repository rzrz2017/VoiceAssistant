package com.szhklt.www.VoiceAssistant.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.nio.channels.FileChannel;

import android.content.Context;
import android.net.LocalSocket;

/**
 * 文件处理类，所有文件的操作均可采用此工具类
 * @author wuhao
 *
 */
public class FileManager {
    private static final String TAG = "--FileUtil--:";
    public static final String separator = "/";

    /**
     * 判断文件是否存在
     * @param strFile
     * @return
     */
    public static boolean fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    
    /**
     * 根据路径删除文件夹和文件夹里面的文件
     * @param pPath
     */
    	public static void deleteDir(final String pPath) {
    		File dir = new File(pPath);
    		deleteDirWihtFile(dir);
    	}
    	/**
    	 * 递归的方法删除目录及其中的所有文件
    	 * @param dir
    	 */
        public static void deleteDirWihtFile(File dir) {
        	 if (dir == null || !dir.exists() || !dir.isDirectory())
                 return;
             for (File file : dir.listFiles()) {
                 if (file.isFile())
                     file.delete(); // 删除所有文件
                 else if (file.isDirectory())
                     deleteDirWihtFile(file); // 递规的方式删除文件夹
             }
             dir.delete();// 删除目录本身
        }
        
    public static boolean isPathStringValid(String path) {
        if (null == path || path.length() == 0) {
            return false;
        }

        if (path.contains(":") || path.contains("*") || path.contains("?")
                || path.contains("\"") || path.contains("<")
                || path.contains(">") || path.contains("|")) {
            LogUtil.w(TAG, "filename can not contains:*:?\"<>|");

            return false;
        }

        return true;
    }

    public static boolean isPath(String path) {
        if (path.contains(separator) || path.contains("\\")) {
            return true;
        }
        return false;
    }

    public static String getPath(String path) {
        int la = path.lastIndexOf(separator);
        String subPath = path.substring(0, la);
        return subPath;
    }

    /**
     * @param path      需要转换的路径或文件名
     * @param defPosfix 默认后缀名，当path不带后缀名时，则自动将其加上
     * @return
     */
    public static String convertValidFilePath(String path, String defPosfix) {
        String filePath = path;
        if (path.contains(separator) || path.contains("\\")) {
            int la = filePath.lastIndexOf(".");
            if (la < 0) {
                filePath = path + defPosfix;
            } else {
                String temp = filePath.substring(la);
                if (temp.contains(separator) || temp.contains("\\")) {
                    // "."是目录名的一部分而不是后缀名的情况
                    filePath = path + defPosfix;
                }
                // else fileName = fileName
            }
        } else {
            if (!path.contains(".")) // 没有有后缀
            {
                filePath = filePath + defPosfix;
            }
        }

        return filePath;
    }

    public static boolean isFileExists(String file) {
        try {
            File f = new File(file);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean isFileValid(File f) {
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {

                return false;
            }
            f.delete();
        }
        return true;
    }

    public static boolean isFileValid(File parent, String name) {
        File f = new File(parent, name);
        return isFileValid(f);
    }

    
    /**
     * BH中的日志保存
     *
     * @param path
     */
    public static boolean createDir(String path) {

        if (isSDCardExist()) {
            return false;
        }

        File f = new File(path);
        if (!f.exists()) {
            return f.mkdirs();
        }
        return true;
    }

    public static void createDir(File f) {

        if (isSDCardExist()) {
            return;
        }

        if (!f.exists()) {
            try {
                f.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭bufferReader
     *
     * @param br
     */
    public static void closeReader(Reader br) {
        if (br != null) {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭Writer
     */
    public static void closeWriter(Writer wr) {
        if (wr != null) {
            try {
                wr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * flush Writer
     */
    public static void flushWriter(Writer wr) {
        if (wr != null) {
            try {
                wr.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 输入流的关闭
     *
     * @param in
     */
    public static void closeInputStream(InputStream in) {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 输出流的关闭
     *
     * @param out
     */
    public static void closeOutputStream(OutputStream out) {
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 文件管道的关闭
     */
    public static void closeFileChannel(FileChannel chl) {
        if (chl != null) {
            try {
                chl.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * RandomAccessFile的关闭
     *
     * @param f RandomAccessFile对象
     */
    public static void closeRandomAccessFile(RandomAccessFile f) {
        if (f != null) {
            try {
                f.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Socket的关闭
     *
     * @param s Socket对象
     */
    public static void colseSocket(Socket s) {
        if (s != null) {
            try {
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * LocalSocket的关闭
     *
     * @param s Socket对象
     */
    public static void colseLocalSocket(LocalSocket s) {
        if (s != null) {
            try {
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    /**
     * 拷贝文件
     *
     * @param s 源文件
     * @param t 目标文件
     */
    public static void copyFile(File s, File t) {
        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;

        try {
            if (!t.exists()) {
                t.createNewFile();
            }

            fi = new FileInputStream(s);
            fo = new FileOutputStream(t);
            in = fi.getChannel();
            out = fo.getChannel();
            // 连接两个通道，并且从in通道读取，然后写入out通道
            in.transferTo(0, in.size(), out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeOutputStream(fo);
            closeInputStream(fi);
            closeFileChannel(in);
            closeFileChannel(out);
        }
    }

    public static void copyInputToFile(InputStream in, String path) {
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        try {
            byte[] buffer = new byte[10 * 1024];
            bis = new BufferedInputStream(in);
            fos = new FileOutputStream(path);
            int a = bis.read(buffer, 0, buffer.length);
            while (a != -1) {
                fos.write(buffer, 0, a);
                fos.flush();
                a = bis.read(buffer, 0, buffer.length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeOutputStream(fos);
            closeInputStream(bis);
            closeInputStream(in);
        }
    }

    /**
     * 是否存在SD卡
     */
    public static boolean isSDCardExist() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }
    /**
	 * 从raw中拷贝
	 */
	public static void copyFilesFromRaw(Context context, int id, String fileName,String storagePath) {
		String SEPARATOR = File.separator;// 路径分隔符
		InputStream inputStream = context.getResources().openRawResource(id);
		File file = new File(storagePath);
		if (!file.exists()) {// 如果文件夹不存在，则创建新的文件夹
			file.mkdirs();
		}
		readInputStream(storagePath + SEPARATOR + fileName, inputStream);
	}

	/**
	 * 复制文件
	 * @param storagePath
	 * @param inputStream
	 */
	public static void readInputStream(String storagePath, InputStream inputStream) {
		File file = new File(storagePath);
		try {
			if (!file.exists()) {
				FileOutputStream fos = new FileOutputStream(file);
				byte[] buffer = new byte[inputStream.available()];
				int lenght = 0;
				while ((lenght = inputStream.read(buffer)) != -1) {
					fos.write(buffer, 0, lenght);
				}
				fos.flush();
				fos.close();
				inputStream.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}