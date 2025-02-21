package com.hanxin;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class CFR2Config {

    @Value("${CFR2.endpoint}")
    private String endpoint;
    @Value("${CFR2.accountId}")
    private String accountId;
    @Value("${CFR2.bucketName}")
    private String bucketName;
    @Value("${CFR2.accessKey}")
    private String accessKey;
    @Value("${CFR2.secretKey}")
    private String secretKey;
    @Value("${CFR2.publicURL}")
    private String publicURL;

    @Bean
    public CFR2Utils creatCFR2Client() {
        return new CFR2Utils(endpoint, accountId, bucketName, accessKey, secretKey, publicURL);
    }
}
