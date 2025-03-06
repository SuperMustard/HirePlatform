package com.hanxin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.hanxin.base.BaseInfoProperties;
import com.hanxin.enums.CompanyReviewStatus;
import com.hanxin.enums.YesOrNo;
import com.hanxin.exceptions.ExceptionWrapper;
import com.hanxin.mapper.CompanyMapper;
import com.hanxin.mapper.CompanyMapperCustom;
import com.hanxin.mapper.CompanyPhotoMapper;
import com.hanxin.pojo.Company;
import com.hanxin.pojo.CompanyPhoto;
import com.hanxin.pojo.bo.CreateCompanyBO;
import com.hanxin.pojo.bo.ModifyCompanyInfoBO;
import com.hanxin.pojo.bo.QueryCompanyBO;
import com.hanxin.pojo.bo.ReviewCompanyBO;
import com.hanxin.pojo.vo.CompanyInfoVO;
import com.hanxin.result.ResponseStatusEnum;
import com.hanxin.service.CompanyService;
import com.hanxin.utils.PagedGridResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CompanyServiceImpl extends BaseInfoProperties implements CompanyService {
    @Autowired
    private CompanyMapper companyMapper;

    @Autowired
    private CompanyMapperCustom companyMapperCustom;

    @Autowired
    private CompanyPhotoMapper companyPhotoMapper;

    @Override
    public Company getByFullName(String fullName) {

        Company tempCompany = companyMapper.selectOne(
                new QueryWrapper<Company>()
                        .eq("company_name", fullName) //the column name should be the name in database not the name in company class
        );

        return tempCompany;
    }

    @Transactional
    @Override
    public String createNewCompany(CreateCompanyBO createCompanyBO) {

        Company newCompany = new Company();

        BeanUtils.copyProperties(createCompanyBO, newCompany);

        newCompany.setIsVip(YesOrNo.NO.type);
        newCompany.setReviewStatus(CompanyReviewStatus.NOTHING.type);
        newCompany.setCreatedTime(LocalDateTime.now());
        newCompany.setUpdatedTime(LocalDateTime.now());

        companyMapper.insert(newCompany);

        return newCompany.getId();
    }

    @Transactional
    @Override
    public String resetNewCompany(CreateCompanyBO createCompanyBO) {

        Company newCompany = new Company();

        BeanUtils.copyProperties(createCompanyBO, newCompany);

        newCompany.setId(createCompanyBO.getCompanyId());
        newCompany.setReviewStatus(CompanyReviewStatus.NOTHING.type);
        newCompany.setUpdatedTime(LocalDateTime.now());

        companyMapper.updateById(newCompany);

        return createCompanyBO.getCompanyId();
    }

    @Override
    public Company getById(String id) {
        return companyMapper.selectById(id);
    }

    @Transactional
    @Override
    public void commitReviewCompanyInfo(ReviewCompanyBO reviewCompanyBO) {

        Company pendingCompany = new Company();
        pendingCompany.setId(reviewCompanyBO.getCompanyId());

        pendingCompany.setReviewStatus(CompanyReviewStatus.REVIEW_ING.type);
        pendingCompany.setReviewReplay(""); // 如果有内容，则重置覆盖之前的审核意见
        pendingCompany.setAuthLetter(reviewCompanyBO.getAuthLetter());

        pendingCompany.setCommitUserId(reviewCompanyBO.getHrUserId());
        pendingCompany.setCommitUserMobile(reviewCompanyBO.getHrMobile());
        pendingCompany.setCommitDate(LocalDate.now());

        pendingCompany.setUpdatedTime(LocalDateTime.now());

        companyMapper.updateById(pendingCompany);
    }

    @Override
    public PagedGridResult queryCompanyListPaged(QueryCompanyBO companyBO,
                                                 Integer page,
                                                 Integer limit) {

        PageHelper.startPage(page, limit);

        Map<String, Object> map = new HashMap<>();
        map.put("companyName", companyBO.getCompanyName());
        map.put("realName", companyBO.getCommitUser());
        map.put("reviewStatus", companyBO.getReviewStatus());
        map.put("commitDateStart", companyBO.getCommitDateStart());
        map.put("commitDateEnd", companyBO.getCommitDateEnd());

        List<CompanyInfoVO> list = companyMapperCustom.queryCompanyList(map);

        return setterPagedGrid(list, page);
    }

    @Override
    public CompanyInfoVO getCompanyInfo(String companyId) {

        Map<String, Object> map = new HashMap<>();
        map.put("companyId", companyId);

        CompanyInfoVO companyInfo = companyMapperCustom.getCompanyInfo(map);
        return companyInfo;
    }

    @Transactional
    @Override
    public void updateReviewInfo(ReviewCompanyBO reviewCompanyBO) {

        Company pendingCompany = new Company();
        pendingCompany.setId(reviewCompanyBO.getCompanyId());

        pendingCompany.setReviewStatus(reviewCompanyBO.getReviewStatus());
        pendingCompany.setReviewReplay(reviewCompanyBO.getReviewReplay());

        pendingCompany.setUpdatedTime(LocalDateTime.now());

        companyMapper.updateById(pendingCompany);
    }

    @Transactional
    @Override
    public void modifyCompanyInfo(ModifyCompanyInfoBO companyInfoBO) {

        String companyId = companyInfoBO.getCompanyId();
        if (StringUtils.isBlank(companyId)) {
            ExceptionWrapper.display(ResponseStatusEnum.COMPANY_INFO_UPDATED_ERROR);
        }

        Company pendingCompany = new Company();
        pendingCompany.setId(companyInfoBO.getCompanyId());
        pendingCompany.setUpdatedTime(LocalDateTime.now());

        BeanUtils.copyProperties(companyInfoBO, pendingCompany);

        companyMapper.updateById(pendingCompany);

        // 修改以后，删除企业的缓存信息
        redis.del(REDIS_COMPANY_MORE_INFO + ":" + companyId);
        redis.del(REDIS_COMPANY_BASE_INFO + ":" + companyId);
    }

    @Transactional
    @Override
    public void savePhotos(ModifyCompanyInfoBO companyInfoBO) {

        String companyId = companyInfoBO.getCompanyId();

        CompanyPhoto companyPhoto = new CompanyPhoto();
        companyPhoto.setCompanyId(companyId);
        companyPhoto.setPhotos(companyInfoBO.getPhotos());

        // 判断企业相册是否存在，不存在则插入，存在则修改
        CompanyPhoto tempPhoto = getPhotos(companyId);
        if (tempPhoto == null) {
            companyPhotoMapper.insert(companyPhoto);
        } else {
            companyPhotoMapper.update(companyPhoto,
                    new UpdateWrapper<CompanyPhoto>()
                            .eq("company_id", companyId)
            );
        }
    }

    @Override
    public CompanyPhoto getPhotos(String companyId) {
        return companyPhotoMapper.selectOne(
                new QueryWrapper<CompanyPhoto>()
                        .eq("company_id", companyId)
        );
    }
}
