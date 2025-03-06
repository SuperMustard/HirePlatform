package com.hanxin.controller;

import com.hanxin.base.BaseInfoProperties;
import com.hanxin.pojo.DataDictionary;
import com.hanxin.pojo.bo.DataDictionaryBO;
import com.hanxin.pojo.bo.QueryDictItemsBO;
import com.hanxin.pojo.vo.CompanyPointsVO;
import com.hanxin.result.CustomJSONResult;
import com.hanxin.service.DataDictionaryService;
import com.hanxin.utils.GsonUtils;
import com.hanxin.utils.PagedGridResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

@RestController
@RequestMapping("dataDict")
public class DataDictController extends BaseInfoProperties {
    @Autowired
    private DataDictionaryService dictionaryService;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    private static final String DDKEY_PREFIX = DATA_DICTIONARY_LIST_TYPECODE + ":";

    @PostMapping("app/getItemsByKeys")
    public CustomJSONResult getItemsByKeys(
            @RequestBody QueryDictItemsBO itemsBO)
            throws Exception {

        CompanyPointsVO list = new CompanyPointsVO();

        CompletableFuture<List<DataDictionary>> advantageFuture = CompletableFuture.supplyAsync(() -> {
            String advantage[] = itemsBO.getAdvantage();
            List<DataDictionary> advantageList = dictionaryService.getItemsByKeys(advantage);
            list.setAdvantageList(advantageList);
            return advantageList;
        }, threadPoolExecutor);

        CompletableFuture<List<DataDictionary>> benefitsFuture = CompletableFuture.supplyAsync(() -> {
            String benefits[] = itemsBO.getBenefits();
            List<DataDictionary> benefitsList = dictionaryService.getItemsByKeys(benefits);
            list.setBenefitsList(benefitsList);
            return benefitsList;
        }, threadPoolExecutor);

        CompletableFuture<List<DataDictionary>> bonusFuture = CompletableFuture.supplyAsync(() -> {
            String bonus[] = itemsBO.getBonus();
            List<DataDictionary> bonusList = dictionaryService.getItemsByKeys(bonus);
            list.setBonusList(bonusList);
            return bonusList;
        }, threadPoolExecutor);

        CompletableFuture<List<DataDictionary>> subsidyFuture = CompletableFuture.supplyAsync(() -> {
            String subsidy[] = itemsBO.getSubsidy();
            List<DataDictionary> subsidyList = dictionaryService.getItemsByKeys(subsidy);
            list.setSubsidyList(subsidyList);
            return subsidyList;
        }, threadPoolExecutor);

        CompletableFuture allOfFuture = CompletableFuture
                .allOf(advantageFuture,
                        benefitsFuture,
                        bonusFuture,
                        subsidyFuture);
        allOfFuture.get();

        return CustomJSONResult.ok(list);
    }

    @PostMapping("app/getItemsByKeys2")
    public CustomJSONResult getItemsByKeys2(@RequestBody QueryDictItemsBO itemsBO) {

        String advantage[] = itemsBO.getAdvantage();
        String benefits[] = itemsBO.getBenefits();
        String bonus[] = itemsBO.getBonus();
        String subsidy[] = itemsBO.getSubsidy();

        List<DataDictionary> advantageList = dictionaryService.getItemsByKeys(advantage);
        List<DataDictionary> benefitsList = dictionaryService.getItemsByKeys(benefits);
        List<DataDictionary> bonusList = dictionaryService.getItemsByKeys(bonus);
        List<DataDictionary> subsidyList = dictionaryService.getItemsByKeys(subsidy);

        CompanyPointsVO list = new CompanyPointsVO();
        list.setAdvantageList(advantageList);
        list.setBenefitsList(benefitsList);
        list.setBonusList(bonusList);
        list.setSubsidyList(subsidyList);

        return CustomJSONResult.ok(list);
    }

    /**
     * 根据字典码获得该分类下的所有数据字典项的列表
     * @param typeCode
     * @return
     */
    @PostMapping("app/getDataByCode")
    public CustomJSONResult getDataByCode(String typeCode) {

        if (StringUtils.isBlank(typeCode)) {
            return CustomJSONResult.error();
        }

        String ddkey = DDKEY_PREFIX + typeCode;

        String ddListStr = redis.get(ddkey);
        List<DataDictionary> list = null;
        if (StringUtils.isNotBlank(ddListStr)) {
            list = GsonUtils.stringToListAnother(ddListStr, DataDictionary.class);
        }

        // 只从redis中查询，如果没有就没有，也不需要从数据库中查询，完全避免缓存的穿透击穿雪崩问题
//        List<DataDictionary> list = dictionaryService.getDataByCode(typeCode);
        return CustomJSONResult.ok(list);
    }

    /**
     * 创建数据字典
     * @param dataDictionaryBO
     * @return
     */
    @PostMapping("create")
    public CustomJSONResult create(
            @RequestBody @Valid DataDictionaryBO dataDictionaryBO) {

        // TODO dataDictionaryBO 校验课后自行完成

        dictionaryService.createOrUpdateDataDictionary(dataDictionaryBO);
        return CustomJSONResult.ok();
    }

    @PostMapping("list")
    public CustomJSONResult list(String typeName,
                                String itemValue,
                                Integer page,
                                Integer limit) {

        if (page == null) page = 1;
        if (limit == null) page = 10;

        PagedGridResult listResult = dictionaryService.getDataDictListPaged(
                typeName,
                itemValue,
                page,
                limit);
        return CustomJSONResult.ok(listResult);
    }

    /**
     * 修改数据字典
     * @param dataDictionaryBO
     * @return
     */
    @PostMapping("modify")
    public CustomJSONResult modify(
            @RequestBody @Valid DataDictionaryBO dataDictionaryBO) {

        if (StringUtils.isBlank(dataDictionaryBO.getId())) {
            return CustomJSONResult.error();
        }

        dictionaryService.createOrUpdateDataDictionary(dataDictionaryBO);
        return CustomJSONResult.ok();
    }

    /**
     * 根据id查询数据字典某一项
     * @param dictId
     * @return
     */
    @PostMapping("item")
    public CustomJSONResult item(String dictId) {
        DataDictionary dd = dictionaryService.getDataDictionary(dictId);
        return CustomJSONResult.ok(dd);
    }

    /**
     * 删除数据字典
     * @param dictId
     * @return
     */
    @PostMapping("delete")
    public CustomJSONResult delete(String dictId) {
        dictionaryService.deleteDataDictionary(dictId);
        return CustomJSONResult.ok();
    }
}
