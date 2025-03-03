package com.hanxin.controller;

import com.hanxin.CFR2Config;
import com.hanxin.CFR2Utils;
import com.hanxin.MinIOConfig;
import com.hanxin.MinIOUtils;
import com.hanxin.exceptions.ExceptionWrapper;
import com.hanxin.pojo.bo.Base64FileBO;
import com.hanxin.result.CustomJSONResult;
import com.hanxin.result.ResponseStatusEnum;
import com.hanxin.utils.Base64ToFile;
import com.hanxin.utils.GeneratePathByOS;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("file")
public class FileController {

    public static final String host = "http://192.168.2.193:8000/";

    @GetMapping("hello")
    public Object hello() {
        return "Hello file service!";
    }

    @PostMapping("uploadFaceLocal")
    public CustomJSONResult uploadFaceLocal(@RequestParam("file") MultipartFile file,
                                       @RequestParam("userId") String userId,
                                       HttpServletRequest request) throws Exception{
        String filename = file.getOriginalFilename();

        String suffixName = filename.substring(filename.lastIndexOf("."));

        String newFileName = userId + suffixName;

        //String rootPath = "D:\\temp" + File.separator;
        String rootPath = GeneratePathByOS.getRootPath();
        String filePath = rootPath + File.separator + "face" + File.separator + newFileName;

        File newFile = new File(filePath);

        if (!newFile.getParentFile().exists()) {
            newFile.getParentFile().mkdirs();
        }

        file.transferTo(newFile);

        String userFaceUrl = host + "static/face/" + newFileName;
        log.info(userFaceUrl);

        return CustomJSONResult.ok(userFaceUrl);
    }

    @Autowired
    private MinIOConfig minIOConfig;

    @PostMapping("uploadFace")
    public CustomJSONResult uploadFace(@RequestParam("file") MultipartFile file,
                                       @RequestParam("userId") String userId,
                                       HttpServletRequest request) throws Exception{
        if (StringUtils.isBlank(userId)) {
            return CustomJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }

        String filename = file.getOriginalFilename();
        if (StringUtils.isBlank(filename)) {
            return CustomJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);
        }

        filename = userId + "/" + filename;
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            MinIOUtils.uploadFile(minIOConfig.getBucketName(), filename, inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionWrapper.display(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        String imageUrl = minIOConfig.getFileHost()
                        + "/"
                        + minIOConfig.getBucketName()
                        + "/"
                        + filename;
        return CustomJSONResult.ok(imageUrl);
    }

    @Autowired
    private CFR2Config cfr2Config;

    @PostMapping("uploadFace2")
    public CustomJSONResult uploadFace2(@RequestParam("file") MultipartFile file,
                                       @RequestParam("userId") String userId,
                                       HttpServletRequest request) throws Exception{
        if (StringUtils.isBlank(userId)) {
            return CustomJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }

        String filename = file.getOriginalFilename();
        if (StringUtils.isBlank(filename)) {
            return CustomJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);
        }

        filename = userId + "/" + filename;
        InputStream inputStream = null;
        String imageURL = "";
        try {
            inputStream = file.getInputStream();
            imageURL = CFR2Utils.uploadFile(cfr2Config.getBucketName(), file, filename, inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionWrapper.display(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        log.info(imageURL);

        return CustomJSONResult.ok(imageURL);
    }

    @PostMapping("uploadAdminFace")
    public CustomJSONResult uploadAdminFace(@RequestBody @Valid Base64FileBO base64FileBO) throws Exception{

        String  base64 = base64FileBO.getBase64File();

        String suffixName = ".png";
        String uuid = UUID.randomUUID().toString();
        String objectName = uuid + suffixName;

        String rootPath = GeneratePathByOS.getRootPath();
        String filePath = rootPath
                            + File.separator
                            + "adminFace"
                            + File.separator
                            + objectName;

        Base64ToFile.Base64ToFile(base64, filePath);

        MinIOUtils.uploadFile(minIOConfig.getBucketName(), objectName, filePath);

        String imageUrl = minIOConfig.getFileHost()
                + "/"
                + minIOConfig.getBucketName()
                + "/"
                + objectName;

        return CustomJSONResult.ok(imageUrl);
    }

    @PostMapping("uploadLogo")
    public CustomJSONResult uploadLogo(@RequestParam("file") MultipartFile file) throws Exception {

        // 获得文件原始名称
        String filename = file.getOriginalFilename();
        if (StringUtils.isBlank(filename)) {
            return CustomJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);
        }

        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            filename = "company/logo/" + dealFilename(filename);
            MinIOUtils.uploadFile(minIOConfig.getBucketName(), filename, inputStream);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        String imageUrl = MinIOUtils.uploadFile(minIOConfig.getBucketName(),
                filename,
                file.getInputStream(),
                true);
        return CustomJSONResult.ok(imageUrl);
    }

    @PostMapping("uploadBizLicense")
    public CustomJSONResult uploadBizLicense(@RequestParam("file") MultipartFile file) throws Exception {

        // 获得文件原始名称
        String filename = file.getOriginalFilename();
        if (StringUtils.isBlank(filename)) {
            return CustomJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);
        }

        @Cleanup
        InputStream inputStream = file.getInputStream();

        filename = "company/bizLicense/" + dealFilename(filename);
        String imageUrl = MinIOUtils.uploadFile(minIOConfig.getBucketName(),
                filename,
                inputStream,
                true);
        return CustomJSONResult.ok(imageUrl);
    }

    @PostMapping("uploadAuthLetter")
    public CustomJSONResult uploadAuthLetter(@RequestParam("file") MultipartFile file) throws Exception {

        // 获得文件原始名称
        String filename = file.getOriginalFilename();
        if (StringUtils.isBlank(filename)) {
            return CustomJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);
        }

        filename = "company/AuthLetter/" + dealFilename(filename);
        @Cleanup
        InputStream inputStream = file.getInputStream();
        String imageUrl = MinIOUtils.uploadFile(minIOConfig.getBucketName(),
                filename,
                inputStream,
                true);
        return CustomJSONResult.ok(imageUrl);
    }

    private String dealFilename(String filename) {
        String suffixName = filename.substring(filename.lastIndexOf("."));
        String fName = filename.substring(0, filename.lastIndexOf("."));
        String uuid = UUID.randomUUID().toString();
        return fName + "-" + uuid + suffixName;
    }

}
