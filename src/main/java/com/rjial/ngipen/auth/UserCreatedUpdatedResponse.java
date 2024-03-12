package com.rjial.ngipen.auth;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Data
@Getter
public class UserCreatedUpdatedResponse {

    private String email;
    private String name;
    private String hp;
    private String address;

    public UserCreatedUpdatedResponse(@NonNull User user) {
        this.email = user.getEmail();
        this.name = user.getName();
        this.hp = user.getHp();
        this.address = user.getAddress();
    }
}
