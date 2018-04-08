package com.example.commonactivity.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.plugins.Page;
import com.example.commonactivity.common.ActivitiConstants;
import com.example.commonactivity.common.ModelParamter;
import com.example.commonactivity.common.RpcDeployResult;
import com.example.commonactivity.common.RpcStartParamter;
import com.example.commonactivity.common.result.PageResult;
import com.example.commonactivity.common.result.Result;
import com.example.commonactivity.entity.ActProcRelease;
import com.example.commonactivity.entity.ActRuTask;
import com.example.commonactivity.service.ActProcReleaseService;
import com.example.commonactivity.service.ActivitiService;
import com.example.commonactivity.service.ProcessGoBack;
import com.example.commonactivity.vo.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.repository.Model;
import org.activiti.engine.task.Task;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

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
    private ProcessGoBack processGoBack;


    @Autowired
    RestTemplate restTemplate;

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
     * 模型部署
     * @param paramter
     * @return
     */
//    @RequestMapping("/deploy")
//    public Result<RpcDeployResult> deploy(@RequestBody ModelParamter paramter){
//        LOGGER.info("模型部署,参数paramter:"+ JSON.toJSONString(paramter));
//        Result<RpcDeployResult> data = null;
//        if(paramter == null || StringUtils.isEmpty(paramter.getModelId())){
//            data = Result.error(1,"参数异常！");
//            return data;
//        }
//        try{
//            data = actProcReleaseService.proceDeploy(paramter,paramter.getUserId());
//        }catch(Exception e){
//            data = Result.error(1,"部署流程异常,错误信息："+e.getMessage());
//            LOGGER.error("部署流程异常!",e);
//            return data;
//        }
//        if(data == null || data.getCode() != 0){
//            data = Result.error(2,"部署流程异常...");
//            return data;
//        }
//        LOGGER.info("模型部署结束！");
//        return data;
//    }

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

//    @RequestMapping(value = "/getProcInstVarObj")
//    public Object getProcInstVarObj(@RequestBody ModelParamter paramter) {
//        LOGGER.info("getProcInstVarObj start,paramter:" + JSON.toJSONString(paramter));
//        List<HistoricVariableInstance> instances = activitiService.getProcessVarByDeployIdAndName(paramter.getProcessId(), paramter.getVariableName());
//        LOGGER.info("getProcInstVarObj end,paramter:" + JSON.toJSONString(instances));
//        if (instances.size() > 0 && instances.get(0).getValue() != null) {
//            return instances.get(0).getValue();
//        }
//        return null;
//    }

    /**
     * 根据用户、候选人、候选组 查询所有任务
     */
    @RequestMapping("/findTaskByAssignee")
    public Result<List<Task>> findMyPersonalTask(@RequestBody FindTaskBeanVo vo) {

        List<ActRuTask> tlist= activitiService.findTaskByAssigneeOrGroup(vo);
        List<ActRuTask> list1= new ArrayList<ActRuTask>();
        tlist.stream().forEach(p -> {
            if(!list1.contains(p)){
                list1.add(p);
            }
        });


        List<Task> list = getProcessEngine().getTaskService()//与正在执行的任务管理相关的Service
                .createTaskQuery()//创建任务查询对象
                /**查询条件（where部分）*/
                .taskAssignee(vo.getAssignee())//指定个人任务查询，指定办理人
//                .taskCandidateGroupIn(li)
//                      .taskCandidateUser(vo.getCandidateUser())//组任务的办理人查询  任务表assign_字段需要设置为null才有效
//                .taskCandidateGroup(vo.getCandidateUser()) // 候选组办理查询
//                      .processDefinitionId(processDefinitionId)//使用流程定义ID查询
//                      .processInstanceId(processInstanceId)//使用流程实例ID查询
//                      .executionId(executionId)//使用执行对象ID查询
                /**排序*/
                .orderByTaskCreateTime().asc()//使用创建时间的升序排列
                /**返回结果集*/
//                      .singleResult()//返回惟一结果集
//                      .count()//返回结果集的数量
//                      .listPage(firstResult, maxResults);//分页查询
                .list();//返回列表
        if (list1 != null && list1.size() > 0) {
            for (ActRuTask task : list1) {
                System.out.println("任务ID:" + task.getId());
                System.out.println("任务名称:" + task.getName());
                System.out.println("任务的创建时间:" + task.getCreateTime());
                System.out.println("任务的办理人:" + task.getAssignee());
                System.out.println("流程实例ID：" + task.getProcInstId());
                System.out.println("执行对象ID:" + task.getExecutionId());
                System.out.println("流程定义ID:" + task.getProcDefId());
                System.out.println("########################################################");
            }

        }
        if (list != null && list.size() > 0) {
            for (Task task : list) {
                System.out.println("任务ID:" + task.getId());
                System.out.println("任务名称:" + task.getName());
                System.out.println("任务的创建时间:" + task.getCreateTime());
                System.out.println("任务的办理人:" + task.getAssignee());
                System.out.println("流程实例ID：" + task.getProcessInstanceId());
                System.out.println("执行对象ID:" + task.getExecutionId());
                System.out.println("流程定义ID:" + task.getProcessDefinitionId());
                System.out.println("########################################################1");
            }
        }
//        return null;
        return Result.success(list);
    }

    /**
     * 完成任务
     */
    @RequestMapping("/complateTask")
    public void completeMyPersonalTask(String taskId) {
        //完成任务的同时，设置流程变量，让流程变量判断连线该如何执行
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("flag", "2");
        getProcessEngine().getTaskService()  //与正在执行的任务管理相关的Service
                .complete(taskId,variables);
        System.out.println("完成任务：任务ID：" + taskId);

    }

    /**
     * 获取下一个任务节点
     * @param taskId
     */
    @RequestMapping("/getNextTask")
    public void getNextTaskInfo(String taskId) {
        TaskDefinition taskDefinition = activitiService.getNextTaskInfo(taskId);

    }

    /**
     * 添加用户组
     * @param taskId
     */
    @RequestMapping("/addGroupTask")
    public void addGroupTask(String taskId) {
        getProcessEngine().getTaskService()
                .addCandidateUser(taskId, "dTest1");

    }

    /**
     * 流程回退至指定节点
     * @param procInstanceId  流程实例id 70030
     * @param toBackNoteId  流程退回节点定义id = sid-26585B1A-9680-4331-AD31-7A107BA03AB7
     */
    @RequestMapping("/processGoBack")
    public void processGoBack(String procInstanceId,String toBackNoteId){
        List<Task> tasks = getProcessEngine().getTaskService().createTaskQuery().processInstanceId(procInstanceId).list();
        for (Task task:tasks){
            try {
                processGoBack.turnBackNew(task.getId(),"流程回退","",procInstanceId,toBackNoteId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 查询流程可退回节点
     * @param procInstanceId  流程实例id 70030
     */
    @RequestMapping("/processBackHisTask")
    public Result<List<ProcessBackTaskNoteVo>> processHisTask(String procInstanceId) throws Exception {
        List<ProcessBackTaskNoteVo> backList=new ArrayList<ProcessBackTaskNoteVo>();
        List<Task> tasks = getProcessEngine().getTaskService().createTaskQuery().processInstanceId(procInstanceId).list();
        for (Task task:tasks){
            List<ActivityImpl> list=processGoBack.getactivities(task.getId());
            for (ActivityImpl li :list){
                ProcessBackTaskNoteVo vo=new ProcessBackTaskNoteVo();
                vo.setId(li.getId());
                vo.setName((String) li.getProperties().get("name"));
                backList.add(vo);
            }
        }
        return Result.success(backList);
    }



}