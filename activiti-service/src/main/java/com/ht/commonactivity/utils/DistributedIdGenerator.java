package com.ht.commonactivity.utils;

import org.activiti.engine.impl.cfg.IdGenerator;

import java.util.UUID;

public class DistributedIdGenerator implements IdGenerator {

    @Override
    public String getNextId() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
