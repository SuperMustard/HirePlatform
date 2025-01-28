package com.hanxin.exceptions;

import com.hanxin.result.CustomJSONResult;
import com.hanxin.result.ResponseStatusEnum;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.SignatureException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public CustomJSONResult returnCustomException(CustomException e) {
        e.printStackTrace();
        return CustomJSONResult.exception(e.getResponseStatusEnum());
    }

    @ExceptionHandler({
            SignatureException.class,
            ExpiredJwtException.class,
            UnsupportedJwtException.class,
            MalformedJwtException.class,
            io.jsonwebtoken.security.SignatureException.class
    })
    @ResponseBody
    public CustomJSONResult returnSignatureException(SignatureException e) {
        e.printStackTrace();
        return CustomJSONResult.exception(ResponseStatusEnum.JWT_SIGNATURE_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public CustomJSONResult returnNotValidException(MethodArgumentNotValidException e) {
        BindingResult result  =  e.getBindingResult();
        Map<String, String> errors = getErrors(result);
        return CustomJSONResult.errorMap(errors);
    }

    public Map<String, String> getErrors(BindingResult result) {
        Map<String, String> map = new HashMap<>();
        List<FieldError> errorList = result.getFieldErrors();
        for (FieldError fe : errorList) {
            String field = fe.getField();
            String message = fe.getDefaultMessage();

            map.put(field, message);
        }

        return map;
    }
}
