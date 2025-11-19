package com.xyex.shared.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Session utility for retrieving current user information
 */
public class SessionUtil {

    /**
     * Get the current user's account/username
     * @return current user account, or null if not authenticated
     */
    public static String getUserAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername();
            } else if (principal instanceof String) {
                return (String) principal;
            }
        }
        return null;
    }

    /**
     * Get the current user's display name
     * @return current user name, or null if not authenticated
     */
    public static String getUserName() {
        // If you have a custom UserDetails implementation with a name field,
        // you can extract it here. For now, we return the account as fallback.
        return getUserAccount();
    }
}
