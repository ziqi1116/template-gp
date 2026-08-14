package com.gp.common.utils;

import com.gp.framework.security.LoginUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static LoginUserDetails getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUserDetails) {
            return (LoginUserDetails) authentication.getPrincipal();
        }
        return null;
    }

    public static Long getUserId() {
        LoginUserDetails user = getLoginUser();
        return user != null ? user.getUserId() : null;
    }

    public static String getUsername() {
        LoginUserDetails user = getLoginUser();
        return user != null ? user.getUsername() : null;
    }

    public static boolean isAdmin() {
        LoginUserDetails user = getLoginUser();
        return user != null && user.getUserId() != null && user.getUserId() == 1L;
    }

}
