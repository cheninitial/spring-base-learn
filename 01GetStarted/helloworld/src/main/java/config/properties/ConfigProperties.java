package config.properties;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "test")
@Data
@ToString
public class ConfigProperties {

    private String str = "";

    private Integer intValue = 0;

    private Integer intValue2 = 0;

    private Double doubleValue = 0.0;

    private List<String> StringList1 = new ArrayList<>();

    private List<String> StringList2 = new ArrayList<>();

    private List<Double> doubleList1 = new ArrayList<>();

    private List<Double> doubleList2 = new ArrayList<>();

    private TestPojo testPojo = new TestPojo();

    private List<TestPojo> testPojoList = new ArrayList<>();

    private Map<String, String> mapStringString = new HashMap<>();

    private Map<String, TestPojo> mapStringTestPojo = new HashMap<>();

}

@Data
@ToString
class TestPojo {
    private String str;
}
