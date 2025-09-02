package com.neutech.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neutech.service.UsersService;
import com.neutech.entity.Users;
import com.neutech.util.NeutechUtil;
import com.neutech.vo.ResultJson;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author jzz
 * @since 2025-08-12
 */
@RestController
@RequestMapping("/users")
public class UsersController {

    @Resource
    UsersService usersService;

    @GetMapping("/list")
    ResultJson<IPage<Users>> list(Integer pageNo, Integer pageSize, String name) throws InterruptedException {
        return ResultJson.success(usersService.page(pageNo, pageSize, name));
    }

    @GetMapping("/ranking")
    public ResultJson<List<Users>> getRanking() {
        return ResultJson.success(usersService.getTopRanking());
    }

    @PostMapping("/login")
    ResultJson<String> login(String email, String password) {
        return ResultJson.success(usersService.login(email, password));
    }
    @PostMapping("/uploadAvatar")
    public ResultJson<String> uploadAvatar(MultipartFile file, Integer userId) {
        System.out.println("收到前端请求");
        // 调用 UsersService 的 uploadAvatar 方法，传递 userId
        String avatarUrl = usersService.uploadAvatar(file, userId);
        return ResultJson.success(avatarUrl);
    }
    @PostMapping("/updateIntroduction")
    public ResultJson<String> updateIntroduction(String introduction) {
        try {
            Users currentUser = getCurrentUserFromToken();
            if (currentUser == null) {
                return ResultJson.failed("用户未登录或token无效");
            }

            Users user = new Users();
            user.setId(currentUser.getId());
            user.setIntroduction(introduction);
            usersService.updateById(user);

            return ResultJson.success("个人简介更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultJson.failed("个人简介更新失败");
        }
    }


    @PostMapping("/register")
    ResultJson<String> register(Users user) {
        usersService.register(user);
        return ResultJson.success("注册成功");
    }
    @GetMapping("/ranking/personal")
    public ResultJson<Map<String, Object>> getPersonalRanking() {
        try {
            // 从请求上下文中获取当前用户信息
            Users currentUser = getCurrentUserFromToken();
            if (currentUser == null) {
                return ResultJson.failed("用户未登录或token无效");
            }

            // 获取用户的排名
            Integer rank = usersService.getPersonalRanking(currentUser.getId());

            // 直接从currentUser获取分数（因为已经在上面通过getId()获取了完整用户信息）
            Map<String, Object> result = new HashMap<>();
            result.put("rank", rank);
            result.put("score", currentUser.getScore());

            return ResultJson.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultJson.failed("获取个人排名失败");
        }
    }

    @GetMapping("/profile")
    public ResultJson<Map<String, Object>> getUserProfile() {
        try {
            // 从请求上下文中获取当前用户信息
            Users currentUser = getCurrentUserFromToken();
            if (currentUser == null) {
                return ResultJson.failed("用户未登录或token无效");
            }

            // 获取用户标签
            List<String> tags = usersService.getUserTags(currentUser.getId());

            // 构造返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("user", currentUser);
            result.put("tags", tags);

            // 返回当前用户信息和标签
            return ResultJson.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultJson.failed("获取用户信息失败");
        }
    }


    // 从token中获取当前用户信息的辅助方法
    // 从token中获取当前用户信息的辅助方法
    private Users getCurrentUserFromToken() {
        // 获取当前HTTP请求
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        // 从请求头中获取token
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                // 去掉 "Bearer " 前缀获取实际token
                String token = authorizationHeader.substring(7);
                // 使用NeutechUtil解码token获取用户信息
                Users user = NeutechUtil.decode(token);
                // 重新从数据库获取最新用户信息（可选）
                if (user != null && user.getId() != null) {
                    return this.usersService.getById(user.getId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    @PostMapping("/users/{userId}/addTag")
    public ResultJson<String> addUserTag(@PathVariable Integer userId,
                                         @RequestParam String competitionName,
                                         @RequestParam String awardLevel,
                                         @RequestParam String awardRank) {
        try {
            // 验证管理员权限（可以从 getCurrentUserFromToken 复用逻辑）
            Users currentUser = getCurrentUserFromToken();
            if (currentUser == null || !currentUser.getIsAdmin()) {
                return ResultJson.failed("权限不足");
            }

            usersService.addTagToUser(userId, competitionName, awardLevel, awardRank);
            return ResultJson.success("标签添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultJson.failed("标签添加失败");
        }
    }

    /**
     * 获取用户标签
     */
    @GetMapping("/users/{userId}/tags")
    public ResultJson<List<String>> getUserTags(@PathVariable Integer userId) {
        try {
            // 管理员权限验证
            Users currentUser = getCurrentUserFromToken();
            if (currentUser == null || !currentUser.getIsAdmin()) {
                return ResultJson.failed("权限不足");
            }

            List<String> tags = usersService.getUserTags(userId);
            return ResultJson.success(tags);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultJson.failed("获取标签失败");
        }
    }



}

