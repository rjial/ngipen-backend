package com.rjial.ngipen.event;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import org.apache.commons.io.FileUtils;
import org.apache.coyote.BadRequestException;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.rjial.ngipen.common.InvalidFileTypeException;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EventGCPCloudStorageFileUpload implements EventFileUpload {

    private final Storage service;
    private final String bucketName;
    private final String projectId;
    private final String gcpIamConfigJson;


    public EventGCPCloudStorageFileUpload(Environment env) throws IOException {
        this.bucketName = env.getProperty("gcpstorage.bucketname");
        assert bucketName != null;
        this.projectId = env.getProperty("gcpstorage.projectid");
        assert projectId != null;
        this.gcpIamConfigJson = env.getProperty("gcpstorage.configjson");
        assert gcpIamConfigJson != null;
        Credentials credentials = GoogleCredentials.fromStream(new FileInputStream(gcpIamConfigJson));
        this.service = StorageOptions.newBuilder().setCredentials(credentials).setProjectId(projectId).build().getService();
    }

    @Override
    public String uploadFile(MultipartFile file, String path) throws IOException {
        String originalFileName = file.getOriginalFilename();
        if(originalFileName == null){
            throw new BadRequestException("Original file name is null");
        }   
        Path pathOriginal = new File(originalFileName).toPath();

        try {
            String contentType = Files.probeContentType(pathOriginal);
            if (file.getOriginalFilename() == null) {
                throw new BadRequestException("Original file name is null");
            }
            log.info(file.getOriginalFilename());
            File convertedFile = new File(file.getOriginalFilename());
            log.info(convertedFile.getName());
            FileOutputStream outputStream = new FileOutputStream(convertedFile);
            outputStream.write(file.getBytes());
            outputStream.close();
            byte[] fileData = FileUtils.readFileToByteArray(convertedFile);

            Bucket bucket = service.get(bucketName  , Storage.BucketGetOption.fields());
            log.info(file.getOriginalFilename());
            
            Blob blob =  bucket.create("cdn/images/" + path +  checkFileExtension(file.getOriginalFilename()), fileData, contentType);
            if (blob != null)  {
                return path + checkFileExtension(file.getOriginalFilename());
            } else {
                throw new RuntimeException("Upload file is failed");
            }
        } catch (InvalidFileTypeException e) {
            throw e;
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } 
    }

    @Override
    public String downloadFile(String path) {
        log.info(path);
        BlobInfo blobInfo = BlobInfo.newBuilder("ngipen",  "cdn/images/" + path).build();
        return service.signUrl(blobInfo, 30, TimeUnit.DAYS).toString();
    }

    private String checkFileExtension(String fileName) {
        if(fileName != null && fileName.contains(".")){
            String[] extensionList = {".png", ".jpeg", ".jpg    "};

            for(String extension: extensionList) {
                if (fileName.endsWith(extension)) {
                    return extension;
                }
            }
        }
        throw new InvalidFileTypeException("Not a permitted file type");
    }
}
