package com.ht.commonactivity.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author zhangzhen
 * @since 2018-01-16
 */
@ApiModel
@TableName("act_proc_release")
public class ActProcRelease extends Model<ActProcRelease> {

    private static final long serialVersionUID = 1L;




    /**
     * 主键
     */
    @TableId("id")
	@ApiModelProperty(required= true,value = "主键")
	private Long id;

    private transient  String idStr;

	/**
	 * 模型定义ID，与 ACT_RE_PROCDEF.ID_ 关联,ACT_RE_PROCDEF 表中有模型部署id
	 */
	@TableField("model_id")
	@ApiModelProperty(required= true,value = "模型ID，与 act_re_model.id_ 关联")
    private String modelId;

	@TableField("model_code")
	@ApiModelProperty(required= true,value = "模型ID，与 act_re_model.key_ 关联")
	private String modelCode;

    /**
     * 模型定义ID，与 ACT_RE_PROCDEF.ID_ 关联,ACT_RE_PROCDEF 表中有模型部署id
     */
	@TableField("model_procdef_id")
	@ApiModelProperty(required= true,value = "模型定义ID，与 ACT_RE_PROCDEF.ID_ 关联,ACT_RE_PROCDEF 表中有模型部署id")
	private String modelProcdefId;
    /**
     * 模型名称
     */
	@TableField("model_name")
	@ApiModelProperty(required= true,value = "模型名称")
	private String modelName;
    /**
     * 模型版本
     */
	@TableField("model_version")
	@ApiModelProperty(required= true,value = "模型版本")
	private String modelVersion;
    /**
     * 模型分类
     */
	@TableField("model_category")
	@ApiModelProperty(required= true,value = "模型分类")
	private String modelCategory;
    /**
     * 版本类型，0-测试版，1-正式版
     */
	@TableField("version_type")
	@ApiModelProperty(required= true,value = "版本类型，0-测试版，1-正式版")
	private String versionType;

	@TableField("is_bind")
	private String isBing;
	@TableField("is_auto_validate")
	private String isAutoValidate;
	@TableField("is_manual_validate")
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

	/**
     * 是否验证通过： 0-待验证，1-验证通过，2-验证不通过；默认为0;
     */
	@TableField("is_validate")
	@ApiModelProperty(required= true,value = "是否验证通过： 0-待验证，1-验证通过，2-验证不通过；默认为0;")
	private String isValidate;
    /**
     * 是否审核通过：0-待审核，1-审核通过，2-审核不通过；默认为0;
     */
	@TableField("is_approve")
	@ApiModelProperty(required= true,value = "是否审核通过：0-待审核，1-审核通过，2-审核不通过；3:提交审核；默认为0;")
	private String isApprove;

	@TableField("approve_task_id")
	@ApiModelProperty(required= true,value = "模型验证关联任务Id")
	private String approveTaskId;
    /**
     * 是否生效：0-有效，1-无效
     */
	@TableField("is_effect")
	@ApiModelProperty(required= true,value = "是否生效：0-有效，1-无效")
	private String isEffect;
    /**
     * 更新时间
     */
	@TableField("update_time")
	@ApiModelProperty(required= true,value = "更新时间")
	private Date updateTime;
    /**
     * 更新用户
     */
	@TableField("update_user")
	@ApiModelProperty(required= true,value = "更新用户")
	private String updateUser;
    /**
     * 创建时间
     */
	@TableField("create_time")
	@ApiModelProperty(required= true,value = "创建时间")
	private Date createTime;
    /**
     * 创建用户
     */
	@TableField("create_user")
	@ApiModelProperty(required= true,value = "创建用户")
	private String createUser;

	private transient String taskId;

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

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	public String getModelId() {
		return modelId;
	}

	public void setModelId(String modelId) {
		this.modelId = modelId;
	}

	public String getIdStr() {
		return idStr;
	}

	public void setIdStr(String idStr) {
		this.idStr = idStr;
	}

	public String getModelCode() {
		return modelCode;
	}

	public void setModelCode(String modelCode) {
		this.modelCode = modelCode;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getApproveTaskId() {
		return approveTaskId;
	}

	public void setApproveTaskId(String approveTaskId) {
		this.approveTaskId = approveTaskId;
	}

	@Override
	public String toString() {
		return "ActProcRelease{" +
			"id=" + id +
			", modelProcdefId=" + modelProcdefId +
			", modelName=" + modelName +
			", modelVersion=" + modelVersion +
			", modelCategory=" + modelCategory +
			", versionType=" + versionType +
			", isValidate=" + isValidate +
			", isApprove=" + isApprove +
			", isEffect=" + isEffect +
			", updateTime=" + updateTime +
			", updateUser=" + updateUser +
			", createTime=" + createTime +
			", createUser=" + createUser +
			"}";
	}

}
