package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Api(tags = "Common Interface")
@Slf4j
public class CommonController {

    @Value("${sky.path.upload-dir}")
    private String uploadDir;

    @PostMapping("/upload")
    @ApiOperation("File upload")
    public Result<String> upload(MultipartFile file) {
        log.info("upload file with path: {}", uploadDir);
        try {
            // 1. 获取原始文件名
            String originalFilename = file.getOriginalFilename();

            // 2. 截取后缀
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

            // 3. 构造新文件名
            String newFileName = UUID.randomUUID().toString() + extension;

            // 4. 检查目录是否存在
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 5. 保存文件
            file.transferTo(new File(dir, newFileName));

            // 6. 返回文件路径
            return Result.success(newFileName);
        } catch (IOException e) {
            return Result.error(MessageConstant.UPLOAD_FAILED);
        }

    }
}
