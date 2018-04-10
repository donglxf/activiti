package com.ht.commonactivity.common;

import java.io.Serializable;

public class RpcModelReleaseInfo implements Serializable{

    /**
     * 主键
     */
    private Long id;
    /**
     * 模型定义ID，与 ACT_RE_PROCDEF.ID_ 关联,ACT_RE_PROCDEF 表中有模型部署id
     */
    private String modelProcdefId;
    /**
     * 模型名称
     */
    private String modelName;
    /**
     * 模型版本
     */
    private String modelVersion;
    /**
     * 模型分类
     */
    private String modelCategory;
    /**
     * 版本类型，0-测试版，1-正式版
     */
    private String versionType;
    /**
     * 是否验证通过： 0-待验证，1-验证通过，2-验证不通过；默认为0;
     */
    private String isValidate;
    /**
     * 是否审核通过：0-待审核，1-审核通过，2-审核不通过；默认为0;
     */
    private String isApprove;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModelProcdefId() {
        return modelProcdefId;
    }

    public void setModelProcdefId(String modelProcdefId) {
        this.modelProcdefId = modelProcdefId;
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

    public String getModelCategory() {
        return modelCategory;
    }

    public void setModelCategory(String modelCategory) {
        this.modelCategory = modelCategory;
    }

    public String getVersionType() {
        return versionType;
    }

    public void setVersionType(String versionType) {
        this.versionType = versionType;
    }

    public String getIsValidate() {
        return isValidate;
    }

    public void setIsValidate(String isValidate) {
        this.isValidate = isValidate;
    }

    public String getIsApprove() {
        return isApprove;
    }

    public void setIsApprove(String isApprove) {
        this.isApprove = isApprove;
    }
}
