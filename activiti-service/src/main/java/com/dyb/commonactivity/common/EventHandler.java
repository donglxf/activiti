package com.dyb.commonactivity.common;

import org.activiti.engine.delegate.event.ActivitiEvent;

public interface EventHandler {
    public void handle(ActivitiEvent event);
}
