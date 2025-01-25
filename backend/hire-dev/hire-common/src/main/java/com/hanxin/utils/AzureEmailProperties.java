package com.hanxin.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
@Component
@Data
@PropertySource("classpath:azureEmail.properties")
@ConfigurationProperties(prefix = "azure")
public class AzureEmailProperties {
    private String EndPoint;
    private String AccessKey;
}
