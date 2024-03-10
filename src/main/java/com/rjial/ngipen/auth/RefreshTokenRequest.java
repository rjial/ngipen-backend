package com.rjial.ngipen.auth;

import com.rjial.ngipen.common.DataResponse;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenRequest {
    private String token;
}
