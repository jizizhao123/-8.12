// src/main/java/com/neutech/service/impl/AnnouncementServiceImpl.java
package com.neutech.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neutech.entity.Announcement;
import com.neutech.mapper.AnnouncementMapper;
import com.neutech.service.AnnouncementService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementService {

    @Resource
    private AnnouncementMapper announcementMapper;

    @Override
    public void addAnnouncement(String userNumber, String title, String content) {
        Announcement announcement = new Announcement();
        announcement.setNumber(userNumber);
        announcement.setTitle(title);
        announcement.setContent(content);
        this.save(announcement);
    }

    @Override
    public List<Announcement> getAllAnnouncementsWithUserInfo() {
        return announcementMapper.selectAllAnnouncementsWithUserInfo();
    }

    @Override
    public Announcement getAnnouncementWithUserInfoById(Integer id) {
        return announcementMapper.selectAnnouncementWithUserInfoById(id);
    }

    // 实现分页查询方法
    @Override
    public IPage<Announcement> getAnnouncementsByPage(IPage<Announcement> page) {
        return announcementMapper.selectAnnouncementsByPage(page);
    }
}
