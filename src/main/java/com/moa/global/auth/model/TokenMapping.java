package com.moa.global.auth.model;

import lombok.Builder;

@Builder
public record TokenMapping(String accessToken, String refreshToken) {
}