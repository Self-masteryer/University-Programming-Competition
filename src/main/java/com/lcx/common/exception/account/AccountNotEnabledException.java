package com.lcx.common.exception.account;

import com.lcx.common.exception.BaseException;

public class AccountNotEnabledException extends BaseException {
    public AccountNotEnabledException() {}
    public AccountNotEnabledException(String message) {
        super(message);
    }
}
