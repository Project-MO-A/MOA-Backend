package com.moa.dto.user;

import lombok.Builder;

@Builder
public record UserLogin(String name, String email) {
}
