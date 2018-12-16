package com.ht.commonactivity.service.impl;

import com.alibaba.fastjson.JSON;
import com.ht.commonactivity.common.*;
import com.ht.commonactivity.common.result.Result;
import com.ht.commonactivity.entity.ActExcuteTask;
import com.ht.commonactivity.entity.ActProcRelease;
import com.ht.commonactivity.entity.ActRuTask;
import com.ht.commonactivity.mapper.ActExcuteTaskMapper;
import com.ht.commonactivity.mapper.ActProcReleaseMapper;
import com.ht.commonactivity.service.ActivitiService;
import com.ht.commonactivity.vo.FindTaskBeanVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.internal.LinkedTreeMap;
import com.ht.commonactivity.vo.Leave;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FieldExtension;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.ServiceTask;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.javax.el.ExpressionFactory;
import org.activiti.engine.impl.javax.el.ValueExpression;
import org.activiti.engine.impl.juel.ExpressionFactoryImpl;
import org.activiti.engine.impl.juel.SimpleContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;

@Service
public class ActivitiServiceImpl implements ActivitiService, ModelDataJsonConstants {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivitiServiceImpl.class);

    @Resource
    private RepositoryService repositoryService;
    @Resource
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private HistoryService historyService;

    @Resource
    private ActProcReleaseMapper actProcReleaseMapper;

    @Resource
    private ActExcuteTaskMapper actExcuteTaskMapper;


    public Model getModelInfo(String modelId) {
        LOGGER.info("getModelInfo invoke paramter modelId = " + modelId);
        Model model = repositoryService.createModelQuery().modelId(modelId).singleResult();
        LOGGER.info("getModelInfo invoke end ");
        return model;
    }


    public String addModel(ModelParamter paramter) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode editorNode = objectMapper.createObjectNode();
        editorNode.put("id", "canvas");
        editorNode.put("resourceId", "canvas");
        ObjectNode stencilSetNode = objectMapper.createObjectNode();
        stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
        editorNode.put("stencilset", stencilSetNode);
        Model modelData = repositoryService.newModel();
        ObjectNode modelObjectNode = objectMapper.createObjectNode();
        modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, paramter.getName());
        modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
        modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, paramter.getDescription());
        modelData.setMetaInfo(modelObjectNode.toString());
        modelData.setName(paramter.getName());
        modelData.setKey(paramter.getKey());
        modelData.setCategory(paramter.getCategory());
        // 存入ACT_RE_MODEL
        repositoryService.saveModel(modelData);
        // 存入ACT_GE_BYTEARRAY
        repositoryService.addModelEditorSource(modelData.getId(), editorNode.toString().getBytes("utf-8"));
        return modelData.getId();
    }

    public RpcDeployResult deploy(String modelId) throws Exception {
        Result<RpcDeployResult> data = null;
        Model modelData = repositoryService.getModel(modelId);
        ObjectNode modelNode;
        modelNode = (ObjectNode) new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelData.getId()));
        byte[] bpmnBytes = null;
        BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
        bpmnBytes = new BpmnXMLConverter().convertToXML(model, "GBK");
        String processName = modelData.getName() + ".bpmn20.xml";
        Deployment deployment = repositoryService.createDeployment().name(modelData.getName())
                .addString(processName, new String(bpmnBytes)).deploy();
        modelData.setDeploymentId(deployment.getId());
        repositoryService.saveModel(modelData);
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
        List<RpcSenceInfo> modelSences = new ArrayList<RpcSenceInfo>();
        RpcDeployResult result = new RpcDeployResult();
        result.setProcessDefineId(processDefinition.getId());
        result.setDeploymentId(deployment.getId());
        result.setSences(getProcScenceList(model));
        result.setVersion(ActivitiConstants.PROC_VERSION_PREFIX + processDefinition.getVersion());


        String prcdefId = result.getProcessDefineId();
        String modelVersion = result.getVersion();
        // 往模型版本控制表中插入一条记录
        Model modelRpc = getModelInfo(modelId);
        ActProcRelease release = new ActProcRelease();
        release.setModelCategory(modelRpc.getCategory());
        release.setModelCode(modelRpc.getKey());
        release.setModelId(modelData.getId());
        release.setModelName(modelRpc.getName());
        release.setModelVersion(modelVersion);
        release.setModelProcdefId(prcdefId);
        release.setVersionType("0");
        release.setCreateTime(new Date(System.currentTimeMillis()));
//        release.setCreateUser(userId);
        actProcReleaseMapper.insert(release);


        return result;
    }

    public RpcDeployResult deployRuleModel(String modelId) throws Exception {
        Result<RpcDeployResult> data = null;
//        InputStream bpmnInputStream = this.getClass().getClassLoader()
//                .getResourceAsStream("drlres/ruleTaskModel.bpmn20.xml");
//        InputStream droolsInputStream = this.getClass().getClassLoader()
//                .getResourceAsStream("drlres/new.drl");
//
//        Deployment deployment = ProcessEngines.getDefaultProcessEngine().getRepositoryService()
//                .createDeployment()
//                .addInputStream("drlres/ruleTaskModel.bpmn20.xml", bpmnInputStream)
//                .addInputStream("drlres/new.drl", droolsInputStream)
//                .deploy();

        Model modelData = repositoryService.getModel(modelId);
        ObjectNode modelNode;
        modelNode = (ObjectNode) new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelData.getId()));
        byte[] bpmnBytes = null;
        BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
        bpmnBytes = new BpmnXMLConverter().convertToXML(model, "GBK");
        String processName = modelData.getName() + ".bpmn20.xml";
        Deployment deployment = repositoryService.createDeployment().name(modelData.getName())
                .addString(processName, new String(bpmnBytes)).deploy();
        modelData.setDeploymentId(deployment.getId());
        repositoryService.saveModel(modelData);
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
        List<RpcSenceInfo> modelSences = new ArrayList<RpcSenceInfo>();
        RpcDeployResult result = new RpcDeployResult();
        result.setProcessDefineId(processDefinition.getId());
        result.setDeploymentId(deployment.getId());
        result.setSences(getProcScenceList(model));
        result.setVersion(ActivitiConstants.PROC_VERSION_PREFIX + processDefinition.getVersion());


        String prcdefId = result.getProcessDefineId();
        String modelVersion = result.getVersion();
        // 往模型版本控制表中插入一条记录
        Model modelRpc = getModelInfo(modelId);
        ActProcRelease release = new ActProcRelease();
        release.setModelCategory(modelRpc.getCategory());
        release.setModelCode(modelRpc.getKey());
        release.setModelId(modelData.getId());
        release.setModelName(modelRpc.getName());
        release.setModelVersion(modelVersion);
        release.setModelProcdefId(prcdefId);
        release.setVersionType("0");
        release.setCreateTime(new Date(System.currentTimeMillis()));
//        release.setCreateUser(userId);
        actProcReleaseMapper.insert(release);


        return result;
    }

    /**
     * 启动流程
     * @param paramter
     * @return
     */
    public String startProcess(RpcStartParamter paramter) {
        // 获取模型版本信息
        Long releaseId = getProcReleaseId(paramter.getProcDefId(), paramter.getVersion());
        if (releaseId == null) {
            return null;
        }
        ActExcuteTask task = this.saveTask(releaseId, paramter.getType(), paramter.getBatchId(), JSON.toJSONString(paramter), "");

        // 流程定义ID
        String procDefId = paramter.getProcDefId();
        Map<String, Object> modelParamter = new HashMap<String, Object>();
        // 模型运行所需数据
        modelParamter.put(ActivitiConstants.PROC_MODEL_DATA_KEY, paramter.getData());
        // 模型执行类型：type:0 模型验证，1 业务调用
        modelParamter.put(ActivitiConstants.PROC_MODEL_EXCUTE_TYPE_KEY, paramter.getType());
        // 模型执行任务流水号
        modelParamter.put(ActivitiConstants.PROC_TASK_ID_VAR_KEY, task.getId());

//        modelParamter.put("userID","role_user");
        ProcessInstance instance = runtimeService.startProcessInstanceById(procDefId, modelParamter);

        // 更新模型任务流程实例ID
        if (StringUtils.isNotEmpty(instance.getId())) {
            task.setProcInstId(instance.getId());
        } else {
            task.setStatus("4");
        }
        updateTask(task);
        String nextId=getNextNode(instance.getId());
        LOGGER.error("nextId=="+nextId);

//        RuntimeService runtimeService = ProcessEngines.getDefaultProcessEngine().getRuntimeService();
//        ProcessInstance pi = runtimeService
//                .startProcessInstanceByKey("myBusinessRuleProcess");
//        TaskService taskService = ProcessEngines.getDefaultProcessEngine().getTaskService();
//        Map<String, Object> vars = new HashMap<String, Object>();
//        vars.put("leave", new Leave(1, 3));
        /**
         * 当前任务
         */
//        List<Task> tasks = taskService.createTaskQuery()
//                .processInstanceId(pi.getId()).list();
//        for (Task task1 : tasks) {
//            System.out.println(task1.getId() + " , " + task1.getName());
//            taskService.complete(task1.getId(), vars);
//        }
        /**
         * 下一步任务
         */
//        tasks = taskService.createTaskQuery().processInstanceId(pi.getId())
//                .list();
//        for (Task task1 : tasks) {
//            System.out.println(task1.getId() + " , " + task1.getName());
//        }

        return instance.getId();
    }

    private void updateTask(ActExcuteTask task) {
        actExcuteTaskMapper.updateById(task);
    }

    /**
     * 获取当前流程的下一个节点
     * @param procInstanceId
     * @return
     */
    public String getNextNode(String procInstanceId){
        ProcessEngine processEngine=ProcessEngines.getDefaultProcessEngine();
        // 1、首先是根据流程ID获取当前任务：
        List<Task> tasks = processEngine.getTaskService().createTaskQuery().processInstanceId(procInstanceId).list();
        String nextId = "";
        for (Task task : tasks) {
            RepositoryService rs = processEngine.getRepositoryService();
            // 2、然后根据当前任务获取当前流程的流程定义，然后根据流程定义获得所有的节点：
            ProcessDefinitionEntity def = (ProcessDefinitionEntity) ((RepositoryServiceImpl) rs)
                    .getDeployedProcessDefinition(task.getProcessDefinitionId());
            List<ActivityImpl> activitiList = def.getActivities(); // rs是指RepositoryService的实例
            // 3、根据任务获取当前流程执行ID，执行实例以及当前流程节点的ID：
            String excId = task.getExecutionId();
            RuntimeService runtimeService = processEngine.getRuntimeService();
            ExecutionEntity execution = (ExecutionEntity) runtimeService.createExecutionQuery().executionId(excId)
                    .singleResult();
            String activitiId = execution.getActivityId();
            // 4、然后循环activitiList
            // 并判断出当前流程所处节点，然后得到当前节点实例，根据节点实例获取所有从当前节点出发的路径，然后根据路径获得下一个节点实例：
            for (ActivityImpl activityImpl : activitiList) {
                String id = activityImpl.getId();
                if (activitiId.equals(id)) {
                    LOGGER.debug("当前任务：" + activityImpl.getProperty("name")); // 输出某个节点的某种属性
                    List<PvmTransition> outTransitions = activityImpl.getOutgoingTransitions();// 获取从某个节点出来的所有线路
                    for (PvmTransition tr : outTransitions) {
                        PvmActivity ac = tr.getDestination(); // 获取线路的终点节点
                        LOGGER.debug("下一步任务任务：" + ac.getProperty("name"));
                        nextId = ac.getId();
                    }
                    break;
                }
            }
        }
        return nextId;
    }

    private ActExcuteTask saveTask(Long releaseId, String type, Long batchId, String inParamter, String userId) {
        ActExcuteTask task = new ActExcuteTask();
        task.setProcReleaseId(releaseId);
        task.setType(StringUtils.isEmpty(type) ? "1" : type);
        task.setCreateTime(new Date(System.currentTimeMillis()));
        task.setBatchId(batchId);
        task.setInParamter(inParamter);
        task.setCreateUser(userId);
        actExcuteTaskMapper.insert(task);
        return task;
    }

    public ObjectNode getEditorJson(String modelId) throws Exception {
        LOGGER.info("启动模型,参数modelId:" + modelId);
        ObjectNode modelNode = null;
        Model model = repositoryService.getModel(modelId);
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
        return modelNode;
    }

    public void saveModel(String modelId, MultiValueMap<String, String> values) throws Exception {
        Model model = repositoryService.getModel(modelId);
        ObjectNode modelJson = (ObjectNode) objectMapper.readTree(model.getMetaInfo());
        modelJson.put(MODEL_NAME, values.getFirst("name"));
        modelJson.put(MODEL_DESCRIPTION, values.getFirst("description"));
        model.setMetaInfo(modelJson.toString());
        model.setName(values.getFirst("name"));
        repositoryService.saveModel(model);
        repositoryService.addModelEditorSource(model.getId(), values.getFirst("json_xml").getBytes("utf-8"));
        InputStream svgStream = new ByteArrayInputStream(values.getFirst("svg_xml").getBytes("utf-8"));
        TranscoderInput input = new TranscoderInput(svgStream);
        PNGTranscoder transcoder = new PNGTranscoder();
        // Setup output
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        TranscoderOutput output = new TranscoderOutput(outStream);
        // Do the transformation
        transcoder.transcode(input, output);
        final byte[] result = outStream.toByteArray();
        repositoryService.addModelEditorSourceExtra(model.getId(), result);
        outStream.close();
    }


    public List<HistoricVariableInstance> getProcessVarByDeployIdAndName(String processId, String variableName) {
        return historyService.createHistoricVariableInstanceQuery().processInstanceId(processId).variableName(variableName).list();

    }

    public List<HistoricVariableInstance> getHisProcessVarByDeployIdAndNameLike(String processId, String variableName) {
        return historyService.createHistoricVariableInstanceQuery().processInstanceId(processId).variableNameLike(variableName).list();
    }

    public void createRuleTask(Map<String, Object> map) {
        List<Map> childShapes = (List<Map>) map.get("childShapes");
        for (Map map1 : childShapes) {
            Map properties = (Map) map1.get("properties");
            Map stencil = (Map) map1.get("stencil");
            //根据节点id,判断是否为自定义节点
            if ("custom".equals(stencil.get("id"))) {
                String senceVersion = String.valueOf(properties.get("scene_version"));
                String senceCode = String.valueOf(properties.get("sence_code"));
                List<Map> fields = new ArrayList<Map>();
                Map<String, String> codeFieldMap = new LinkedTreeMap<String, String>();
                codeFieldMap.put("name", "versionExp");
                codeFieldMap.put("implementation", senceVersion);
                codeFieldMap.put("stringValue", "");
                codeFieldMap.put("express", "");
                codeFieldMap.put("string", senceVersion);
                Map<String, String> versionFieldMap = new LinkedTreeMap<String, String>();
                versionFieldMap.put("name", "senceCodeExp");
                versionFieldMap.put("implementation", senceVersion);
                versionFieldMap.put("stringValue", "");
                versionFieldMap.put("express", "");
                versionFieldMap.put("string", senceVersion);
                fields.add(codeFieldMap);
                fields.add(versionFieldMap);
                Map<String, Object> taskFieldMap = new LinkedTreeMap<String, Object>();
                taskFieldMap.put("fields", fields);
                stencil.put("id", "ServiceTask");
                properties.put("overrideid", "");
                properties.put("name", "");
                properties.put("documentation", "");
                properties.put("asynchronousdefinition", "false");
                properties.put("exclusivedefinition", "false");
                properties.put("executionlisteners", "");
                properties.put("multiinstance_type", "None");
                properties.put("multiinstance_cardinality", "");
                properties.put("multiinstance_collection", "");
                properties.put("multiinstance_variable ", "");
                properties.put("multiinstance_condition", "");
                properties.put("isforcompensation ", "false");
                properties.put("servicetaskclass", "");
                properties.put("servicetaskexpression", "");
                properties.put("servicetaskdelegateexpression", "${processTestService}");
                properties.put("servicetaskfields", taskFieldMap);
                properties.put("servicetaskresultvariable", "");
                properties.put("delegateExpression", "");
            }
        }
        //重新设置回去
        map.put("childShapes", childShapes);
    }


    private List<RpcSenceInfo> getProcScenceList(BpmnModel model) {
        List<RpcSenceInfo> modelSences = new ArrayList<RpcSenceInfo>();
        if (model != null) {
            Collection<FlowElement> flowElements = model.getMainProcess().getFlowElements();
            ServiceTask task = null;
            String implementation = null;
            String implementationType = null;
            String senceCode = null;
            String senceName = null;
            String version = null;
            List<FieldExtension> fieldExtensions = null;
            RpcSenceInfo senceInfo = null;
            for (FlowElement e : flowElements) {
                if (e instanceof ServiceTask) {
                    task = (ServiceTask) e;
                    implementation = task.getImplementation();
                    implementationType = task.getImplementationType();
                    fieldExtensions = task.getFieldExtensions();
                    if (ActivitiConstants.DROOL_RULE_SERVICE_NAME.equals(implementation) && ActivitiConstants.DROOL_RULE_SERVICE_TYPE.equals(implementationType) && fieldExtensions.size() > 1) {
                        senceCode = fieldExtensions.get(0).getStringValue();
                        version = fieldExtensions.get(1).getStringValue();
                        senceInfo = new RpcSenceInfo();
                        senceInfo.setSenceCode(senceCode);
                        senceInfo.setSenceVersion(version);
                        modelSences.add(senceInfo);
                    }
                }
            }
        }
        return modelSences;
    }

    private Long getProcReleaseId(String procDefId, String version) {
        Map<String, Object> paramter = new HashMap<String, Object>();
        paramter.put("MODEL_PROCDEF_ID", procDefId);
        paramter.put("MODEL_VERSION", version);
        paramter.put("IS_EFFECT", "0");//有效
        List<ActProcRelease> releases = actProcReleaseMapper.selectByMap(paramter);
        if (releases == null || releases.size() == 0) {
            return null;
        }
        return releases.get(0).getId();
    }


    public TaskDefinition getNextTaskInfo(String taskId) {
        String id = null;
        TaskDefinition task = null;
        String procInstId = taskService.createTaskQuery().taskId(taskId).singleResult().getProcessDefinitionId(); // 流程实例id
        String definitionId = runtimeService.createProcessInstanceQuery().processInstanceId(procInstId).singleResult().getProcessDefinitionId();
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
                .getDeployedProcessDefinition(definitionId);
        ExecutionEntity execution = (ExecutionEntity) runtimeService.createProcessInstanceQuery().processInstanceId(procInstId).singleResult();
        String activitiId = execution.getActivityId(); ////当前流程节点Id信息
        List<ActivityImpl> activitiList = processDefinitionEntity.getActivities(); //获取流程所有节点信息
        //遍历所有节点信息
        for (ActivityImpl activityImpl : activitiList) {
            id = activityImpl.getId();
            if (activitiId.equals(id)) {
                //获取下一个节点信息
                task = nextTaskDefinition(activityImpl, activityImpl.getId(), null, procInstId);
                break;
            }
        }

        return task;

    }

    @Override
    public List<ActRuTask> findTaskByAssigneeOrGroup(FindTaskBeanVo vo) {
        return actProcReleaseMapper.findTaskByAssigneeOrGroup(vo);
    }

    /**
     * 下一个任务节点信息,
     * <p>
     * 如果下一个节点为用户任务则直接返回,
     * <p>
     * 如果下一个节点为排他网关, 获取排他网关Id信息, 根据排他网关Id信息和execution获取流程实例排他网关Id为key的变量值,
     * 根据变量值分别执行排他网关后线路中的el表达式, 并找到el表达式通过的线路后的用户任务
     *
     * @param activityImpl activityImpl     流程节点信息
     * @param String       activityId             当前流程节点Id信息
     * @param String       elString               排他网关顺序流线段判断条件
     * @param String       processInstanceId      流程实例Id信息
     * @return
     */
    private TaskDefinition nextTaskDefinition(ActivityImpl activityImpl, String activityId, String elString, String processInstanceId) {

        PvmActivity ac = null;

        Object s = null;

        // 如果遍历节点为用户任务并且节点不是当前节点信息
        if ("userTask".equals(activityImpl.getProperty("type")) && !activityId.equals(activityImpl.getId())) {
            // 获取该节点下一个节点信息
            TaskDefinition taskDefinition = ((UserTaskActivityBehavior) activityImpl.getActivityBehavior())
                    .getTaskDefinition();
            return taskDefinition;
        } else {
            // 获取节点所有流向线路信息
            List<PvmTransition> outTransitions = activityImpl.getOutgoingTransitions();
            List<PvmTransition> outTransitionsTemp = null;
            for (PvmTransition tr : outTransitions) {
                ac = tr.getDestination(); // 获取线路的终点节点
                // 如果流向线路为排他网关
                if ("exclusiveGateway".equals(ac.getProperty("type"))) {
                    outTransitionsTemp = ac.getOutgoingTransitions();

                    // 如果网关路线判断条件为空信息
                    if (StringUtils.isEmpty(elString)) {
                        // 获取流程启动时设置的网关判断条件信息
                        elString = getGatewayCondition(ac.getId(), processInstanceId);
                    }

                    // 如果排他网关只有一条线路信息
                    if (outTransitionsTemp.size() == 1) {
                        return nextTaskDefinition((ActivityImpl) outTransitionsTemp.get(0).getDestination(), activityId,
                                elString, processInstanceId);
                    } else if (outTransitionsTemp.size() > 1) { // 如果排他网关有多条线路信息
                        for (PvmTransition tr1 : outTransitionsTemp) {
                            s = tr1.getProperty("conditionText"); // 获取排他网关线路判断条件信息
                            // 判断el表达式是否成立
                            if (isCondition(ac.getId(), StringUtils.trim(s.toString()), elString)) {
                                return nextTaskDefinition((ActivityImpl) tr1.getDestination(), activityId, elString,
                                        processInstanceId);
                            }
                        }
                    }
                } else if ("userTask".equals(ac.getProperty("type"))) {
                    return ((UserTaskActivityBehavior) ((ActivityImpl) ac).getActivityBehavior()).getTaskDefinition();
                } else {
                }
            }
            return null;
        }
    }


    /**
     * 查询流程启动时设置排他网关判断条件信息
     *
     * @param String gatewayId          排他网关Id信息, 流程启动时设置网关路线判断条件key为网关Id信息
     * @param String processInstanceId  流程实例Id信息
     * @return
     */
    public String getGatewayCondition(String gatewayId, String processInstanceId) {
        Execution execution = runtimeService.createExecutionQuery().processInstanceId(processInstanceId).singleResult();
        Object object = runtimeService.getVariable(execution.getId(), gatewayId);
        return object == null ? "" : object.toString();
    }

    /**
     * 根据key和value判断el表达式是否通过信息
     *
     * @param String key    el表达式key信息
     * @param String el     el表达式信息
     * @param String value  el表达式传入值信息
     * @return
     */
    public boolean isCondition(String key, String el, String value) {
        ExpressionFactory factory = new ExpressionFactoryImpl();
        SimpleContext context = new SimpleContext();
        context.setVariable(key, factory.createValueExpression(value, String.class));
        ValueExpression e = factory.createValueExpression(context, el, boolean.class);
        return (Boolean) e.getValue(context);
    }

    public void getCurrentTask(){
//        ProcessEngines.getDefaultProcessEngine().getTaskService().createTaskQuery().
    }

}
