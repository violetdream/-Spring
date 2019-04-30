package com.personal.spring.framework.context;

import com.personal.spring.framework.annotation.LXWAutowired;
import com.personal.spring.framework.annotation.LXWController;
import com.personal.spring.framework.annotation.LXWService;
import com.personal.spring.framework.beans.LXWBeanWrapper;
import com.personal.spring.framework.beans.config.LXWBeanDefinition;
import com.personal.spring.framework.beans.config.LXWBeanPostProcessor;
import com.personal.spring.framework.beans.support.LXWBeanDefinitionReader;
import com.personal.spring.framework.beans.support.LXWDefaultListableBeanFactory;
import com.personal.spring.framework.core.LXWBeanFactory;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.reflect.Field;

/**
 * 2019/4/30/0030
 * Create by 刘仙伟
 */
public class LXWApplicationContext extends LXWDefaultListableBeanFactory implements LXWBeanFactory {

    private LXWBeanDefinitionReader beanDefinitionReader;

    private Map<String,Object> singletonObject=new ConcurrentHashMap<String,Object>();
    private Map<String, LXWBeanWrapper> factoryBeanInstanceCache=new ConcurrentHashMap<String, LXWBeanWrapper>();
    //单例的IOC容器缓存
    private Map<String,Object> factoryBeanObjectCache = new ConcurrentHashMap<String, Object>();

    private String[] configLocations;

    public LXWApplicationContext(String[] configLocations) {
        this.configLocations = configLocations;
        try{
            refresh();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void refresh() throws Exception {
        beanDefinitionReader=new LXWBeanDefinitionReader(this.configLocations);

        List<LXWBeanDefinition> beanDefinitions=beanDefinitionReader.loadBeanDefinitions();

        doRegisterBeanDefinition(beanDefinitions);

        doAutowrited();
    }

    private void doAutowrited() {
        for (Map.Entry<String,LXWBeanDefinition> beanDefinitionEntry: super.beanDefinitionMap.entrySet()){
            String factoryBeanName=beanDefinitionEntry.getKey();
            if(!beanDefinitionEntry.getValue().isLazyInit()){
                try {
                    getBean(factoryBeanName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doRegisterBeanDefinition(List<LXWBeanDefinition> beanDefinitions)throws Exception {
        for (LXWBeanDefinition beanDefinition:beanDefinitions) {
            if(super.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())){
                throw new Exception(beanDefinition.getFactoryBeanName()+" is existed ");
            }
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(),beanDefinition);
        }
    }

    @Override
    public Object getBean(String beanName) throws Exception {
        LXWBeanDefinition lxwBeanDefinition = this.beanDefinitionMap.get(beanName);
        Object instance = null;

        //这个逻辑还不严谨，自己可以去参考Spring源码
        //工厂模式 + 策略模式
        LXWBeanPostProcessor postProcessor = new LXWBeanPostProcessor();

        postProcessor.postProcessBeforeInitialization(instance,beanName);

        instance = instantiateBean(beanName,lxwBeanDefinition);

        //3、把这个对象封装到BeanWrapper中
        LXWBeanWrapper beanWrapper = new LXWBeanWrapper(instance);

        //4、把BeanWrapper存到IOC容器里面
//        //1、初始化

//        //class A{ B b;}
//        //class B{ A a;}
//        //先有鸡还是先有蛋的问题，一个方法是搞不定的，要分两次

        //2、拿到BeanWraoper之后，把BeanWrapper保存到IOC容器中去
        this.factoryBeanInstanceCache.put(beanName,beanWrapper);

        postProcessor.postProcessAfterInitialization(instance,beanName);

//        //3、注入
        populateBean(beanName,new LXWBeanDefinition(),beanWrapper);


        return this.factoryBeanInstanceCache.get(beanName).getWrappedInstance();
    }

    @Override
    public Object getBean(Class<?> beanClass) throws Exception {
        return getBean(beanClass.getName());
    }

    private void populateBean(String beanName, LXWBeanDefinition lxwBeanDefinition, LXWBeanWrapper lxwBeanWrapper) {
        Object instance = lxwBeanWrapper.getWrappedClass();

//        LXWBeanDefinition.getBeanClassName();

        Class<?> clazz = lxwBeanWrapper.getWrappedClass();
        //判断只有加了注解的类，才执行依赖注入
        if(!(clazz.isAnnotationPresent(LXWController.class) || clazz.isAnnotationPresent(LXWService.class))){
            return;
        }

        //获得所有的fields
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if(!field.isAnnotationPresent(LXWAutowired.class)){ continue;}

            LXWAutowired autowired = field.getAnnotation(LXWAutowired.class);

            String autowiredBeanName =  autowired.value().trim();
            if("".equals(autowiredBeanName)){
                autowiredBeanName = field.getType().getName();
            }

            //强制访问
            field.setAccessible(true);

            try {
                //为什么会为NULL，先留个坑
                if(this.factoryBeanInstanceCache.get(autowiredBeanName) == null){ continue; }
//                if(instance == null){
//                    continue;
//                }
                field.set(instance,this.factoryBeanInstanceCache.get(autowiredBeanName).getWrappedInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }

    }

    private Object instantiateBean(String beanName, LXWBeanDefinition lxwBeanDefinition) {
        //1、拿到要实例化的对象的类名
        String className = lxwBeanDefinition.getBeanClassName();

        //2、反射实例化，得到一个对象
        Object instance = null;
        try {
//            LXWBeanDefinition.getFactoryBeanName()
            //假设默认就是单例,细节暂且不考虑，先把主线拉通
            if(this.factoryBeanObjectCache.containsKey(className)){
                instance = this.factoryBeanObjectCache.get(className);
            }
            else {
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();
//
//                LXWAdvisedSupport config = instantionAopConfig(LXWBeanDefinition);
//                config.setTargetClass(clazz);
//                config.setTarget(instance);
//
//                //符合PointCut的规则的话，闯将代理对象
//                if(config.pointCutMatch()) {
//                    instance = createProxy(config).getProxy();
//                }

                this.factoryBeanObjectCache.put(className,instance);
                this.factoryBeanObjectCache.put(lxwBeanDefinition.getFactoryBeanName(),instance);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return instance;
    }
}
