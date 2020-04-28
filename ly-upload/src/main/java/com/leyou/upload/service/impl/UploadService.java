package com.leyou.upload.service.impl;

import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.upload.config.UploadProperties;
import com.leyou.upload.service.IUploadService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@EnableConfigurationProperties(UploadProperties.class)
public class UploadService implements IUploadService {

    //private static final List<String> ALLOW_TYPES = Arrays.asList("image/jpeg", "image/png", "image/jpg", "image/bmp");

    @Autowired
    private UploadProperties prop;

    @Autowired
    private FastFileStorageClient storageClient;

    @Override
    public String uploadImage(MultipartFile file) {
        try {
            //类型检验
            if (!prop.getAllowTypes().contains(file.getContentType())) {
                //不是有效类型
                throw new LyException(ExceptionEnum.VALIDATION_FILE_TYPE);
            }
            //内容检验
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                throw new LyException(ExceptionEnum.VALIDATION_FILE_CONTENT);
            }
            /*//1.1准备目标目录
            File dest = new File("E:\\my_java\\uploads", file.getOriginalFilename());
            //2.上传到本地

            file.transferTo(dest);*/
            //获取文件扩展名
            String extension = FilenameUtils.getExtension(file.getOriginalFilename());
            StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), extension, null);

            //3.返回url
            return prop.getBaseUrl() + storePath.getFullPath();
        } catch (IOException e) {
            log.error("文件上传失败！");
            throw new LyException(ExceptionEnum.UPLOAD_FILE_ERROR);
        }
    }
}
