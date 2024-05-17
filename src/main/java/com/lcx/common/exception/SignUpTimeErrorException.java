package com.lcx.common.exception;

import com.fasterxml.jackson.databind.ser.std.StdKeySerializers;

public class SignUpTimeErrorException extends BaseException {
    public SignUpTimeErrorException() {

    }

    public SignUpTimeErrorException(String message) {
        super(message);
    }
}
