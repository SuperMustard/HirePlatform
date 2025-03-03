package com.hanxin.pojo.vo;

import com.hanxin.pojo.Industry;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TopIndustryWithThirdListVO {
    private String topId;
    private List<Industry> thirdIndustryList;
}