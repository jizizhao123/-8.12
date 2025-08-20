// src/main/java/com/neutech/controller/UserApplicationController.java
package com.neutech.controller;

import com.neutech.dto.UserApplicationDTO;
import com.neutech.entity.UserApplication;
import com.neutech.entity.Users;
import com.neutech.mapper.UserApplicationMapper;
import com.neutech.util.NeutechUtil;
import com.neutech.vo.ResultJson;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author jzz
 * @since 2025-08-18
 */
@RestController
@RequestMapping("/userApplication")
public class UserApplicationController {

    @Resource
    private UserApplicationMapper userApplicationMapper;

    @PostMapping("/submit")
    public ResultJson<String> submitApplication(@RequestBody UserApplicationDTO dto,
                                                HttpServletRequest request) {
        try {
            // 参数校验
            if (dto == null) {
                return ResultJson.failed("请求参数不能为空");
            }

            if (dto.getTypeId() == null) {
                return ResultJson.failed("申请类型不能为空");
            }

            if (dto.getDescription() == null || dto.getDescription().trim().isEmpty()) {
                return ResultJson.failed("申请描述不能为空");
            }

            if (dto.getImageUrl() == null || dto.getImageUrl().trim().isEmpty()) {
                return ResultJson.failed("图片证明不能为空");
            }

            // 从 token 中获取用户信息
            Users currentUser = getCurrentUserFromToken(request);
            if (currentUser == null) {
                return ResultJson.failed("用户未登录或token无效");
            }

            UserApplication application = new UserApplication();
            application.setNumber(currentUser.getNumber()); // 从用户信息中获取学号
            application.setTypeId(dto.getTypeId());
            application.setDescription(dto.getDescription().trim());
            application.setImageUrl(dto.getImageUrl().trim());
            application.setStatus("待审核"); // 初始状态
            application.setCreateTime(LocalDateTime.now());
            application.setUpdateTime(LocalDateTime.now());

            userApplicationMapper.insert(application);

            return ResultJson.success("申请提交成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultJson.failed("提交失败：" + e.getMessage());
        }
    }

    // 查看当前用户的所有申请记录
    @GetMapping("/list")
    public ResultJson<List<UserApplication>> listMyApplications(HttpServletRequest request) {
        try {
            // 从 token 中获取用户信息
            Users currentUser = getCurrentUserFromToken(request);
            if (currentUser == null) {
                return ResultJson.failed("用户未登录或token无效");
            }

            // 查询当前用户的所有申请记录
            QueryWrapper<UserApplication> wrapper = new QueryWrapper<>();
            wrapper.eq("number", currentUser.getNumber());
            wrapper.orderByDesc("create_time"); // 按创建时间倒序排列

            List<UserApplication> applications = userApplicationMapper.selectList(wrapper);

            return ResultJson.success(applications, "查询成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultJson.failed("查询失败：" + e.getMessage());
        }
    }

    // 分页查看当前用户的所有申请记录
    @GetMapping("/list/page")
    public ResultJson<IPage<UserApplication>> listMyApplicationsByPage(
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        try {
            // 从 token 中获取用户信息
            Users currentUser = getCurrentUserFromToken(request);
            if (currentUser == null) {
                return ResultJson.failed("用户未登录或token无效");
            }

            // 分页查询当前用户的所有申请记录
            QueryWrapper<UserApplication> wrapper = new QueryWrapper<>();
            wrapper.eq("number", currentUser.getNumber());
            wrapper.orderByDesc("create_time"); // 按创建时间倒序排列

            IPage<UserApplication> page = new Page<>(pageNo, pageSize);
            IPage<UserApplication> result = userApplicationMapper.selectPage(page, wrapper);

            return ResultJson.success(result, "查询成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultJson.failed("查询失败：" + e.getMessage());
        }
    }

    // 从token中获取当前用户信息的辅助方法
    private Users getCurrentUserFromToken(HttpServletRequest request) {
        // 从请求头中获取token
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                // 去掉 "Bearer " 前缀获取实际token
                String token = authorizationHeader.substring(7);
                // 使用NeutechUtil解码token获取用户信息
                return NeutechUtil.decode(token);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
