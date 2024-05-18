package com.lcx.common.exception.account;

import com.lcx.common.exception.BaseException;

public class PasswordErrorException extends BaseException {

    public PasswordErrorException() {}

    public PasswordErrorException(String message) {
        super(message);
    }
}
