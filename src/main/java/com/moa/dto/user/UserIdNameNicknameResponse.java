package com.moa.dto.user;

public record UserIdNameNicknameResponse(
        Long userId,
        String userName,
        String nickname
) {
}
