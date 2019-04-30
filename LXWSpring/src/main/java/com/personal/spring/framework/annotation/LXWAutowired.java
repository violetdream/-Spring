package com.personal.spring.framework.annotation;

import java.lang.annotation.*;

/**
 * 2019/4/30/0030
 * Create by 刘仙伟
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LXWAutowired {
    String value() default "";
}
