
package com.neutech.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.neutech.entity.UserApplication;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserApplicationService extends IService<UserApplication> {
    IPage<UserApplication> getPendingApplications(IPage<UserApplication> page); // 获取待审核申请
    boolean reviewApplication(Long id, String status, String remark); // 审核申请
    IPage<UserApplication> getAllApplications(IPage<UserApplication> page, String status); // 获取所有申请
}
