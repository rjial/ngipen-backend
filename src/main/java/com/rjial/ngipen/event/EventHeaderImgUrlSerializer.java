package com.rjial.ngipen.event;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class EventHeaderImgUrlSerializer extends StdSerializer<String> implements ApplicationContextAware {

    private static ApplicationContext context;

    public EventHeaderImgUrlSerializer() {
        this(null);
    }

    protected EventHeaderImgUrlSerializer(Class<String> t) {
        super(t);
    }

    private static EventGCPCloudStorageFileUpload eventGCPCloudStorageFileUpload() {
        return context.getBean(EventGCPCloudStorageFileUpload.class);
    }

    @Override
    public void serialize(String s, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (s != null) {
            jsonGenerator.writeString(eventGCPCloudStorageFileUpload().downloadFile(s));
        } else {
            jsonGenerator.writeNull();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}
