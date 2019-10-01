package datasource.jpa.dao;


import datasource.jpa.JpaTest;
import datasource.jpa.pojo.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = JpaTest.class)
public class JpaUserRepositoryTest {

    @Autowired
    private JpaUserRepository jpaUserRepository;
    
    @Test
    public void getUser() {
        User user = jpaUserRepository.findById(7L).get();
        System.out.println(user);
        
    }
    

}
