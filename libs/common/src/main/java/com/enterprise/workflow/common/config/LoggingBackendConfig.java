package com.enterprise.workflow.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

/**
 * Logging & Observability Configuration.
 * Supports multiple backends: OpenSearch (default/OSS) and Splunk (enterprise).
 * 
 * Usage:
 *   logging.backend=opensearch  (local/open-source)
 *   logging.backend=splunk      (enterprise)
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "observability.logging")
public class LoggingBackendConfig {

    /**
     * Logging backend type: 'opensearch', 'elasticsearch', or 'splunk'
     */
    private String backend = "opensearch";

    private OpenSearchConfig opensearch = new OpenSearchConfig();
    private SplunkConfig splunk = new SplunkConfig();

    @Data
    public static class OpenSearchConfig {
        private String host = "localhost";
        private int port = 9200;
        private String scheme = "http";
        private String username;
        private String password;
        private String indexPrefix = "workflow";
        private boolean sslEnabled = false;
    }

    @Data
    public static class SplunkConfig {
        private String hecUrl;
        private String hecToken;
        private String index = "workflow_events";
        private String source = "workflow-platform";
        private String sourceType = "_json";
        private boolean sslVerify = true;
        private int batchSize = 100;
        private long flushIntervalMs = 5000;
        
        // Splunk Enterprise Security (ES) integration
        private boolean siemEnabled = false;
        private String siemIndex = "notable";
    }

    public boolean isOpenSearch() {
        return "opensearch".equalsIgnoreCase(backend) || "elasticsearch".equalsIgnoreCase(backend);
    }

    public boolean isSplunk() {
        return "splunk".equalsIgnoreCase(backend);
    }
}
