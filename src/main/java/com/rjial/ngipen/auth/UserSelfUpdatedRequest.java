package com.rjial.ngipen.auth;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class UserSelfUpdatedRequest {

    private String email;
    private String name;
    private String hp;
    private String address;
    private Level level;
    private String password;
    private Long id;

}
