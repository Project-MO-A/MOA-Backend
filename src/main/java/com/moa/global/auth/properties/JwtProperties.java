package com.moa.global.auth.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties (String secretKey, Map<String, TokenInfo> info){
    public int getExpiration(String tokenFlag) {
        return info.get(tokenFlag).expiration();
    }

    public String getHeader(String tokenFlag) {
        return info.get(tokenFlag).header();
    }

    public record TokenInfo (int expiration, String header){
        public TokenInfo(int expiration, String header) {
            this.expiration = expiration * 1000;
            this.header = header;
        }
    }
}
