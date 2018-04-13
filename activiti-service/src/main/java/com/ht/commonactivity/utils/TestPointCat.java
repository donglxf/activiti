package com.ht.commonactivity.utils;

import javax.swing.*;
import java.lang.annotation.*;

@Target({ElementType.PARAMETER, ElementType.METHOD,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TestPointCat {

    String ids() default "";

    String[] name() default {};
}
