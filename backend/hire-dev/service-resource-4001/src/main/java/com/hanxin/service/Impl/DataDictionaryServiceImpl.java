package com.hanxin.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.hanxin.base.BaseInfoProperties;
import com.hanxin.enums.YesOrNo;
import com.hanxin.exceptions.ExceptionWrapper;
import com.hanxin.mapper.DataDictionaryMapper;
import com.hanxin.pojo.DataDictionary;
import com.hanxin.pojo.bo.DataDictionaryBO;
import com.hanxin.result.ResponseStatusEnum;
import com.hanxin.service.DataDictionaryService;
import com.hanxin.utils.PagedGridResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DataDictionaryServiceImpl extends BaseInfoProperties implements DataDictionaryService {
    @Autowired
    private DataDictionaryMapper dataDictionaryMapper;

    @Transactional
    @Override
    public void createOrUpdateDataDictionary(DataDictionaryBO dataDictionaryBO) {

        DataDictionary dataDictionary = new DataDictionary();
        BeanUtils.copyProperties(dataDictionaryBO, dataDictionary);

        if (StringUtils.isBlank(dataDictionaryBO.getId())) {
            // 如果id为空，则新增新的数据字典项

            // 判断数据字典项是不可重复的
            DataDictionary ddExist =  dataDictionaryMapper.selectOne(
                    new QueryWrapper<DataDictionary>()
                            .eq("item_key", dataDictionaryBO.getItemKey())
                            .eq("item_value", dataDictionaryBO.getItemValue())
            );
            if (ddExist != null) {
                ExceptionWrapper.display(ResponseStatusEnum.DATA_DICT_EXIST_ERROR);
            }

            dataDictionaryMapper.insert(dataDictionary);
        } else {
            // 否则id不为空，则修改数据字典项
            dataDictionaryMapper.updateById(dataDictionary);
        }

    }

    @Override
    public PagedGridResult getDataDictListPaged(String typeName,
                                                String itemValue,
                                                Integer page,
                                                Integer limit) {

        PageHelper.startPage(page, limit);

        List<DataDictionary> ddList = dataDictionaryMapper.selectList(
                new QueryWrapper<DataDictionary>()
                        .like("type_name", typeName)
                        .like("item_value", itemValue)
                        .orderByAsc("type_code")
                        .orderByAsc("sort")
        );

        return setterPagedGrid(ddList, page);
    }

    @Override
    public DataDictionary getDataDictionary(String dictId) {
        return dataDictionaryMapper.selectById(dictId);
    }

    @Transactional
    @Override
    public void deleteDataDictionary(String dictId) {
        int res = dataDictionaryMapper.deleteById(dictId);
        if (res == 0 ) ExceptionWrapper.display(ResponseStatusEnum.DATA_DICT_DELETE_ERROR);
    }

    @Override
    public List<DataDictionary> getDataByCode(String typeCode) {

        List<DataDictionary> ddList = dataDictionaryMapper.selectList(
                new QueryWrapper<DataDictionary>()
                        .eq("type_code", typeCode)
                        .eq("enable", YesOrNo.YES.type)
                        .orderByAsc("sort")
        );

        return ddList;
    }

    @Override
    public List<DataDictionary> getItemsByKeys(String... keys) {

        List<DataDictionary> ddList = dataDictionaryMapper.selectList(
                new QueryWrapper<DataDictionary>()
                        .eq("enable", YesOrNo.YES.type)
                        .in("item_key", keys)
        );

        return ddList;
    }
}
