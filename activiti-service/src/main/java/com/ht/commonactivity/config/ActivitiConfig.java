package com.ht.commonactivity.config;

import com.ht.commonactivity.utils.DistributedIdGenerator;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.spring.SpringAsyncExecutor;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.ProcessEngineConfigurationConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Configuration
public class ActivitiConfig  {

    @Autowired
    private SpringProcessEngineConfiguration springProcessEngineConfiguration;



        @Bean
    public ProcessEngineConfiguration setProcessEngineConfiguration1() {
        DistributedIdGenerator distributedIdGenerator = new DistributedIdGenerator();
        springProcessEngineConfiguration.setIdGenerator(distributedIdGenerator);
        return springProcessEngineConfiguration.getProcessEngineConfiguration();
    }



}
