package com.ht.commonactivity.common;

import java.io.Serializable;

public class RpcModelVerfication implements Serializable{

    private Long batchId;
    private Long taskId;
    private Long procReleaseId;


    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getProcReleaseId() {
        return procReleaseId;
    }

    public void setProcReleaseId(Long procReleaseId) {
        this.procReleaseId = procReleaseId;
    }
}
