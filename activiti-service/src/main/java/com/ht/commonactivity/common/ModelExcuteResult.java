package com.ht.commonactivity.common;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ModelExcuteResult implements Serializable{

    private String code;
    private String msg;
    private String procInstId;
    private Long taskId;
    private String procMsg;
    private String businessKey;
    private Map<String,List<RuleExcuteDetail>> ruleResultList;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getProcInstId() {
        return procInstId;
    }

    public void setProcInstId(String procInstId) {
        this.procInstId = procInstId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Map<String, List<RuleExcuteDetail>> getRuleResultList() {
        return ruleResultList;
    }

    public void setRuleResultList(Map<String, List<RuleExcuteDetail>> ruleResultList) {
        this.ruleResultList = ruleResultList;
    }
    public String getProcMsg() {
        return procMsg;
    }

    public void setProcMsg(String procMsg) {
        this.procMsg = procMsg;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }
}
