package com.gp.business.gen.controller;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import com.gp.business.gen.domain.GenConfig;
import com.gp.business.gen.service.GenService;
import com.gp.common.core.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "代码生成器")
@RestController
@RequestMapping("/gen")
public class GenController {

    @Autowired
    private GenService genService;

    @Operation(summary = "可生成的业务表列表")
    @GetMapping("/tables")
    public Result<List<Map<String, Object>>> tables() {
        return Result.success(genService.listTables());
    }

    @Operation(summary = "预览生成代码")
    @PostMapping("/preview")
    public Result<Map<String, String>> preview(@RequestBody GenConfig config) {
        return Result.success(genService.preview(config));
    }

    @Operation(summary = "下载生成代码（ZIP）")
    @PostMapping("/download")
    public ResponseEntity<byte[]> download(@RequestBody GenConfig config) {
        byte[] zip = genService.download(config);
        String fileName;
        try {
            fileName = URLEncoder.encode("gen-" + config.getModule() + ".zip", "UTF-8")
                    .replace("+", "%20");
        } catch (java.io.UnsupportedEncodingException e) {
            fileName = "gen-" + config.getModule() + ".zip";
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + fileName)
                .contentType(MediaType.parseMediaType("application/zip"))
                .body(zip);
    }

}
