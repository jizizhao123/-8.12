// src/main/java/com/neutech/service/AnnouncementService.java
package com.neutech.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neutech.entity.Announcement;

import java.util.List;

public interface AnnouncementService extends IService<Announcement> {
    void addAnnouncement(String userNumber, String title, String content);
    List<Announcement> getAllAnnouncementsWithUserInfo();
    Announcement getAnnouncementWithUserInfoById(Integer id);
    IPage<Announcement> getAnnouncementsByPage(IPage<Announcement> page); // 新增分页方法
}
