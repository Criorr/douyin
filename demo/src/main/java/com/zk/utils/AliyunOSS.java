package com.zk.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * AliyunOSS
 *
 * @author ZhengKai
 * @date 2023/4/24
 */
public class AliyunOSS {


    public String fileUpload(MultipartFile multipartFile) {
        String filename = multipartFile.getOriginalFilename();

        //给文件名添加一个随机的值
        String uuid = UUID.randomUUID().toString().replaceAll("-","");
        filename = uuid+filename;

//        //把文件按照日期进行分类
//        String datePath = new DateTime().toString("yyyy/MM/dd");
//
//        filename = datePath+"/"+filename;

        InputStream inputStream = null;
        OSS ossClient = null;
        try {
            // 创建OSSClient实例。
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

            // 获取上传文件流。
            inputStream = multipartFile.getInputStream();
            ossClient.putObject(bucketName, filename, inputStream);
            //把上传后的文件路径返回
            //需要把上传到阿里云oss路径手动拼接出来
            String url = "https://"+bucketName+"."+endpoint+"/"+ filename;
            return url;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            // 关闭OSSClient。
            ossClient.shutdown();
        }
    }

}
