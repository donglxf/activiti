package com.example.commonactivity.common;

public class ActivitiConstants {

    public final static String DROOL_RULE_SERVICE_NAME = "${droolsRuleEngineService}";

    public final static String DROOL_RULE_SERVICE_TYPE = "delegateExpression";

    public final static String PROC_VERSION_PREFIX = "V.";


    public final static String DROOLS_VARIABLE_NAME = "DATA";

    /**
     * 模型调用类型参数名称，模型启动时传入，规则调用时需要从流程变量中获取，0-业务调用 1-验证中心调用
     */
    public final static String EXCUTE_TYPE_VARIABLE_NAME = "ExcuteType";
    /**
     * 实际业务调用
     */
    public final static String EXCUTE_TYPE_SERVICE = "0";
    /**
     *  验证
     */
    public final static String EXCUTE_TYPE_VERFICATION = "1";


    public final static String MODEL_UNEXIST = "model_unexist";

    public final static String RULE_SENCE_CODE = "RULE_SENCE_CODE";

    public final static String PROC_STATUS = "PROC_STATUS";
    public final static String PROC_STATUS_WARIT_START = "0";//流程未启动
    public final static String PROC_STATUS_START = "1";//流程已启动
    public final static String PROC_STATUS_SUCCESS = "2";//流程执行成功
    public final static String PROC_STATUS_EXCEPTION = "3";//流程执行异常

    public final static String SENCE_EXCUTE_RESULT_VAR = "SENCE_EXCUTE_RESULT_";//规则执行结果流程变量标识

    public final static String PROC_EXCUTE_MSG = "PROC_EXCUTE_MSG";//规则执行结果流程变量标识

    public final static String PROC_MODEL_DATA_KEY = "PROC_MODEL_DATA_KEY";

    public final static String PROC_MODEL_EXCUTE_TYPE_KEY = "excuteType";// 模型运行模式流程变量key值
    public final static String PROC_TASK_ID_VAR_KEY = "proc_task_id";// 模型执行任务taskid在流程变量key值

    public final static String PROC_BUSINESS_KEY = "PROC_BUSINESS_KEY";// 流程业务
}
