package com.ht.commonactivity.entity;

import java.io.Serializable;

import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * <p>
 * </p>
 *
 * @author dyb
 * @since 2018-04-04
 */
@ApiModel
@TableName("ACT_RU_TASK")
public class ActRuTask extends Model<ActRuTask> {

    private static final long serialVersionUID = 1L;

    @TableId("ID_")
    @ApiModelProperty(required = true, value = "")
    private String id;
    @TableField("REV_")
    @ApiModelProperty(required = true, value = "")
    private Integer rev;
    @TableField("EXECUTION_ID_")
    @ApiModelProperty(required = true, value = "")
    private String executionId;
    @TableField("PROC_INST_ID_")
    @ApiModelProperty(required = true, value = "")
    private String procInstId;
    @TableField("PROC_DEF_ID_")
    @ApiModelProperty(required = true, value = "")
    private String procDefId;
    @TableField("NAME_")
    @ApiModelProperty(required = true, value = "")
    private String name;
    @TableField("PARENT_TASK_ID_")
    @ApiModelProperty(required = true, value = "")
    private String parentTaskId;
    @TableField("DESCRIPTION_")
    @ApiModelProperty(required = true, value = "")
    private String description;
    @TableField("TASK_DEF_KEY_")
    @ApiModelProperty(required = true, value = "")
    private String taskDefKey;
    @TableField("OWNER_")
    @ApiModelProperty(required = true, value = "")
    private String owner;
    @TableField("ASSIGNEE_")
    @ApiModelProperty(required = true, value = "")
    private String assignee;
    @TableField("DELEGATION_")
    @ApiModelProperty(required = true, value = "")
    private String delegation;
    @TableField("PRIORITY_")
    @ApiModelProperty(required = true, value = "")
    private Integer priority;
    @TableField("CREATE_TIME_")
    @ApiModelProperty(required = true, value = "")
    private Date createTime;
    @TableField("DUE_DATE_")
    @ApiModelProperty(required = true, value = "")
    private Date dueDate;
    @TableField("CATEGORY_")
    @ApiModelProperty(required = true, value = "")
    private String category;
    @TableField("SUSPENSION_STATE_")
    @ApiModelProperty(required = true, value = "")
    private Integer suspensionState;
    @TableField("TENANT_ID_")
    @ApiModelProperty(required = true, value = "")
    private String tenantId;
    @TableField("FORM_KEY_")
    @ApiModelProperty(required = true, value = "")
    private String formKey;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getRev() {
        return rev;
    }

    public void setRev(Integer rev) {
        this.rev = rev;
    }

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    public String getProcInstId() {
        return procInstId;
    }

    public void setProcInstId(String procInstId) {
        this.procInstId = procInstId;
    }

    public String getProcDefId() {
        return procDefId;
    }

    public void setProcDefId(String procDefId) {
        this.procDefId = procDefId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentTaskId() {
        return parentTaskId;
    }

    public void setParentTaskId(String parentTaskId) {
        this.parentTaskId = parentTaskId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTaskDefKey() {
        return taskDefKey;
    }

    public void setTaskDefKey(String taskDefKey) {
        this.taskDefKey = taskDefKey;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getDelegation() {
        return delegation;
    }

    public void setDelegation(String delegation) {
        this.delegation = delegation;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getSuspensionState() {
        return suspensionState;
    }

    public void setSuspensionState(Integer suspensionState) {
        this.suspensionState = suspensionState;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getFormKey() {
        return formKey;
    }

    public void setFormKey(String formKey) {
        this.formKey = formKey;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "ActRuTask{" +
                "id=" + id +
                ", rev=" + rev +
                ", executionId=" + executionId +
                ", procInstId=" + procInstId +
                ", procDefId=" + procDefId +
                ", name=" + name +
                ", parentTaskId=" + parentTaskId +
                ", description=" + description +
                ", taskDefKey=" + taskDefKey +
                ", owner=" + owner +
                ", assignee=" + assignee +
                ", delegation=" + delegation +
                ", priority=" + priority +
                ", createTime=" + createTime +
                ", dueDate=" + dueDate +
                ", category=" + category +
                ", suspensionState=" + suspensionState +
                ", tenantId=" + tenantId +
                ", formKey=" + formKey +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ActRuTask person = (ActRuTask) o;

        if (!id.equals(person.id)) return false;
        return true;

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
//        result = 31 * result + name.hashCode();
        return result;
    }
}
