package com.changgou.controller;

import com.changgou.file.FastDFSFile;
import com.changgou.util.FastDFSUtil;
import entity.Result;
import entity.StatusCode;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author lhy
 * @version 1.0
 * @date 2021/1/28 0:00
 */
@RestController
@RequestMapping("/upload")
@CrossOrigin
public class FileUpLoadController {

    @PostMapping
    public Result upload(@RequestParam("file") MultipartFile file) throws Exception {
        //调用FastDFSUtil工具类将文件传入到FastDFS中
        FastDFSFile fastDFSFile = new FastDFSFile(
                file.getOriginalFilename(),//获取文件名
                file.getBytes(),//获取文件的字节数组
                StringUtils.getFilenameExtension(file.getOriginalFilename())//获取文件扩展名
        );
        String[] upload = FastDFSUtil.upload(fastDFSFile);
        //拼接访问地址
        String url = FastDFSUtil.getTrackerInfo()+"/" + upload[0] + "/" + upload[1];
        return new Result(true, StatusCode.OK, "上传成功!",url);
    }
}
