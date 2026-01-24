package com.enterprise.workflow.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

/**
 * Identity Provider Configuration.
 * Supports multiple identity providers: Keycloak (default) and Ping Federation.
 * 
 * Usage:
 *   identity.provider=keycloak  (local/open-source)
 *   identity.provider=ping      (enterprise)
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "identity")
public class IdentityProviderConfig {

    /**
     * Identity provider type: 'keycloak' or 'ping'
     */
    private String provider = "keycloak";

    private KeycloakConfig keycloak = new KeycloakConfig();
    private PingConfig ping = new PingConfig();

    @Data
    public static class KeycloakConfig {
        private String serverUrl = "http://localhost:8180";
        private String realm = "workflow";
        private String clientId = "workflow-backend";
        private String clientSecret;
        private String adminUsername = "admin";
        private String adminPassword;
    }

    @Data
    public static class PingConfig {
        private String issuerUrl;
        private String clientId;
        private String clientSecret;
        private String authorizationEndpoint = "/as/authorization.oauth2";
        private String tokenEndpoint = "/as/token.oauth2";
        private String userInfoEndpoint = "/idp/userinfo.openid";
        private String jwksUri = "/pf/JWKS";
        private String endSessionEndpoint = "/idp/startSLO.ping";
        private String scimEndpoint = "/pf-ws/rest/scim/v2";
        private boolean mfaEnabled = true;
    }

    public boolean isKeycloak() {
        return "keycloak".equalsIgnoreCase(provider);
    }

    public boolean isPing() {
        return "ping".equalsIgnoreCase(provider);
    }
}
