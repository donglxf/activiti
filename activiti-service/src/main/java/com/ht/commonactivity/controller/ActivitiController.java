package com.ht.commonactivity.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.ht.commonactivity.common.ActivitiConstants;
import com.ht.commonactivity.common.ModelParamter;
import com.ht.commonactivity.common.RpcDeployResult;
import com.ht.commonactivity.common.RpcStartParamter;
import com.ht.commonactivity.common.result.PageResult;
import com.ht.commonactivity.common.result.Result;
import com.ht.commonactivity.entity.ActModelDefinition;
import com.ht.commonactivity.entity.ActProcRelease;
import com.ht.commonactivity.entity.ActProcessAuditHis;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ht.commonactivity.service.*;
import com.ht.commonactivity.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.repository.Model;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.*;

/**
 * ${DESCRIPTION}
 *
 * @author wanghaobin
 * @create 2017-06-06 13:34
 */
@RestController
@Api("工作流")
public class ActivitiController implements ModelDataJsonConstants {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ActivitiController.class);

    @Resource
    private RepositoryService repositoryService;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private ActivitiService activitiService;

    @Resource
    private ActProcReleaseService actProcReleaseService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ActModelDefinitionService modelDefinitionService;

    @Autowired
    private ProcessGoBack processGoBack;

    @Autowired
    private ActProcessAuditHisService auditHisService;


    private static volatile ProcessEngine processEngine = null;

    private static ProcessEngine getProcessEngine() {
        synchronized (ProcessEngine.class) {
            if (processEngine == null) {
                processEngine = ProcessEngines.getDefaultProcessEngine();
            }
        }
        return processEngine;
    }

    /**
     * 查询待验证的模型信息
     *
     * @param page
     * @param actProcRelease
     * @return
     */
    @GetMapping("/page")
    public PageResult<List<ActProcReleaseVo>> queryProcReleaseForPage(ActProcRelease actProcRelease, int limit, int page) {
        LOGGER.info("查询模型版本分页信息开始");
        PageResult<List<ActProcReleaseVo>> result = null;
        if (actProcRelease == null || actProcRelease.getModelId() == null) {
            result = PageResult.success(null, 0);
            return result;
        }
        Page<ActProcRelease> modelReleasePage = new Page<ActProcRelease>();
        modelReleasePage.setCurrent(page);
        modelReleasePage.setSize(limit);
        modelReleasePage = actProcReleaseService.queryProcReleaseForPage(modelReleasePage, actProcRelease);
        List<ActProcReleaseVo> releaseVos = new ArrayList<ActProcReleaseVo>(modelReleasePage.getRecords().size());
        for (Iterator<ActProcRelease> iterator = modelReleasePage.getRecords().iterator(); iterator.hasNext(); ) {
            releaseVos.add(new ActProcReleaseVo(iterator.next()));
        }
        result = PageResult.success(releaseVos, modelReleasePage.getTotal());
        LOGGER.info("查询模型版本证分页信息结束");
        return result;
    }


    /**
     * 获取模型信息
     *
     * @param paramter
     * @return
     */
    @RequestMapping(value = "/getModelInfo")
    public Result<ModelParamter> getModelInfo(@RequestBody ModelParamter paramter) {
        LOGGER.info("获取模型信息,参数paramter:" + JSON.toJSONString(paramter));
        Result<ModelParamter> data = null;
        try {
            if (paramter == null || StringUtils.isEmpty(paramter.getModelId())) {
                LOGGER.error("获取模型失败：参数异常，模型ID为空！");
                data = Result.error(1, "参数异常，模型ID为空！");
                return data;
            }
            Model model = activitiService.getModelInfo(paramter.getModelId());
            if (model == null) {
                LOGGER.error("获取模型失败：没有对应的模型！");
                data = Result.error(1, "没有对应的模型！");
                return data;
            }
            paramter.setModelId(model.getId());
            paramter.setName(model.getName());
            paramter.setKey(model.getKey());
            paramter.setCategory(model.getCategory());
            data = Result.success(paramter);
        } catch (Exception e) {
            LOGGER.error("获取模型失败：", e);
            data = Result.error(1, "获取模型失败");
        }
        LOGGER.info("获取模型信息！");
        return data;
    }


    /**
     * 新增流程模型
     *
     * @param paramter
     * @return
     */
    @GetMapping(value = "/addModeler")
    @ResponseBody
    public Result<ModelParamter> addModel(ModelParamter paramter) {
        LOGGER.info("addModel paramter:" + JSON.toJSONString(paramter));
        Result<ModelParamter> data = null;
        try {
            paramter.setCategory(paramter.getBusinessId());
            String key = paramter.getKey();
            List modelList = repositoryService.createModelQuery().modelKey(key).list();
            if (modelList != null && modelList.size() > 0) {
                data = Result.error(1, "创建模型失败，模型编码已存在！");
                return data;
            }
            String modelId = activitiService.addModel(paramter);
            paramter.setModelId(modelId);
            ActModelDefinition t = new ActModelDefinition();
            t.setBelongSystem(paramter.getBelongSystem());
            t.setBusinessId(paramter.getBusinessId());
            t.setModelId(modelId);
            t.setModelCode(paramter.getKey());
            t.setModelName(paramter.getName());
            t.setModelDesc(paramter.getDescription());
            modelDefinitionService.insert(t);
            data = Result.success(paramter);
        } catch (Exception e) {
            LOGGER.error("addModel error：", e);
            data = Result.error(1, "创建模型失败");
        }
        LOGGER.info("addModel end !");
        return data;
    }

    /**
     * 删除流程模型
     *
     * @param paramter
     */
    @RequestMapping(value = "/deleteModel")
    public Result<ModelParamter> deleteModel(ModelParamter paramter) {
        LOGGER.info("delete model paramter:" + JSON.toJSONString(paramter));
        Result<ModelParamter> data = null;
        try {
            if (paramter == null || StringUtils.isEmpty(paramter.getModelId())) {
                LOGGER.error("delete model error.");
                data = Result.error(1, "参数异常.");
                return data;
            }
            Model model = repositoryService.createModelQuery().modelId(paramter.getModelId()).singleResult();
            if (model != null && StringUtils.isNotEmpty(model.getDeploymentId())) {
                data = Result.error(1, "模型已部署，无法删除！");
                return data;
            }
            repositoryService.deleteModel(paramter.getModelId());
            data = Result.success(paramter);
        } catch (Exception e) {
            LOGGER.error("delete model error,errorMsg:", e);
            data = Result.error(1, "删除模型失败");
        }
        LOGGER.info("delete model end");
        return data;
    }

    /**
     * 根据Model部署
     *
     * @param paramter
     */
    @RequestMapping(value = "/deploy")
    public Result<RpcDeployResult> deploy(ModelParamter paramter) {
        LOGGER.info("deploy model,paramter:" + JSON.toJSONString(paramter));
        Result<RpcDeployResult> data = null;
        try {
            if (paramter == null || StringUtils.isEmpty(paramter.getModelId())) {
                LOGGER.error("deploy model error.");
                data = Result.error(1, "deploy model error.");
                return data;
            }
            RpcDeployResult result = activitiService.deploy(paramter.getModelId());
            data = Result.success(result);
        } catch (Exception e) {
            LOGGER.error("deploy model error,error message：", e);
            data = Result.error(1, "部署流程失败!");
        }
        return data;
    }

    @RequestMapping("/start")
    @ApiOperation("启动模型")
    public Result<String> startProcess(RpcStartParamter paramter) {
        LOGGER.info("start model,paramter:" + JSON.toJSONString(paramter));
        Result<String> data = null;
        try {
            paramter.setType(ActivitiConstants.EXCUTE_TYPE_VERFICATION);
            paramter.setBatchSize(1);

            if (paramter == null || StringUtils.isEmpty(paramter.getProcDefId())) {
                LOGGER.info("start model error,paramter error.");
                data = Result.error(1, "参数异常！");
                return data;
            }
            String processInstanceId = activitiService.startProcess(paramter);
            data = Result.success(processInstanceId);
        } catch (Exception e) {
            data = Result.error(1, "模型启动异常！");
            LOGGER.error("deploy model error,error message：", e);
        }
        LOGGER.info("start model sucess.");
        return data;
    }


    @RequestMapping("/editModel")
    public ObjectNode getEditorJson(ModelParamterVo paramter) {
        LOGGER.info("getEditorJson invoke start ,paramter:" + JSON.toJSONString(paramter));
        ObjectNode modelNode = null;
        Model model = repositoryService.getModel(paramter.getModelId());
        if (model != null) {
            try {
                if (StringUtils.isNotEmpty(model.getMetaInfo())) {
                    modelNode = (ObjectNode) objectMapper.readTree(model.getMetaInfo());
                } else {
                    modelNode = objectMapper.createObjectNode();
                    modelNode.put(MODEL_NAME, model.getName());
                }
                modelNode.put(MODEL_ID, model.getId());
                ObjectNode editorJsonNode = (ObjectNode) objectMapper.readTree(
                        new String(repositoryService.getModelEditorSource(model.getId()), "utf-8"));
                modelNode.put("model", editorJsonNode);
            } catch (Exception e) {
                LOGGER.error("Error creating model JSON", e);
                throw new ActivitiException("Error creating model JSON", e);
            }
        }
        return modelNode;
    }

    @RequestMapping(value = "/model/save", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void saveModel(@RequestParam String modelId, @RequestBody MultiValueMap<String, String> values) {
        LOGGER.info("saveModel invoke ,paramter[modelId:" + modelId + ";values:" + JSON.toJSONString(values));
        List<String> list = values.get("json_xml");
        String json = list.get(0);
        Map<String, Object> map = new GsonJsonParser().parseMap(json);
        activitiService.createRuleTask(map);
        values.set("json_xml", JSON.toJSONString(map));
        try {
            activitiService.saveModel(modelId, values);
        } catch (Exception e) {
            LOGGER.error("Error saving model", e);
            throw new ActivitiException("Error saving model", e);
        }
        LOGGER.info("模型定义保存成功！");
    }

    @RequestMapping(value = "/editor/stencilset")
    public String getStencilset() {
        InputStream stencilsetStream = this.getClass().getClassLoader().getResourceAsStream("stencilset.json");
        try {
            return IOUtils.toString(stencilsetStream, "utf-8");
        } catch (Exception e) {
            throw new ActivitiException("Error while loading stencil set", e);
        }
    }

    /**
     * 查询列表
     *
     * @param page
     * @param actProcRelease
     * @return
     */
    @RequestMapping(value = "/list1")
    public PageResult<List<ActModelDefinition>> list1(String key, Integer page, Integer limit) {
        Wrapper<ActModelDefinition> wrapper = new EntityWrapper<>();
        if (org.apache.commons.lang.StringUtils.isNotBlank(key)) {
            wrapper.like("model_name", key);
        }
        wrapper.orderBy("cre_time", false);
        Page<ActModelDefinition> pages = new Page<>();
        pages.setCurrent(page);
        pages.setSize(limit);
        pages = modelDefinitionService.selectPage(pages, wrapper);
        return PageResult.success(pages.getRecords(), pages.getTotal());
    }

    @RequestMapping(value = "/list")
    public PageResult<List<ModelVo>> list(ModelPage modelPage) {
        PageResult<List<ModelVo>> data = null;
        try {
            if (modelPage == null) {
                data = PageResult.error(1, "参数异常，分页信息为空！");
                return data;
            }
            String modelName = StringUtils.isEmpty(modelPage.getModelName()) ? "" : modelPage.getModelName();
            modelName = StringUtils.isEmpty(modelName) ? "" : modelName;
            modelName = "%" + modelName + "%";
            List<Model> list = null;
            Long count = null;
            int start = (modelPage.getPage() - 1) * modelPage.getLimit();
            int end = start + modelPage.getLimit();
            if (StringUtils.isNotEmpty(modelPage.getModeType())) {
                list = repositoryService.createModelQuery().modelNameLike(modelName).modelCategory(modelPage.getModeType()).orderByCreateTime().desc().listPage(start, end);
                count = repositoryService.createModelQuery().modelNameLike(modelName).modelCategory(modelPage.getModeType()).count();
            } else {
                list = repositoryService.createModelQuery().modelNameLike(modelName).orderByCreateTime().desc().listPage(start, end);
                count = repositoryService.createModelQuery().modelNameLike(modelName).count();
            }
            List<ModelVo> ovs = null;
            if (list != null && list.size() > 0) {
                ovs = new ArrayList<ModelVo>();
                for (Iterator<Model> iterator = list.iterator(); iterator.hasNext(); ) {
                    ovs.add(new ModelVo(iterator.next()));
                }
            }
            data = PageResult.success(ovs, count);
        } catch (Exception e) {
            data = PageResult.error(1, "查询模型列表失败");
            LOGGER.error("查询模型列表失败!", e);
        }
        return data;
    }

    /**
     * 根据候选人 查询所有代办任务
     */
    @RequestMapping("/findTaskByCandidateUser")
    @ResponseBody
    public Result<List<TaskVo>> findTaskByCandidateUser(@RequestBody FindTaskBeanVo vo) {
        List<TaskVo> voList = new ArrayList<>();
        Result<List<TaskVo>> data = null;
        if (vo.getCandidateUser() == null) {
            data = Result.error(1, "参数异常！");
            return data;
        }
        List<Task> list = getProcessEngine().getTaskService()//与正在执行的任务管理相关的Service
                .createTaskQuery().taskCandidateUser(vo.getCandidateUser()).orderByTaskCreateTime().asc().list();

        for (Task task : list) {
            TaskVo tvo = new TaskVo();
            tvo.setCreateTime(task.getCreateTime());
            tvo.setId(task.getId());
            tvo.setExecutionId(task.getExecutionId());
            tvo.setName(task.getName());
            tvo.setProcDefId(task.getProcessDefinitionId());
            tvo.setProInstId(task.getProcessInstanceId());
            tvo.setAssign(task.getAssignee());
            voList.add(tvo);
        }
        return data = Result.success(voList);
    }

    /**
     * 根据候选组 查询所有代办任务
     */
    @RequestMapping("/findTaskByCandidateGroup")
    @ResponseBody
    public Result<List<TaskVo>> findTaskByCandidateGroup(@RequestBody FindTaskBeanVo vo) {
        List<TaskVo> voList = new ArrayList<>();
        Result<List<TaskVo>> data = null;
        if (vo.getCandidateGroup() == null) {
            data = Result.error(1, "参数异常！");
            return data;
        }
        List<Task> list = getProcessEngine().getTaskService()//与正在执行的任务管理相关的Service
                .createTaskQuery().taskCandidateGroup(vo.getCandidateGroup()).orderByTaskCreateTime().asc().list();

        for (Task task : list) {
            TaskVo tvo = new TaskVo();
            tvo.setCreateTime(task.getCreateTime());
            tvo.setId(task.getId());
            tvo.setExecutionId(task.getExecutionId());
            tvo.setName(task.getName());
            tvo.setProcDefId(task.getProcessDefinitionId());
            tvo.setProInstId(task.getProcessInstanceId());
            tvo.setAssign(task.getAssignee());
            voList.add(tvo);
        }
        return data = Result.success(voList);
    }


    /**
     * 根据用户、候选人、候选组 查询所有任务
     */
    @RequestMapping("/findTaskByAssignee")
    @ResponseBody
    public Result<List<TaskVo>> findMyPersonalTask(@RequestBody FindTaskBeanVo vo) {
        List<TaskVo> voList = new ArrayList<>();
        Result<List<TaskVo>> data = null; // new ArrayList<TaskVo>();
//        List<ActRuTask> tlist= activitiService.findTaskByAssigneeOrGroup(vo);
//        List<ActRuTask> list1= new ArrayList<ActRuTask>();
//        tlist.stream().forEach(p -> {
//            if(!list1.contains(p)){
//                list1.add(p);
//            }
//        });
        if ((vo.getAssignee() == null && vo.getCandidateUser() == null) && vo.getCandidateGroup() == null) {
            data = Result.error(1, "参数异常！");
            return data;
        }


//        List<Task>
        TaskQuery list = getProcessEngine().getTaskService()//与正在执行的任务管理相关的Service
                .createTaskQuery();//创建任务查询对象
        /**查询条件（where部分）*/
        if (vo.getAssignee() != null) {
            list.taskAssignee(vo.getAssignee()); //指定个人任务查询，指定办理人
        }
        if (vo.getCandidateUser() != null) {
            list.taskCandidateUser(vo.getCandidateUser());
        }
        if (vo.getCandidateGroup() != null) {
            list.taskCandidateGroup(vo.getCandidateGroup());
        }
        /**排序*/
        List<Task> d = list.orderByTaskCreateTime().asc()//使用创建时间的升序排列
                /**返回结果集*/
//                      .singleResult()//返回惟一结果集
//                      .count()//返回结果集的数量
//                      .listPage(firstResult, maxResults);//分页查询
                .list();//返回列表
        if (d != null && d.size() > 0) {
            for (Task task : d) {
                TaskVo tvo = new TaskVo();
                tvo.setCreateTime(task.getCreateTime());
                tvo.setId(task.getId());
                tvo.setExecutionId(task.getExecutionId());
                tvo.setName(task.getName());
                tvo.setProcDefId(task.getProcessDefinitionId());
                tvo.setProInstId(task.getProcessInstanceId());
                tvo.setAssign(task.getAssignee());
                voList.add(tvo);
            }
        }
        return data = Result.success(voList);
    }

    /**
     * 完成任务
     */
    @RequestMapping("/complateTask")
    @ResponseBody
    public Result<String> completeMyPersonalTask(@RequestBody ComplateTaskVo vo) {
        String taskId = vo.getTaskId();
        try {
            if (StringUtils.isEmpty(taskId)) {
                return Result.error(1, "参数不合法，taskId不能为空");
            }
            Task t = getProcessEngine().getTaskService().createTaskQuery().taskId(taskId).singleResult();
//        TaskInfo tt=  getProcessEngine().getHistoryService().createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
            //完成任务的同时，设置流程变量，让流程变量判断连线该如何执行
            Map<String, Object> variables = new HashMap<String, Object>();
            variables.put("flag", "2");
            TaskService service = getProcessEngine().getTaskService();  //与正在执行的任务管理相关的Service
            Authentication.setAuthenticatedUserId("cmc"); // 添加批注设置审核人
            service.addComment(taskId, t.getProcessInstanceId(), vo.getOpinion());
            service.complete(taskId, variables);

            // 记录审批内容
            ActProcessAuditHis his = new ActProcessAuditHis();
            his.setAssignee(t.getAssignee());
            his.setOpinion(vo.getOpinion());
            his.setProDefineId(t.getProcessDefinitionId());
            his.setProInstId(t.getProcessInstanceId());
            his.setStatus("completed");
            his.setTaskDefineKey(t.getTaskDefinitionKey());
            his.setTaskId(t.getId());
            his.setTaskName(t.getName());
//            auditHisService.insert(his);

            return Result.success("完成任务：任务ID：" + taskId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.error(1, "完成任务失败" + taskId);
    }

    /**
     * 获取下一个任务节点
     *
     * @param taskId
     */
    @RequestMapping("/getNextTask")
    public void getNextTaskInfo(String taskId) {
        TaskDefinition taskDefinition = activitiService.getNextTaskInfo(taskId);

    }

    /**
     * 添加用户组
     *
     * @param taskId
     */
    @RequestMapping("/addGroupTask")
    public void addGroupTask(String taskId) {
        getProcessEngine().getTaskService()
                .addCandidateUser(taskId, "dTest1");

    }

    /**
     * 流程回退至指定节点
     *
     * @param procInstanceId 流程实例id 70030
     * @param toBackNoteId   流程退回节点定义id = sid-26585B1A-9680-4331-AD31-7A107BA03AB7
     */
    @RequestMapping("/processGoBack")
    @ResponseBody
    public Result<String> processGoBack(String procInstanceId, String toBackNoteId) {
        List<Task> tasks = getProcessEngine().getTaskService().createTaskQuery().processInstanceId(procInstanceId).list();
        for (Task task : tasks) {
            try {
                String currentTaskId = processGoBack.turnBackNew(task.getId(), "流程回退", "", procInstanceId, toBackNoteId);
                return Result.success(currentTaskId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 流程回退至指定节点后再退回至原节点
     *
     * @param procInstanceId 流程实例id 70030
     * @param toBackNoteId   流程退回节点定义id = sid-26585B1A-9680-4331-AD31-7A107BA03AB7
     */
    @RequestMapping("/processGoTargetTask")
    public void processGoTargetTask(String procInstanceId, String toTargetNoteId) {
        processGoBack(procInstanceId, toTargetNoteId);
    }

    /**
     * 查询流程可退回节点
     *
     * @param procInstanceId 流程实例id ：70030
     */
    @RequestMapping("/processBackTaskList")
    @ResponseBody
    public Result<List<ProcessBackTaskNoteVo>> processHisTask(String procInstanceId) throws Exception {
        List<ProcessBackTaskNoteVo> backList = new ArrayList<ProcessBackTaskNoteVo>();
        List<ProcessBackTaskNoteVo> newBackList = new ArrayList<ProcessBackTaskNoteVo>();
        List<Task> tasks = getProcessEngine().getTaskService().createTaskQuery().processInstanceId(procInstanceId).list();
        for (Task task : tasks) {
            List<ActivityImpl> list = processGoBack.getactivities(task.getId());
            for (ActivityImpl li : list) {
                ProcessBackTaskNoteVo vo = new ProcessBackTaskNoteVo();
                vo.setId(li.getId());
                vo.setName((String) li.getProperties().get("name"));
                backList.add(vo);
            }
        }
        backList.stream().forEach(back -> {
            if (!newBackList.contains(back)) {
                newBackList.add(back);
            }
        });
        return Result.success(newBackList);
    }

    @RequestMapping("/processHis")
    @ResponseBody
    public Result<List<ProAutoResult>> queryHistoricActivitiInstance(String processInstanceId) {
        List<ProAutoResult> result_list=new ArrayList<>();
        TaskService taskService= getProcessEngine().getTaskService();
        List<HistoricTaskInstance> list = getProcessEngine().getHistoryService()
                .createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId)
                .list();
        if (list != null && list.size() > 0) {
            for (HistoricTaskInstance hti : list) {
                ProAutoResult result=new ProAutoResult();
                List<Comment> li= taskService.getTaskComments(hti.getId());
                for(Comment com:li){
                    result.setComment(com.getFullMessage());
                    result.setUserName(com.getUserId());
                }
                result.setAssignee(hti.getAssignee());
                result.setTaskName(hti.getName());
                result.setTaskId(hti.getId());
                result.setProcessDefinitionId(hti.getProcessDefinitionId());
                result.setProcessInstanceId(hti.getProcessInstanceId());
                result.setTaskDefinitionKey(hti.getTaskDefinitionKey());
                result_list.add(result);
            }
        }
        return Result.success(result_list);
    }


}