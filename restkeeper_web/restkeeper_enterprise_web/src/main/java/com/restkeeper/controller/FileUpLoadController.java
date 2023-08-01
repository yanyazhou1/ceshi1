package com.restkeeper.controller;

import com.aliyun.oss.OSSClient;
import com.restkeeper.utils.Result;
import com.restkeeper.utils.ResultCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 文件上传控制器
 */
@RestController
@Api(tags = "图片上传通用接口")
@RefreshScope
@SuppressWarnings("all")
public class FileUpLoadController {

    @Value("${bucketName}")
    private String bucketName; //存储桶名称

    @Value("${spring.cloud.alicloud.oss.endpoint}")
    private String endpoint; //图片访问地址

    //使用OSS和Cloud整合的客户端
    @Autowired
    private OSSClient ossClient;

    /**
     * 图片缩略上传
     */
     @PostMapping("/imageUploadResize")
     @ApiImplicitParam(paramType = "form",dataType = "file",name = "fileName",value = "上传文件",required = true)
     public String imageUploadResize(@RequestParam("fileName") MultipartFile file){
         String fileName = System.currentTimeMillis()+"_"+file.getOriginalFilename();
         try {
            ossClient.putObject(bucketName,fileName,file.getInputStream());
         }catch (Exception e){
             e.printStackTrace();
         }
         String logoPath = "https://"+bucketName+"."+endpoint+"/"+fileName+"?x-oss-process=image/resize,m_fill,h_100,w_200";
         return logoPath;

     }

    /**
     * MultipartFile file 接收的文件类型
     */
    @PostMapping("/fileUpLoad")
    public Result fileUpLoad(@RequestParam("file") MultipartFile file){
        Result result = new Result();
        try {
          //1.获取上传的文件名称
          String fileName = System.currentTimeMillis()+"_"+file.getOriginalFilename();
          //2.使用oss客户端进行图片上传
          ossClient.putObject(bucketName,fileName,file.getInputStream());
          //3.拼接图片访问路径返回给前端
          String logoPath = "https://"+bucketName+"."+endpoint+"/"+fileName;
          result.setStatus(ResultCode.success);
          result.setDesc("图片上传成功");
          result.setData(logoPath);
          return result;
        }catch (Exception e){
            e.printStackTrace();
            result.setStatus(ResultCode.error);
            result.setDesc("图片上传失败"+e.getMessage());
            return result;
        }
    }
}
