package com.moa.dto.reply;

import jakarta.validation.constraints.NotBlank;

public record ReplyUpdateRequest(
        @NotBlank String content
) {
}
