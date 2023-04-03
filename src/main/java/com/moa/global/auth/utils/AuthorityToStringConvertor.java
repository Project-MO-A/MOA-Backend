package com.moa.global.auth.utils;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

public class AuthorityToStringConvertor {

    @SuppressWarnings("unchecked")
    public static List<String> convert(Collection<? extends GrantedAuthority> authorities) {
        return ((List<GrantedAuthority>) authorities).stream().map(GrantedAuthority::getAuthority).toList();
    }

    public static List<SimpleGrantedAuthority> reverseConvert(List<String> role) {
        return role.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }
}
