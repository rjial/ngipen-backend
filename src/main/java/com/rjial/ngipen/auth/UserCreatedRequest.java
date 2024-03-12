package com.rjial.ngipen.auth;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
public class UserCreatedRequest {

    private String email;
    private String name;
    private String hp;
    private String address;
    private Level level;
    private String password;

}
