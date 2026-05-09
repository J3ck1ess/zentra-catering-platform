package com.zentra.common.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Authentication information stored in JWT
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthInfo {

    /**
     * Current login user id
     */
    private Long userId;

    /**
     * Current merchant id
     */
    private Long merchantId;

    /**
     * Current user type
     */
    private String userType;
}
