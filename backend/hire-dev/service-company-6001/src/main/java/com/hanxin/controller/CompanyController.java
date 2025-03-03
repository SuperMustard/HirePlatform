package com.hanxin.controller;

import com.google.gson.Gson;
import com.hanxin.api.feign.UserInfoMicroServiceFeign;
import com.hanxin.base.BaseInfoProperties;
import com.hanxin.pojo.Company;
import com.hanxin.pojo.bo.CreateCompanyBO;
import com.hanxin.pojo.bo.ReviewCompanyBO;
import com.hanxin.pojo.vo.CompanySimpleVO;
import com.hanxin.pojo.vo.UsersVO;
import com.hanxin.result.CustomJSONResult;
import com.hanxin.service.CompanyService;
import com.hanxin.utils.JsonUtils;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("company")
public class CompanyController extends BaseInfoProperties {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private UserInfoMicroServiceFeign userInfoMicroServiceFeign;

    /**
     * 根据全称查询企业信息
     * @param fullName
     * @return
     */
    @PostMapping("getByFullName")
    public CustomJSONResult getByFullName(String fullName) {

        if (StringUtils.isBlank(fullName)) {
            return CustomJSONResult.error();
        }

        Company company = companyService.getByFullName(fullName);
        if (company == null) return CustomJSONResult.ok(null);

        CompanySimpleVO companySimpleVO = new CompanySimpleVO();
        BeanUtils.copyProperties(company, companySimpleVO);

        return CustomJSONResult.ok(companySimpleVO);
    }

    /**
     * 创建待审核的企业或者重新发起审核
     * @param createCompanyBO
     * @return
     */
    @PostMapping("createNewCompany")
    public CustomJSONResult createNewCompany(
            @RequestBody @Valid CreateCompanyBO createCompanyBO) {

        // TODO 课后自行校验 CreateCompanyBO

        String companyId = createCompanyBO.getCompanyId();
        String doCompanyId = "";
        if (StringUtils.isBlank(companyId)) {
            // 如果为空，则创建公司
            doCompanyId = companyService.createNewCompany(createCompanyBO);
        } else {
            // 否则不为空，则在原有的公司信息基础上做修改
            doCompanyId = companyService.resetNewCompany(createCompanyBO);
        }

        return CustomJSONResult.ok(doCompanyId);
    }

    /**
     * 获得企业信息
     * @param companyId
     * @param withHRCounts
     * @return
     */
    @PostMapping("getInfo")
    public CustomJSONResult getInfo(String companyId, boolean withHRCounts) {

        CompanySimpleVO companySimpleVO = getCompany(companyId);
        // 根据companyId获得旗下有多少个hr绑定，微服务的远程调用
        if (withHRCounts && companySimpleVO != null) {
            CustomJSONResult graceJSONResult =
                    userInfoMicroServiceFeign.getCountsByCompanyId(companyId);
            Object data = graceJSONResult.getData();
            Long hrCounts = Long.valueOf(data.toString());
            companySimpleVO.setHrCounts(hrCounts);
        }

        return CustomJSONResult.ok(companySimpleVO);
    }


    private CompanySimpleVO getCompany(String companyId) {
        if (StringUtils.isBlank(companyId)) return null;

        String companyJson = redis.get(REDIS_COMPANY_BASE_INFO + ":" + companyId);
        if (StringUtils.isBlank(companyJson)) {
            // 查询数据库
            Company company = companyService.getById(companyId);
            if (company == null) {
                return null;
            }

            CompanySimpleVO simpleVO = new CompanySimpleVO();
            BeanUtils.copyProperties(company, simpleVO);

            redis.set(REDIS_COMPANY_BASE_INFO + ":" + companyId,
                    new Gson().toJson(simpleVO),
                    1 * 60);
            return simpleVO;
        } else {
            // 不为空，直接转换对象
            return new Gson().fromJson(companyJson, CompanySimpleVO.class);
        }
    }

    /**
     * 提交企业的审核信息
     * @param reviewCompanyBO
     * @return
     */
    @PostMapping("goReviewCompany")
    @GlobalTransactional
    public CustomJSONResult goReviewCompany(
            @RequestBody @Valid ReviewCompanyBO reviewCompanyBO) {

        // 1. 微服务调用，绑定HR企业id
        CustomJSONResult result = userInfoMicroServiceFeign.bindingHRToCompany(
                reviewCompanyBO.getHrUserId(),
                reviewCompanyBO.getRealname(),
                reviewCompanyBO.getCompanyId());
        String hrMobile = result.getData().toString();
//        System.out.println(hrMobile);

        // 2. 保存审核信息，修改状态为[3：审核中（等待审核）]
        reviewCompanyBO.setHrMobile(hrMobile);
        companyService.commitReviewCompanyInfo(reviewCompanyBO);

        return CustomJSONResult.ok();
    }

    /**
     * 根据hr的用户id查询最新的企业信息
     * @param hrUserId
     * @return
     */
    @PostMapping("information")
    public CustomJSONResult information(String hrUserId) {

        UsersVO hrUser = getHRInfoVO(hrUserId);

        CompanySimpleVO company = getCompany(hrUser.getHrInWhichCompanyId());

        return CustomJSONResult.ok(company);
    }

    private UsersVO getHRInfoVO(String hrUserId) {
        CustomJSONResult jsonResult = userInfoMicroServiceFeign.get(hrUserId);
        Object data = jsonResult.getData();

        String json = JsonUtils.objectToJson(data);
        UsersVO hrUser = JsonUtils.jsonToPojo(json, UsersVO.class);
        return hrUser;
    }

}
