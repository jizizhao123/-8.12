// src/main/java/com/neutech/mapper/AnnouncementMapper.java
package com.neutech.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.neutech.entity.Announcement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AnnouncementMapper extends BaseMapper<Announcement> {
    List<Announcement> selectAllAnnouncementsWithUserInfo();
    Announcement selectAnnouncementWithUserInfoById(Integer id);
    IPage<Announcement> selectAnnouncementsByPage(IPage<Announcement> page); // 新增分页查询方法
}
