package com.ht.commonactivity.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.task.IdentityLink;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Set;


public class MangerTaskHandlerCandidateUsers implements TaskListener {

    @Override
    public void notify(DelegateTask delegateTask) {
        System.out.println("=========>"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date()));
        String [] candidateUsers={"role_users1"};
        String assignee=delegateTask.getAssignee();
        delegateTask.setVariable("userID",Arrays.asList(candidateUsers));


        Map<String,Object> map=delegateTask.getVariables();
        for (Map.Entry<String,Object> ma:map.entrySet()){
            System.out.println("====="+ma.getKey());
            System.out.println("====="+ma.getValue());
        }
//        delegateTask.setAssignee("asbacd"); // 如果用户节点配置了代理人，会导致流程启动报错


        System.out.println(assignee);
//        Set<IdentityLink> candidate=delegateTask.getCandidates(); // 获取候选人
//        candidate.forEach(identityLink -> {
//            System.out.println("taskId==>"+identityLink.getTaskId());
//        });


//        String[] empLoyees = {"经纪人1","经纪人2","asdf"};
//        delegateTask.addCandidateUsers(Arrays.asList(empLoyees));//完成多处理人的指定

    }
}
