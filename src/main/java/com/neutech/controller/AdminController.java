// src/main/java/com/neutech/controller/AdminController.java
package com.neutech.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neutech.entity.UserApplication;
import com.neutech.entity.Users;
import com.neutech.service.UserApplicationService;
import com.neutech.service.UsersService;
import com.neutech.vo.ResultJson;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Resource
    private UserApplicationService userApplicationService;

    @Resource
    private UsersService usersService;

    // 获取所有用户的申请列表（待审核）
    @GetMapping("/applications/pending")
    public ResultJson<IPage<UserApplication>> getPendingApplications(
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            IPage<UserApplication> page = new Page<>(pageNo, pageSize);
            // 查询待审核的申请
            IPage<UserApplication> result = userApplicationService.getPendingApplications(page);
            return ResultJson.success(result, "查询成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultJson.failed("查询失败：" + e.getMessage());
        }
    }

    // 审核申请
    @PutMapping("/applications/{id}/review")
    public ResultJson<String> reviewApplication(
            @PathVariable Long id,
            @RequestParam String status, // "已通过" 或 "已拒绝"
            @RequestParam(required = false) String remark) { // 审核备注
        try {
            boolean success = userApplicationService.reviewApplication(id, status, remark);
            if (success) {
                return ResultJson.success("审核成功");
            } else {
                return ResultJson.failed("审核失败：申请不存在或状态错误");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultJson.failed("审核失败：" + e.getMessage());
        }
    }

    // 获取所有用户列表
    @GetMapping("/users")
    public ResultJson<IPage<Users>> getAllUsers(
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name) {
        try {
            IPage<Users> result = usersService.page(pageNo, pageSize, name);
            return ResultJson.success(result, "查询成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultJson.failed("查询失败：" + e.getMessage());
        }
    }

    // 设置用户为管理员
    @PutMapping("/users/{id}/admin")
    public ResultJson<String> setUserAsAdmin(
            @PathVariable Integer id,
            @RequestParam Boolean isAdmin) { // true-设为管理员，false-取消管理员
        try {
            Users user = usersService.getById(id);
            if (user == null) {
                return ResultJson.failed("用户不存在");
            }

            user.setIsAdmin(isAdmin);
            usersService.updateById(user);

            String message = isAdmin ? "已设为管理员" : "已取消管理员权限";
            return ResultJson.success(message);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultJson.failed("操作失败：" + e.getMessage());
        }
    }

    // 获取所有申请记录（包括已审核的）
    @GetMapping("/applications/all")
    public ResultJson<IPage<UserApplication>> getAllApplications(
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status) { // 可选筛选状态
        try {
            IPage<UserApplication> page = new Page<>(pageNo, pageSize);
            IPage<UserApplication> result = userApplicationService.getAllApplications(page, status);
            return ResultJson.success(result, "查询成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultJson.failed("查询失败：" + e.getMessage());
        }
    }

}
