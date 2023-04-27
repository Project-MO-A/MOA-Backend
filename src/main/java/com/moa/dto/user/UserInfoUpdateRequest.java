package com.moa.dto.user;

public record UserInfoUpdateRequest(
        String name,
        String nickname,
        String currentPassword,
        String newPassword,
        String imageUrl
) {}
