package com.hanxin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hanxin.pojo.Industry;
import com.hanxin.pojo.vo.TopIndustryWithThirdListVO;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 行业表 Mapper 接口
 * </p>
 *
 * @author hanxin
 * @since 2025-01-09
 */
@Repository
public interface IndustryMapperCustom {
    public List<Industry> getThirdIndustryByTop(
            @Param("paramMap") Map<String, Object> map);

    public String getTopIndustryId(
            @Param("paramMap") Map<String, Object> map);

    public List<TopIndustryWithThirdListVO> getAllThirdIndustryList();
}
