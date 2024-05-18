package com.lcx.common.exception.account;

import com.lcx.common.exception.BaseException;

public class AccountNotFoundException extends BaseException {

    public AccountNotFoundException() {}

    public AccountNotFoundException(String msg) {
        super(msg);
    }

}
