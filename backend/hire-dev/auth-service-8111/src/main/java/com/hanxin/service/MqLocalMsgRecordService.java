package com.hanxin.service;

import com.hanxin.pojo.MqLocalMsgRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hanxin
 * @since 2025-02-13
 */
public interface MqLocalMsgRecordService extends IService<MqLocalMsgRecord> {
    public List<MqLocalMsgRecord> getBatchLocalMsgRecordList(List<String> msgIds);
}
