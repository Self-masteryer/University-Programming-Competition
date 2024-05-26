package com.lcx.common.exception.account;

import com.lcx.common.exception.BaseException;

public class UsernameExistsException extends BaseException {

    public UsernameExistsException() {
    }

    public UsernameExistsException(String message) {
        super(message);
    }
}
