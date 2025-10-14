package com.nationwide.nationwide_server.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "upload")
@Data
public class UploadProperties {
    private String memberDir;
    private String communityDir;
    private String rootDir;
}
