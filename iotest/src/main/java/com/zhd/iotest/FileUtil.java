package com.zhd.iotest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.content.Context;

public class FileUtil {

	private static FileUtil _fileUtil;
	
	private FileUtil(){}

	public static FileUtil getInstance() {
		if(_fileUtil==null)
			_fileUtil = new FileUtil();
		return _fileUtil;
	}

	/**
	 * 是否存在
	 * @param filepath
	 * @return
	 */
	public boolean isExist(String filepath) throws IOException {
		File file = new File(filepath);
		return file.isFile();
	}

	/**
	 * 删除文件
	 * @param filepath
	 * @return
	 */
	private synchronized void deleteFile(String filePath){
		File file = new File(filePath);
		if (!file.exists())
			return;
		try{
			if (file.isDirectory()) {// 处理目录
				File files[] = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					deleteFile(files[i].getAbsolutePath());
				}
			}
			if (!file.isDirectory()) {// 如果是文件，删除
				file.delete();
			} else {// 目录
				if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
					file.delete();
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
     * 删除缓存
     * @param filepath
     * @return
     */
	public synchronized void clearCache(String filePath)
	{
	    File file = new File(filePath);
        if (!file.exists())
            return;
        try{
            if (file.isDirectory()) {//目录
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i].getAbsolutePath());
                }
            }
            if (!file.isDirectory()) {//文件
                file.delete();
            }
        } catch(Exception e){
            e.printStackTrace();
        }
	}

	/**
	 * 读取文件内容
	 * 
	 * @param filepath
	 * @return
	 */
	public String readFileByString(String filePath) {
		try{
			if (!isExist(filePath)) {
				return null;
			}
			File file = new File(filePath);
			FileInputStream fis = null;
			InputStreamReader isr = null;
			BufferedReader br = null;
			StringBuffer xml = new StringBuffer();
			try {
				fis = new FileInputStream(file);
				isr = new InputStreamReader(fis, "UTF-8");
				char[] b = new char[4096];
				for (int n; (n = isr.read(b)) != -1;) {
					xml.append(new String(b, 0, n));
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (null != fis){
					fis.close();
				}
				if (null != br) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return xml.toString();
		}catch(Exception e){
			return null;
		}
	}

	/**
	 * 读取文件内容
	 * 
	 * @param filepath
	 * @return
	 * @throws IOException
	 */
	public byte[] readFileByByte(String filePath) {
	    try{
    	    if (!isExist(filePath)) {
    			return null;
    		}
    		File file = new File(filePath);
    		int length = (int) file.length();
    		byte content[] = new byte[length];
    		FileInputStream fis = new FileInputStream(file);
    		fis.read(content, 0, length);
    		fis.close();
    
    		return content;
	    }catch(Exception e){
            return null;
        }
	}

	/**
	 * 内容写入文件
	 * @param filepath 写入的地址
	 * @param data 写入的内容
	 */
	public void writeFileByString(String filePath, String data) {
		FileOutputStream fOut = null;
		OutputStreamWriter osw = null;
		File file = new File(filePath);
		//如果传入的是一个文件夹，文件夹前一个路径创建文件夹，然后再创建一个
		try {
		    if (!file.isFile()) {
                String path = filePath.substring(0, filePath.lastIndexOf("/"));
                File dirFile = new File(path);
                dirFile.mkdirs();
                file.createNewFile();
            }
		    
			fOut = new FileOutputStream(file,false);
			osw = new OutputStreamWriter(fOut, "UTF-8");
			osw.write(data);
			osw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
			    if(osw!=null)
			        osw.close();
			    if(fOut!=null)
			        fOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 内容写入文件，根据字节写入，首先判断是否为文件，不是文件，就复
	 * @param filepath
	 * @param data C:/Users/2015032501/Desktop/项目帮助文档
	 */
	public void writeFileByByte(String filePath, byte content[]) {
		FileOutputStream fOut = null;
		File file = new File(filePath);//给定位置是一个文件夹，再上一个目录创建文件夹，然后创建一个文件
		try {
		    if (!file.isFile()) {
                String path = filePath.substring(0, filePath.lastIndexOf("/"));
                File dirFile = new File(path);
                dirFile.mkdirs();
                file.createNewFile();
            }
		    //获取文件位置
			fOut = new FileOutputStream(file,false);
			fOut.write(content, 0, content.length);
			fOut.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
			    if(fOut != null)
			        fOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public byte[] readAsset(Context context, String file)
	{
	    InputStream is = null;
	    try {
    	    is = context.getAssets().open(file);  
            int size = is.available();  
            byte[] buffer = new byte[size];  
            is.read(buffer);  
            is.close();
            
            return buffer;
	    } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(is != null)
                    is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	    return null;
	}
}
