package com.rjial.ngipen.auth;

import com.rjial.ngipen.common.DataResponse;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailResponse implements DataResponse {
    private User user;
}
