package com.ht.commonactivity.entity;

import java.io.Serializable;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * <p>
 * 流程回退历史
 * </p>
 *
 * @author dyb
 * @since 2018-04-12
 */
@ApiModel
@TableName("act_process_jump_his")
public class ActProcessJumpHis extends Model<ActProcessJumpHis> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
	@ApiModelProperty(required= true,value = "主键")
	private Long id;
    /**
     * 流程名
     */
	@TableField("pro_name")
	@ApiModelProperty(required= true,value = "流程名")
	private String proName;
    /**
     * 流程定义id
     */
	@TableField("proc_def_id")
	@ApiModelProperty(required= true,value = "流程定义id")
	private String procDefId;
    /**
     * 实例id
     */
	@TableField("proc_inst_id")
	@ApiModelProperty(required= true,value = "实例id")
	private String procInstId;
    /**
     * 跳转原节点id
     */
	@TableField("source_task_id")
	@ApiModelProperty(required= true,value = "跳转原节点id")
	private String sourceTaskId;
    /**
     * 跳转原节点Name
     */
	@TableField("source_task_name")
	@ApiModelProperty(required= true,value = "跳转原节点Name")
	private String sourceTaskName;
    /**
     * 跳转目标节点id
     */
	@TableField("target_task_id")
	@ApiModelProperty(required= true,value = "跳转目标节点id")
	private String targetTaskId;
    /**
     * 跳转目标节点Name
     */
	@TableField("target_task_name")
	@ApiModelProperty(required= true,value = "跳转目标节点Name")
	private String targetTaskName;
    /**
     * 执行人id
     */
	@TableField("cre_user_id")
	@ApiModelProperty(required= true,value = "执行人id")
	private String creUserId;
    /**
     * 跳转时间
     */
	@TableField("cre_time")
	@ApiModelProperty(required= true,value = "跳转时间")
	private Date creTime;


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getProName() {
		return proName;
	}

	public void setProName(String proName) {
		this.proName = proName;
	}

	public String getProcDefId() {
		return procDefId;
	}

	public void setProcDefId(String procDefId) {
		this.procDefId = procDefId;
	}

	public String getProcInstId() {
		return procInstId;
	}

	public void setProcInstId(String procInstId) {
		this.procInstId = procInstId;
	}

	public String getSourceTaskId() {
		return sourceTaskId;
	}

	public void setSourceTaskId(String sourceTaskId) {
		this.sourceTaskId = sourceTaskId;
	}

	public String getSourceTaskName() {
		return sourceTaskName;
	}

	public void setSourceTaskName(String sourceTaskName) {
		this.sourceTaskName = sourceTaskName;
	}

	public String getTargetTaskId() {
		return targetTaskId;
	}

	public void setTargetTaskId(String targetTaskId) {
		this.targetTaskId = targetTaskId;
	}

	public String getTargetTaskName() {
		return targetTaskName;
	}

	public void setTargetTaskName(String targetTaskName) {
		this.targetTaskName = targetTaskName;
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
		return "ActProcessJumpHis{" +
			"id=" + id +
			", proName=" + proName +
			", procDefId=" + procDefId +
			", procInstId=" + procInstId +
			", sourceTaskId=" + sourceTaskId +
			", sourceTaskName=" + sourceTaskName +
			", targetTaskId=" + targetTaskId +
			", targetTaskName=" + targetTaskName +
			", creUserId=" + creUserId +
			", creTime=" + creTime +
			"}";
	}
}
