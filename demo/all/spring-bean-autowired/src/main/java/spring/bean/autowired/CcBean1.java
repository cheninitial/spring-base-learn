package spring.bean.autowired;

import org.springframework.stereotype.Component;

@Component(value = "CcBean1")
public class CcBean1 extends CcBean{

    @Override
    public String name() {
        return "CcBean1";
    }

}
