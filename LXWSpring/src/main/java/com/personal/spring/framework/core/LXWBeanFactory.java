package com.personal.spring.framework.core;

/**
 * 2019/4/30/0030
 * Create by 刘仙伟
 */
public interface LXWBeanFactory {
    Object getBean(String beanName) throws Exception;
    public Object getBean(Class<?> className) throws Exception;

}
