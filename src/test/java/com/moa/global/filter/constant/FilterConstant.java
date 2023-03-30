package com.moa.global.filter.constant;

import com.moa.global.auth.model.Claims;
import com.moa.global.auth.model.TokenMapping;

import java.util.Date;
import java.util.List;

public class FilterConstant {
    public static final TokenMapping tokens = new TokenMapping("access", "refresh");
    public static final TokenMapping notValidBothTokens = new TokenMapping("", "");
    public static final TokenMapping notValidRefreshTokens = new TokenMapping("access", "");
    public static final Claims claims = new Claims(1L, List.of("USER"), new Date());
}
