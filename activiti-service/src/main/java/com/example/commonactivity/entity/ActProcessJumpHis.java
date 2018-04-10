package com.example.commonactivity.entity;

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
 * @since 2018-04-10
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
     * 流程定义id
     */
	@TableField("model_def_id")
	@ApiModelProperty(required= true,value = "流程定义id")
	private String modelDefId;
    /**
     * 实例id
     */
	@TableField("model_proc_id")
	@ApiModelProperty(required= true,value = "实例id")
	private String modelProcId;
    /**
     * 跳转原节点
     */
	@TableField("source_task")
	@ApiModelProperty(required= true,value = "跳转原节点")
	private String sourceTask;
    /**
     * 跳转目标节点
     */
	@TableField("target_task")
	@ApiModelProperty(required= true,value = "跳转目标节点")
	private String targetTask;
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

	public String getModelDefId() {
		return modelDefId;
	}

	public void setModelDefId(String modelDefId) {
		this.modelDefId = modelDefId;
	}

	public String getModelProcId() {
		return modelProcId;
	}

	public void setModelProcId(String modelProcId) {
		this.modelProcId = modelProcId;
	}

	public String getSourceTask() {
		return sourceTask;
	}

	public void setSourceTask(String sourceTask) {
		this.sourceTask = sourceTask;
	}

	public String getTargetTask() {
		return targetTask;
	}

	public void setTargetTask(String targetTask) {
		this.targetTask = targetTask;
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
			", modelDefId=" + modelDefId +
			", modelProcId=" + modelProcId +
			", sourceTask=" + sourceTask +
			", targetTask=" + targetTask +
			", creUserId=" + creUserId +
			", creTime=" + creTime +
			"}";
	}
}
