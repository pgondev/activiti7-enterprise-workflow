package com.enterprise.workflow.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Utility for accessing current user information from security context.
 */
@Component
public class SecurityUtils {

    /**
     * Get the current authenticated user's ID.
     */
    public Optional<String> getCurrentUserId() {
        return getJwt().map(jwt -> jwt.getClaimAsString("sub"));
    }

    /**
     * Get the current authenticated user's username.
     */
    public Optional<String> getCurrentUsername() {
        return getJwt().map(jwt -> jwt.getClaimAsString("preferred_username"));
    }

    /**
     * Get the current authenticated user's email.
     */
    public Optional<String> getCurrentUserEmail() {
        return getJwt().map(jwt -> jwt.getClaimAsString("email"));
    }

    /**
     * Get the current authenticated user's full name.
     */
    public Optional<String> getCurrentUserFullName() {
        return getJwt().map(jwt -> jwt.getClaimAsString("name"));
    }

    /**
     * Get the current authenticated user's roles.
     */
    @SuppressWarnings("unchecked")
    public List<String> getCurrentUserRoles() {
        return getJwt()
                .map(jwt -> {
                    Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
                    if (realmAccess != null && realmAccess.containsKey("roles")) {
                        return (List<String>) realmAccess.get("roles");
                    }
                    return Collections.<String>emptyList();
                })
                .orElse(Collections.emptyList());
    }

    /**
     * Get the current authenticated user's groups.
     */
    @SuppressWarnings("unchecked")
    public List<String> getCurrentUserGroups() {
        return getJwt()
                .map(jwt -> {
                    Object groups = jwt.getClaim("groups");
                    if (groups instanceof List) {
                        return (List<String>) groups;
                    }
                    return Collections.<String>emptyList();
                })
                .orElse(Collections.emptyList());
    }

    /**
     * Get the current tenant ID.
     */
    public Optional<String> getCurrentTenantId() {
        return getJwt().map(jwt -> jwt.getClaimAsString("tenant_id"));
    }

    /**
     * Check if the current user has a specific role.
     */
    public boolean hasRole(String role) {
        return getCurrentUserRoles().contains(role);
    }

    /**
     * Check if the current user belongs to a specific group.
     */
    public boolean belongsToGroup(String group) {
        return getCurrentUserGroups().contains(group);
    }

    /**
     * Check if user is authenticated.
     */
    public boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && !(auth.getPrincipal() instanceof String);
    }

    /**
     * Get the JWT from the security context.
     */
    private Optional<Jwt> getJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            return Optional.of(jwtAuth.getToken());
        }
        return Optional.empty();
    }
}
