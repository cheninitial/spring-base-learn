package spring.bean.autowired;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AutowiredInConfigMethodBean {


    private AaBean aaBean;

    @Autowired
    public void config(AaBean aaBean) {
        this.aaBean = aaBean;
    }

    @Autowired
    private BbBean bbBean;

    public String say() {
        return String.format("[%s] aaBean: %s, bbBean: %s", this, aaBean, bbBean);
    }

}
