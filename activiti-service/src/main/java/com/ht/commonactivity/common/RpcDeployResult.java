package com.ht.commonactivity.common;

import java.io.Serializable;
import java.util.List;

public class RpcDeployResult implements Serializable {

    private String deploymentId;
    private String processDefineId;
    private String releaseId;
    private String version;
    private List<RpcSenceInfo> sences = null;

    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    public String getProcessDefineId() {
        return processDefineId;
    }

    public void setProcessDefineId(String processDefineId) {
        this.processDefineId = processDefineId;
    }

    public List<RpcSenceInfo> getSences() {
        return sences;
    }

    public void setSences(List<RpcSenceInfo> sences) {
        this.sences = sences;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getReleaseId() {
        return releaseId;
    }

    public void setReleaseId(String releaseId) {
        this.releaseId = releaseId;
    }
}
