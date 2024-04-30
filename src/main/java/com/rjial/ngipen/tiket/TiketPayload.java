package com.rjial.ngipen.tiket;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class TiketPayload {
    private String tiket;
    private String event;
    private String user;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Jakarta")
    private LocalDateTime expiredDateTime;
}
