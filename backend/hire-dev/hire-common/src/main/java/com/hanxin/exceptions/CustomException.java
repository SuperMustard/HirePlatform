package com.hanxin.exceptions;

import com.hanxin.result.CustomJSONResult;
import com.hanxin.result.ResponseStatusEnum;

public class CustomException extends  RuntimeException{
    private ResponseStatusEnum responseStatusEnum;

    public CustomException(ResponseStatusEnum responseStatusEnum) {
        super("error code is: " + responseStatusEnum.status() +
                "error message is: " + responseStatusEnum.msg());
        this.responseStatusEnum = responseStatusEnum;
    }

    public ResponseStatusEnum getResponseStatusEnum() {
        return responseStatusEnum;
    }

    public void setResponseStatusEnum(ResponseStatusEnum responseStatusEnum) {
        this.responseStatusEnum = responseStatusEnum;
    }
}
