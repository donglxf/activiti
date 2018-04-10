package com.ht.commonactivity.common;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RuleExcuteDetail implements Serializable{

    private String senceVersionId;
    private String senceName;
    private String logId;
    private List<RpcRuleDetail> ruleDetails;
    private List<String> ruleList;
    private Map<String,Object> inParamter;

    public String getSenceVersionId() {
        return senceVersionId;
    }

    public void setSenceVersionId(String senceVersionId) {
        this.senceVersionId = senceVersionId;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public List<String> getRuleList() {
        return ruleList;
    }

    public void setRuleList(List<String> ruleList) {
        this.ruleList = ruleList;
    }

    public String getSenceName() {
        return senceName;
    }

    public void setSenceName(String senceName) {
        this.senceName = senceName;
    }

    public List<RpcRuleDetail> getRuleDetails() {
        return ruleDetails;
    }

    public void setRuleDetails(List<RpcRuleDetail> ruleDetails) {
        this.ruleDetails = ruleDetails;
    }

    public Map<String, Object> getInParamter() {
        return inParamter;
    }

    public void setInParamter(Map<String, Object> inParamter) {
        this.inParamter = inParamter;
    }
}
