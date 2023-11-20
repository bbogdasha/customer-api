package com.bogdan.fullstackproject.exception;

import java.time.LocalDateTime;

public record ApiError(
        String path,
        String message,
        int statusCode,
        LocalDateTime localDateTime
) {
}
