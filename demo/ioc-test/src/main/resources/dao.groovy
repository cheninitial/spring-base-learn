import com.fang2chen.test.spring.ioctest.xml.HelloConfig

beans {
    helloConfig(HelloConfig){
        ext = "大声说2"
    }
}