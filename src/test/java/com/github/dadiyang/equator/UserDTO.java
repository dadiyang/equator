package com.github.dadiyang.equator;

import java.util.Arrays;
import java.util.Date;

public class UserDTO extends BaseUser {
    private Date expireTime;
    /**
     * 爱好，此属性用于测试数组属性 deepEquals
     */
    private String[] hobbies;
    /**
     * UserDTO 里有，但是 User 类里没有的字段
     */
    private String uniqField;

    public UserDTO(long id, String username, Date expireTime, String[] hobbies, String uniqField) {
        super(id, username);
        this.expireTime = expireTime;
        this.hobbies = hobbies;
        this.uniqField = uniqField;
    }

    public String[] getHobbies() {
        return hobbies;
    }

    public void setHobbies(String[] hobbies) {
        this.hobbies = hobbies;
    }

    /**
     * 是否过期，由expireTime计算得出
     * <p>
     * 此属性用于测试基于getter的比对器和基于属性的比对器的不同结果
     */
    public boolean isExpired() {
        return expireTime.before(new Date());
    }

    public String getUniqField() {
        return uniqField;
    }

    public void setUniqField(String uniqField) {
        this.uniqField = uniqField;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", username='" + getUsername() + '\'' +
                ", expireTime=" + expireTime +
                ", hobbies=" + Arrays.toString(hobbies) +
                '}';
    }
}
