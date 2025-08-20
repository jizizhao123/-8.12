
package com.neutech.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neutech.entity.ApplicationType;
import com.neutech.entity.UserApplication;
import com.neutech.entity.Users;
import com.neutech.mapper.ApplicationTypeMapper;
import com.neutech.mapper.UserApplicationMapper;
import com.neutech.mapper.UsersMapper;
import com.neutech.service.UserApplicationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;

@Service
public class UserApplicationServiceImpl extends ServiceImpl<UserApplicationMapper, UserApplication> implements UserApplicationService {

    @Resource
    private UserApplicationMapper userApplicationMapper;

    @Resource
    private ApplicationTypeMapper applicationTypeMapper;

    @Resource
    private UsersMapper usersMapper;

    @Override
    public IPage<UserApplication> getPendingApplications(IPage<UserApplication> page) {
        QueryWrapper<UserApplication> wrapper = new QueryWrapper<>();
        wrapper.eq("status", "待审核");
        wrapper.orderByDesc("create_time");
        return userApplicationMapper.selectPage(page, wrapper);
    }

    @Override
    public IPage<UserApplication> getAllApplications(IPage<UserApplication> page, String status) {
        QueryWrapper<UserApplication> wrapper = new QueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("create_time");
        return userApplicationMapper.selectPage(page, wrapper);
    }

    @Transactional
    @Override
    public boolean reviewApplication(Long id, String status, String remark) {
        // 查询申请
        UserApplication application = userApplicationMapper.selectById(id);
        if (application == null) {
            return false;
        }

        // 检查状态是否为待审核
        if (!"待审核".equals(application.getStatus())) {
            return false;
        }

        // 更新申请状态
        application.setStatus(status);
        application.setUpdateTime(LocalDateTime.now());
        userApplicationMapper.updateById(application);

        // 如果审核通过，更新用户积分
        if ("已通过".equals(status)) {
            // 查询申请类型对应的分数
            ApplicationType appType = applicationTypeMapper.selectById(application.getTypeId());
            if (appType != null) {
                Float score = appType.getScore();
                if (score != null && score > 0) {
                    // 查询用户并更新积分
                    QueryWrapper<Users> userWrapper = new QueryWrapper<>();
                    userWrapper.eq("number", application.getNumber());
                    Users user = usersMapper.selectOne(userWrapper);
                    if (user != null) {
                        Float currentScore = user.getScore() != null ? user.getScore() : 0.0f;
                        user.setScore(currentScore + score);
                        usersMapper.update(user, userWrapper);
                    }
                }
            }
        }

        return true;
    }
}
