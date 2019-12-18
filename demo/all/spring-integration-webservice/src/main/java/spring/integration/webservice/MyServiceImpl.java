package spring.integration.webservice;

import javax.jws.WebService;

@WebService(endpointInterface = "spring.integration.webservice.MyService")
public class MyServiceImpl implements MyService {
    @Override
    public String show() {
        System.out.println("MyServiceImpl 调用  show");
        return "调用成功";
    }

}
