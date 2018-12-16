package com.ht.commonactivity.config;

import com.ht.commonactivity.common.EventHandler;
import com.ht.commonactivity.utils.WebAppUtil;
import lombok.extern.log4j.Log4j2;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@Component
public class GlobalActivitiEvent implements ActivitiEventListener {

    private static Map<String, String> handlers = new HashMap<String, String>();

    static {
        handlers.put("TASK_CREATED", "myGlobalEvent");
    }

    @Override
    public void onEvent(ActivitiEvent activitiEvent) {
        String eventType = activitiEvent.getType().name().toUpperCase();
        log.info("Event Type is ==>" + eventType);
        String eventHandlerBeanId = handlers.get(eventType);
        if (eventHandlerBeanId != null) {
            EventHandler handler = (EventHandler) WebAppUtil.getBean(eventHandlerBeanId);
            handler.handle(activitiEvent);
        }
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }

    public Map<String, String> getHandlers() {

        return handlers;
    }

    public void setHandlers(Map<String, String> handlers) {
        this.handlers = handlers;
    }
}
