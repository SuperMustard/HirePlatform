package com.hanxin.mapper;

import com.hanxin.pojo.vo.CompanyInfoVO;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import java.util.List;

@Repository
public interface CompanyMapperCustom {

    public List<CompanyInfoVO> queryCompanyList(
            @Param("paramMap") Map<String, Object> map);


    public CompanyInfoVO getCompanyInfo(
            @Param("paramMap") Map<String, Object> map);
}