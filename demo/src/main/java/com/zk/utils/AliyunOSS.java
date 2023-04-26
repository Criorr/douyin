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
    // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
    String endpoint = "oss-cn-beijing.aliyuncs.com";
    // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
    String accessKeyId = "LTAI5tHmyLc1yRVH3jrL1nMz";
    String accessKeySecret = "sxEMdwlD0FZuOZDI3cngJuvbVELCZe";
    // 填写Bucket名称，例如examplebucket。
    String bucketName = "douyin000";

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
