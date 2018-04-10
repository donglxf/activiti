package com.ht.commonactivity.entity;

import java.io.Serializable;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * <p>
 * 
 * </p>
 *
 * @author dyb
 * @since 2018-04-09
 */
@ApiModel
@TableName("act_model_definition")
public class ActModelDefinition extends Model<ActModelDefinition> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
	@ApiModelProperty(required= true,value = "主键")
	private Long id;
    /**
     * 模型id
     */
	@TableField("model_id")
	@ApiModelProperty(required= true,value = "模型id")
	private String modelId;
    /**
     * 模型编码
     */
	@TableField("model_code")
	@ApiModelProperty(required= true,value = "模型编码")
	private String modelCode;
    /**
     * 模型名称
     */
	@TableField("model_name")
	@ApiModelProperty(required= true,value = "模型名称")
	private String modelName;
    /**
     * 所属系统
     */
	@TableField("belong_system")
	@ApiModelProperty(required= true,value = "所属系统")
	private String belongSystem;
    /**
     * 业务线
     */
	@TableField("business_id")
	@ApiModelProperty(required= true,value = "业务线")
	private String businessId;
    /**
     * 模型描述
     */
	@TableField("model_desc")
	@ApiModelProperty(required= true,value = "模型描述")
	private String modelDesc;
    /**
     * 状态
     */
	@ApiModelProperty(required= true,value = "状态")
	private String status;
    /**
     * 创建人id
     */
	@TableField("cre_user_id")
	@ApiModelProperty(required= true,value = "创建人id")
	private String creUserId;
    /**
     * 创建时间
     */
	@TableField("cre_time")
	@ApiModelProperty(required= true,value = "创建时间")
	private Date creTime;


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getModelId() {
		return modelId;
	}

	public void setModelId(String modelId) {
		this.modelId = modelId;
	}

	public String getModelCode() {
		return modelCode;
	}

	public void setModelCode(String modelCode) {
		this.modelCode = modelCode;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getBelongSystem() {
		return belongSystem;
	}

	public void setBelongSystem(String belongSystem) {
		this.belongSystem = belongSystem;
	}

	public String getBusinessId() {
		return businessId;
	}

	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}

	public String getModelDesc() {
		return modelDesc;
	}

	public void setModelDesc(String modelDesc) {
		this.modelDesc = modelDesc;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCreUserId() {
		return creUserId;
	}

	public void setCreUserId(String creUserId) {
		this.creUserId = creUserId;
	}

	public Date getCreTime() {
		return creTime;
	}

	public void setCreTime(Date creTime) {
		this.creTime = creTime;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	@Override
	public String toString() {
		return "ActModelDefinition{" +
			"id=" + id +
			", modelId=" + modelId +
			", modelCode=" + modelCode +
			", modelName=" + modelName +
			", belongSystem=" + belongSystem +
			", businessId=" + businessId +
			", modelDesc=" + modelDesc +
			", status=" + status +
			", creUserId=" + creUserId +
			", creTime=" + creTime +
			"}";
	}
}
