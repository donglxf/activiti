package com.dyb.commonactivity.controller;

import com.dyb.commonactivity.common.EventHandler;
import lombok.extern.log4j.Log4j2;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.springframework.stereotype.Service;

@Service("myGlobalEvent")
@Log4j2
public class MyGlobalEvent implements EventHandler {

    @Override
    public void handle(ActivitiEvent event) {
        ActivitiEventType type=event.getType();
        log.info("===========" + event.getType().name());
    }
}
