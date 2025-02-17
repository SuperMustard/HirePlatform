package com.hanxin.api.feign;

import com.hanxin.result.CustomJSONResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("work-service") //declare the name of the service, we need to call
public interface WorkMicroServiceFeign {
    @PostMapping("/resume/init")
    public CustomJSONResult init(@RequestParam("userId") String userId);
}
