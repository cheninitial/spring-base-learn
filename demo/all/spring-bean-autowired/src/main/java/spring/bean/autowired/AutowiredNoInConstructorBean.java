package spring.bean.autowired;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
public class AutowiredNoInConstructorBean {

    private AaBean aaBean;
    private BbBean bbBean;


    @Autowired
    private AutowiredNoInConstructorBean(AaBean aaBean) {
        System.out.println("[AutowiredNoInConstructorBean] AutowiredNoInConstructorBean(AaBean aaBean)");
        this.aaBean = aaBean;

    }

    public AutowiredNoInConstructorBean(AaBean aaBean, BbBean bbBean) {
        System.out.println("[AutowiredNoInConstructorBean] AutowiredNoInConstructorBean(AaBean aaBean, BbBean bbBean)");
        this.aaBean = aaBean;
        this.bbBean = bbBean;
    }


//    public AutowiredNoInConstructorBean() {
//        System.out.println("[AutowiredNoInConstructorBean] AutowiredNoInConstructorBean()");
//    }

    public String say() {
        return String.format("[%s] aaBean: %s, bbBean: %s", this, aaBean, bbBean);
    }

}
