package com.ht.commonactivity.service.impl;

import com.ht.commonactivity.entity.ActProcessJumpHis;
import com.ht.commonactivity.service.ActProcessJumpHisService;
import com.ht.commonactivity.service.ProcessGoBack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ht.commonactivity.vo.ProjectWorkflowRequest;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ProcessGoBackImpl implements ProcessGoBack {


    @Resource
    protected RepositoryService repositoryService;

    @Resource
    protected RuntimeService runtimeService;

    @Resource
    protected TaskService taskService;

    @Resource
    protected FormService formService;

    @Resource
    protected HistoryService historyService;

    @Autowired
    private ActProcessJumpHisService jumpHisService;

    /**
     * 根据当前任务ID，查询可以驳回的任务节点
     *
     * @param taskId 当前任务ID
     */
    public List<ActivityImpl> findBackAvtivity(String taskId) throws Exception {
        List<ActivityImpl> rtnList = new ArrayList<ActivityImpl>();
        rtnList = iteratorBackActivity(taskId, findActivitiImpl(taskId,
                null), new ArrayList<ActivityImpl>(),
                new ArrayList<ActivityImpl>());
        return reverList(rtnList);
    }

    /**
     * 反向排序list集合，便于驳回节点按顺序显示
     *
     * @param list
     * @return
     */
    private List<ActivityImpl> reverList(List<ActivityImpl> list) {
        List<ActivityImpl> rtnList = new ArrayList<ActivityImpl>();
        // 由于迭代出现重复数据，排除重复
        for (int i = list.size(); i > 0; i--) {
            if (!rtnList.contains(list.get(i - 1)))
                rtnList.add(list.get(i - 1));
        }
        return rtnList;
    }

    /**
     * 迭代循环流程树结构，查询当前节点可驳回的任务节点
     *
     * @param taskId       当前任务ID
     * @param currActivity 当前活动节点
     * @param rtnList      存储回退节点集合
     * @param tempList     临时存储节点集合（存储一次迭代过程中的同级userTask节点）
     * @return 回退节点集合
     */
    private List<ActivityImpl> iteratorBackActivity(String taskId,
                                                    ActivityImpl currActivity, List<ActivityImpl> rtnList,
                                                    List<ActivityImpl> tempList) throws Exception {
        // 查询流程定义，生成流程树结构
        ProcessInstance processInstance = findProcessInstanceByTaskId(taskId);

        // 当前节点的流入来源
        List<PvmTransition> incomingTransitions = currActivity
                .getIncomingTransitions();
        // 条件分支节点集合，userTask节点遍历完毕，迭代遍历此集合，查询条件分支对应的userTask节点
        List<ActivityImpl> exclusiveGateways = new ArrayList<ActivityImpl>();
        // 并行节点集合，userTask节点遍历完毕，迭代遍历此集合，查询并行节点对应的userTask节点
        List<ActivityImpl> parallelGateways = new ArrayList<ActivityImpl>();
        // 遍历当前节点所有流入路径
        for (PvmTransition pvmTransition : incomingTransitions) {
            TransitionImpl transitionImpl = (TransitionImpl) pvmTransition;
            ActivityImpl activityImpl = transitionImpl.getSource();
            String type = (String) activityImpl.getProperty("type");
            /**
             * 并行节点配置要求：<br>
             * 必须成对出现，且要求分别配置节点ID为:XXX_start(开始)，XXX_end(结束)
             */
            if ("parallelGateway".equals(type)) {// 并行路线
                String gatewayId = activityImpl.getId();
                String gatewayType = gatewayId.substring(gatewayId
                        .lastIndexOf("_") + 1);
                if ("START".equals(gatewayType.toUpperCase())) {// 并行起点，停止递归
                    return rtnList;
                } else {// 并行终点，临时存储此节点，本次循环结束，迭代集合，查询对应的userTask节点
                    parallelGateways.add(activityImpl);
                }
            } else if ("startEvent".equals(type)) {// 开始节点，停止递归
                return rtnList;
            } else if ("userTask".equals(type)) {// 用户任务
                tempList.add(activityImpl);
            } else if ("exclusiveGateway".equals(type)) {// 分支路线，临时存储此节点，本次循环结束，迭代集合，查询对应的userTask节点
                currActivity = transitionImpl.getSource();
                exclusiveGateways.add(currActivity);
            }
        }

        /**
         * 迭代条件分支集合，查询对应的userTask节点
         */
        for (ActivityImpl activityImpl : exclusiveGateways) {
            iteratorBackActivity(taskId, activityImpl, rtnList, tempList);
        }

        /**
         * 迭代并行集合，查询对应的userTask节点
         */
        for (ActivityImpl activityImpl : parallelGateways) {
            iteratorBackActivity(taskId, activityImpl, rtnList, tempList);
        }

        /**
         * 根据同级userTask集合，过滤最近发生的节点
         */
        currActivity = filterNewestActivity(processInstance, tempList);
        if (currActivity != null) {
            // 查询当前节点的流向是否为并行终点，并获取并行起点ID
            String id = findParallelGatewayId(currActivity);
            if (StringUtils.isEmpty(id)) {// 并行起点ID为空，此节点流向不是并行终点，符合驳回条件，存储此节点
                rtnList.add(currActivity);
            } else {// 根据并行起点ID查询当前节点，然后迭代查询其对应的userTask任务节点
                currActivity = findActivitiImpl(taskId, id);
            }

            // 清空本次迭代临时集合
            tempList.clear();
            // 执行下次迭代
            iteratorBackActivity(taskId, currActivity, rtnList, tempList);
        }
        return rtnList;
    }

    /**
     * 根据当前节点，查询输出流向是否为并行终点，如果为并行终点，则拼装对应的并行起点ID
     *
     * @param activityImpl 当前节点
     * @return
     */
    private String findParallelGatewayId(ActivityImpl activityImpl) {
        List<PvmTransition> incomingTransitions = activityImpl
                .getOutgoingTransitions();
        for (PvmTransition pvmTransition : incomingTransitions) {
            TransitionImpl transitionImpl = (TransitionImpl) pvmTransition;
            activityImpl = transitionImpl.getDestination();
            String type = (String) activityImpl.getProperty("type");
            if ("parallelGateway".equals(type)) {// 并行路线
                String gatewayId = activityImpl.getId();
                String gatewayType = gatewayId.substring(gatewayId
                        .lastIndexOf("_") + 1);
                if ("END".equals(gatewayType.toUpperCase())) {
                    return gatewayId.substring(0, gatewayId.lastIndexOf("_"))
                            + "_start";
                }
            }
        }
        return null;
    }

    /**
     * 查询指定任务节点的最新记录
     *
     * @param processInstance 流程实例
     * @param activityId
     * @return
     */
    private HistoricActivityInstance findHistoricUserTask(
            ProcessInstance processInstance, String activityId) {
        HistoricActivityInstance rtnVal = null;
        // 查询当前流程实例审批结束的历史节点
        List<HistoricActivityInstance> historicActivityInstances = historyService
                .createHistoricActivityInstanceQuery().activityType("userTask")
                .processInstanceId(processInstance.getId()).activityId(
                        activityId).finished()
                .orderByHistoricActivityInstanceEndTime().desc().list();
        if (historicActivityInstances.size() > 0) {
            rtnVal = historicActivityInstances.get(0);
        }

        return rtnVal;
    }

    /**
     * 根据流入任务集合，查询最近一次的流入任务节点
     *
     * @param processInstance 流程实例
     * @param tempList        流入任务集合
     * @return
     */
    private ActivityImpl filterNewestActivity(ProcessInstance processInstance,
                                              List<ActivityImpl> tempList) {
        while (tempList.size() > 0) {
            ActivityImpl activity_1 = tempList.get(0);
            HistoricActivityInstance activityInstance_1 = findHistoricUserTask(
                    processInstance, activity_1.getId());
            if (activityInstance_1 == null) {
                tempList.remove(activity_1);
                continue;
            }

            if (tempList.size() > 1) {
                ActivityImpl activity_2 = tempList.get(1);
                HistoricActivityInstance activityInstance_2 = findHistoricUserTask(
                        processInstance, activity_2.getId());
                if (activityInstance_2 == null) {
                    tempList.remove(activity_2);
                    continue;
                }

                if (activityInstance_1.getEndTime().before(
                        activityInstance_2.getEndTime())) {
                    tempList.remove(activity_1);
                } else {
                    tempList.remove(activity_2);
                }
            } else {
                break;
            }
        }
        if (tempList.size() > 0) {
            return tempList.get(0);
        }
        return null;
    }

    /**
     * 根据任务ID和节点ID获取活动节点 <br>
     *
     * @param taskId     任务ID
     * @param activityId 活动节点ID <br>
     *                   如果为null或""，则默认查询当前活动节点 <br>
     *                   如果为"end"，则查询结束节点 <br>
     * @return
     * @throws Exception
     */
    private ActivityImpl findActivitiImpl(String taskId, String activityId)
            throws Exception {
        // 取得流程定义
        ProcessDefinitionEntity processDefinition = findProcessDefinitionEntityByTaskId(taskId);

        // 获取当前活动节点ID
        if (StringUtils.isEmpty(activityId)) {
            activityId = findTaskById(taskId).getTaskDefinitionKey();
        }

        // 根据流程定义，获取该流程实例的结束节点
        if (activityId.toUpperCase().equals("END")) {
            for (ActivityImpl activityImpl : processDefinition.getActivities()) {
                List<PvmTransition> pvmTransitionList = activityImpl
                        .getOutgoingTransitions();
                if (pvmTransitionList.isEmpty()) {
                    return activityImpl;
                }
            }
        }

        // 根据节点ID，获取对应的活动节点
        ActivityImpl activityImpl = ((ProcessDefinitionImpl) processDefinition)
                .findActivity(activityId);

        return activityImpl;
    }

    /**
     * 根据任务ID获取流程定义
     *
     * @param taskId 任务ID
     * @return
     * @throws Exception
     */
    private ProcessDefinitionEntity findProcessDefinitionEntityByTaskId(
            String taskId) throws Exception {
        // 取得流程定义
        ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
                .getDeployedProcessDefinition(findTaskById(taskId)
                        .getProcessDefinitionId());

        if (processDefinition == null) {
            throw new Exception("流程定义未找到!");
        }

        return processDefinition;
    }

    /**
     * 根据任务ID获得任务实例
     *
     * @param taskId 任务ID
     * @return
     * @throws Exception
     */
    private TaskEntity findTaskById(String taskId) throws Exception {
        TaskEntity task = (TaskEntity) taskService.createTaskQuery().taskId(
                taskId).singleResult();
        if (task == null) {
            throw new Exception("任务实例未找到!");
        }
        return task;
    }

    /**
     * 根据任务ID获取对应的流程实例
     *
     * @param taskId 任务ID
     * @return
     * @throws Exception
     */
    private ProcessInstance findProcessInstanceByTaskId(String taskId)
            throws Exception {
        // 找到流程实例
        ProcessInstance processInstance = runtimeService
                .createProcessInstanceQuery().processInstanceId(
                        findTaskById(taskId).getProcessInstanceId())
                .singleResult();
        if (processInstance == null) {
            throw new Exception("流程实例未找到!");
        }
        return processInstance;
    }




    /*20180408****************************************************************************************************************
    **************************************************************************************************************************
    **************************************************************************************************************************
    **************************************************************************************************************************
    **************************************************************************************************************************
    **************************************************************************************************************************
    *****************************************************************************************************************20180408*/

    // 查询当前节点可驳回的任务节点
    public List<ActivityImpl> getactivities(String taskId)  throws Exception {
        Map<String, Object> variables;
        // 取得当前任务
        HistoricTaskInstance currTask = historyService
                .createHistoricTaskInstanceQuery().taskId(taskId)
                .singleResult();
        // 取得流程实例
        ProcessInstance instance = runtimeService
                .createProcessInstanceQuery()
                .processInstanceId(currTask.getProcessInstanceId())
                .singleResult();
        if (instance == null) {
            throw new RuntimeException("流程已结束");
        }
        variables = instance.getProcessVariables();
        // 取得流程定义
        ProcessDefinitionEntity definition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
                .getDeployedProcessDefinition(currTask
                        .getProcessDefinitionId());
        if (definition == null) {
            throw new RuntimeException("流程定义未找到");
        }
        // 当前活动节点
        ActivityImpl currActivity = ((ProcessDefinitionImpl) definition)
                .findActivity(currTask.getTaskDefinitionKey());
        List<ActivityImpl> rtnList = new ArrayList<>();
        List<ActivityImpl> tempList = new ArrayList<>();
        // 查询当前节点可驳回的任务节点
        List<ActivityImpl> activities = iteratorBackActivity(
                taskId,
                currActivity,
                rtnList,
                tempList
        );
        return activities;
    }


    /**
     * @param taskId        任务id
     * @param msg           批注
     * @param endActivityId 结束节点的activitiyId
     * @throws Exception
     */
    public String turnBackNew(String taskId, String msg, String endActivityId,String toBackNoteId) throws Exception {
//        Map<String, Object> variables;
//        // 取得当前任务
        HistoricTaskInstance currTask = historyService
                .createHistoricTaskInstanceQuery().taskId(taskId)
                .singleResult();
        // 取得流程实例
        ProcessInstance instance = runtimeService
                .createProcessInstanceQuery()
                .processInstanceId(currTask.getProcessInstanceId())
                .singleResult();
        HistoricTaskInstance targetTask= historyService.createHistoricTaskInstanceQuery().taskDefinitionKey(toBackNoteId).
                processInstanceId(currTask.getProcessInstanceId()).list().get(0);

        if (instance == null) {
            throw new RuntimeException("流程已结束");
        }
//        variables = instance.getProcessVariables();
//        // 取得流程定义
//        ProcessDefinitionEntity definition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
//                .getDeployedProcessDefinition(currTask
//                        .getProcessDefinitionId());
//        if (definition == null) {
//            throw new RuntimeException("流程定义未找到");
//        }
//        // 取得上一步活动
//        ActivityImpl currActivity = ((ProcessDefinitionImpl) definition)
//                .findActivity(currTask.getTaskDefinitionKey());
//        List<ActivityImpl> rtnList = new ArrayList<>();
//        List<ActivityImpl> tempList = new ArrayList<>();
        // 查询当前节点可驳回的任务节点
//        List<ActivityImpl> activities = getactivities(taskId);
//        List<ActivityImpl> activities = iteratorBackActivity( taskId, currActivity, rtnList, tempList);

//        if (activities == null || activities.size() <= 0) throw new RuntimeException("没有可以选择的驳回节点!");
        List<Task> list = taskService.createTaskQuery().processInstanceId(instance.getId()).list();
        for (Task task : list) {
            if (!task.getId().equals(taskId)) {
                task.setAssignee("排除标记");
                commitProcess(task.getId(), null, endActivityId);
            }
        }

//        turnTransition(taskId, activities.get(0).getId(), null); // 逐步退回
        turnTransition(taskId, toBackNoteId, null); // 退回指定节点
        // 流程退回日志
        ActProcessJumpHis his=new ActProcessJumpHis();
        his.setProcDefId(currTask.getProcessDefinitionId());
        his.setProName(instance.getProcessDefinitionName());
        his.setProcInstId(currTask.getProcessInstanceId());
        his.setSourceTaskId(currTask.getId());
        his.setSourceTaskName(currTask.getName());
        his.setTargetTaskId(toBackNoteId);
        his.setTargetTaskName(targetTask.getName());
        jumpHisService.insert(his);
        return currTask.getTaskDefinitionKey() ;
    }

    /**
     * @param taskId     当前任务ID
     * @param variables  流程变量
     * @param activityId 流程转向执行任务节点ID<br>
     *                   此参数为空，默认为提交操作
     * @throws Exception
     */
    private void commitProcess(String taskId, Map<String, Object> variables,
                               String activityId) throws Exception {
        if (variables == null) {
            variables = new HashMap<String, Object>();
        }
        // 跳转节点为空，默认提交操作
        if (activityId == null || activityId.equals("")) {
            taskService.complete(taskId, variables);
        } else {// 流程转向操作
            turnTransition(taskId, activityId, variables);
        }
    }

    /**
     * 流程转向操作
     *
     * @param taskId     当前任务ID
     * @param activityId 目标节点任务ID
     * @param variables  流程变量
     * @throws Exception
     */
    private void turnTransition(String taskId, String activityId,
                                Map<String, Object> variables) throws Exception {
        // 当前节点
        ActivityImpl currActivity = findActivitiImpl(taskId, null);
        // 清空当前流向
        List<PvmTransition> oriPvmTransitionList = clearTransition(currActivity);

        // 创建新流向
        TransitionImpl newTransition = currActivity.createOutgoingTransition();
        // 目标节点
        ActivityImpl pointActivity = findActivitiImpl(taskId, activityId);
        // 设置新流向的目标节点
        newTransition.setDestination(pointActivity);

        // 执行转向任务
        taskService.complete(taskId, variables);
        // 删除目标节点新流入
        pointActivity.getIncomingTransitions().remove(newTransition);

        // 还原以前流向
        restoreTransition(currActivity, oriPvmTransitionList);
    }

    /**
     * 清空指定活动节点流向
     *
     * @param activityImpl 活动节点
     * @return 节点流向集合
     */
    private List<PvmTransition> clearTransition(ActivityImpl activityImpl) {
        // 存储当前节点所有流向临时变量
        List<PvmTransition> oriPvmTransitionList = new ArrayList<PvmTransition>();
        // 获取当前节点所有流向，存储到临时变量，然后清空
        List<PvmTransition> pvmTransitionList = activityImpl
                .getOutgoingTransitions();
        for (PvmTransition pvmTransition : pvmTransitionList) {
            oriPvmTransitionList.add(pvmTransition);
        }
        pvmTransitionList.clear();

        return oriPvmTransitionList;
    }

    /**
     * 还原指定活动节点流向
     *
     * @param activityImpl         活动节点
     * @param oriPvmTransitionList 原有节点流向集合
     */
    private void restoreTransition(ActivityImpl activityImpl,
                                   List<PvmTransition> oriPvmTransitionList) {
        // 清空现有流向
        List<PvmTransition> pvmTransitionList = activityImpl
                .getOutgoingTransitions();
        pvmTransitionList.clear();
        // 还原以前流向
        for (PvmTransition pvmTransition : oriPvmTransitionList) {
            pvmTransitionList.add(pvmTransition);
        }
    }


    /**
     * 暂时作废，不调用
     * @param param
     * @param user
     * @throws Exception
     */
    public void submitTask(ProjectWorkflowRequest param, User user) throws Exception {
        //  if (true) throw new RuntimeException();
        Task task = taskService.createTaskQuery().taskId(param.getTaskId()).singleResult();// 获取当前任务
        String processInstanceId = task.getProcessInstanceId();
        taskService.addComment(param.getTaskId(), processInstanceId, param.getMsg());// 添加任务批注
//        taskService.setVariableLocal(param.getTaskId(), "projectId", param.getProject().getTab1().getProjectId());// 此段也是为了记录历史而诞生,大家可以删掉
//        runtimeService.setVariables(task.getExecutionId(), new HashMap() {{
//            this.put("LAST_USER", userCode);
//            this.put("LAST_USER_NAME", username);
//        }});// 大家可以删掉

//        runtimeService.setVariable(processInstanceId, "LAST_USER", username);// 大家可以删掉
// 因为前台标记驳回还是通过还是终止流程,分别用0,1,2标识,所以,你就当是三种情况,通过驳回,终止
        if ("1".equals(param.getFlag())) {// 通过
            switch (param.getType()) {
                case "1": {
                    String sid = task.getTaskDefinitionKey();// 获取当前任务的activitiyId
                    List<Task> list = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).list();// 获取当前流程的所有未完成任务
                    if (list.size() == 1) { // 如果只剩一个任务,可能是普通任务,可能是会签任务
                        if (
                                "sid-71E0BFA1-AD56-45D6-907E-D4B61F445D84".equals(sid) ||
                                        "sid-9795199D-77F0-4F34-A0D6-E388525FA8BE".equals(sid) ||
                                        "sid-6340E7E8-2BA1-4B2F-B7CF-19FE31412E58".equals(sid)) { // 如果是会签,则提交到下一个审批节点(并行网关之后的第一个节点)
//工作流任意流转,流转到下一个审批节点,因为我的会签里面包含三个节点,所以经过activitiyId来判定是不是这三个节点,如果是,则任意流转节点,如果不是,则正常完成任务,
// 注意:工作流程图确定之后,每个节点的activityId是永恒不变的!因此,才可以做此判定
                            commitProcess(task.getId(), null, "sid-C6334E47-F8A8-430C-9A35-FA98CFD0C405");
                        } else if (
                                "sid-62D73895-5AB3-4818-8795-D8CCCDB59CE6".equals(sid) ||
                                        "sid-CFDED207-B694-478A-AE94-3DFFD486E77F".equals(sid) ||
                                        "sid-7A523F96-9F43-4C2C-979F-06B7BC9656B6".equals(sid)) {

                        } else {
                            taskService.complete(task.getId());// 如果不是会签的3个节点则直接审批
                        }
                    } else {
                        commitProcess(task.getId(), null, "sid-404150FC-8E2D-4582-9CFB-BC9FF43F09E1");
                    }
                }
                break;
                case "2": {
                    Map<String, Object> variables = new HashMap<>();
                    variables.put("a", param.getU1());
                    variables.put("b", param.getU2());
                    taskService.complete(param.getTaskId(), variables);
                }
                break;
                case "3": {
                    Map<String, Object> variables = new HashMap<>();
                    variables.put("flag", param.getFlag1());
                    taskService.complete(param.getTaskId(), variables);
                }
                break;
            }

        } else if ("0".equals(param.getFlag())) {// 拒绝 // // TODO: 2017/11/8 0008
            turnBackNew(param.getTaskId(), param.getMsg(), "sid-404150FC-8E2D-4582-9CFB-BC9FF43F09E1","");
        } else if ("2".equals(param.getFlag())) {// 终止流程
//            endProcess(param.getTaskId(), "sid-404150FC-8E2D-4582-9CFB-BC9FF43F09E1");
            ProcessEngines.getDefaultProcessEngine().getTaskService()//与正在执行的任务管理相关的Service
                    .complete(param.getTaskId());
        }
    }


}
