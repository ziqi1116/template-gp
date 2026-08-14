package com.gp.framework.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.gp.common.core.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Tag(name = "文件上传")
@RestController
@RequestMapping("/common")
public class FileController {

    @Value("${upload.path:/tmp/upload}")
    private String uploadPath;

    @Operation(summary = "上传文件")
    @PostMapping("/upload")
    public Result<Map<String, Object>> upload(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return Result.error("上传文件不能为空");
        }

        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String dirPath = uploadPath + "/" + datePath;

        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String newFilename = UUID.randomUUID().toString().replace("-", "") + ext;
        File dest = new File(dir, newFilename);

        try {
            file.transferTo(dest);
        } catch (IOException e) {
            return Result.error("文件上传失败");
        }

        String url = "/common/download?fileName=" + newFilename + "&delete=true";

        Map<String, Object> result = new HashMap<>();
        result.put("url", url);
        result.put("fileName", newFilename);
        result.put("originalFilename", originalFilename);
        return Result.success(result);
    }

}