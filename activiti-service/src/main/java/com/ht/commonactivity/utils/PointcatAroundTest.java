package com.ht.commonactivity.utils;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
@Aspect
@Log4j2
public class PointcatAroundTest {

    @Pointcut("@annotation(com.ht.commonactivity.utils.TestPointCat)")
    public void pointCut() {

    }

    @Around(value = "pointCut() && @annotation(test)")
    public void aspects(ProceedingJoinPoint join,TestPointCat test) throws Exception {
        log.info("环绕前....");
        Object[] obj = join.getArgs();
        Class[] cls = new Class[join.getArgs().length];
        for (int i = 0; i < obj.length; i++) {
            cls[i] = Class.forName(obj[i].toString());
        }
        for (int i = 0; i < cls.length; i++) {
            Method[] method = cls[i].getMethods();
            for (Method meth : method) {
                TestPointCat cat = meth.getAnnotation(TestPointCat.class);
                log.info(cat.ids());
                log.info(cat.name()[0]);
            }
        }
        try {
            join.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        log.info("环绕后....");
    }


    @Around(value = "execution(* com.ht.commonactivity.controller.*.*(..)) && @annotation(test)")
    public void aspect(ProceedingJoinPoint join, TestPointCat test) throws Exception {
        log.info("环绕前....");
        log.error(test.ids());
        try {
            join.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        log.info("环绕后....");
    }

}
