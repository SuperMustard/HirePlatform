package com.hanxin.service;

import com.hanxin.pojo.Company;
import com.hanxin.pojo.CompanyPhoto;
import com.hanxin.pojo.bo.CreateCompanyBO;
import com.hanxin.pojo.bo.ModifyCompanyInfoBO;
import com.hanxin.pojo.bo.QueryCompanyBO;
import com.hanxin.pojo.bo.ReviewCompanyBO;
import com.hanxin.pojo.vo.CompanyInfoVO;
import com.hanxin.utils.PagedGridResult;

public interface CompanyService {

    /**
     * 根据企业全称查询企业信息
     * @param fullName
     * @return
     */
    public Company getByFullName(String fullName);

    /**
     * 创建企业（状态：未发起审核）
     * @param createCompanyBO
     * @return
     */
    public String createNewCompany(CreateCompanyBO createCompanyBO);

    /**
     * 重启发起审核，修改企业（状态：未发起审核）
     * @param createCompanyBO
     * @return
     */
    public String resetNewCompany(CreateCompanyBO createCompanyBO);

    /**
     * 根据企业id获得企业信息
     * @param id
     * @return
     */
    public Company getById(String id);

    /**
     * 审核提交的企业信息
     * @param reviewCompanyBO
     */
    public void commitReviewCompanyInfo(ReviewCompanyBO reviewCompanyBO);

    /**
     * admin在运营平台查询企业列表
     * @param companyBO
     * @param page
     * @param limit
     * @return
     */
    public PagedGridResult queryCompanyListPaged(QueryCompanyBO companyBO,
                                                 Integer page,
                                                 Integer limit);

    /**
     * 根据企业id查询数据库获得最新企业信息
     * @param companyId
     * @return
     */
    public CompanyInfoVO getCompanyInfo(String companyId);

    /**
     * 更新审核后的信息
     * @param reviewCompanyBO
     */
    public void updateReviewInfo(ReviewCompanyBO reviewCompanyBO);

    /**
     * 修改企业信息
     * @param companyInfoBO
     */
    public void modifyCompanyInfo(ModifyCompanyInfoBO companyInfoBO);

    /**
     * 修改企业相册
     * @param companyInfoBO
     */
    public void savePhotos(ModifyCompanyInfoBO companyInfoBO);

    /**
     * 根据企业id获得相册内容
     * @param companyId
     * @return
     */
    public CompanyPhoto getPhotos(String companyId);
}


