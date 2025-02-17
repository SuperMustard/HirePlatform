package com.hanxin.pojo;

import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author hanxin
 * @since 2025-02-13
 */
public class MqLocalMsgRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String targetExchange;

    private String routingKey;

    private String msgContent;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTargetExchange() {
        return targetExchange;
    }

    public void setTargetExchange(String targetExchange) {
        this.targetExchange = targetExchange;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    @Override
    public String toString() {
        return "MqLocalMsgRecord{" +
        "id=" + id +
        ", targetExchange=" + targetExchange +
        ", routingKey=" + routingKey +
        ", msgContent=" + msgContent +
        ", createdTime=" + createdTime +
        ", updatedTime=" + updatedTime +
        "}";
    }
}
