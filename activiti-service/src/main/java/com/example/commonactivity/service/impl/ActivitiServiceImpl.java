package com.example.commonactivity.service.impl;

import com.example.commonactivity.common.*;
import com.example.commonactivity.common.result.Result;
import com.example.commonactivity.service.ActivitiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.internal.LinkedTreeMap;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FieldExtension;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.ServiceTask;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;

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
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private HistoryService historyService;


    /**
     * 获取模型信息
     *
     * @param modelId
     * @return
     */
    @RequestMapping(value = "/getModelInfo")
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
        return result;
    }


    public String startProcess(RpcStartParamter paramter) {
        // 流程定义ID
        String procDefId = paramter.getProcDefId();
        Map<String,Object> modelParamter = new HashMap<String,Object>();
        // 模型运行所需数据
        modelParamter.put(ActivitiConstants.PROC_MODEL_DATA_KEY,paramter.getData());
        // 模型执行类型：type:0 模型验证，1 业务调用
        modelParamter.put(ActivitiConstants.PROC_MODEL_EXCUTE_TYPE_KEY, paramter.getType());
        // 模型执行任务流水号
        modelParamter.put(ActivitiConstants.PROC_TASK_ID_VAR_KEY,paramter.getTaskId());
        ProcessInstance instance = runtimeService.startProcessInstanceById(procDefId, modelParamter);
        return instance.getId();
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
        return  historyService.createHistoricVariableInstanceQuery().processInstanceId(processId).variableName(variableName).list();

    }

    public List<HistoricVariableInstance> getHisProcessVarByDeployIdAndNameLike(String processId, String variableName) {
        return historyService.createHistoricVariableInstanceQuery().processInstanceId(processId).variableNameLike(variableName).list();
    }

    public void createRuleTask(Map<String, Object> map){
        List<Map> childShapes = (List<Map>) map.get("childShapes");
        for (Map map1 : childShapes) {
            Map properties = (Map) map1.get("properties");
            Map stencil = (Map) map1.get("stencil");
            //根据节点id,判断是否为自定义节点
            if ("custom".equals(stencil.get("id"))) {
                String senceVersion = String.valueOf(properties.get("scene_version"));
                String senceCode = String.valueOf(properties.get("sence_code"));
                List<Map> fields = new ArrayList<Map>();
                Map<String,String> codeFieldMap = new LinkedTreeMap<String,String>();
                codeFieldMap.put("name","versionExp");
                codeFieldMap.put("implementation",senceVersion);
                codeFieldMap.put("stringValue","");
                codeFieldMap.put("express","");
                codeFieldMap.put("string",senceVersion);
                Map<String,String> versionFieldMap = new LinkedTreeMap<String,String>();
                versionFieldMap.put("name","senceCodeExp");
                versionFieldMap.put("implementation",senceVersion);
                versionFieldMap.put("stringValue","");
                versionFieldMap.put("express","");
                versionFieldMap.put("string",senceVersion);
                fields.add(codeFieldMap);
                fields.add(versionFieldMap);
                Map<String,Object> taskFieldMap = new LinkedTreeMap<String,Object>();
                taskFieldMap.put("fields",fields);
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


}
