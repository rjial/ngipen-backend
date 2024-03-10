package com.rjial.ngipen.auth;

import com.rjial.ngipen.common.DataResponse;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse implements DataResponse {
    private String email;
    private String token;
}
