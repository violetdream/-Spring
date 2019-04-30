package com.personal.spring.framework.beans;

/**
 * 2019/4/30/0030
 * Create by 刘仙伟
 */
public class LXWBeanWrapper {
    private Object wrappedInstance;
    private Class<?> warappedClass;

    public LXWBeanWrapper(Object wrappedInstance) {
        this.wrappedInstance = wrappedInstance;
    }

    public Object getWrappedInstance() {
        return wrappedInstance;
    }

    public Class<?> getWrappedClass() {
        return this.wrappedInstance.getClass();
    }
}
