package com.lxkj.common.util;

import java.io.File;
import java.io.FileOutputStream;

/**
 * 文件工具类
 * @Author apple
 * @Date 2018/3/31 14:46
 * @Description:
 * @Modified by:
 */
public class FileUtils {
    /**
     * 文件上传
     * @param file
     * @param filePath
     * @param fileName
     * @throws Exception
     */
    public static void uploadFile(byte[] file, String filePath, String fileName) throws Exception {
        File targetFile = new File(filePath);
        if(!targetFile.exists()){
            targetFile.mkdirs();
        }
        FileOutputStream out = new FileOutputStream(filePath+fileName);
        out.write(file);
        out.flush();
        out.close();
    }
}
