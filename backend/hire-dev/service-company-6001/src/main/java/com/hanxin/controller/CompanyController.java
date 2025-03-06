package com.hanxin.controller;

import com.google.gson.Gson;
import com.hanxin.api.feign.UserInfoMicroServiceFeign;
import com.hanxin.api.intercept.JWTCurrentUserInterceptor;
import com.hanxin.base.BaseInfoProperties;
import com.hanxin.enums.CompanyReviewStatus;
import com.hanxin.exceptions.ExceptionWrapper;
import com.hanxin.pojo.Company;
import com.hanxin.pojo.Users;
import com.hanxin.pojo.bo.CreateCompanyBO;
import com.hanxin.pojo.bo.ModifyCompanyInfoBO;
import com.hanxin.pojo.bo.QueryCompanyBO;
import com.hanxin.pojo.bo.ReviewCompanyBO;
import com.hanxin.pojo.vo.CompanyInfoVO;
import com.hanxin.pojo.vo.CompanySimpleVO;
import com.hanxin.pojo.vo.UsersVO;
import com.hanxin.result.CustomJSONResult;
import com.hanxin.result.ResponseStatusEnum;
import com.hanxin.service.CompanyService;
import com.hanxin.utils.JsonUtils;
import com.hanxin.utils.PagedGridResult;
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

    /**
     * saas获得企业基础信息
     * @return
     */
    @PostMapping("info")
    public CustomJSONResult info() {

        Users currentUser = JWTCurrentUserInterceptor.currentUser.get();

        CompanySimpleVO companyInfo = getCompany(currentUser.getHrInWhichCompanyId());

        return CustomJSONResult.ok(companyInfo);
    }

    /**
     * saas获得查询企业详情
     * @return
     */
    @PostMapping("saas/moreInfo")
    public CustomJSONResult saasMoreInfo() {

        Users currentUser = JWTCurrentUserInterceptor.currentUser.get();

        CompanyInfoVO companyInfo = getCompanyMoreInfo(
                currentUser.getHrInWhichCompanyId());

        return CustomJSONResult.ok(companyInfo);
    }

    /**
     * app用户端获得查询企业详情
     * @return
     */
    @PostMapping("moreInfo")
    public CustomJSONResult moreInfo(String companyId) {
        CompanyInfoVO companyInfo = getCompanyMoreInfo(companyId);
        return CustomJSONResult.ok(companyInfo);
    }

    private CompanyInfoVO getCompanyMoreInfo(String companyId) {
        if (StringUtils.isBlank(companyId)) return null;

        String companyJson = redis.get(REDIS_COMPANY_MORE_INFO + ":" + companyId);
        if (StringUtils.isBlank(companyJson)) {
            // 查询数据库
            Company company = companyService.getById(companyId);
            if (company == null) {
                return null;
            }

            CompanyInfoVO infoVO = new CompanyInfoVO();
            BeanUtils.copyProperties(company, infoVO);

            redis.set(REDIS_COMPANY_MORE_INFO + ":" + companyId,
                    new Gson().toJson(infoVO),
                    1 * 60);
            return infoVO;
        } else {
            // 不为空，直接转换对象
            return new Gson().fromJson(companyJson, CompanyInfoVO.class);
        }
    }

    /**
     * 维护企业信息
     * @param companyInfoBO
     * @return
     */
    @PostMapping("modify")
    public CustomJSONResult modify(
            @RequestBody ModifyCompanyInfoBO companyInfoBO) {

        // 判断当前用户绑定的企业，是否和修改的企业一致，如果不一致，则异常
        checkUser(companyInfoBO.getCurrentUserId(), companyInfoBO.getCompanyId());

        // 修改企业信息
        companyService.modifyCompanyInfo(companyInfoBO);

        // 企业相册信息的保存
        if (StringUtils.isNotBlank(companyInfoBO.getPhotos())) {
            companyService.savePhotos(companyInfoBO);
        }

        return CustomJSONResult.ok();
    }

    /**
     * 获得企业相册内容
     * @param companyId
     * @return
     */
    @PostMapping("getPhotos")
    public CustomJSONResult getPhotos(String companyId) {
        return CustomJSONResult.ok(companyService.getPhotos(companyId));
    }

    /**
     * 获得企业相册内容
     * @return
     */
    @PostMapping("saas/getPhotos")
    public CustomJSONResult getPhotosSaas() {
        String companyId = JWTCurrentUserInterceptor.currentUser.get()
                .getHrInWhichCompanyId();
        return CustomJSONResult.ok(companyService.getPhotos(companyId));
    }

    /**
     * 校验企业下的HR是否OK
     * @param currentUserId
     * @param companyId
     */
    private void checkUser(String currentUserId, String companyId) {

        if (StringUtils.isBlank(currentUserId)) {
            ExceptionWrapper.display(ResponseStatusEnum.COMPANY_INFO_UPDATED_ERROR);
        }

        UsersVO hrUser = getHRInfoVO(currentUserId);
        if (hrUser != null && !hrUser.getHrInWhichCompanyId().equalsIgnoreCase(companyId)) {
            ExceptionWrapper.display(ResponseStatusEnum.COMPANY_INFO_UPDATED_NO_AUTH_ERROR);
        }
    }

    // **************************** 以上为用户端所使用 ****************************

    // **************************** 以下为运营平台所使用 ****************************


    @PostMapping("admin/getCompanyList")
    public CustomJSONResult adminGetCompanyList(
            @RequestBody @Valid QueryCompanyBO companyBO,
            Integer page,
            Integer limit) {

        if (page == null) page = 1;
        if (limit == null) limit = 10;

        PagedGridResult gridResult = companyService.queryCompanyListPaged(
                companyBO,
                page,
                limit);
        return CustomJSONResult.ok(gridResult);
    }

    /**
     * 根据企业id获得最新企业数据
     * @param companyId
     * @return
     */
    @PostMapping("admin/getCompanyInfo")
    public CustomJSONResult getCompanyInfo(String companyId) {

        CompanyInfoVO companyInfo = companyService.getCompanyInfo(companyId);

        return CustomJSONResult.ok(companyInfo);
    }

    /**
     * 企业审核通过，用户成为HR角色
     * @param reviewCompanyBO
     * @return
     */
    @PostMapping("admin/doReview")
    public CustomJSONResult getCompanyInfo(
            @RequestBody @Valid ReviewCompanyBO reviewCompanyBO) {

        // 1. 审核企业
        companyService.updateReviewInfo(reviewCompanyBO);

        // 2. 如果审核成功，则更新用户角色成为HR
        if (reviewCompanyBO.getReviewStatus() == CompanyReviewStatus.SUCCESSFUL.type) {
            userInfoMicroServiceFeign.changeUserToHR(reviewCompanyBO.getHrUserId());
        }

        // 3. 清除用户端的企业缓存
        redis.del(REDIS_COMPANY_BASE_INFO + ":" + reviewCompanyBO.getCompanyId());

        return CustomJSONResult.ok();
    }
}
