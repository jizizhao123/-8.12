package com.neutech.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author jzz
 * @since 2025-08-18
 */
@Data
@TableName("user_application")
public class UserApplication implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String number;

    private Long typeId;

    private String description;

    private String imageUrl;

    private String status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
