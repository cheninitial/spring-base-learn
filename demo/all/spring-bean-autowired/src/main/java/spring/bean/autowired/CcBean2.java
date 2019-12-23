package spring.bean.autowired;

import org.springframework.stereotype.Component;

@Component("CcBean2")
public class CcBean2 extends CcBean{

    @Override
    public String name() {
        return "CcBean2";
    }

}
