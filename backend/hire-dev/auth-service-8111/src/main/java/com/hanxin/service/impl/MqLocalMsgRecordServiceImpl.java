package com.hanxin.service.impl;

import com.hanxin.pojo.MqLocalMsgRecord;
import com.hanxin.mapper.MqLocalMsgRecordMapper;
import com.hanxin.service.MqLocalMsgRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hanxin
 * @since 2025-02-13
 */
@Service
public class MqLocalMsgRecordServiceImpl extends ServiceImpl<MqLocalMsgRecordMapper, MqLocalMsgRecord> implements MqLocalMsgRecordService {

    @Autowired
    private MqLocalMsgRecordMapper msgRecordMapper;

    @Override
    public List<MqLocalMsgRecord> getBatchLocalMsgRecordList(List<String> msgIds) {
        return msgRecordMapper.selectBatchIds(msgIds);
    }
}
