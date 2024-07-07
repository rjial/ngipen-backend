package com.rjial.ngipen.event;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventHeaderImgUrlSerializerConfig {
    @Bean
    public EventHeaderImgUrlSerializer eventHeaderImgUrlSerializer() {
        return new EventHeaderImgUrlSerializer();
    }
}
