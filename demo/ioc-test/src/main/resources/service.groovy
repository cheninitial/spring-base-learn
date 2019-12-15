import com.fang2chen.test.spring.ioctest.xml.HelloServiceImpl

beans {
    helloService(HelloServiceImpl){
        name = "cyl"
        helloConfig = {AnotherBean bean -> helloConfig = helloConfig}
    }
}