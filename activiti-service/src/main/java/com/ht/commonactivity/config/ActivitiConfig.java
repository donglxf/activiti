package com.ht.commonactivity.config;

import com.ht.commonactivity.utils.DistributedIdGenerator;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActivitiConfig {

    @Autowired
    private SpringProcessEngineConfiguration springProcessEngineConfiguration;

    @Bean
    public ProcessEngineConfiguration setProcessEngineConfiguration(){
        DistributedIdGenerator distributedIdGenerator = new DistributedIdGenerator();
        springProcessEngineConfiguration.setIdGenerator(distributedIdGenerator);
        return springProcessEngineConfiguration.getProcessEngineConfiguration();
    }


}
