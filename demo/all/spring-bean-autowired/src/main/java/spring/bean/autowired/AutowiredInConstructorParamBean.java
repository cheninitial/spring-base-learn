package spring.bean.autowired;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AutowiredInConstructorParamBean {

    private AaBean aaBean;
    private BbBean bbBean;

    public AutowiredInConstructorParamBean(@Autowired AaBean aaBean, BbBean bbBean) {
        this.aaBean = aaBean;
        this.bbBean = bbBean;
    }

    public String say() {
        return String.format("[%s] aaBean: %s, bbBean: %s", this, aaBean, bbBean);
    }

}
