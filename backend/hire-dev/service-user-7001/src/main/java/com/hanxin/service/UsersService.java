package com.hanxin.service;

import com.hanxin.pojo.Users;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hanxin.pojo.bo.ModifyUserBO;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author hanxin
 * @since 2025-01-09
 */
public interface UsersService {
    public void modifyUserInfo(ModifyUserBO userBO);

    public Users getById(String uid);

    /**
     * 查询企业下HR数量
     * @param companyId
     * @return
     */
    public Long getCountsByCompanyId(String companyId);

    /**
     * 更新用户的企业id（绑定公司与hr的关系）
     * @param hrUserId
     * @param realname
     * @param companyId
     */
    public void updateUserCompanyId(String hrUserId,
                                    String realname,
                                    String companyId);
}
