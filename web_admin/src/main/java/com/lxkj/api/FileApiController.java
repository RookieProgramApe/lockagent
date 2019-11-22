package com.lxkj.api;

import com.lxkj.common.bean.BaseController;
import com.lxkj.common.bean.FileMappingProperties;
import com.lxkj.common.bean.FileModel;
import com.lxkj.common.util.DateUtil;
import com.lxkj.common.util.ID;
import io.swagger.annotations.*;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

@Api("手机端文件接口")
@RestController
@RequestMapping("/api/file")
@Slf4j
public class FileApiController extends BaseController {
    @Autowired
    private FileMappingProperties fileUtil;

    /**
     * 手机端文件上传
     */
    @ApiOperation("上传文件")
    @PostMapping("/uploadFile")
    @ApiResponses({
            @ApiResponse(code = 200, message = "{\n" +
                    "    \"code\": 0,  状态码\n" +
                    "    \"fileUrl\":  \"http://127.0.0.1:8088/lockagent/static/downFile/Appraise/20191118/B7EF96D2496E40A3A420FEAD8D1F9160.jpg\",文件url\n" +
                    "    \"fileName\": \"微信图片_20191118085445.jpg\",文件名\n" +
                    "    \"filePath\": \"D:\\\\projects\\\\Appraise\\\\20191118\\\\B7EF96D2496E40A3A420FEAD8D1F9160.jpg\"文件存储地址\n" +
                    "}"),
    })
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

    /**
     * 手机端文件上传
     */
    @ApiOperation("上传文件")
    @PostMapping("/test")
    @ApiResponses({
            @ApiResponse(code = 200, message = "{\n" +
                    "    \"code\": 0,  状态码\n" +
                    "    \"fileUrl\":  \"http://127.0.0.1:8088/lockagent/static/downFile/Appraise/20191118/B7EF96D2496E40A3A420FEAD8D1F9160.jpg\",文件url\n" +
                    "    \"fileName\": \"微信图片_20191118085445.jpg\",文件名\n" +
                    "    \"filePath\": \"D:\\\\projects\\\\Appraise\\\\20191118\\\\B7EF96D2496E40A3A420FEAD8D1F9160.jpg\"文件存储地址\n" +
                    "}"),
    })
    public String test(
            @RequestParam(value = "model", required = false, defaultValue = "temp") String model
    ) {

        return "success";
    }

}
