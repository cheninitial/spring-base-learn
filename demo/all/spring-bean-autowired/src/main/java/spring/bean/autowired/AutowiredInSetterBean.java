package spring.bean.autowired;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AutowiredInSetterBean {


    private AaBean aaBean;

    @Autowired
    private final void setAaBean(AaBean aaBean) {
        this.aaBean = aaBean;
    }

    @Autowired
    private BbBean bbBean;

    public String say() {
        return String.format("[%s] aaBean: %s, bbBean: %s", this, aaBean, bbBean);
    }

}
