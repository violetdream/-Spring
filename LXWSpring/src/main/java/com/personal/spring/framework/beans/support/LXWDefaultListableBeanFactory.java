package com.personal.spring.framework.beans.support;

import com.personal.spring.framework.beans.config.LXWBeanDefinition;
import com.personal.spring.framework.context.support.LXWAbstractApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 2019/4/30/0030
 * Create by 刘仙伟
 */
public class LXWDefaultListableBeanFactory extends LXWAbstractApplicationContext {

    //存储注册信息的BeanDefinition
    protected final Map<String, LXWBeanDefinition> beanDefinitionMap=new ConcurrentHashMap<String, LXWBeanDefinition>();
}
