package com.ht.commonactivity.vo;

import java.util.Date;

public class TaskVo {
    private String id;
    private String name;
    private Date createTime;
    private String proInstId;
    private String executionId;
    private String procDefId;
    private String assign;

    public String getAssign() {
        return assign;
    }

    public void setAssign(String assign) {
        this.assign = assign;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getProInstId() {
        return proInstId;
    }

    public void setProInstId(String proInstId) {
        this.proInstId = proInstId;
    }

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    public String getProcDefId() {
        return procDefId;
    }

    public void setProcDefId(String procDefId) {
        this.procDefId = procDefId;
    }
}
