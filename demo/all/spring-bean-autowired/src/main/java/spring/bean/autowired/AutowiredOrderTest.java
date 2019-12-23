package spring.bean.autowired;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class AutowiredOrderTest {

//    @Autowired
//    @Qualifier("CcBean")
    private CcBean ccBean;

//    @Autowired
//    public void config(@Qualifier("CcBean3") CcBean ccBean) {
//        System.out.println("[AutowiredOrderTest] config");
//        this.ccBean = ccBean;
//    }

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    public AutowiredOrderTest(@Qualifier("CcBean1") CcBean ccBean) {
        System.out.println("[AutowiredOrderTest] constructor");
        this.ccBean = ccBean;

    }

//    @Autowired
//    public void setAaBean(@Qualifier("CcBean2") CcBean ccBean) {
//        System.out.println("[AutowiredOrderTest] setAaBean");
//        this.ccBean = ccBean;
//    }



    public String say() {
        return String.format("[%s] aaBean: %s", this, ccBean.name());
    }



}
