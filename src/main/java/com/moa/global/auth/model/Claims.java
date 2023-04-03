package com.moa.global.auth.model;

import java.util.Date;
import java.util.List;

public record Claims(Long userId, List<String> role, Date expiration) {}