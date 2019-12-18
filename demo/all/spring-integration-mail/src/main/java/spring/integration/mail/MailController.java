package spring.integration.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

@RestController
@RequestMapping("/mail")
public class MailController {

    @Autowired
    private JavaMailSender mailSender;

    @RequestMapping(value = "/send", method = RequestMethod.GET)
    public String sendMail(@RequestParam(value = "message") String message) {

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("cinitial_test@163.com");
        simpleMailMessage.setTo("cyl003@foxmail.com");
        simpleMailMessage.setSubject("spring boot mail test");
        simpleMailMessage.setText(message);

        try {
            mailSender.send(simpleMailMessage);
            System.out.println("发送邮件成功");
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("发送邮件失败");
            return "fail " + e;
        }
    }

    @RequestMapping(value = "/att", method = RequestMethod.GET)
    public String sendAtt() throws MessagingException {

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setFrom("cinitial_test@163.com");
        helper.setTo("cyl003@foxmail.com");
        helper.setSubject("主题：发送有附件的邮件");

        helper.setText("你好，我是小黄，我正在测试发送一封有附件的邮件。");


        FileSystemResource file1 = new FileSystemResource(new File("/Users/dcmini/Documents/study2/mygithub/spring-base-learn/demo/all/spring-integration-mail/src/main/java/spring/integration/mail/SpringIntegrationMailApplication.java"));
        FileSystemResource file2 = new FileSystemResource(new File("/Users/dcmini/Documents/study2/mygithub/spring-base-learn/demo/all/spring-integration-mail/src/main/resources/application.properties"));
        helper.addAttachment("附件-1.jpg", file1);
        helper.addAttachment("附件-2.jpg", file2);

        try {
            mailSender.send(mimeMessage);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }


}
