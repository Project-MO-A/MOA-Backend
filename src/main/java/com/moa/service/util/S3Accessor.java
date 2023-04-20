package com.moa.service.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.moa.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

import static com.moa.global.exception.ErrorCode.IMAGE_NOT_PROCESS;
import static com.moa.global.exception.ErrorCode.IO_ERROR;

@Component
@RequiredArgsConstructor
public class S3Accessor {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3Client;

    public String save(MultipartFile image, String dirName) {
        File uploadFile = convert(image)
                .orElseThrow(() -> new BusinessException(IMAGE_NOT_PROCESS));
        return upload(uploadFile, dirName);
    }

    public String load(String key) {
        S3ObjectInputStream objectContent = amazonS3Client.getObject(bucket, key).getObjectContent();
        byte[] bytes;
        try {
            bytes = IOUtils.toByteArray(objectContent);
        } catch (IOException e) {
            throw new BusinessException(IO_ERROR);
        }
        return Base64.getEncoder().encodeToString(bytes);
    }

    private Optional<File> convert(MultipartFile file) {
        File convertFile = new File(file.getOriginalFilename());
        try {
            if (convertFile.createNewFile()) {
                try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                    fos.write(file.getBytes());
                }
                return Optional.of(convertFile);
            }
        } catch (IOException e) {
            throw new BusinessException(IO_ERROR);
        }
        return Optional.empty();
    }

    private String upload(File uploadFile, String dirName) {
        String fileName = dirName.concat("/").concat(uploadFile.getName());
        putS3(uploadFile, fileName);
        removeNewFile(uploadFile);
        return fileName;
    }

    private void putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
    }

    private void removeNewFile(File targetFile) {
        targetFile.delete();
    }
}
