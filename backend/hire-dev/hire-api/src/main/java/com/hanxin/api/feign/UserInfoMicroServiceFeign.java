package com.hanxin.api.feign;

import com.hanxin.result.CustomJSONResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("user-service")
public interface UserInfoMicroServiceFeign {
    @PostMapping("/userinfo/getCountsByCompanyId")
    public CustomJSONResult getCountsByCompanyId(
            @RequestParam("companyId") String companyId);


    @PostMapping("/userinfo/bindingHRToCompany")
    public CustomJSONResult bindingHRToCompany(
            @RequestParam("hrUserId") String hrUserId,
            @RequestParam("realname") String realname,
            @RequestParam("companyId") String companyId);

    @PostMapping("/userinfo/get")
    public CustomJSONResult get(@RequestParam("userId") String userId);
}
