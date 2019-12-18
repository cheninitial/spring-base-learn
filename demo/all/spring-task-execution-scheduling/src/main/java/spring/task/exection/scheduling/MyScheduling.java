package spring.task.exection.scheduling;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class MyScheduling {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH::mm::ss");

    @Scheduled(fixedRate=5000)
    public void proFixCurrentTime() {
        System.out.println("每5秒钟执行一次：" + dateFormat.format(new Date()));
    }

    @Scheduled(cron="0 53 18 ? * *")
    public void cornCurrentTime() {
        System.out.println("自定执行时间: " + dateFormat.format(new Date()));
    }
}
