package datasource.mybatis.dao;

import datasource.mybatis.pojo.User;
import org.springframework.stereotype.Repository;

@Repository
public interface MybatisUserDao {

    public User getUser(Long id);

}
