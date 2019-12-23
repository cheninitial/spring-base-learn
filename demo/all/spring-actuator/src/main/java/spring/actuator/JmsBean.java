package spring.actuator;

import cn.hutool.json.JSONObject;
import org.springframework.boot.actuate.endpoint.annotation.*;
import org.springframework.context.annotation.Configuration;

@Configuration
@Endpoint(id = "jmsBeanTest")

public class JmsBean {

    private String tag;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }


    @ReadOperation()
    public String readOperation() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.append("value", "readOperation: " + tag);
        return jsonObject.toString();
    }

    @WriteOperation
    public void writeOperation(String tag) {
        this.tag = tag;
    }

    /**
     * /actuator/jmsBeanTest/{name}/{counter}
     * */
    @WriteOperation
    public void write(@Selector String name,@Selector Integer counter) {
        System.out.println("name: " + name);
        System.out.println("counter: " + counter);
    }

    @DeleteOperation
    public void deleteOperation() {
        this.tag = null;
    }


}
