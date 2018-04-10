package com.ht.commonactivity.common;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

public class RpcActExcuteTask implements Serializable {

    private static final long serialVersionUID = 1L;

    private String modelName;
    private String modelVersion;
    private String modelStatus;

    /**
     * 主键
     */
    private Long id;
    /**
     * 批次号，验证任务调用时存在
     */
    private Long batchId;
    /**
     * 模型版本ID
     */
    private Long procReleaseId;
    /**
     * 流程运行实例ID
     */
    private String procInstId;
    /**
     * 任务状态，0-待执行，1-执行结束，2-执行异常
     */
    private String status;
    /**
     * 任务类型，0-验证任务，1-业务系统调用
     */
    private String type;
    /**
     * 入参
     */
    private String inParamter;
    /**
     * 出参
     */
    private String outParamter;
    /**
     * 花费时间
     */
    private Long spendTime;
    /**
     * 备注
     */
    private String remark;
    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 创建用户
     */
    private String createUser;
    /**
     * 结束时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public Long getProcReleaseId() {
        return procReleaseId;
    }

    public void setProcReleaseId(Long procReleaseId) {
        this.procReleaseId = procReleaseId;
    }

    public String getProcInstId() {
        return procInstId;
    }

    public void setProcInstId(String procInstId) {
        this.procInstId = procInstId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInParamter() {
        return inParamter;
    }

    public void setInParamter(String inParamter) {
        this.inParamter = inParamter;
    }

    public String getOutParamter() {
        return outParamter;
    }

    public void setOutParamter(String outParamter) {
        this.outParamter = outParamter;
    }

    public Long getSpendTime() {
        return spendTime;
    }

    public void setSpendTime(Long spendTime) {
        this.spendTime = spendTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public String getModelStatus() {
        return modelStatus;
    }

    public void setModelStatus(String modelStatus) {
        this.modelStatus = modelStatus;
    }
}
