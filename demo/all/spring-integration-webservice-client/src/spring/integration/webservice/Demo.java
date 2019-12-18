package spring.integration.webservice;

public class Demo {
    public static void main(String[] args) throws Exception{
        MyServiceImplServiceLocator myServiceImplService = new MyServiceImplServiceLocator();
        String show = myServiceImplService.getMyServiceImplPort().show();
        System.out.println(show);
    }
}
