package spring.integration.quartz.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class MyCommandLineRunner implements CommandLineRunner {

    @Autowired
    private QuartzService quartzService;

    @Override
    public void run(String... args) throws Exception {
        HashMap<String,Object> map = new HashMap<>();
        map.put("name",1);
        quartzService.deleteJob("job", "test");
        quartzService.addJob(Job.class, "job", "test", "0 * * * * ?", map);
        map.put("name",2);
        quartzService.deleteJob("job2", "test");
        quartzService.addJob(Job.class, "job2", "test", "10 * * * * ?", map);

        map.put("name",3);
        quartzService.deleteJob("job3", "test2");
        quartzService.addJob(Job.class, "job3", "test2", "15 * * * * ?", map);
    }
}
