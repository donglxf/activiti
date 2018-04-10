package com.ht.commonactivity.vo;

import com.ht.commonactivity.entity.ActProcRelease;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

public class ActProcReleaseVo implements Serializable{

    public ActProcReleaseVo(){
        super();
    }

    public ActProcReleaseVo(ActProcRelease release){
        BeanUtils.copyProperties(release,this);
        this.setId(String.valueOf(release.getId()));
    }

    /**
     * 主键
     */
    private String id;
    /**
     * 模型定义ID，与 ACT_RE_PROCDEF.ID_ 关联,ACT_RE_PROCDEF 表中有模型部署id
     */
    private String modelProcdefId;
    /**
     * 模型名称
     */
    private String modelName;

    /**
     * 模型编码
     */
    private String modelCode;
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

    private String approveTaskId;
    /**
     * 是否生效：0-有效，1-无效
     */
    private String isEffect;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 更新用户
     */
    private String updateUser;
    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 创建用户
     */
    private String createUser;


    private String taskId;

    private String taskStatus;

    private String cornText;

    private String isBing;
    private String isAutoValidate;
    private String isManualValidate;


    public String getIsBing() {
        return isBing;
    }

    public void setIsBing(String isBing) {
        this.isBing = isBing;
    }

    public String getIsAutoValidate() {
        return isAutoValidate;
    }

    public void setIsAutoValidate(String isAutoValidate) {
        this.isAutoValidate = isAutoValidate;
    }

    public String getIsManualValidate() {
        return isManualValidate;
    }

    public void setIsManualValidate(String isManualValidate) {
        this.isManualValidate = isManualValidate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getIsEffect() {
        return isEffect;
    }

    public void setIsEffect(String isEffect) {
        this.isEffect = isEffect;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
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

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getCornText() {
        return cornText;
    }

    public void setCornText(String cornText) {
        this.cornText = cornText;
    }

    public String getModelCode() {
        return modelCode;
    }

    public void setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }

    public String getApproveTaskId() {
        return approveTaskId;
    }

    public void setApproveTaskId(String approveTaskId) {
        this.approveTaskId = approveTaskId;
    }
}
