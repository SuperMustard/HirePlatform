package com.hanxin;

import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * MinIO工具类
 */
@Slf4j
public class CFR2Utils {

    private static S3Client s3Client;

    private static String endpoint;
    private static String accountId;
    private static String bucketName;
    private static String accessKey;
    private static String secretKey;

    private static String publicURL;

    private static final String SEPARATOR = "/";

    public CFR2Utils() {
    }

    public CFR2Utils(String endpoint, String accountId, String bucketName, String accessKey, String secretKey, String publicURL) {
        CFR2Utils.endpoint = endpoint;
        CFR2Utils.accountId = accountId;
        CFR2Utils.bucketName = bucketName;
        CFR2Utils.accessKey = accessKey;
        CFR2Utils.secretKey = secretKey;
        CFR2Utils.publicURL = publicURL;
        createMinioClient();
    }

    /**
     * 创建基于Java端的MinioClient
     */
    public void createMinioClient() {
        try {
            AwsBasicCredentials credentials = AwsBasicCredentials.create(
                    accessKey,
                    secretKey
            );

            S3Configuration serviceConfiguration = S3Configuration.builder()
                    .pathStyleAccessEnabled(true)
                    .build();

            s3Client = S3Client.builder()
                        .endpointOverride(URI.create(endpoint))
                        .credentialsProvider(StaticCredentialsProvider.create(credentials))
                        .region(Region.of("auto"))
                        .serviceConfiguration(serviceConfiguration)
                        .build();
        } catch (Exception e) {
            log.error("CFS3服务器异常：{}", e);
        }
    }

    /******************************  Operate Files Start  ******************************/

    /**
     * 使用MultipartFile进行文件上传
     * @param bucketName 存储桶
     * @param file 文件名
     * @param objectName 对象名
     * @return
     * @throws Exception
     */
    public static String uploadFile(String bucketName, MultipartFile file,
                                               String objectName, InputStream inputStream) throws Exception {
        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectName)
                    .contentType(file.getContentType())
                    .build();

            PutObjectResponse response = s3Client.putObject(putRequest,
                    RequestBody.fromInputStream(inputStream, file.getSize()));
        } finally {
            s3Client.close();
        }

        String imageURL = publicURL + SEPARATOR + objectName;

        return imageURL;
    }

    /******************************  Operate Files End  ******************************/


}

