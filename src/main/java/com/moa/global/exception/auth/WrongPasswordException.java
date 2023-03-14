package com.moa.global.exception.auth;


public class WrongPasswordException extends CustomAuthException{
    public WrongPasswordException() {
        super("잘못된 비밀번호입니다.");
    }
}
