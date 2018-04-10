package com.ht.commonactivity.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
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
 * @since 2018-01-22
 */
@ApiModel
@TableName("act_excute_task")
public class ActExcuteTask extends Model<ActExcuteTask> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId("id")
	@ApiModelProperty(required= true,value = "主键")
	private Long id;
    /**
     * 批次号，验证任务调用时存在
     */
	@TableField("batch_id")
	@ApiModelProperty(required= true,value = "批次号，验证任务调用时存在")
	private Long batchId;
    /**
     * 模型版本ID
     */
	@TableField("proc_release_id")
	@ApiModelProperty(required= true,value = "模型版本ID")
	private Long procReleaseId;
    /**
     * 流程运行实例ID
     */
	@TableField("proc_inst_id")
	@ApiModelProperty(required= true,value = "流程运行实例ID")
	private String procInstId;
    /**
     * 任务状态，0-待执行，1-执行结束，2-执行异常
     */
	@TableField("status")
	@ApiModelProperty(required= true,value = "任务状态，0-待执行，1-启动成功，2-执行完成，3-执行异常")
	private String status;
    /**
     * 任务类型，0-验证任务，1-业务系统调用
     */
	@TableField("type")
	@ApiModelProperty(required= true,value = "任务类型，1-验证任务，0-业务系统调用")
	private String type;
    /**
     * 入参
     */
	@TableField("in_paramter")
	@ApiModelProperty(required= true,value = "入参")
	private String inParamter;
    /**
     * 出参
     */
	@TableField("out_paramter")
	@ApiModelProperty(required= true,value = "出参")
	private String outParamter;
    /**
     * 花费时间
     */
	@TableField("spend_time")
	@ApiModelProperty(required= true,value = "花费时间")
	private Long spendTime;
    /**
     * 备注
     */
	@TableField("remark")
	@ApiModelProperty(required= true,value = "备注")
	private String remark;
    /**
     * 创建时间
     */
	@TableField("create_time")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(required= true,value = "创建时间")
	private Date createTime;
    /**
     * 创建用户
     */
	@TableField("create_user")
	@ApiModelProperty(required= true,value = "创建用户")
	private String createUser;
    /**
     * 结束时间
     */
	@TableField("update_time")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(required= true,value = "结束时间")
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

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	@Override
	public String toString() {
		return "ActExcuteTask{" +
			"id=" + id +
			", batchId=" + batchId +
			", procReleaseId=" + procReleaseId +
			", procInstId=" + procInstId +
			", status=" + status +
			", type=" + type +
			", inParamter=" + inParamter +
			", outParamter=" + outParamter +
			", spendTime=" + spendTime +
			", remark=" + remark +
			", createTime=" + createTime +
			", createUser=" + createUser +
			", updateTime=" + updateTime +
			"}";
	}
}
