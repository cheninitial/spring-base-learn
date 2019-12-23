package spring.actuator;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

@ManagedResource(objectName = "bean:name=TestFactory", description = "测试工厂服务")
@Component
public class TestFactory {

    private Long id;

    @ManagedAttribute(description = "属性Id")
    public Long getId() {
        return id;
    }

    @ManagedOperation(description = "设置ID")
    public void setId(Long id) {
        this.id = id;
    }

    @ManagedOperation(description = "刷新")
    public void flush() {
        System.out.println("刷新了" + id + "支付配置缓存");
    }

}
