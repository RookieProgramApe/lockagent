package com.lxkj.controller;

import com.lxkj.common.bean.BaseController;
import com.lxkj.common.bean.FileMappingProperties;
import com.lxkj.common.bean.FileModel;
import com.lxkj.common.util.DateUtil;
import com.lxkj.common.util.ID;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

@Controller
@RequestMapping("/file")
@Slf4j
public class FileController extends BaseController {
    @Autowired
    private FileMappingProperties fileUtil;

    /**
     * 文件上传
     */
    @PostMapping("/uploadFile")
    @ResponseBody
    public FileModel uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "model", required = false, defaultValue = "temp") String model
    ) {
        FileModel res=new FileModel();
        String time = DateUtil.DateToString(new Date(), "yyyyMMdd");
        //文件存放根路径
        String docPath = fileUtil.getPath() + File.separator + model + File.separator + time;
        //文件名
        String fileName = ID.nextGUID() + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        //文件全路径
        File fileAllPath = new File(docPath + File.separator + fileName);
        if (!fileAllPath.getParentFile().exists())fileAllPath.getParentFile().mkdirs();
        try {
            @Cleanup BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(fileAllPath));
            out.write(file.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            res.setCode(-1);
            return res;
        }
        res.setCode(0);
        res.setFileUrl(fileUtil.getMapping() + "/" + model + "/" + time + "/" + fileName);
        res.setFileName(file.getOriginalFilename());
        res.setFilePath(fileAllPath.getAbsolutePath());
        return res;
    }
}
