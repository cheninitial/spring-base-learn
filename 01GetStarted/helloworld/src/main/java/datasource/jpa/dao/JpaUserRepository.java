package datasource.jpa.dao;

import datasource.jpa.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRepository extends JpaRepository<User, Long> {

}
