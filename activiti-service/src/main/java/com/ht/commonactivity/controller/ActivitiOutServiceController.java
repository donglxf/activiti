package com.ht.commonactivity.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ht.commonactivity.common.ActivitiConstants;
import com.ht.commonactivity.common.RpcStartParamter;
import com.ht.commonactivity.common.enumtype.ActivitiSignEnum;
import com.ht.commonactivity.common.result.Result;
import com.ht.commonactivity.entity.ActModelDefinition;
import com.ht.commonactivity.entity.ActProcRelease;
import com.ht.commonactivity.entity.ModelCallLog;
import com.ht.commonactivity.entity.ModelCallLogParam;
import com.ht.commonactivity.rpc.UcAppRpc;
import com.ht.commonactivity.service.*;
import com.ht.commonactivity.utils.NextTaskInfo;
import com.ht.commonactivity.utils.ObjectUtils;
import com.ht.commonactivity.vo.*;
import com.ht.ussp.util.DateUtil;
import com.sun.media.sound.ModelDestination;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.engine.*;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.spring.web.json.Json;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@Api("工作流对外提供的接口服务")
@Log4j2
public class ActivitiOutServiceController implements ModelDataJsonConstants {

    @Resource
    protected RepositoryService repositoryService;

    @Autowired
    protected HistoryService historyService;

    @Resource
    protected ActivitiService activitiService;

    @Autowired
    protected TaskService taskService;

    @Autowired
    protected ActModelDefinitionService modelDefinitionService;

    @Autowired
    protected ActProcReleaseService actProcReleaseService;

    @Autowired
    protected ProcessGoBack processGoBack;

    @Autowired
    protected ActProcessAuditHisService auditHisService;

    @Autowired
    protected ActProcessJumpHisService jumpHisService;

    @Autowired
    protected RuntimeService runtimeService;

    @Autowired
    protected ModelCallLogService modelCallLogService;

    @Autowired
    protected ModelCallLogParamService modelCallLogParamService;

    @Autowired
    private RedisTemplate<String, String> redis;

    @Autowired
    protected UcAppRpc ucAppRpc;

    protected static volatile ProcessEngine processEngine = null;

    protected static ProcessEngine getProcessEngine() {
        synchronized (ProcessEngine.class) {
            if (processEngine == null) {
                processEngine = ProcessEngines.getDefaultProcessEngine();
            }
        }
        return processEngine;
    }

    @PostMapping("/startProcessInstanceByKey")
    @ApiOperation("启动模型")
    public Result<List<NextTaskInfo>> startProcessInstanceByKey(@RequestBody RpcStartParamter paramter) {
        log.info("startProcessInstanceByKey start model,paramter========》》:" + JSON.toJSONString(paramter));
        Result<List<NextTaskInfo>> data = null;
        try {
            if (StringUtils.isEmpty(paramter.getBusinessKey())) {
                return Result.error(1, "businessKey is null ,check");
            }
            if (StringUtils.isEmpty(paramter.getSysCode())) {
                return Result.error(1, "sysCode系统编码不能为空!!");
            }
            if (StringUtils.isEmpty(paramter.getProcessDefinedKey())) {
                return Result.error(1, "流程key不能为空!!");
            }

            ActProcRelease release = null;
            // 版本信息为空，获取模型最新版本
            if (StringUtils.isEmpty(paramter.getVersion())) {
                release = activitiService.getModelLastedVersion(paramter.getProcessDefinedKey());
            } else {
                release = activitiService.getModelInfo(paramter.getProcessDefinedKey(), paramter.getVersion());
            }
            if (release == null) {
                return Result.error(1, ActivitiConstants.MODEL_UNEXIST);
            }
            String ruleDrl = redis.opsForValue().get(paramter.getSysCode().toUpperCase() + paramter.getBusinessKey());
            if (StringUtils.isNotEmpty(ruleDrl)) {
                // 过滤非本系统具有相同业务key的流程
                List<ModelCallLog> callList = modelCallLogService.selectList(new EntityWrapper<ModelCallLog>().eq("sys_code", paramter.getSysCode().toUpperCase()));
                List<ProcessInstance> processInstancs = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(paramter.getBusinessKey()).list();
                List<ProcessInstance> processInstancsN = new ArrayList<>();
                for (ModelCallLog model : callList) {
                    for (ProcessInstance processInstance : processInstancs) {
                        if (model.getProInstId().equals(processInstance.getProcessInstanceId())) {
                            processInstancsN.add(processInstance);
                        }
                    }
                }
                String deleteReason = "删除重新启动";
                if (processInstancs != null) {
                    for (ProcessInstance processInstance : processInstancsN) {
                        runtimeService.deleteProcessInstance(processInstance.getProcessInstanceId(), deleteReason);
                    }
                }
            }

            ProcessInstance instance = runtimeService.startProcessInstanceById(release.getModelProcdefId(),
                    paramter.getBusinessKey(), paramter.getData());
            String str = "{\"sysCode\":\"" + paramter.getSysCode().toUpperCase() + "\",\"businessKey\":\"" + paramter.getBusinessKey() + "\"}";
            redis.opsForValue().set(paramter.getSysCode().toUpperCase() + paramter.getBusinessKey(), JSON.toJSONString(str));

            log.info("process start installId======" + instance.getId());
            String instId = instance.getId();
            boolean bool = procIsEnd(instId);
            List<NextTaskInfo> list = new ArrayList<NextTaskInfo>();
            List<Task> tasks = taskService.createTaskQuery().processInstanceId(instId).list();
            for (Task t : tasks) {
                NextTaskInfo result = new NextTaskInfo();
                result.setTaskDefineKey(t.getTaskDefinitionKey());
                result.setTaskText(t.getName());
                result.setProcInstId(instId);
                result.setTaskAssign(t.getAssignee());
                result.setProIsEnd(bool ? "Y" : "N");
                result.setProcDefinedId(t.getProcessDefinitionId());
//                result.setTaskId(t.getId());
                list.add(result);
            }
            activitiService.saveModelLog(release, paramter, instId);

//            String processInstanceId = activitiService.startProcess(paramter);
//            TaskDefinition taskDefinition = activitiService.getNextTaskInfoByProcessId(instance.getId());
//            NextTaskInfo result = new NextTaskInfo();
//            result.setTaskDefineKey(taskDefinition.getKey());
//            result.setTaskText(taskDefinition.getNameExpression().getExpressionText());
//            result.setProcInstId(instance.getId());
//            result.setTaskAssign(taskDefinition.getAssigneeExpression().getExpressionText());
            log.info("process result Date=====》》》" + JSON.toJSONString(list));
            data = Result.success(list);
        } catch (Exception e) {
            data = Result.error(1, "模型启动异常！");
            log.error("deploy model error,error message：", e);
        }
        log.info("start model success.");
        return data;
    }

    /**
     * 判断流程是否结束
     *
     * @param proInstId
     * @return true-结束，false-未结束
     */
    public boolean procIsEnd(String proInstId) {
        ProcessInstance rpi = getProcessEngine().getRuntimeService()//
                .createProcessInstanceQuery()//创建流程实例查询对象
                .processInstanceId(proInstId)
                .singleResult();
        return rpi == null ? true : false;
    }

    /**
     * 任务转办,a办理人转给b办理，b办理完成之后流程往下走
     *
     * @param taskId 任务id
     * @param owner  转办人
     * @return
     */
    @PostMapping("/taskChangeOther")
    @ApiOperation("任务转办或动态设置代理人")
    public Result<String> taksChangeOther(@RequestBody TaskChangeOtherVo vo) {
        log.info("taskChangeOther param Date=====》》》" + JSON.toJSONString(vo.getTaskId()) + "============" + JSON.toJSONString(vo.getOwner()));
        taskService.setAssignee(vo.getTaskId(), vo.getOwner());
        return Result.success();
    }


    /**
     * 根据用户、候选人、候选组 查询所有任务
     * {
     * "firstResult": 0,
     * "maxResults": 99,
     * "assignee": "张三",
     * "sysCode":"ACTIVITI",
     * "processDefinitionKey":[{"modelCode":"lcTest","modelVersion":"V.2"},{"modelCode":"proTest","modelVersion":"V.25"}],
     * "paramMap":[ {
     * "name":"name",
     * "value":"name1",
     * "type":"1"
     * }]
     * }
     */
    @PostMapping("/findTaskByAssignee")
    @ApiOperation("根据用户查询所有任务")
    @ResponseBody
    public Result<List<TaskVo>> findMyPersonalTask(@RequestBody FindTaskBeanVo vo) {
        log.info("findTaskByAssignee param date-------->>>:" + JSON.toJSONString(vo));
        List<TaskVo> voList = new ArrayList<>();
        if (StringUtils.isEmpty(vo.getSysCode())) {
            return Result.error(1, "sysCode系统编码不能为空!!");
        }
        Result<List<TaskVo>> data = null; // new ArrayList<TaskVo>();
//        泛型过滤重复对象
//        List<ActRuTask> tlist= activitiService.findTaskByAssigneeOrGroup(vo);
//        List<ActRuTask> list1= new ArrayList<ActRuTask>();
//        tlist.stream().forEach(p -> {
//            if(!list1.contains(p)){
//                list1.add(p);
//            }
//        });

        if (StringUtils.isEmpty(vo.getAssignee())) {
            data = Result.error(1, "用户名不能为空！");
            return data;
        }

        TaskQuery query = getProcessEngine().getTaskService()//与正在执行的任务管理相关的Service
                .createTaskQuery();//创建任务查询对象

        if (com.ht.commonactivity.utils.ObjectUtils.isNotEmpty(vo.getParamMap())) {
            for (int i = 0; i < vo.getParamMap().size(); i++) {
                Map<String, Object> o = vo.getParamMap().get(i);
                if (ActivitiSignEnum.equle.getVal().equals(o.get("type"))) {
                    query.processVariableValueEquals(String.valueOf(o.get("name")), String.valueOf(o.get("value")));
                } else if (ActivitiSignEnum.notequle.getVal().equals(o.get("key"))) {
                    query.processVariableValueNotEquals(String.valueOf(o.get("name")), String.valueOf(o.get("value")));
                } else if (ActivitiSignEnum.great.getVal().equals(o.get("key"))) {
                    query.processVariableValueGreaterThan(String.valueOf(o.get("name")), String.valueOf(o.get("value")));
                } else if (ActivitiSignEnum.greatEq.getVal().equals(o.get("key"))) {
                    query.processVariableValueGreaterThanOrEqual(String.valueOf(o.get("name")), String.valueOf(o.get("value")));
                } else if (ActivitiSignEnum.less.getVal().equals(o.get("key"))) {
                    query.processVariableValueLessThan(String.valueOf(o.get("name")), String.valueOf(o.get("value")));
                } else if (ActivitiSignEnum.lessEq.getVal().equals(o.get("key"))) {
                    query.processVariableValueLessThanOrEqual(String.valueOf(o.get("name")), String.valueOf(o.get("value")));
                } else if (ActivitiSignEnum.like.getVal().equals(o.get("key"))) {
                    query.processVariableValueLike(String.valueOf(o.get("name")), String.valueOf(o.get("value")));
                }
            }
        }

        /**查询条件（where部分）*/
        if (vo.getAssignee() != null) {
            query.taskAssignee(vo.getAssignee()); //指定个人任务查询，指定办理人
        }


        if (null != vo.getProcessDefinitionKey() && vo.getProcessDefinitionKey().size() > 0) {
            List<String> list = new ArrayList<>();
            vo.getProcessDefinitionKey().forEach(li -> {
                Wrapper<ActProcRelease> wrapper = new EntityWrapper<ActProcRelease>();
                wrapper.eq("model_code", li.getModelCode());
                wrapper.eq("model_version", li.getModelVersion());
                wrapper.orderBy("create_time", false);
                List<ActProcRelease> ls = actProcReleaseService.selectList(wrapper);
                if (null != ls && ls.size() > 0) {
                    list.add(getProcessEngine().getRepositoryService().getProcessDefinition(ls.get(0).getModelProcdefId()).getKey());
                } else {
                    list.add("dybadff" + DateUtil.getDate("yyyy-MM-dd")); // 如果所传模型code,version错误，添加随机值保证流程查不到记录
                }
            });
            query.processDefinitionKeyIn(list);
        }

        /**过滤非本系统流程任务*/
        List<Task> list = query.orderByTaskCreateTime().desc().listPage(vo.getFirstResult(), vo.getMaxResults());//返回列表
        List<Task> newList = new ArrayList<>();
        List<ModelCallLog> callList = modelCallLogService.selectList(new EntityWrapper<ModelCallLog>().eq("sys_code", vo.getSysCode().toUpperCase()));
        if (list != null && list.size() > 0) {
            for (Task task : list) {
                for (ModelCallLog callLog : callList) {
                    if (task.getProcessInstanceId().equals(callLog.getProInstId())) {
                        newList.add(task);
                    }
                }
            }
        }
        if (newList != null && newList.size() > 0) {
            for (Task task : newList) {
                ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
                TaskVo tvo = new TaskVo();
                tvo.setBusinessKey(pi.getBusinessKey());
                tvo.setCreateTime(task.getCreateTime());
                tvo.setTaskId(task.getId());
                tvo.setExecutionId(task.getExecutionId());
                tvo.setName(task.getName());
                tvo.setProcDefId(task.getProcessDefinitionId());
                tvo.setProInstId(task.getProcessInstanceId());
                tvo.setAssign(task.getAssignee());
                voList.add(tvo);
            }
        }

        data = Result.success(voList);
        log.info("findTaskByAssignee result Data-------->>>>" + JSON.toJSONString(data));
        return data;
    }

    /**
     * 根据候选组 查询所有代办任务,角色
     */
    @PostMapping("/findTaskByCandidateGroup")
    @ApiOperation("根据候选组查询所有任务")
    @ResponseBody
    public Result<List<TaskVo>> findTaskByCandidateGroup(@RequestBody FindTaskBeanVo vo) {
        log.info("findTaskByCandidateGroup param Data-------->>:" + JSON.toJSONString(vo));
        List<TaskVo> voList = new ArrayList<>();
        Result<List<TaskVo>> data = null;
        if (vo.getSysCode() == null) {
            data = Result.error(1, "sysCode系统编码不能为空！");
            return data;
        }
        TaskQuery query = getProcessEngine().getTaskService()//与正在执行的任务管理相关的Service
                .createTaskQuery();
        if (ObjectUtils.isNotEmpty(vo.getTaskDefinId())) {
            query.taskDefinitionKeyLike(vo.getTaskDefinId());
//            if (vo.getTaskDefinId().size() == 1) {
//                query.processDefinitionKeyLike(vo.getTaskDefinId().get(0));
//            } else {
//                query.processDefinitionKeyIn(vo.getTaskDefinId());
//            }

        }

        if (com.ht.commonactivity.utils.ObjectUtils.isNotEmpty(vo.getParamMap())) {
            for (int i = 0; i < vo.getParamMap().size(); i++) {
                Map<String, Object> o = vo.getParamMap().get(i);
                if (ActivitiSignEnum.equle.getVal().equals(o.get("type"))) {
                    query.processVariableValueEquals(String.valueOf(o.get("name")), String.valueOf(o.get("value")));
                } else if (ActivitiSignEnum.notequle.getVal().equals(o.get("key"))) {
                    query.processVariableValueNotEquals(String.valueOf(o.get("name")), String.valueOf(o.get("value")));
                } else if (ActivitiSignEnum.great.getVal().equals(o.get("key"))) {
                    query.processVariableValueGreaterThan(String.valueOf(o.get("name")), String.valueOf(o.get("value")));
                } else if (ActivitiSignEnum.greatEq.getVal().equals(o.get("key"))) {
                    query.processVariableValueGreaterThanOrEqual(String.valueOf(o.get("name")), String.valueOf(o.get("value")));
                } else if (ActivitiSignEnum.less.getVal().equals(o.get("key"))) {
                    query.processVariableValueLessThan(String.valueOf(o.get("name")), String.valueOf(o.get("value")));
                } else if (ActivitiSignEnum.lessEq.getVal().equals(o.get("key"))) {
                    query.processVariableValueLessThanOrEqual(String.valueOf(o.get("name")), String.valueOf(o.get("value")));
                } else if (ActivitiSignEnum.like.getVal().equals(o.get("key"))) {
                    query.processVariableValueLike(String.valueOf(o.get("name")), String.valueOf(o.get("value")));
                }
            }
        }

        if (null != vo.getProcessDefinitionKey() && vo.getProcessDefinitionKey().size() > 0) {
            List<String> list = new ArrayList<>();
            vo.getProcessDefinitionKey().forEach(li -> {
                Wrapper<ActProcRelease> wrapper = new EntityWrapper<ActProcRelease>();
                wrapper.eq("model_code", li.getModelCode());
                wrapper.eq("model_version", li.getModelVersion());
                wrapper.orderBy("create_time", false);
                List<ActProcRelease> ls = actProcReleaseService.selectList(wrapper);
                if (null != ls && ls.size() > 0) {
                    list.add(getProcessEngine().getRepositoryService().getProcessDefinition(ls.get(0).getModelProcdefId()).getKey());
                } else {
                    list.add("dybadff" + DateUtil.getDate("yyyy-MM-dd")); // 如果所传模型code错误，添加随机值保证流程查不到记录
                }
            });
            query.processDefinitionKeyIn(list);
        }


        List<Task> list = new ArrayList<>();
        if (null != vo.getCandidateGroup() && vo.getCandidateGroup().size() > 0) {
            list.addAll(query.taskCandidateGroupIn(vo.getCandidateGroup())
                    .orderByTaskCreateTime().desc().listPage(vo.getFirstResult(), vo.getMaxResults()));
        }
        if (StringUtils.isNotEmpty(vo.getCandidateUser())) {
            list.addAll(query.taskCandidateUser(vo.getCandidateUser())
                    .orderByTaskCreateTime().desc().listPage(vo.getFirstResult(), vo.getMaxResults()));
        }
        /**过滤非本系统流程任务*/
        List<Task> newList = new ArrayList<>();
        List<ModelCallLog> callList = modelCallLogService.selectList(new EntityWrapper<ModelCallLog>().eq("sys_code", vo.getSysCode().toUpperCase()));
        if (list != null && list.size() > 0) {
            for (Task task : list) {
                for (ModelCallLog callLog : callList) {
                    if (task.getProcessInstanceId().equals(callLog.getProInstId())) {
                        newList.add(task);
                    }
                }
            }
        }
        for (Task task : newList) {
            ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
            TaskVo tvo = new TaskVo();
            tvo.setBusinessKey(pi.getBusinessKey());
            tvo.setCreateTime(task.getCreateTime());
            tvo.setTaskId(task.getId());
            tvo.setExecutionId(task.getExecutionId());
            tvo.setName(task.getName());
            tvo.setProcDefId(task.getProcessDefinitionId());
            tvo.setProInstId(task.getProcessInstanceId());
            tvo.setAssign(task.getAssignee());
            voList.add(tvo);
        }
        data = Result.success(voList);
        log.info("findTaskByCandidateGroup result Data-------->>:" + JSON.toJSONString(data));
        return data;
    }

    /**
     * 完成任务
     *
     * @param vo
     * @return
     */
    @PostMapping("/complateTask")
    @ApiOperation("完成任务")
    @ResponseBody
    public Result<List<NextTaskInfo>> completeMyPersonalTask(@RequestBody ComplateTaskVo vo) {
        log.info("complateTask param data--------->:" + JSON.toJSONString(vo));
        List<NextTaskInfo> list = new ArrayList<NextTaskInfo>();
        String taskId = vo.getTaskId();
        try {
            if (StringUtils.isEmpty(taskId)) {
                return Result.error(1, "参数不合法，taskId不能为空");
            }
            if (StringUtils.isEmpty(vo.getSysCode())) {
                return Result.error(1, "参数不合法，系统编码不能为空");
            }
//            Task t = getProcessEngine().getTaskService().createTaskQuery().taskId(taskId).singleResult();
//        TaskInfo tt=  getProcessEngine().getHistoryService().createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
//            TaskDefinition taskDefinition = activitiService.getNextTaskInfo(taskId, vo.getData());
//            NextTaskInfo result = new NextTaskInfo();
//            result.setTaskDefineKey(taskDefinition.getKey());
//            result.setTaskText(taskDefinition.getNameExpression().getExpressionText());
//            result.setProcInstId(vo.getProInstId());
//            result.setTaskAssign(taskDefinition.getAssigneeExpression().getExpressionText());
//            result.setProIsEnd(procIsEnd(vo.getProInstId()) ? "Y" : "N");
//            list.add(result);
            Task task = taskService.createTaskQuery().taskId(vo.getTaskId()).singleResult();
            if (ObjectUtils.isEmpty(task)) {
                return Result.error(1, "异常，任务id不存在!!");
            }

            ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
            //完成任务的同时，设置流程变量，让流程变量判断连线该如何执行
            Map<String, Object> variables = new HashMap<String, Object>();
            variables.put("hxUser", vo.getCandidateUser());

            //完成任务的同时，设置流程变量，让流程变量判断连线该如何执行
            TaskService service = getProcessEngine().getTaskService();
            Authentication.setAuthenticatedUserId(vo.getUserName()); // 添加批注设置审核人,记入日志
            service.addComment(taskId, task.getProcessInstanceId(), vo.getOpinion());
            service.complete(taskId, variables);

            List<Task> taskList = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).list();
            if (taskList.size() <= 0) {
                NextTaskInfo result = new NextTaskInfo();
                result.setProIsEnd("Y");
                list.add(result);
                activitiService.updateModelLog("0", task.getProcessInstanceId());
                log.info("redis cache key:---" + vo.getSysCode().toUpperCase() + pi.getBusinessKey());
                redis.delete(vo.getSysCode().toUpperCase() + pi.getBusinessKey());
                log.info("complateTask result data , process is end --------->:" + JSON.toJSONString(list));
                return Result.success(list);
            }

            taskList.forEach(ta -> {
                NextTaskInfo result = new NextTaskInfo();
                result.setTaskId(ta.getId());
                result.setTaskDefineKey(ta.getTaskDefinitionKey());
                result.setTaskText(ta.getName());
                result.setProcInstId(ta.getProcessInstanceId());
                result.setTaskAssign(ta.getAssignee());
//                result.setProIsEnd(procIsEnd(ta.getProcessInstanceId()) ? "Y" : "N");
                result.setProIsEnd("N");
                list.add(result);
            });

            activitiService.updateModelLog("1", task.getProcessInstanceId());
            log.info("complateTask result data--------->:" + JSON.toJSONString(list));
            return Result.success(list);
        } catch (Exception e) {
            log.error("complateTask Exception-------", e);
        }
        return Result.error(1, "完成任务失败" + taskId);
    }


    @ApiOperation("动态设置自定义候选人组")
    @PostMapping("/setCandidateGroup")
    public Result<String> addGroupTask(@RequestBody SetCandidateGroupVo vo) {
        log.info("setCandidateGroup param data:===>" + JSON.toJSONString(vo));
        if (StringUtils.isEmpty(vo.getTaskId())) {
            return Result.error(1, "任务id不能为空");
        }
//        if (StringUtils.isEmpty(vo.getProInstId())) {
//            return Result.error(1, "实例id不能为空");
//        }
//        List<Task> taskList = taskService.createTaskQuery().processInstanceId(vo.getProInstId()).list();
//        taskList.forEach(task -> {
//            taskService.setVariables("dynName", vo.getParamMap());
//        });
        for (String s : vo.getCandidateUser()) {
            taskService.addCandidateUser(vo.getTaskId(), s);
        }
        return Result.success("操作成功");
    }

    /**
     * 撤销整个流程
     *
     * @param proId 流程实例id
     * @return
     */
    @PostMapping("/repealPro")
    @ApiOperation("撤销")
    public Result<String> repealPro(@RequestBody RepealProVo vo) {
        log.info("repealPro param data:===>" + JSON.toJSONString(vo.getProInstId()));
        try {
            runtimeService.deleteProcessInstance(vo.getProInstId(), "撤销流程");
            return Result.success("撤销成功");
        } catch (Exception e) {
            log.error("repealPro exception --------", e);
        }
        return Result.error(1, "撤销异常！！");
    }


    /**
     * 单步撤销
     *
     * @param proInstId    流程实例Id
     * @param toBackNoteId 任务id，如在流程设置则为设置的id，否则为默认id，格式：sid-26585B1A-9680-4331-AD31-7A107BA03AB7
     * @return
     */
    @PostMapping("/singleRepealPro")
    @ApiOperation("单步撤销")
    public Result<String> singleRepealPro(@RequestBody RepealProVo vo) {
        log.info("singleRepealPro param data:=========>" + JSON.toJSONString(vo.getProInstId()) + "=======" + JSON.toJSONString(vo.getToBackNoteId()));
        try {
            // 根据流程实例找到当前任务节点
            List<Task> tasks = taskService.createTaskQuery().processInstanceId(vo.getProInstId()).list();
            for (Task t : tasks) {
                String currentTaskId = processGoBack.turnBackNew(t.getId(), "流程单步撤销", "", vo.getToBackNoteId());
            }
            return Result.success("撤销成功");
        } catch (Exception e) {
            log.error("singleRepealPro error------------:", e);
        }
        return Result.error(1, "单步撤销失败");
    }

    /**
     * 委托任务,a指派b办理，b办理之后回到a，然后a办理之后流程继续往下
     *
     * @param taskId 任务id
     * @param owner  被委托人
     * @return
     */
    @PostMapping("/ownerTask")
    @ApiOperation("委托任务")
    public Result<String> ownerTask(@RequestBody TaskChangeOtherVo vo) {
        log.info("ownerTask param data:=========>" + JSON.toJSONString(vo));
        try {
            taskService.delegateTask(vo.getTaskId(), vo.getOwner());
            return Result.success();
        } catch (Exception e) {
            log.error("ownerTask exception----------", e);
        }
        return Result.error(1, "委托异常!!");
    }

    /**
     * 拒绝操作
     *
     * @param proInsId 实例id
     * @return
     */
    @PostMapping("/refuseTask")
    @ApiOperation("拒绝")
    public Result<String> refuseTask(@RequestBody ProcessParamVo vo) {
        log.info("refuseTask param data:=========>" + JSON.toJSONString(vo));
//        BpmnModel model = repositoryService.getBpmnModel(procDefId);
//        if (model != null) {
//            Collection<FlowElement> flowElements = model.getMainProcess().getFlowElements();
//            for (FlowElement e : flowElements) {
//                if (e.getClass().toString().contains("EndEvent")) {
//                    singleRepealPro(proInsId, e.getId());
//                    System.out.println("flowelement id:" + e.getId() + "  name:" + e.getName() + "   class:" + e.getClass().toString() + "  type:");
//                }
//            }
//        }

        try {
            List<Task> tasks = taskService.createTaskQuery().processInstanceId(vo.getProInstId()).list();
            if (ObjectUtils.isNotEmpty(tasks)) {
                Task t = tasks.get(0);
                ActivityImpl endActivity = null;
                endActivity = processGoBack.findActivitiImpl(t.getId(), "end");
                processGoBack.commitProcess(t.getId(), null, endActivity.getId());
            }
            return Result.success("操作成功");
        } catch (Exception e) {
            log.error("refuseTask exception--------", e);
        }
        return Result.error(1, "操作异常");
    }

    /**
     * 任务签收
     *
     * @param taskId 实例id
     * @return
     */
    @PostMapping("/claimTask")
    @ApiOperation("任务签收")
    public Result<String> claimTask(@RequestBody TaskChangeOtherVo vo) {
        log.info("claimTask param data:=========>" + JSON.toJSONString(vo));
        try {
            taskService.claim(vo.getTaskId(), vo.getOwner());
            return Result.success("签收成功");
        } catch (Exception e) {
            log.error("claimTask exception-----------", e);
        }
        return Result.error(1, "签收异常!!");
    }

    /**
     * 根据系统获取所有未签收任务角色
     *
     * @param sysName 系统名
     * @return
     */
    @PostMapping("/getAllTaskGroupBySysName")
    @ApiOperation("根据系统名称获取所有为完成流程当前所有未签收任务角色集合")
    public Result<TaskRoleAssignResult> getAllTaskGroupBySysName(@RequestBody AllTaskGroupBySysNameVo vo) {
        log.info("getAllTaskGroupBySysName param data:=========>" + JSON.toJSONString(vo));
//        List<String> clainList = new ArrayList<>();

        // 根据系统名查询未完成的流程实例
        List<ModelCallLog> list = modelCallLogService.selectList(new EntityWrapper<ModelCallLog>().eq("sys_code", vo.getSysCode().toUpperCase()).eq("is_end", "1"));
        List<String> instIdList = new ArrayList<>(); // 未结束流程的实例Id
        list.forEach(li -> {
            if (!procIsEnd(li.getProInstId())) {
                instIdList.add(li.getProInstId());
            }
        });
        List<Task> listTask = new ArrayList<>();
        instIdList.forEach(li -> {
            listTask.addAll(taskService.createTaskQuery().processInstanceId(li).list());
        });
        TaskRoleAssignResult result = new TaskRoleAssignResult();
        List<TaskGroup> userList = new ArrayList<TaskGroup>();
        List<TaskGroup> roleList = new ArrayList<TaskGroup>();
        listTask.forEach(li -> {
            List<String> noClainList = new ArrayList<>();
            TaskGroup group = new TaskGroup();
// 获取任务所属角色
            if (StringUtils.isEmpty(li.getAssignee())) {  // assignee为空表示未签收
                group.setTaskId(li.getId());
                group.setTaskName(li.getName());
                List<IdentityLink> linn = taskService.getIdentityLinksForTask(li.getId());
                linn.forEach(link -> {
                    noClainList.add(link.getGroupId());
                });
                group.setRoleGroup(noClainList);
                roleList.add(group);
            } else {
                group.setTaskId(li.getId());
                group.setTaskName(li.getName());
//                clainList.add(li.getAssignee());
                group.setUserName(li.getAssignee());
                userList.add(group);
            }
        });
        result.setUser(userList);
        result.setRole(roleList);
        System.out.println(JSON.toJSONString(result));
        log.info("getAllTaskGroupBySysName result data:=========>" + JSON.toJSONString(result));
//        Object[] args = new Object[2];
//        args[0] = noClainList; // 未签收任务角色集合
//        args[1] = clainList;  // 已签收任务人名集合
        return Result.success(result);
    }

    @PostMapping("/getUserTaskByProInsId")
    @ApiOperation("根据实例id获取所有userTask")
    public Result<List<AllUserTaskOutVo>> getAllUserTask(@RequestBody ProcessParamVo vo) {
        log.info("getUserTaskByProInsId param data:=========>" + JSON.toJSONString(vo));
        List<AllUserTaskOutVo> result = new ArrayList<>();
        TaskDefinition task = null;
        String definitionId = runtimeService.createProcessInstanceQuery().processInstanceId(vo.getProInstId()).singleResult().getProcessDefinitionId();
        result = getAllUserTask(definitionId);
//        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
//                .getDeployedProcessDefinition(definitionId);
//        List<ActivityImpl> activitiList = processDefinitionEntity.getActivities(); //获取流程所有节点信息
//        for (ActivityImpl activityImpl : activitiList) {
//            if ("userTask".equals(activityImpl.getProperty("type"))) {
//                AllUserTaskOutVo outVo = new AllUserTaskOutVo();
//                task = (TaskDefinition) activityImpl.getProperties().get("taskDefinition");
//                outVo.setTaskName(String.valueOf(activityImpl.getProperty("name")));
//                outVo.setTaskDefinedId(task.getKey());
//                if (ObjectUtils.isNotEmpty(task.getAssigneeExpression())) {
//                    outVo.setAssignName(task.getAssigneeExpression().getExpressionText());
//                }
//                if (ObjectUtils.isNotEmpty(task.getCandidateGroupIdExpressions())) {
//                    List<String> li = new ArrayList<String>();
//                    Set<Expression> s = task.getCandidateGroupIdExpressions();
//                    Iterator<Expression> iter = s.iterator();
//                    while (iter.hasNext()) {
//                        Expression exp = iter.next();
//                        li.add(exp.getExpressionText());
//                    }
//                    outVo.setCanditionUserGroup(li);
//                }
//                result.add(outVo);
//            }
//        }
        log.info("getUserTaskByProInsId result data:=========>" + JSON.toJSONString(result));
        return Result.success(result);
    }

    public List<AllUserTaskOutVo> getAllUserTask(String proDefinitionId) {
        List<AllUserTaskOutVo> result = new ArrayList<>();
        TaskDefinition task = null;
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
                .getDeployedProcessDefinition(proDefinitionId);
        List<ActivityImpl> activitiList = processDefinitionEntity.getActivities(); //获取流程所有节点信息
        for (ActivityImpl activityImpl : activitiList) {
            if ("userTask".equals(activityImpl.getProperty("type"))) {
                AllUserTaskOutVo outVo = new AllUserTaskOutVo();
                task = (TaskDefinition) activityImpl.getProperties().get("taskDefinition");
                outVo.setTaskName(String.valueOf(activityImpl.getProperty("name")));
                outVo.setTaskDefinedId(task.getKey());
                if (ObjectUtils.isNotEmpty(task.getAssigneeExpression())) {
                    outVo.setAssignName(task.getAssigneeExpression().getExpressionText());
                }
                if (ObjectUtils.isNotEmpty(task.getCandidateGroupIdExpressions())) {
                    List<String> li = new ArrayList<String>();
                    Set<Expression> s = task.getCandidateGroupIdExpressions();
                    Iterator<Expression> iter = s.iterator();
                    while (iter.hasNext()) {
                        Expression exp = iter.next();
                        li.add(exp.getExpressionText());
                    }
                    outVo.setCanditionUserGroup(li);
                }
                result.add(outVo);
            }
        }
        return result;
    }

    @PostMapping("/getProValiable")
    @ApiOperation("获取流程运行过程中所有参数")
    public Result<Map<String, Object>> getProValiable(@RequestBody ProcessParamVo vo) {
        log.info("getProValiable param data:=========>" + JSON.toJSONString(vo));
        Map<String, Object> a = runtimeService.getVariablesLocal(vo.getProInstId());
        log.info("getProValiable result data:------------>" + JSON.toJSONString(a));
        return Result.success(a);
    }

    @PostMapping("/getAllTaskByModelCode")
    @ApiOperation("根据模型编码获取流程所有节点")
    public Result<List<AllUserTaskOutVo>> getAllTaskByModelCode(@RequestBody GetAllTaskByModelCodeVo vo) {
        log.info("getAllTaskByModelCode param data:=========>" + JSON.toJSONString(vo));
        if (com.ht.commonactivity.utils.StringUtils.isEmpty(vo.getModeCode())) {
            return Result.error(1, "modeCode模型编码不能为空!!");
        }
        List<AllUserTaskOutVo> result = new ArrayList<>();
        ActModelDefinition model = modelDefinitionService.selectOne(new EntityWrapper().eq("model_code", vo.getModeCode()));
        if (null != model) {
            Wrapper<ActProcRelease> proc = new EntityWrapper<>();
            proc.eq("model_id", model.getModelId());
            proc.orderBy("create_time", false);
            List<ActProcRelease> list = actProcReleaseService.selectList(proc);
            if (null != list && list.size() > 0) {
                ActProcRelease release = list.get(0);
                result = getAllUserTask(release.getModelProcdefId());
            }
        }
        log.info("getAllTaskByModelCode result data:------------>" + JSON.toJSONString(result));
        return Result.success(result);
    }


}
