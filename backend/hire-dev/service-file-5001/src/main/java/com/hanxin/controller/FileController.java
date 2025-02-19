package com.hanxin.controller;

import com.hanxin.result.CustomJSONResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
@Slf4j
@RestController
@RequestMapping("file")
public class FileController {

    public static final String host = "http://192.168.2.193:8000/";

    @GetMapping("hello")
    public Object hello() {
        return "Hello file service!";
    }

    @PostMapping("uploadFace")
    public CustomJSONResult uploadFace(@RequestParam("file") MultipartFile file,
                                       @RequestParam("userId") String userId,
                                       HttpServletRequest request) throws Exception{
        String filename = file.getOriginalFilename();

        String suffixName = filename.substring(filename.lastIndexOf("."));

        String newFileName = userId + suffixName;

        String rootPath = "D:\\temp" + File.separator;
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
}
