import com.personal.spring.framework.context.LXWApplicationContext;
import org.junit.Test;

/**
 * 2019/4/30/0030
 * Create by 刘仙伟
 */

public class TestLXWSpring {
    @Test
    public static void main(String[] args) throws Exception {
        LXWApplicationContext applicationContext=new LXWApplicationContext(new String[]{"classpath:application.properties"});

       Object object= applicationContext.getBean("modifyService");

        System.out.println("object = " + object);

        Object object2= applicationContext.getBean("com.personal.spring.demo.service.IQueryService");

        System.out.println("object = " + object2);
    }
}
