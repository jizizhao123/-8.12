package com.neutech.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author jzz
 * @since 2025-08-12
 */
@Data
@TableName("users")
public class Users implements Serializable {

    private static final long serialVersionUID = 1L;

    public Users() {
    }



    public Users(Integer id, String email){
        this.id = id;
        this.email = email;
    }
    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }


    public Users(String name, String email, String role, String password,String major,String number,Float score,String avatarUrl) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.password = password;
        this.major = major;
        this.number = number;
        this.score = score;
        this.avatarUrl = avatarUrl;
    }
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String name;

    private String email;

    private String major;

    private String number;

    private Float score;

    @JsonIgnore
    private String password;

    private String role;

    private Boolean isAdmin = false;

    private String avatarUrl;

    private String introduction;

    private String tags;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
