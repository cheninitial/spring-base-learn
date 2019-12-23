package spring.bean.autowired;

import org.springframework.stereotype.Component;

@Component(value = "CcBean")
public class CcBean {

    public String name() {
        return "CcBean";
    }

}
