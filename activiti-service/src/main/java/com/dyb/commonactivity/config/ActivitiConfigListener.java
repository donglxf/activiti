package com.dyb.commonactivity.config;

import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.ProcessEngineConfigurationConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ActivitiConfigListener implements ProcessEngineConfigurationConfigurer {
    @Autowired
    private GlobalActivitiEvent comActivitiEventListener;

    @Override
    public void configure(SpringProcessEngineConfiguration springProcessEngineConfiguration) {
        List<ActivitiEventListener> activitiEventListener = new ArrayList<ActivitiEventListener>();
        activitiEventListener.add(comActivitiEventListener);//配置全局监听器
        springProcessEngineConfiguration.setEventListeners(activitiEventListener);
    }
}
