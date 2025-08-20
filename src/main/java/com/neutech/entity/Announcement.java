package com.neutech.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 公告实体类
 * </p>
 *
 * @author jzz
 * @since 2025-08-12
 */
@Data
@TableName("announcement")
public class Announcement implements Serializable {

    private static final long serialVersionUID = 1L;

    public Announcement() {
    }

    /**
     * 构造函数
     * @param id 主键ID
     * @param number 编号
     * @param title 标题
     * @param content 内容
     * @param createTime 创建时间
     */
    public Announcement(Integer id, String number, String title, String content, LocalDateTime createTime) {
        this.id = id;
        this.number = number;
        this.title = title;
        this.content = content;
        this.createTime = createTime;
    }

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String number;

    private String title;

    private String content;

    private LocalDateTime createTime;

    @TableField(exist = false)
    private String userName;
    @TableField(exist = false)
    private String userMajor;
    @TableField(exist = false)
    private String userEmail;
    @TableField(exist = false)
    private String userRole;
}
