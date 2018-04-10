package com.ht.commonactivity.vo;

import java.io.Serializable;
import java.util.Map;

public class ModelStartVo implements Serializable{


    private static final long serialVersionUID = 7969576144352432139L;
    /**
     * 模型编码
     */
    private String modelCode;
    /**
     * 模型版本
     */
    private String modelVersion;
    /**
     * 业务系统编号
     */
    private String appCode;
    /**
     * 渠道号
     */
    private String channelNo;
    /**
     * 业务数据
     */
    private Map<String,Object> data;

    private String userId;

    public String getModelCode() {
        return modelCode;
    }

    public void setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public String getChannelNo() {
        return channelNo;
    }

    public void setChannelNo(String channelNo) {
        this.channelNo = channelNo;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
