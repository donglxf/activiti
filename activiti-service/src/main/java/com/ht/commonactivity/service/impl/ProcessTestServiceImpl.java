package com.ht.commonactivity.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.ht.commonactivity.vo.FrontSeaDtoOut;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Service("processTestService")
public class ProcessTestServiceImpl implements JavaDelegate ,Serializable{

    private Expression url;


    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessTestServiceImpl.class);

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        RestTemplate restTemplate=new RestTemplate();
        String version = (String) url.getValue(execution);
        String ur="http://172.16.200.112:30526/black/frontSea";
//        MultiValueMap<String, Object> obj = new LinkedMultiValueMap<String, Object>();
//        obj.add("realName","王元坤");
//        obj.add("identityCard","441622199210206694");
//        obj.add("idType","1");
//        obj.add("reasonNo","01");
        Map<String,Object> obj=new HashMap<String,Object>();
        obj.put("realName","王元坤");
        obj.put("identityCard","441622199210206694");
        obj.put("idType","1");
        obj.put("reasonNo","01");
        JSONObject jsonObject = new JSONObject(obj);

        FrontSeaDtoOut t=restTemplate.postForObject(ur,jsonObject,FrontSeaDtoOut.class);
        LOGGER.info("============>"+JSONObject.toJSONString(t));
        LOGGER.info("ProcessTestServiceImpl service invoke...senceCode:"+";version:"+version);
    }


    public Expression getUrl() {
        return url;
    }

    public void setUrl(Expression url) {
        this.url = url;
    }
}
