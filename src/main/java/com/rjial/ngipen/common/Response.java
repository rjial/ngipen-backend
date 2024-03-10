package com.rjial.ngipen.common;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"message", "status_code", "data"})
public class Response<T> {
    @NonNull
    private String message;
    @NonNull
    @JsonProperty("status_code")
    private Long statusCode;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;
}
