package com.rjial.ngipen.event;

import java.io.IOException;

import org.apache.coyote.BadRequestException;
import org.springframework.web.multipart.MultipartFile;

public interface EventFileUpload {

    public String uploadFile(MultipartFile file, String path) throws BadRequestException, IOException;
    public String downloadFile(String path);

}
