package com.mofa.loan.utils;

import java.io.File;

public class FileUtils {
	/**
	 * 递归删除文件和文件夹
	 * @param file    要删除的根目录
	 */
	public static void RecursionDeleteFileForLogcatHelper(File file){
	    if(file.isFile()){
	        file.delete();
	        return;
	    }
	    if(file.isDirectory()){
	        File[] childFile = file.listFiles();
	        if(childFile == null || childFile.length == 0){
	            file.delete();
	            return;
	        }
	        for(File f : childFile){
//	        	if (("Log-" + LogcatHelper.getFileName() + ".logger").equalsIgnoreCase(f.getName())) continue;
	        	if (f.getName().contains(LogcatHelper.getFileName())) continue;
	            RecursionDeleteFile(f);
	        }
	        file.delete();
	    }
	}
	/**
	 * 递归删除文件和文件夹
	 * @param file    要删除的根目录
	 */
	public static void RecursionDeleteFile(File file){
		if(file.isFile()){
			file.delete();
			return;
		}
		if(file.isDirectory()){
			File[] childFile = file.listFiles();
			if(childFile == null || childFile.length == 0){
				file.delete();
				return;
			}
			for(File f : childFile){
				RecursionDeleteFile(f);
			}
			file.delete();
		}
	}
}
