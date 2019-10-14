package datasource.pojo;

import datasource.enumeration.SexEnum;

public class User {

    public User() {
    }

    public User(Long id, String userName, SexEnum sex, String note) {
        this.id = id;
        this.userName = userName;
        this.sex = sex;
        this.note = note;
    }

    private Long id = null;
    private String userName = null;
    private SexEnum sex = null;
    private String note = null;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", sex=" + sex +
                ", note='" + note + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public SexEnum getSex() {
        return sex;
    }

    public void setSex(SexEnum sex) {
        this.sex = sex;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
