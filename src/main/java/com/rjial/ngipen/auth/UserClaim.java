package com.rjial.ngipen.auth;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
public class UserClaim {
    private String email;
    private String name;
    private String address;
    private String level;
}
