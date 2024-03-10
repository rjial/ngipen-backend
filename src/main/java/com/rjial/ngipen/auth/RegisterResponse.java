package com.rjial.ngipen.auth;

import com.rjial.ngipen.common.DataResponse;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponse implements DataResponse {
    private String name;
    private String email;
    private String hp;
    private String address;
}
