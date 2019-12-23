package spring.bean.autowired;

import org.springframework.stereotype.Component;

@Component("CcBean3")
public class CcBean3 extends CcBean{

    @Override
    public String name() {
        return "CcBean3";
    }

}
