package com.hanxin.exceptions;

import com.hanxin.result.ResponseStatusEnum;

public class ExceptionWrapper {
    public static void display(ResponseStatusEnum responseStatusEnum) {
        throw new CustomException(responseStatusEnum);
    }
}
