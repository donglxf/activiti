package com.ht.commonactivity.service.serviceTask;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;


@Service("getRuleResult")
public class GetRuleResult implements JavaDelegate{

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        RuntimeService runtimeService=delegateExecution.getEngineServices().getRuntimeService();
        System.out.println("+++++++规则引擎计算后的变量+++++++++++");
        System.out.println(delegateExecution.getVariable("reason"));
        System.out.println(delegateExecution.getVariable("leave"));
        System.out.println("+++++++规则引擎计算后的变量+++++++++++");
    }
}
