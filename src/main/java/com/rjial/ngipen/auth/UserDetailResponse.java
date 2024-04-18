package com.rjial.ngipen.auth;

import com.fasterxml.jackson.annotation.JsonValue;
import com.rjial.ngipen.common.DataResponse;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailResponse implements DataResponse {
    @JsonValue
    private User user;
}
