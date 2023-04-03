package com.moa.global.auth.model;

import lombok.Builder;

@Builder
public record TokenMapping(String accessToken, String refreshToken) {
    public boolean hasNoAccessToken() {
        return this.accessToken.equals("");
    }

    public boolean hasNoRefreshToken() {
        return this.refreshToken.equals("");
    }
}