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
 * 流程审批历史
 * </p>
 *
 * @author dyb
 * @since 2018-04-10
 */
@ApiModel
@TableName("act_process_audit_his")
public class ActProcessAuditHis extends Model<ActProcessAuditHis> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
	@ApiModelProperty(required= true,value = "主键")
	private Long id;
    /**
     * 任务id
     */
	@TableField("task_id")
	@ApiModelProperty(required= true,value = "任务id")
	private String taskId;
    /**
     * 任务名
     */
	@TableField("task_name")
	@ApiModelProperty(required= true,value = "任务名")
	private String taskName;
    /**
     * 流程实例id
     */
	@TableField("pro_inst_id")
	@ApiModelProperty(required= true,value = "流程实例id")
	private String proInstId;
    /**
     * 流程定义id
     */
	@TableField("pro_define_id")
	@ApiModelProperty(required= true,value = "流程定义id")
	private String proDefineId;
    /**
     * 任务定义key
     */
	@TableField("task_define_key")
	@ApiModelProperty(required= true,value = "任务定义key")
	private String taskDefineKey;
    /**
     * 节点审批人
     */
	@ApiModelProperty(required= true,value = "节点审批人")
	private String assignee;
    /**
     * 审批意见
     */
	@ApiModelProperty(required= true,value = "审批意见")
	private String opinion;
    /**
     * 状态，completed-完成
     */
	@ApiModelProperty(required= true,value = "状态，completed-完成")
	private String status;
    /**
     * 执行人id
     */
	@TableField("cre_user_id")
	@ApiModelProperty(required= true,value = "执行人id")
	private String creUserId;
    /**
     * 审批时间
     */
	@TableField("cre_time")
	@ApiModelProperty(required= true,value = "审批时间")
	private Date creTime;


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getProInstId() {
		return proInstId;
	}

	public void setProInstId(String proInstId) {
		this.proInstId = proInstId;
	}

	public String getProDefineId() {
		return proDefineId;
	}

	public void setProDefineId(String proDefineId) {
		this.proDefineId = proDefineId;
	}

	public String getTaskDefineKey() {
		return taskDefineKey;
	}

	public void setTaskDefineKey(String taskDefineKey) {
		this.taskDefineKey = taskDefineKey;
	}

	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	public String getOpinion() {
		return opinion;
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
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
		return "ActProcessAuditHis{" +
			"id=" + id +
			", taskId=" + taskId +
			", taskName=" + taskName +
			", proInstId=" + proInstId +
			", proDefineId=" + proDefineId +
			", taskDefineKey=" + taskDefineKey +
			", assignee=" + assignee +
			", opinion=" + opinion +
			", status=" + status +
			", creUserId=" + creUserId +
			", creTime=" + creTime +
			"}";
	}
}
