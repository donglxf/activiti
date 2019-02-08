package com.dyb.commonactivity.vo;

public class FindTaskBeanVo {
    /**
     * 用户名
     */
    private String assignee;

    public String getCandidateUser() {
        return candidateUser;
    }

    public void setCandidateUser(String candidateUser) {
        this.candidateUser = candidateUser;
    }

    /**
     * 候选人
     */
    private String candidateUser ;

    /**
     * 候选组
     */
    private String candidateGroup;

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getCandidateGroup() {
        return candidateGroup;
    }

    public void setCandidateGroup(String candidateGroup) {
        this.candidateGroup = candidateGroup;
    }
}
