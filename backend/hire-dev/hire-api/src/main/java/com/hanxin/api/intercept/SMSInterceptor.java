package com.hanxin.api.intercept;

import com.hanxin.base.BaseInfoProperties;
import com.hanxin.exceptions.ExceptionWrapper;
import com.hanxin.result.ResponseStatusEnum;
import com.hanxin.utils.IPUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class SMSInterceptor extends BaseInfoProperties implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userIp = IPUtil.getRequestIp(request);
        boolean ipExist = redis.keyIsExist(MOBILE_SMSCODE + ":" + userIp);

        if (ipExist) {
            log.error("短信发送频率过高");
            ExceptionWrapper.display(ResponseStatusEnum.SMS_NEED_WAIT_ERROR);
            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
