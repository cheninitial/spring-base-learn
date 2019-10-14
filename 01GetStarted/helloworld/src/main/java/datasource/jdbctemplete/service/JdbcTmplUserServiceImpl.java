package datasource.jdbctemplete.service;

import com.sun.org.apache.regexp.internal.RE;
import datasource.enumeration.SexEnum;
import datasource.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.List;

@Service
public class JdbcTmplUserServiceImpl implements JdbcTmplUserService {

    @Autowired
    private JdbcTemplate jdbcTemplate = null;

    /**
     * <p>
     *     定义一个映射关系
     * </p>
     * @return : RowMapper<User>
     */
    private RowMapper<User> getUserMapper() {
        RowMapper<User> userRowMapper = new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet resultSet, int i) throws SQLException {
                User user = new User();
                user.setId(resultSet.getLong("id"));
                user.setUserName(resultSet.getString("user_name"));
                int sexId = resultSet.getInt("sex");
                SexEnum sexEnum = SexEnum.getEnumById(sexId);
                user.setSex(sexEnum);
                user.setNote(resultSet.getString("note"));
                return user;
            }
        };
        return userRowMapper;
    }

    @Override
    public User getUser(Long id) {
        String sql = "select id, user_name, sex, note from t_user where id = ?";
        Object[] params = new Object[]{id.toString()};
        // 直接写 id 会出现错误
//        Object[] params = new Object[]{id};
        User user = jdbcTemplate.queryForObject(sql, params, getUserMapper());
        return user;
    }

    @Override
    public List<User> findUsers(String userName, String note) {
        String sql = "select id, user_name, sex, note from t_user " +
                "where user_name like concat('%', ?, '%') " +
                "and note like concat('%', ? '%') ";
        Object[] params = new Object[]{userName, note};
        List<User> userList = jdbcTemplate.query(sql, params, getUserMapper());
        return userList;
    }

    @Override
    public int insertUser(User user) {
        String sql = "insert into t_user (user_name, sex, note) values (?,?,?)";
        return jdbcTemplate.update(sql, user.getUserName(), user.getSex().getId(), user.getNote());
    }

    @Override
    public int updateUser(User user) {
        String sql = "update t_user set user_name = ?, sex = ?, note = ? where id =?";
        return jdbcTemplate.update(sql, user.getUserName(), user.getSex().getId(), user.getNote(), user.getId());
    }

    @Override
    public int deleteUser(Long id) {
        String sql = "delete from t_user where id = ?";
        return jdbcTemplate.update(sql, id);
    }

    @Override
    public User getUser2(Long id) {
        User result = jdbcTemplate.execute(new StatementCallback<User>() {
            @Override
            public User doInStatement(Statement statement) throws SQLException, DataAccessException {
                String sql1 = "select count(*) as total from t_user where id = " + id;
                ResultSet rs1 = statement.executeQuery(sql1);
                while (rs1.next()) {
                    int total = rs1.getInt("total");
                    System.out.println(total);
                }

                String sql2 = "select id, user_name, sex, note from t_user where id = " + id;
                ResultSet rs2 = statement.executeQuery(sql2);
                User user = null;
                while (rs2.next()) {
                    int rowNum = rs2.getRow();
                    user = getUserMapper().mapRow(rs2, rowNum);
                }
                return user;
            }
        });

        return result;
    }

    @Override
    public User getUser3(Long id) {
        return jdbcTemplate.execute(new ConnectionCallback<User>() {
            @Override
            public User doInConnection(Connection connection) throws SQLException, DataAccessException {

                // 到现在还不知道为什么会报错
                /**
                 *
                 * User{id=9, userName='chen', sex=MALE, note='测试'}
                 *
                 * org.springframework.dao.TransientDataAccessResourceException: ConnectionCallback; Parameter index out of range (1 > number of parameters, which is 0).; nested exception is java.sql.SQLException: Parameter index out of range (1 > number of parameters, which is 0).
                 *
                 * 	at org.springframework.jdbc.support.SQLStateSQLExceptionTranslator.doTranslate(SQLStateSQLExceptionTranslator.java:110)
                 * 	at org.springframework.jdbc.support.AbstractFallbackSQLExceptionTranslator.translate(AbstractFallbackSQLExceptionTranslator.java:72)
                 * 	at org.springframework.jdbc.support.AbstractFallbackSQLExceptionTranslator.translate(AbstractFallbackSQLExceptionTranslator.java:81)
                 * 	at org.springframework.jdbc.support.AbstractFallbackSQLExceptionTranslator.translate(AbstractFallbackSQLExceptionTranslator.java:81)
                 * 	at org.springframework.jdbc.core.JdbcTemplate.translateException(JdbcTemplate.java:1442)
                 * 	at org.springframework.jdbc.core.JdbcTemplate.execute(JdbcTemplate.java:335)
                 * 	at datasource.jdbctemplete.service.JdbcTmplUserServiceImpl.getUser3(JdbcTmplUserServiceImpl.java:112)
                 * 	at datasource.jdbctemplete.service.JdbcTmplUserServiceImplTest.case1(JdbcTmplUserServiceImplTest.java:38)
                 * 	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
                 * 	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
                 * 	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
                 * 	at java.lang.reflect.Method.invoke(Method.java:498)
                 * 	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)
                 * 	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
                 * 	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47)
                 * 	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
                 * 	at org.springframework.test.context.junit4.statements.RunBeforeTestExecutionCallbacks.evaluate(RunBeforeTestExecutionCallbacks.java:74)
                 * 	at org.springframework.test.context.junit4.statements.RunAfterTestExecutionCallbacks.evaluate(RunAfterTestExecutionCallbacks.java:84)
                 * 	at org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks.evaluate(RunBeforeTestMethodCallbacks.java:75)
                 * 	at org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks.evaluate(RunAfterTestMethodCallbacks.java:86)
                 * 	at org.springframework.test.context.junit4.statements.SpringRepeat.evaluate(SpringRepeat.java:84)
                 * 	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)
                 * 	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:251)
                 * 	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:97)
                 * 	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)
                 * 	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)
                 * 	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)
                 * 	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)
                 * 	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)
                 * 	at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61)
                 * 	at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:70)
                 * 	at org.junit.runners.ParentRunner.run(ParentRunner.java:363)
                 * 	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:190)
                 * 	at org.junit.runner.JUnitCore.run(JUnitCore.java:137)
                 * 	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68)
                 * 	at com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:47)
                 * 	at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:242)
                 * 	at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70)
                 * Caused by: java.sql.SQLException: Parameter index out of range (1 > number of parameters, which is 0).
                 * 	at com.mysql.cj.jdbc.exceptions.SQLError.createSQLException(SQLError.java:129)
                 * 	at com.mysql.cj.jdbc.exceptions.SQLError.createSQLException(SQLError.java:97)
                 * 	at com.mysql.cj.jdbc.exceptions.SQLError.createSQLException(SQLError.java:89)
                 * 	at com.mysql.cj.jdbc.exceptions.SQLError.createSQLException(SQLError.java:63)
                 * 	at com.mysql.cj.jdbc.ClientPreparedStatement.checkBounds(ClientPreparedStatement.java:1370)
                 * 	at com.mysql.cj.jdbc.ClientPreparedStatement.getCoreParameterIndex(ClientPreparedStatement.java:1383)
                 * 	at com.mysql.cj.jdbc.ClientPreparedStatement.setString(ClientPreparedStatement.java:1750)
                 * 	at com.zaxxer.hikari.pool.HikariProxyPreparedStatement.setString(HikariProxyPreparedStatement.java)
                 * 	at datasource.jdbctemplete.service.JdbcTmplUserServiceImpl$3.doInConnection(JdbcTmplUserServiceImpl.java:119)
                 * 	at datasource.jdbctemplete.service.JdbcTmplUserServiceImpl$3.doInConnection(JdbcTmplUserServiceImpl.java:112)
                 * 	at org.springframework.jdbc.core.JdbcTemplate.execute(JdbcTemplate.java:327)
                 * 	... 32 more
                 * */
                String sql1 = "select count(*) as total from t_user " +
                        "where id = ？";
                PreparedStatement ps1 = connection.prepareStatement(sql1);
                ps1.setString(1, id.toString());
                ResultSet rs1 = ps1.executeQuery();
                while (rs1.next()) {
                    System.out.println(rs1.getInt("total"));
                }

                String sql2 = "select id, user_name, sex, note from t_user " +
                        "where id = ?";
                PreparedStatement ps2 = connection.prepareStatement(sql2);
                ps2.setString(1, id.toString());
                ResultSet rs2 = ps2.executeQuery();
                User user = null;
                while (rs2.next()) {
                    int rowNum = rs2.getRow();
                    user = getUserMapper().mapRow(rs2, rowNum);
                }

                return user;
            }
        });
    }
}
