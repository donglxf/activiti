package com.example.commonactivity.service;

import com.example.commonactivity.vo.ProjectWorkflowRequest;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.runtime.ProcessInstance;

import java.util.List;

public interface ProcessGoBack  {

    /**
     *
     * @param taskId 任务id
     * @param msg 批注
     * @param endActivityId 结束节点的activitiyId
     * @throws Exception
     */
    public void turnBackNew(String taskId, String msg, String endActivityId,String processInstanceId,String toBackNoteId ) throws Exception;

    /**
     * 会签退回
     * @param param
     * @param user
     * @throws Exception
     */
    public void submitTask(ProjectWorkflowRequest param, User user) throws Exception ;

    /**
     *
     * @param taskId
     * @return
     * @throws Exception
     */
    public List<ActivityImpl> getactivities(String taskId)  throws Exception ;
//    public List<ActivityImpl> getactivities(HistoricTaskInstance currTask, ProcessInstance instance, String taskId)  throws Exception

}
