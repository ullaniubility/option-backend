package com.ulla.common.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.Bucket;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;

/**
 * @Description {阿里云oss}
 * @author {clj}
 * @since {2023-2-10}
 */
public class AliyunClient {

    // 阿里云API的外网域名
    public String endpoint;

    // 阿里云API的密钥Access Key ID
    public String access_key_id;

    // 阿里云API的密钥Access Key Secret
    public String access_key_secret;

    // 阿里云API的bucket名称
    public String bucket_name;

    // 阿里云API的文件夹名称
    public String folder;

    public OSSClient ossClient;

    /**
     * 获取阿里云OSS客户端对象
     *
     * @return ossClient
     */
    public AliyunClient(String endpoint, String access_key_id, String access_key_secret, String bucket_name,
        String folder) {

        this.endpoint = endpoint;
        this.access_key_id = access_key_id;
        this.access_key_secret = access_key_secret;
        this.bucket_name = bucket_name;
        this.folder = folder;
        this.ossClient = new OSSClient(endpoint, access_key_id, access_key_secret);
    }

    /**
     * 创建存储空间
     *
     * @param bucketName
     *            存储空间
     * @return
     */
    public String createBucketName(String bucketName) {
        // 存储空间
        String bucketNames = bucketName;
        if (!ossClient.doesBucketExist(bucketName)) {
            // 创建存储空间
            Bucket bucket = ossClient.createBucket(bucketName);

            return bucket.getName();
        }
        return bucketNames;
    }

    /**
     * 删除存储空间buckName
     *
     * @param bucketName
     *            存储空间
     */
    public void deleteBucket(String bucketName) {
        ossClient.deleteBucket(bucketName);
    }

    /**
     * 创建模拟文件夹
     *
     * @param bucketName
     *            存储空间
     * @param folder
     *            模拟文件夹名如"xxx/"
     * @return 文件夹名
     */
    public String createFolder(String bucketName, String folder) {
        // 文件夹名
        String keySuffixWithSlash = folder;
        // 判断文件夹是否存在，不存在则创建
        if (!ossClient.doesObjectExist(bucketName, keySuffixWithSlash)) {
            // 创建文件夹
            ossClient.putObject(bucketName, keySuffixWithSlash, new ByteArrayInputStream(new byte[0]));
            // 得到文件夹名
            OSSObject object = ossClient.getObject(bucketName, keySuffixWithSlash);
            String fileDir = object.getKey();
            return fileDir;
        }
        return keySuffixWithSlash;
    }

    /**
     * 根据key删除OSS服务器上的文件
     *
     * @param bucketName
     *            存储空间
     * @param folder
     *            模拟文件夹名 如"xxx/"
     * @param key
     *            Bucket下的文件的路径名+文件名 如："upload/cake.jpg"
     */
    public void deleteFile(String bucketName, String folder, String key) {
        ossClient.deleteObject(bucketName, folder + key);
    }

    /**
     * 上传图片至OSS
     *
     * @param file
     *            上传文件（文件全路径如：D:\\image\\cake.jpg）
     * @return String 返回的唯一MD5数字签名
     */
    public String uploadObject2OSS(File file, String name) {
        String resultStr = null;
        try {
            // 以输入流的形式上传文件
            InputStream is = new FileInputStream(file);
            // 文件名
            // 上传文件 (上传文件流的形式)
            PutObjectResult putResult = ossClient.putObject(bucket_name, folder + name, is);
            // 解析结果
            resultStr = putResult.getETag();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultStr;
    }

    public String sendOss(MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();
            // 文件的后缀名
            String fileExtension = fileName.substring(fileName.lastIndexOf("."));
            String name = UUID.randomUUID().toString().replace("-", "");
            ossClient.putObject(bucket_name, folder + name + fileExtension, new ByteArrayInputStream(file.getBytes()));
            Date expiration = new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 365 * 10);
            String url =
                ossClient.generatePresignedUrl(bucket_name, folder + name + fileExtension, expiration).toString();
            ossClient.shutdown();
            return url;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 上传图片至OSS
     *
     * @return String 返回的唯一MD5数字签名
     */
    public String uploadObject2OSS(InputStream is, String fileName) {
        String resultStr = null;
        try {
            // 创建上传Object的Metadata
            ObjectMetadata metadata = new ObjectMetadata();
            // 上传的文件的长度
            metadata.setContentLength(is.available());
            // 指定该Object被下载时的网页的缓存行为
            metadata.setCacheControl("no-cache");
            // 指定该Object下设置Header
            metadata.setHeader("Pragma", "no-cache");
            // 指定该Object被下载时的内容编码格式
            metadata.setContentEncoding("utf-8");
            // 文件的MIME，定义文件的类型及网页编码，决定浏览器将以什么形式、什么编码读取文件。如果用户没有指定则根据Key或文件名的扩展名生成，
            // 如果没有扩展名则填默认值application/octet-stream
            metadata.setContentType(getContentType(fileName));
            // 指定该Object被下载时的名称（指示MINME用户代理如何显示附加的文件，打开或下载，及文件名称）
            String name = UUID.randomUUID().toString().replace("-", "");
            String filePath = folder + name + fileName;
            metadata.setContentDisposition("filename/filesize=" + (filePath));
            // 上传文件 (上传文件流的形式)
            PutObjectResult putResult = ossClient.putObject(bucket_name, filePath, is, metadata);
            // 解析结果
            // PutObjectResult putResult = ossClient.putObject(bucketName, filePath, is, metadata);
            resultStr = putResult.getETag();
            StringBuilder sb = new StringBuilder(endpoint + "/" + filePath);// 构造一个StringBuilder对象
            sb.insert(0, bucket_name + ".");// 在指定的位置插入指定的字符串
            resultStr = sb.toString();
            // 关闭OSSClient。
            ossClient.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultStr;
    }

    /**
     * 通过文件名判断并获取OSS服务文件上传时文件的contentType
     *
     * @param fileName
     *            文件名
     * @return 文件的contentType
     */
    public String getContentType(String fileName) {
        // 文件的后缀名
        String fileExtension = fileName.substring(fileName.lastIndexOf("."));
        if (".bmp".equalsIgnoreCase(fileExtension)) {
            return "image/bmp";
        }
        if (".gif".equalsIgnoreCase(fileExtension)) {
            return "image/gif";
        }
        if (".svg".equalsIgnoreCase(fileExtension)) {
            return "image/svg";
        }
        if (".jpeg".equalsIgnoreCase(fileExtension) || ".jpg".equalsIgnoreCase(fileExtension)
            || ".png".equalsIgnoreCase(fileExtension)) {
            return "image/jpeg";
        }
        if (".html".equalsIgnoreCase(fileExtension)) {
            return "text/html";
        }
        if (".txt".equalsIgnoreCase(fileExtension)) {
            return "application/txt";
        }
        if (".vsd".equalsIgnoreCase(fileExtension)) {
            return "application/vnd.visio";
        }
        if (".ppt".equalsIgnoreCase(fileExtension) || "pptx".equalsIgnoreCase(fileExtension)) {
            return "application/vnd.ms-powerpoint";
        }
        if (".doc".equalsIgnoreCase(fileExtension) || "docx".equalsIgnoreCase(fileExtension)) {
            return "application/msword";
        }
        if (".xml".equalsIgnoreCase(fileExtension)) {
            return "application/xml";
        }
        if (".pdf".equalsIgnoreCase(fileExtension)) {
            return "application/pdf";
        }
        if (".wav".equalsIgnoreCase(fileExtension)) {
            return "application/wav";
        }
        if (".mp4".equalsIgnoreCase(fileExtension)) {
            return "application/mp4";
        }
        // 默认返回类型
        return "image/jpeg";
    }
}
